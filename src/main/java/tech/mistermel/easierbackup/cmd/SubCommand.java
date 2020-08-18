package tech.mistermel.easierbackup.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface SubCommand {

	public void onCommand(CommandSender sender, String[] args);
	public List<String> onTabComplete(CommandSender sender, String[] args);
	
	public String getUsage();
	public String getDescription();
	
}
