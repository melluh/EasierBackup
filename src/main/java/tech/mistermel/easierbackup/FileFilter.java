package tech.mistermel.easierbackup;

import java.util.regex.Pattern;

public class FileFilter {
	
	private Pattern pattern;
	
	public FileFilter(String rule) {
		String regex = rule.toLowerCase().replaceAll("[\\W]", "\\\\$0").replace("\\*", ".+");
		this.pattern = Pattern.compile(regex);
	}
	
	public boolean matches(String str) {
		return pattern.matcher(str.toLowerCase()).find();
	}
	
}
