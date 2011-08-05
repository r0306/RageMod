package net.rageland.ragemod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

// Contains methods for sending and formatting messages
public class Text 
{
	private RageMod plugin;
	
	private Pattern playerPattern;
	private Pattern commandPattern;
	private Pattern requiredPattern;
	private Pattern optionalPattern;
	private Pattern parenthesesPattern;
	private Pattern silverPattern;
	private Pattern copperPattern;
	private Pattern urlPattern;
	private Pattern townPattern;
	private Pattern numberPattern;
	
	private ChatColor DEFAULT_COLOR = ChatColor.GREEN;
	
	
	public Text( RageMod plugin )
	{
		this.plugin = plugin;
		
		 playerPattern = Pattern.compile("<p(.)>(\\w+)</p.>");
		 townPattern = Pattern.compile("<t(.)>(.+)</t.>");
		 
		 commandPattern = Pattern.compile("( /[a-zA-Z]+)");
		 requiredPattern = Pattern.compile("(<.+>)");
		 optionalPattern = Pattern.compile("(\\[.+\\])");
		 parenthesesPattern = Pattern.compile("([(].+[)])");
		 silverPattern = Pattern.compile("([\\d,]+ Silver)");
		 copperPattern = Pattern.compile("([\\d,]+ Copper)");
		 urlPattern = Pattern.compile("(http://\\S+)");
		 numberPattern = Pattern.compile("(\\s)([\\d+,-\\.]+)");
		 
	}
	
	// Handles default color 
	public void message(Player player, String message)
	{
		message(player, message, DEFAULT_COLOR);
	}
	
	// Formats player messages with colors
	public void message(Player player, String message, ChatColor color)
	{
		message = color + message;
		
		message = highlightPlayers(message, color);
		message = highlightTowns(message, color);
		
		message = highlightCommands(message, color);
		message = highlightRequired(message, color);
		message = highlightOptional(message, color);
		message = highlightParentheses(message, color);
		message = highlightSilver(message, color);
		message = highlightCopper(message, color);
		message = highlightURL(message, color);
		message = highlightNumbers(message, color);
		
		player.sendMessage(message);
	}
	
	// Sends a message without parsing, for speed
	public void messageBasic(Player player, String message)
	{
		player.sendMessage(DEFAULT_COLOR + message);
	}
	public void messageBasic(Player player, String message, ChatColor color)
	{
		player.sendMessage(color + message);
	}
	
	
	private String highlightPlayers(String message, ChatColor color)
	{
		Matcher matcher = playerPattern.matcher(message);
	    ChatColor playerColor = ChatColor.GRAY;		// bad code
	    
	    
	    StringBuffer sb = new StringBuffer();
        while( matcher.find() )
        {
        	if( matcher.group(1).equals("o") )		// owner
	    		playerColor = ChatColor.GOLD;
	    	else if( matcher.group(1).equals("a") )	// admin
	    		playerColor = ChatColor.YELLOW;
	    	else if( matcher.group(1).equals("m") )	// moderator
	    		playerColor = ChatColor.DARK_GREEN;
	    	else if( matcher.group(1).equals("e") )	// merchant
	    		playerColor = ChatColor.WHITE;
	    	else if( matcher.group(1).equals("b") )	// blue
	    		playerColor = ChatColor.BLUE;
	    	else if( matcher.group(1).equals("r") )	// red
	    		playerColor = ChatColor.RED;
	    	else if( matcher.group(1).equals("c") )	// citizen
	    		playerColor = ChatColor.GRAY;
	    	else if( matcher.group(1).equals("t") )	// tourist
	    		playerColor = ChatColor.DARK_GRAY;
        	
        	matcher.appendReplacement(sb, playerColor + "$2" + color);
        }
        matcher.appendTail(sb);
        
        return sb.toString();

	}
	private String highlightTowns(String message, ChatColor color)
	{
	    Matcher matcher = townPattern.matcher(message);
	    ChatColor townColor = ChatColor.WHITE;		// neutral/bad code
	    if( matcher.find() )
	    {
	    	if( matcher.group(1).equals("b") )		// blue
	    		townColor = ChatColor.BLUE;
	    	else if( matcher.group(1).equals("r") )	// red
	    		townColor = ChatColor.RED;
	    }
	   
	    return matcher.replaceAll(townColor + "$2" + color);
	}
	
	private String highlightNumbers(String message, ChatColor color)
	{
	    Matcher matcher = numberPattern.matcher(message);
	    return matcher.replaceAll("$1" + ChatColor.WHITE + "$2" + color);
	}
	private String highlightCommands(String message, ChatColor color)
	{
	    Matcher matcher = commandPattern.matcher(message);
	    return matcher.replaceAll(ChatColor.DARK_GREEN + "$1" + color);
	}
	private String highlightRequired(String message, ChatColor color)
	{
	    Matcher matcher = requiredPattern.matcher(message);
	    return matcher.replaceAll(ChatColor.GOLD + "$1" + color);
	}
	private String highlightOptional(String message, ChatColor color)
	{
	    Matcher matcher = optionalPattern.matcher(message);
	    return matcher.replaceAll(ChatColor.YELLOW + "$1" + color);
	}
	private String highlightParentheses(String message, ChatColor color)
	{
	    Matcher matcher = parenthesesPattern.matcher(message);
	    return matcher.replaceAll(ChatColor.GRAY + "$1" + color);
	}
	private String highlightSilver(String message, ChatColor color)
	{
	    Matcher matcher = silverPattern.matcher(message);
	    return matcher.replaceAll(ChatColor.GRAY + "$1" + color);
	}
	private String highlightCopper(String message, ChatColor color)
	{
	    Matcher matcher = copperPattern.matcher(message);
	    return matcher.replaceAll(ChatColor.DARK_GRAY + "$1" + color);
	}
	private String highlightURL(String message, ChatColor color)
	{
	    Matcher matcher = urlPattern.matcher(message);
	    return matcher.replaceAll(ChatColor.AQUA + "$1" + color);
	}


}
