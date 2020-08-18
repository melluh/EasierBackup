package tech.mistermel.easierbackup.cmd;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DropboxSubCommand extends CommandBranch {

	public DropboxSubCommand() {
		this.addSubCommand("check", new CheckSubCommand());
		this.addSubCommand("generatelink", new GenerateLinkCommand());
	}
	
	@Override
	public String getDescription() {
		return "Manages the connection to your Dropbox account";
	}
	
	public class CheckSubCommand implements SubCommand {

		@Override
		public void onCommand(CommandSender sender, String[] args) {
			// TODO: Implement command
			sender.sendMessage(ChatColor.RED + "This command has not been implemented yet.");
		}

		@Override
		public List<String> onTabComplete(CommandSender sender, String[] args) {
			return Collections.emptyList();
		}

		@Override
		public String getUsage() {
			return "/backup dropbox check";
		}

		@Override
		public String getDescription() {
			return "Checks if your Dropbox connection is functioning correctly";
		}
		
	}
	
	public class GenerateLinkCommand implements SubCommand {

		@Override
		public void onCommand(CommandSender sender, String[] args) {
			
		}

		@Override
		public List<String> onTabComplete(CommandSender sender, String[] args) {
			return Collections.emptyList();
		}

		@Override
		public String getUsage() {
			return "/backup dropbox generatelink";
		}

		@Override
		public String getDescription() {
			return "Generates a new URL to link your Dropbox account";
		}
		
	}

}
