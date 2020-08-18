package tech.mistermel.easierbackup.cmd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import tech.mistermel.easierbackup.EasierBackup;

public abstract class AsyncSubCommand extends SubCommand {

	@Override
	public final void onCommand(CommandSender sender, String[] args) {
		Bukkit.getServer().getScheduler().runTaskAsynchronously(EasierBackup.instance(), () -> onAsyncCommand(sender, args));
	}
	
	public abstract void onAsyncCommand(CommandSender sender, String[] args);

}
