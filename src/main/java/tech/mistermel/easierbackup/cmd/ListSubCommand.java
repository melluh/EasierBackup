package tech.mistermel.easierbackup.cmd;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;

public class ListSubCommand extends SubCommand {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE dd MMM yyyy 'at' HH:mm");
	
	public ListSubCommand() {
		this.setUsage("/backup list");
		this.setDescription("Displays a list of made backups");
		this.setRequiredPermission("easierbackup.list");
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		File folder = EasierBackup.instance().getBackupsFolder();
		Set<File> files = Arrays.stream(folder.listFiles())
				.filter(file -> file.isFile()) // Filter out folders
				.collect(Collectors.toSet());
		
		if(files.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "No backups made");
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + Integer.toString(files.size()) + " backups made:");
		files.forEach(file -> sender.sendMessage(ChatColor.GRAY + "- " + this.formatFile(file)));
	}
	
	private String formatFile(File file) {
		return DATE_FORMAT.format(new Date(file.lastModified())) + " (" + EasierBackup.readableFileSize(file.length()) + ")";
	}

}
