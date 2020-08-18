package tech.mistermel.easierbackup;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import tech.mistermel.easierbackup.util.NMSUtil;

public class CommandHandler implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		EasierBackup.instance().getLogger().info("Saving world...");
		NMSUtil.setSavingEnabled(false);
		NMSUtil.saveAll(true, true, true);
		EasierBackup.instance().getLogger().info("Doing backup...");
		// backup here
		NMSUtil.setSavingEnabled(true);
		EasierBackup.instance().getLogger().info("Backup finished!");
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}
	
}
