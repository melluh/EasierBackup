package tech.mistermel.easierbackup.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;
import tech.mistermel.easierbackup.uploader.DropboxUploader.AuthenticationResult;

public class DropboxSubCommand extends CommandBranch {

	public DropboxSubCommand() {
		this.setUsage("/backup dropbox");
		this.setDescription("Manages the connection to your Dropbox account");
		
		this.addSubCommand("check", new CheckSubCommand());
		this.addSubCommand("genlink", new GenerateLinkCommand());
		this.addSubCommand("code", new CodeCommand());
	}
	
	public class CheckSubCommand extends SubCommand {

		public CheckSubCommand() {
			this.setUsage("/backup dropbox check");
			this.setDescription("Checks if your Dropbox connection is functioning correctly");
		}
		
		@Override
		public void onCommand(CommandSender sender, String[] args) {
			// TODO: Implement command
			sender.sendMessage(ChatColor.RED + "This command has not been implemented yet.");
		}
		
	}
	
	public class GenerateLinkCommand extends SubCommand {

		public GenerateLinkCommand() {
			this.setUsage("/backup dropbox genlink");
			this.setDescription("Generates a new URL to connect your Dropbox account");
		}
		
		@Override
		public void onCommand(CommandSender sender, String[] args) {
			String url = EasierBackup.instance().getDropboxUploader().generateUserURL();
			sender.sendMessage(ChatColor.AQUA + "Open the following URL in your browser to connect your Dropbox account:");
			sender.sendMessage(url);
			sender.sendMessage(ChatColor.AQUA + "After you have received your authorization code, use " + ChatColor.WHITE + " /backup dropbox code <Authorization Code>");
		}
		
	}
	
	public class CodeCommand extends AsyncSubCommand {
		
		public CodeCommand() {
			this.setUsage("/backup dropbox code <Authorization Code>");
			this.setDescription("Used to enter your authorization code and finalize the connection");
		}
		
		@Override
		public void onAsyncCommand(CommandSender sender, String[] args) {
			if(args.length == 0) {
				this.sendUsage(sender);
				return;
			}
			
			AuthenticationResult result = EasierBackup.instance().getDropboxUploader().getBearerToken(args[0]);
			if(result == null) {
				sender.sendMessage(ChatColor.RED + "Something went wrong while attempting to process your request. Please try again later.");
				return;
			}
			
			if(!result.isSuccess()) {
				sender.sendMessage(ChatColor.RED + "That authorization code is invalid.");
				return;
			}
			
			sender.sendMessage(ChatColor.GREEN + "Success! Your Dropbox account has been connected.");
			// TODO: Save authorization code
		}
		
	}

}
