package tech.mistermel.easierbackup.cmd;

import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;

public class FinishSubCommand extends SubCommand {

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		EasierBackup.instance().executeConsoleCommands();
		EasierBackup.instance().executeTerminalCommands();
		sender.sendMessage("Console and terminal commands executed");
	}
	
}
