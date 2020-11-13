package tech.mistermel.easierbackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import tech.mistermel.easierbackup.AnnouncementHandler.AnnouncementType;
import tech.mistermel.easierbackup.cmd.CommandHandler;
import tech.mistermel.easierbackup.schedule.ScheduleHandler;

public class EasierBackup extends JavaPlugin {

	private static EasierBackup instance;

	private ScheduleHandler scheduleHandler;
	private ActionBarHandler actionBarHandler;
	
	private File serverFolder;
	private File backupsFolder;

	private SimpleDateFormat dateFormat;
	private int compressionLevel;
	private long backupsFolderSize, maxBackupSize;

	private boolean isRunning;
	private long completeSize, processedSize;
	private int lastPercentage;

	@Override
	public void onEnable() {
		instance = this;

		File configFile = new File(this.getDataFolder(), "config.yml");
		if(!configFile.exists()) {
			this.saveDefaultConfig();
		}
		
		this.scheduleHandler = new ScheduleHandler();
		this.actionBarHandler = new ActionBarHandler();
		
		this.setupConfigVariables();

		this.serverFolder = this.getServer().getWorldContainer();
		this.backupsFolder = new File(serverFolder, "backups");
		if(!backupsFolder.isDirectory()) {
			backupsFolder.mkdir();
		}
		
		this.getCommand("easierbackup").setExecutor(new CommandHandler());
	}
	
	@Override
	public void onDisable() {
		if(this.isRunning()) {
			this.abortBackup();
		}
	}
	
	public void doConfigReload() {
		if(isRunning) {
			throw new IllegalStateException("Cannot reload while running");
		}
		
		this.reloadConfig();
		this.setupConfigVariables();
	}
	
	private void setupConfigVariables() {
		this.dateFormat = new SimpleDateFormat(this.getConfig().getString("date-format"));

		this.compressionLevel = this.getConfig().getInt("compression-level");
		if (compressionLevel > 9) {
			compressionLevel = 9;
			this.getLogger().warning("Compression level cannot be set higher than 9. Defaulting to 9.");
		} else {
			this.getLogger().info("Compression level is set to " + compressionLevel);
		}
		
		double configMaxBackupSize = this.getConfig().getDouble("max-backup-folder-size");
		if(configMaxBackupSize == -1) {
			this.getLogger().warning("Max backup folder size is disabled. You will need to manually delete old backups.");
			this.maxBackupSize = -1;
		} else {
			this.maxBackupSize = (long) (configMaxBackupSize * 1073741824);
			this.getLogger().info("Max backup folder size is set to " + readableFileSize(maxBackupSize));
		}
		
		scheduleHandler.load(this.getConfig().getConfigurationSection("schedule"));
	}
	
	public void abortBackup() {
		if(!isRunning)
			throw new IllegalStateException("Backup is not running");
		
		this.isRunning = false;
		this.getLogger().info("Aborting backup...");
	}

	public void doBackup() {
		if(isRunning) {
			throw new IllegalStateException("Backup is already running");
		}
		
		AnnouncementHandler.sendAnnouncement(AnnouncementType.BACKUP_STARTING);
		
		this.isRunning = true;
		this.lastPercentage = 0;
		this.processedSize = 0;
		
		actionBarHandler.start();
		
		Set<World> autosaveWorlds = new HashSet<>();
		for(World world : Bukkit.getWorlds()) {
			this.getLogger().info("Saving " + world.getName() + "...");
			world.save();
			
			if(world.isAutoSave()) {
				autosaveWorlds.add(world);
			}
			
			world.setAutoSave(false);
		}
		this.getLogger().info("Creating ZIP file, please wait...");
		
		String fileName = this.getConfig().getString("file-name").replace("%%date%%", dateFormat.format(new Date()));
		File zipFile = new File(backupsFolder, fileName);

		try {
			zipFile.createNewFile();
		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "Error occurred while attempting to create backup file", e);
			this.isRunning = false;
			return;
		}

