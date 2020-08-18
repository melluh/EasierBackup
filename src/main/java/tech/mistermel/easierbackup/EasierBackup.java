package tech.mistermel.easierbackup;

import org.bukkit.plugin.java.JavaPlugin;

public class EasierBackup extends JavaPlugin {

	private static EasierBackup instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		this.getCommand("easierbackup").setExecutor(new CommandHandler());
	}
	
	public static EasierBackup instance() {
		return instance;
	}
	
}
