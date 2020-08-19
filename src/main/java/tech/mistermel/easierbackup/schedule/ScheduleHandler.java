package tech.mistermel.easierbackup.schedule;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import tech.mistermel.easierbackup.EasierBackup;

public class ScheduleHandler {

	private static final Map<String, DayOfWeek> DAYS = new HashMap<>();
	
	static {
		DAYS.put("mon", DayOfWeek.MONDAY);
		DAYS.put("tue", DayOfWeek.TUESDAY);
		DAYS.put("wed", DayOfWeek.WEDNESDAY);
		DAYS.put("thu", DayOfWeek.THURSDAY);
		DAYS.put("fri", DayOfWeek.FRIDAY);
		DAYS.put("sat", DayOfWeek.SATURDAY);
		DAYS.put("sun", DayOfWeek.SUNDAY);
	}
	private BukkitTask checkingTask;
	
	private Set<ScheduleEntry> entries = new HashSet<>();
	
	public void load(ConfigurationSection configSection) {
		entries.clear();
		if(checkingTask != null) {
			checkingTask.cancel();
		}
		
		List<String> times = configSection.getStringList("times");
		for(String timeStr : times) {
			ScheduleEntry entry = this.parse(timeStr);
			entries.add(entry);
		}
		
		int tickInterval = configSection.getInt("check-interval") * 20;
		this.checkingTask = new BukkitRunnable() {
			public void run() {
				LocalDateTime dateTime = LocalDateTime.now();
				DayOfWeek today = dateTime.getDayOfWeek();
				int hours = dateTime.getHour();
				int minutes = dateTime.getMinute();
				
				for(ScheduleEntry entry : entries) {
					if(entry.getDays().contains(today) && hours == entry.getHours() && minutes == entry.getMinutes()) {
						if(entry.getTimeSinceLastTrigger() > 60000) {
							entry.trigger();
						}
					}
				}
			}
		}.runTaskTimer(EasierBackup.instance(), tickInterval, tickInterval);
	}
	
	private ScheduleEntry parse(String str) {
		String[] args = str.split(" ");
		List<DayOfWeek> days = new ArrayList<>();
		
		int[] time = this.parseTime(args[args.length - 1]);
		if(time == null) {
			EasierBackup.instance().getLogger().warning("Invalid time: '" + args[args.length - 1] + "'");
			return null;
		}
		
		if(args.length > 1) {
			for(int i = 0; i < args.length - 2; i++) {
				String dayArg = args[i].toLowerCase();
				DayOfWeek day = DAYS.get(dayArg);
				if(day == null) {
					EasierBackup.instance().getLogger().warning("Invalid day: '" + dayArg + "'");
					return null;
				}
				days.add(day);
			}
		} else {
			for(DayOfWeek day : DayOfWeek.values()) {
				days.add(day);
			}
		}
		
		return new ScheduleEntry(days, time[0], time[1]);
	}
	
	private int[] parseTime(String str) {
		String[] args = str.split(":");
		if(args.length != 2) {
			return null;
		}
		
		return new int[] { Integer.parseInt(args[0]), Integer.parseInt(args[1]) };
	}
	
}
