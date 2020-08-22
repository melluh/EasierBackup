package tech.mistermel.easierbackup.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;

public class ReloadSubCommand extends SubCommand {

	public ReloadSubCommand() {
		this.setUsage("/backup reload");
		this.setDescription("Reloads the EasierBackup config");
		this.setRequiredPermission("easierbackup.command.reload");
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if(EasierBackup.instance().isRunning()) {
			sender.sendMessage(ChatColor.RED + "Cannot reload config while a backup is running");
			return;
		}
		
		EasierBackup.instance().doConfigReload();
		sender.sendMessage(ChatColor.GREEN + "Config reloaded");
	}

}
