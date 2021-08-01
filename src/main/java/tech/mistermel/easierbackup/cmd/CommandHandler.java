package tech.mistermel.easierbackup.cmd;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandHandler extends CommandBranch implements CommandExecutor, TabCompleter {

	public CommandHandler() {
		this.setUsage("/backup");
		
		this.addSubCommand(new StartSubCommand(), "start", "create");
		this.addSubCommand(new AbortSubCommand(), "abort", "stop", "cancel");
		this.addSubCommand(new ReloadSubCommand(), "reload");
		this.addSubCommand(new ListSubCommand(), "list");
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
