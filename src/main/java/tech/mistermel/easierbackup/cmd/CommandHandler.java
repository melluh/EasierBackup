package tech.mistermel.easierbackup.cmd;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandHandler extends CommandBranch implements CommandExecutor, TabCompleter {

	public CommandHandler() {
		this.setUsage("/backup");
		
		this.addSubCommand("start", new StartSubCommand());
		this.addSubCommand("reload", new ReloadSubCommand());
		this.addSubCommand("abort", new AbortSubCommand());
		this.addSubCommand("list", new ListSubCommand());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		onCommand(sender, args);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return onTabComplete(sender, args);
	}
	
}
