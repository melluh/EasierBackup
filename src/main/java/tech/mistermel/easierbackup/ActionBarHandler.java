package tech.mistermel.easierbackup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarHandler {

	private int taskId, lastPercentage;
	private BaseComponent[] message;
	
	public void start() {
		this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(EasierBackup.instance(), () -> sendActionBar(), 1, 1);
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(taskId);
		
		/* Immediately clears action bar instead of fading out */
		this.setMessage("");
		this.sendActionBar();
	}
	
	public void setProgress(int progressPercentage) {
		if(progressPercentage == lastPercentage)
			return;
		this.lastPercentage = progressPercentage;
		
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < 10; i++) {
			int percentageTreshold = (i + 1) * 10;
			ChatColor color = progressPercentage >= percentageTreshold ? ChatColor.GREEN : ChatColor.GRAY;
			builder.append(color + "|");
		}
		
		builder.append(" " + ChatColor.AQUA + progressPercentage + "%");
		this.setMessage(builder.toString());
	}
	
	private void setMessage(String str) {
		this.message = TextComponent.fromLegacyText(str);
	}
	
	private void sendActionBar() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(!player.hasPermission("easierbackup.showprogress") && !player.hasPermission("easierbackup.controlbackup")) {
				continue;
			}
			
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
		}
	}
	
}
