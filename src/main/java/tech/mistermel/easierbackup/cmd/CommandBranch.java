package tech.mistermel.easierbackup.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public abstract class CommandBranch extends SubCommand {

	private Map<String, SubCommand> subCommands = new HashMap<>();
	private Map<String, SubCommand> aliases = new HashMap<>();
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(ChatColor.AQUA + "Available subcommands:");
			
			for(SubCommand subCmd : subCommands.values()) {
				sender.sendMessage("- " + subCmd.getUsage() + ": " + subCmd.getDescription());
			}
			
			return;
		}
		
		SubCommand subCmd = getSubCommand(args[0]);
		if(subCmd == null) {
			sender.sendMessage(ChatColor.RED + "Subcommand not found. Use " + getUsage() + " for a list of available subcommands.");
			return;
		}
		
		if(subCmd.getRequiredPermission() != null && !sender.hasPermission(subCmd.getRequiredPermission())) {
			sender.sendMessage(ChatColor.RED + "You do not have the required permission to use this command.");
			return;
		}
		
		subCmd.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		if(args.length == 1) {
			List<String> result = new ArrayList<String>();
			StringUtil.copyPartialMatches(args[0], subCommands.keySet(), result);
			Collections.sort(result);
			
			return result;
		}
		
		SubCommand subCmd = getSubCommand(args[0].toLowerCase());
		if(subCmd == null) {
			return Collections.emptyList();
		}
		
		return subCmd.onTabComplete(sender, args);
	}
	
	private SubCommand getSubCommand(String label) {
		if(subCommands.containsKey(label))
			return subCommands.get(label);
		
		if(aliases.containsKey(label))
			return aliases.get(label);
		
		return null;
	}
	
	public void addSubCommand(SubCommand subCmd, String label, String... aliases) {
		subCommands.put(label, subCmd);
		Arrays.stream(aliases).forEach(alias -> this.aliases.put(alias, subCmd));
	}
	
}
