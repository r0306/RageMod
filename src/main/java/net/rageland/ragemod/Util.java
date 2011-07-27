package net.rageland.ragemod;

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

// Misc. methods
public class Util 
{
	// Formats the cooldown time into a lovely string
	public static String formatCooldown(int totalSeconds)
	{
		int minutes, seconds;
		
		minutes = totalSeconds / 60;
		seconds = totalSeconds % 60;
		
		java.text.DecimalFormat nft = new java.text.DecimalFormat("#00.###");
		nft.setDecimalSeparatorAlwaysShown(false);
		
		return minutes + ":" + nft.format(seconds);
	}
	
	// Returns the current time
	public static Timestamp now()
	{
		Date today = new java.util.Date();
		Timestamp now = new java.sql.Timestamp(today.getTime());
		return now;
	}
	
	// Returns the number of days between the two Timestamps
	public static int daysBetween(Timestamp time1, Timestamp time2)
	{
		return (int)((time1.getTime() - time2.getTime()) / 86400000);
	}
	
	// Returns the number of seconds since the Timestamp occurred
	public static int secondsSince(Timestamp timestamp)
	{
		return (int)((now().getTime() - timestamp.getTime()) / 1000);
	}
	
	// Handles default color 
	public static void message(Player player, String message)
	{
		Util.message(player, message, ChatColor.GREEN);
	}
	
	// Formats player messages with colors
	public static void message(Player player, String message, ChatColor color)
	{
		message = ChatColor.GREEN + message;
		message = highlightPlayers(message, color);
		message = highlightCommands(message, color);
		message = highlightRequired(message, color);
		message = highlightOptional(message, color);
		message = highlightParentheses(message, color);
		message = highlightSilver(message, color);
		message = highlightCopper(message, color);
		message = highlightURL(message, color);
		
		player.sendMessage(message);
	}
	
	private static String highlightPlayers(String message, ChatColor color)
	{
		Pattern pattern = Pattern.compile("\\^\\*(\\S+)\\*\\^");
	    Matcher matcher = pattern.matcher(message);
	    return matcher.replaceAll(ChatColor.LIGHT_PURPLE + "$1" + color);
	}
	private static String highlightCommands(String message, ChatColor color)
	{
		Pattern pattern = Pattern.compile("( /[a-zA-Z]+)");
	    Matcher matcher = pattern.matcher(message);
	    return matcher.replaceAll(ChatColor.DARK_GREEN + "$1" + color);
	}
	private static String highlightRequired(String message, ChatColor color)
	{
		Pattern pattern = Pattern.compile("(<.+>)");
	    Matcher matcher = pattern.matcher(message);
	    return matcher.replaceAll(ChatColor.GOLD + "$1" + color);
	}
	private static String highlightOptional(String message, ChatColor color)
	{
		Pattern pattern = Pattern.compile("(\\[.+\\])");
	    Matcher matcher = pattern.matcher(message);
	    return matcher.replaceAll(ChatColor.YELLOW + "$1" + color);
	}
	private static String highlightParentheses(String message, ChatColor color)
	{
		Pattern pattern = Pattern.compile("([(].+[)])");
	    Matcher matcher = pattern.matcher(message);
	    return matcher.replaceAll(ChatColor.GRAY + "$1" + color);
	}
	private static String highlightSilver(String message, ChatColor color)
	{
		Pattern pattern = Pattern.compile("([\\d,]+ Silver)");
	    Matcher matcher = pattern.matcher(message);
	    return matcher.replaceAll(ChatColor.GRAY + "$1" + color);
	}
	private static String highlightCopper(String message, ChatColor color)
	{
		Pattern pattern = Pattern.compile("([\\d,]+ Copper)");
	    Matcher matcher = pattern.matcher(message);
	    return matcher.replaceAll(ChatColor.DARK_GRAY + "$1" + color);
	}
	private static String highlightURL(String message, ChatColor color)
	{
		Pattern pattern = Pattern.compile("(http://\\S+)");
	    Matcher matcher = pattern.matcher(message);
	    return matcher.replaceAll(ChatColor.AQUA + "$1" + color);
	}
}
