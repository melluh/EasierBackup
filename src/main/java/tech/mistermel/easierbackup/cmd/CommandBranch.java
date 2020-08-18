package tech.mistermel.easierbackup.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class CommandBranch extends SubCommand {

	private Map<String, SubCommand> subCommands = new HashMap<>();
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(ChatColor.AQUA + "Available subcommands:");
			
			for(SubCommand subCmd : subCommands.values()) {
				sender.sendMessage("- " + subCmd.getUsage() + ": " + subCmd.getDescription());
			}
			
			return;
		}
		
		SubCommand subCmd = subCommands.get(args[0]);
		if(subCmd == null) {
			sender.sendMessage(ChatColor.RED + "Subcommand not found. Use " + getUsage() + " for a list of available subcommands.");
			return;
		}
		
		subCmd.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
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
	
	public void addSubCommand(String label, SubCommand subCmd) {
		subCommands.put(label, subCmd);
	}
	
}
