package tech.mistermel.easierbackup.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;

public class AbortSubCommand extends SubCommand {

	public AbortSubCommand() {
		this.setUsage("/backup abort");
		this.setDescription("Stops creating the backup");
		this.setRequiredPermission("easierbackup.command.abort");
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if(!EasierBackup.instance().isRunning()) {
			sender.sendMessage(ChatColor.RED + "Backup is not running");
			return;
		}
		
		EasierBackup.instance().abortBackup();
	}

}
