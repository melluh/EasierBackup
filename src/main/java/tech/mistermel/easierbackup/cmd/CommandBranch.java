package tech.mistermel.easierbackup.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class CommandBranch implements SubCommand {

	private Map<String, SubCommand> subCommands = new HashMap<>();
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if(args.length == 0) {
			return;
		}
		
		SubCommand subCmd = subCommands.get(args[0]);
		if(subCmd == null) {
			sender.sendMessage(ChatColor.RED + "Subcommand not found.");
			return;
		}
		
		subCmd.onCommand(sender, args);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		if(args.length == 0) {
			return new ArrayList<String>(subCommands.keySet());
		}
		
		SubCommand subCmd = subCommands.get(args[0]);
		if(subCmd == null) {
			return Collections.emptyList();
		}
		
		return subCmd.onTabComplete(sender, args);
	}
	
	@Override
	public String getUsage() {
		return null;
	}
	
	public void addSubCommand(String label, SubCommand subCmd) {
		subCommands.put(label, subCmd);
	}
	
}
