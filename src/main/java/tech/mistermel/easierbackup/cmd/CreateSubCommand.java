package tech.mistermel.easierbackup.cmd;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

public class CreateSubCommand implements SubCommand {

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

	@Override
	public String getUsage() {
		return "/backup create";
	}

	@Override
	public String getDescription() {
		return "Creates a new backup";
	}
	
}
