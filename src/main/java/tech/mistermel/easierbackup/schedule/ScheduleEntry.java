package tech.mistermel.easierbackup.schedule;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import tech.mistermel.easierbackup.EasierBackup;

public class ScheduleEntry {

	private List<DayOfWeek> days;
	private int hours, minutes;
	private long lastTrigger;
	
	public ScheduleEntry(List<DayOfWeek> days, int hours, int minutes) {
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
	}
	
	public void trigger() {
		EasierBackup.instance().getLogger().info("Automatic backup triggered");
		EasierBackup.instance().doBackup();
		this.lastTrigger = System.currentTimeMillis();
	}
	
	public long getTimeSinceLastTrigger() {
		return System.currentTimeMillis() - lastTrigger;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for(DayOfWeek day : days) {
			String displayName = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
			builder.append(displayName + ", ");
		}
		builder.delete(builder.length() - 2, builder.length());
		builder.append(" at " + String.format("%02d", hours) + ":" + String.format("%02d", minutes));
		
		return builder.toString();
	}
	
	public List<DayOfWeek> getDays() {
		return days;
	}
	
	public int getHours() {
		return hours;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
}
