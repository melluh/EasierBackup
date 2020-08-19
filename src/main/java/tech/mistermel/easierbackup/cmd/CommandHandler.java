package tech.mistermel.easierbackup.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler extends CommandBranch implements CommandExecutor  {

	public CommandHandler() {
		this.setUsage("/backup");
		
		this.addSubCommand("create", new CreateSubCommand());
		this.addSubCommand("reload", new ReloadSubCommand());
		this.addSubCommand("finish", new FinishSubCommand());
		this.addSubCommand("abort", new AbortSubCommand());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		onCommand(sender, args);
		return true;
	}
	
}
