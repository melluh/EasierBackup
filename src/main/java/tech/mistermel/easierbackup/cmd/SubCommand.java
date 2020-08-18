package tech.mistermel.easierbackup.cmd;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class SubCommand {

	private String usage, description;
	
	public abstract void onCommand(CommandSender sender, String[] args);
	
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}
	
	protected void sendUsage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage());
	}
	
	protected void setUsage(String usage) {
		this.usage = usage;
	}
	
	protected void setDescription(String description) {
		this.description = description;
	}
	
	public String getUsage() {
		return usage;
	}
	
	public String getDescription() {
		return description;
	}
	
}
