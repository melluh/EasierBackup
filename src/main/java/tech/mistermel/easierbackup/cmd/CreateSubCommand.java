package tech.mistermel.easierbackup.cmd;

import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;

public class CreateSubCommand extends SubCommand {

	public CreateSubCommand() {
		this.setUsage("/backup create");
		this.setDescription("Creates a new backup");
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		EasierBackup.instance().doBackup();
	}
	
}
