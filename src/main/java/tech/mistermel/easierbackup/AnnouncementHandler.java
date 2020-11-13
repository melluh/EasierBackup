package tech.mistermel.easierbackup;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import net.md_5.bungee.api.ChatColor;

public class AnnouncementHandler {

	private AnnouncementHandler() {}
	
	public static enum AnnouncementType {
		BACKUP_STARTING, BACKUP_FINISHED, BACKUP_ABORTED;
		
		public String getConfigName() {
			return name().toLowerCase().replace("_", "-");
		}
	}
	
	public static void sendAnnouncement(AnnouncementType type) {
		ConfigurationSection section = EasierBackup.instance().getConfig().getConfigurationSection("announcements." + type.getConfigName());
		if(!section.getBoolean("enabled")) {
			return;
		}
		
		Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', section.getString("message")));
	}
	
}
