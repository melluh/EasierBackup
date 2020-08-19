package tech.mistermel.easierbackup.cmd;

import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;

public class StartSubCommand extends SubCommand {

	public StartSubCommand() {
		this.setUsage("/backup start");
		this.setDescription("Starts creating a new backup");
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		EasierBackup.instance().doBackup();
	}
	
}
