package tech.mistermel.easierbackup.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;

public class StartSubCommand extends SubCommand {

	public StartSubCommand() {
		this.setUsage("/backup start");
		this.setDescription("Starts creating a new backup");
		this.setRequiredPermission("easierbackup.controlbackup");
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if(EasierBackup.instance().isRunning()) {
			sender.sendMessage(ChatColor.RED + "Backup is already running");
			return;
		}
		
		EasierBackup.instance().doBackup();
	}
	
}
