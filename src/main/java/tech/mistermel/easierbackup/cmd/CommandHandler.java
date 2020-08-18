package tech.mistermel.easierbackup.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler extends CommandBranch implements CommandExecutor  {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		onCommand(sender, args);
		return true;
	}
	
	@Override
	public String getDescription() {
		return null;
	}
	
}
