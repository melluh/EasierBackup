package tech.mistermel.easierbackup.util;

import java.util.Iterator;

import org.bukkit.Bukkit;

import tech.mistermel.easierbackup.util.Reflection.FieldAccessor;

public class NMSUtil {

	private NMSUtil() {}
	
	/**
	 * Saves all world data. Equivalent of /save-all
	 * 
	 * @param doLog If set to true, world save will be logged to console. Otherwise, no log messages will be generated.
	 * @param doFlush If set to true, this immediately saves all chunks to disk causing a lag spike. Otherwise, saving is done over a period of time.
	 * @param doOverride If set to true, this overrides the the disabling of world saving (see {@link #setSavingEnabled(boolean)})
	 */
	public static void saveAll(boolean doLog, boolean doFlush, boolean doOverride) {		
		Object server = getMinecraftServer();
		
		Object playerList = Reflection.getMethod(server.getClass(), "getPlayerList").invoke(server);
		Reflection.getMethod(playerList.getClass(), "savePlayers").invoke(playerList);
		
		Reflection.getMethod(server.getClass(), "saveChunks", boolean.class, boolean.class, boolean.class).invoke(server, !doLog, doFlush, doOverride);
	}
	
	/**
	 * Toggles world saving. Equivalent of /save-on and /save-off
	 * 
	 * @param savingEnabled Whether world saving is enabled
	 */
	public static void setSavingEnabled(boolean savingEnabled) {
		Object server = getMinecraftServer();
		
		@SuppressWarnings("unchecked")
		Iterable<Object> worlds = (Iterable<Object>) Reflection.getMethod(server.getClass(), "getWorlds").invoke(server);
		Iterator<Object> iterator = worlds.iterator();
		
		FieldAccessor<Boolean> savingDisabledField = Reflection.getField("{nms}.WorldServer", "savingDisabled", boolean.class);
		
		while(iterator.hasNext()) {
			savingDisabledField.set(iterator.next(), !savingEnabled);
		}
	}
	
	private static Object getMinecraftServer() {
		return Reflection.getMethod(Bukkit.getServer().getClass(), "getServer").invoke(Bukkit.getServer());
	}
	
}
