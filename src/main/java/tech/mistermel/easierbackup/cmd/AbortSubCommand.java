package tech.mistermel.easierbackup.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;

public class AbortSubCommand extends SubCommand {

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if(!EasierBackup.instance().isRunning()) {
			sender.sendMessage(ChatColor.RED + "Backup is not running");
			return;
		}
		
		EasierBackup.instance().abortBackup();
	}

}