		this.getServer().getScheduler().runTaskAsynchronously(this, () -> {			
			try {
				this.zipFolder(serverFolder, zipFile);
			} catch (IOException e) {
				this.getLogger().log(Level.SEVERE, "Error occurred while attempting to create zip file", e);
				this.isRunning = false;
				return;
			}
			
			// Checks if the backup was aborted
			if(!isRunning) {
				actionBarHandler.stop();
				AnnouncementHandler.sendAnnouncement(AnnouncementType.BACKUP_ABORTED);
				
				this.enableAutosave(autosaveWorlds);
				
				zipFile.delete();
				this.getLogger().info("Backup aborted, file deleted");
				return;
			}
			
			this.getLogger().info("ZIP file created (" + readableFileSize(completeSize) + " -> " + readableFileSize(zipFile.length()) + ")");
			
			int removedFiles = this.removeOldBackups();
			if(removedFiles > 0) {
				this.getLogger().info("Removed " + removedFiles + " old backup" + (removedFiles == 1 ? "" : "s") + ". Backup folder size is now " + readableFileSize(backupsFolderSize));
			}
			
			this.enableAutosave(autosaveWorlds);
			
			this.getServer().getScheduler().runTask(this, () -> {
				// Commands can only be dispatched synchronously
				this.executeConsoleCommands();
			});
			
			this.executeTerminalCommands();
			
			actionBarHandler.stop();
			AnnouncementHandler.sendAnnouncement(AnnouncementType.BACKUP_FINISHED);
			
			this.isRunning = false;
		});
	}
	
	private void enableAutosave(Set<World> autosaveWorlds) {
		for(World world : autosaveWorlds) {
			world.setAutoSave(true);
		}
		this.getLogger().info("Re-enabled autosave for " + autosaveWorlds.size() + (autosaveWorlds.size() == 1 ? " world" : " worlds"));
	}
	
	public void executeTerminalCommands() {
		List<String> commands = this.getConfig().getStringList("terminal-commands");
		if(commands.size() == 0) {
			return;
		}
		
		for(String consoleCmd : commands) {
			try {
				new ProcessBuilder("bash", "-c", consoleCmd).inheritIO().start();
			} catch (IOException e) {
				this.getLogger().log(Level.SEVERE, "Error occurred while attempting to execute terminal command", e);
			}
		}
		
		this.getLogger().info("Executed " + commands.size() + " terminal command" + (commands.size() == 1 ? "" : "s"));
	}
	
	public void executeConsoleCommands() {
		List<String> commands = this.getConfig().getStringList("console-commands");
		if(commands.size() == 0) {
			return;
		}
		
		for(String consoleCmd : commands) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCmd);
		}
		
		this.getLogger().info("Executed " + commands.size() + " console command" + (commands.size() == 1 ? "" : "s"));
	}
	
	private int removeOldBackups() {
		if(maxBackupSize == -1) {
			return 0;
		}
		
		int removedFiles = 0;
		long backupSize = getFolderSize(backupsFolder);
		while(backupSize > maxBackupSize) {
			File oldestFile = this.getOldestFile(backupsFolder);
			long fileSize = oldestFile.length();
			
			if(oldestFile.delete()) {
				backupSize -= fileSize;
				removedFiles++;
			} else {
				this.getLogger().warning("Failed to remove file " + oldestFile.getName());
			}
		}
		
		this.backupsFolderSize = backupSize;
		return removedFiles;
	}
	
	private File getOldestFile(File folder) {
		if(!folder.isDirectory())
			throw new IllegalArgumentException("Not a directory");
		
		File oldestFile = null;
		for(File file : folder.listFiles()) {
			if(oldestFile == null || file.lastModified() < oldestFile.lastModified()) {
				oldestFile = file;
			}
		}
		
		return oldestFile;
	}
	
	private long getFolderSizeWithExempt(File folder) {
		if(!folder.isDirectory())
			throw new IllegalStateException("Not a folder");
		
		long size = 0;
		for(File file : folder.listFiles()) {
			if(file.getName().equals("session.lock") || this.isExempt(file)) {
				continue;
			}
			
			if(file.isDirectory()) {
				size += this.getFolderSizeWithExempt(file);
				continue;
			}
			
			size += file.length();
		}
		
		return size;
	}
	
	private long getFolderSize(File folder) {
		if(!folder.isDirectory())
			return folder.length();
		
		long size = 0;
		
		for(File file : folder.listFiles()) {
			if(file.isDirectory()) {
				size += this.getFolderSize(file);
				continue;
			}
			size += file.length();
		}
		
		return size;
	}

	private void zipFolder(File srcFolder, File destFile) throws IOException {
		this.completeSize = this.getFolderSizeWithExempt(serverFolder);
		
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(destFile));
		zipOut.setLevel(compressionLevel);

		this.addFolderToZip(srcFolder, "", zipOut);

		zipOut.flush();
		zipOut.close();
	}

	private void addFolderToZip(File folder, String path, ZipOutputStream zipOut) {
		if(this.isExempt(folder)) {
			return;
		}

		for(File file : folder.listFiles()) {
			String filePath = (path.isEmpty() ? "" : path + "/") + file.getName();

			if(!isRunning) {
				break;
			}
			
			if(file.isDirectory()) {
				this.addFolderToZip(file, filePath, zipOut);
				continue;
			}

			this.addFileToZip(file, filePath, zipOut);
		}
	}

	private void addFileToZip(File file, String path, ZipOutputStream zipOut) {
		if(file.getName().equals("session.lock") || this.isExempt(file) || !isRunning) {
			return;
		}

		try {
			FileInputStream fileIn = new FileInputStream(file);
			zipOut.putNextEntry(new ZipEntry(path));

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fileIn.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
				
				processedSize += length;
				
				int percentage = (int) (0.5d + ((double) processedSize / (double) completeSize) * 100);
				actionBarHandler.setProgress(percentage);
				if(percentage != lastPercentage && percentage % this.getConfig().getInt("percentage-log-interval") == 0) {
					lastPercentage = percentage;
					this.getLogger().info(readableFileSize(processedSize) + "/" + readableFileSize(completeSize) + " (" + percentage + "%)");
				}
			}

			zipOut.closeEntry();
			fileIn.close();
		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "Error occurred while attempting to add file to zip (" + file.getName() + ")", e);
		}
	}

	public static String readableFileSize(long size) {
	    if(size <= 0)
	    	return "0 B";
	    
	    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
	    return new DecimalFormat("#.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	private boolean isExempt(File file) {
		String path = file.getPath();
		if(path.startsWith(".\\") || path.startsWith("./")) {
			path = path.substring(2);
		}
		
		return this.getConfig().getStringList("exempt").contains(path);
	}
	
	public boolean isRunning() {
		return isRunning;
	}

	public static EasierBackup instance() {
		return instance;
	}

}
