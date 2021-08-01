package tech.mistermel.easierbackup.cmd;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;

public class ListSubCommand extends SubCommand {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy 'at' HH:mm");
	
	public ListSubCommand() {
		this.setUsage("/backup list");
		this.setDescription("Displays a list of made backups");
		this.setRequiredPermission("easierbackup.list");
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		File folder = EasierBackup.instance().getBackupsFolder();
		List<File> files = Arrays.stream(folder.listFiles())
				.filter(file -> file.isFile()) // Filter out folders
				.sorted(Comparator.comparing(File::lastModified).reversed())
				.collect(Collectors.toList());
		
		if(files.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "No backups made");
			return;
		}
		
		long lastBackup = files.get(0).lastModified();
		Duration timeAgo = Duration.between(Instant.ofEpochMilli(lastBackup), Instant.now());
		sender.sendMessage(ChatColor.GREEN + Integer.toString(files.size()) + " " + (files.size() == 1 ? "backup" : "backups") + " made:");
		sender.sendMessage(ChatColor.GRAY + "Your last backup was " + this.formatDuration(timeAgo) + " ago");
		files.forEach(file -> sender.sendMessage(ChatColor.GRAY + "- " + this.formatFile(file)));
	}
	
	private String formatDuration(Duration duration) {
		if(duration.getSeconds() < 60)
			return duration.getSeconds() + " seconds";
		
		long minutes = duration.toMinutes();
		if(minutes < 60)
			return minutes + " minutes";
		
		long hours = duration.toHours();
		if(duration.toHours() < 24) {
			long minutesPart = minutes % 60;
			return String.format(hours + " " + plural("hour", hours) + (minutesPart != 0 ? " and " + minutesPart + " " + plural("minute", minutesPart) : ""));
		}
		
		long days = duration.toDays();
		long hoursPart = hours % 24;
		return String.format(days + " " + plural("day", days) + (hoursPart != 0 ? " and " + hoursPart + " " + plural("hour", hoursPart) : ""));
	}
	
	private String plural(String singular, long amount) {
		return singular + (amount == 1 ? "" : "s");
	}
	
	private String formatFile(File file) {
		return DATE_FORMAT.format(new Date(file.lastModified())) + " (" + EasierBackup.readableFileSize(file.length()) + ")";
	}

}
