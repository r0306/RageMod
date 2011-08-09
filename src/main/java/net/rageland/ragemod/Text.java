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
	private Pattern urlPattern;
	private Pattern townPattern;
	private Pattern numberPattern;
	
	private ChatColor DEFAULT_COLOR = ChatColor.GREEN;
	private ChatColor COLOR_NO = ChatColor.DARK_RED;
	private ChatColor BROADCAST_COLOR = ChatColor.DARK_GREEN;
	
	public Text( RageMod plugin )
	{
		this.plugin = plugin;
		
		playerPattern = Pattern.compile("<p(.)>(\\w+)</p.>");
		townPattern = Pattern.compile("<t(.)>(.+)</t.>");
		 
		commandPattern = Pattern.compile("([\\s'])(/[a-zA-Z]+)");
		requiredPattern = Pattern.compile("(<.+>)");
		optionalPattern = Pattern.compile("(\\[.+\\])");
		parenthesesPattern = Pattern.compile("([(].+[)])");
		urlPattern = Pattern.compile("(http://\\S+)");
		numberPattern = Pattern.compile("(\\s)([\\d+,-\\.]+( " + plugin.config.CURRENCY_NAME + ")?( " + plugin.config.CURRENCY_MINOR + ")?)");
		 
	}
	
	// Handles default color 
	public void parse(Player player, String message)
	{
		parse(player, message, DEFAULT_COLOR);
	}
	public void parseNo(Player player, String message)
	{
		parse(player, message, COLOR_NO);
	}
	
	// Formats player messages with colors
	public void parse(Player player, String message, ChatColor color)
	{
		player.sendMessage(process(message, color));
	}
	
	// Perform the processing and parsing
	private String process(String message, ChatColor color)
	{
		message = color + message;
		
		// Process XML codes first 
		message = highlightPlayers(message, color);
		message = highlightTowns(message, color);
		
		// Parse for basic text
		message = highlightCommands(message, color);
		message = highlightRequired(message, color);
		message = highlightOptional(message, color);
		message = highlightParentheses(message, color);
		//message = highlightURL(message, color);				// not worth the CPU for the 2-3 URLs present in this plugin text <_<
		message = highlightNumbers(message, color);
		
		return message;
	}
	
	// Sends a message without parsing, for speed
	public void message(Player player, String message)
	{
		player.sendMessage(DEFAULT_COLOR + message);
	}
	public void message(Player player, String message, ChatColor color)
	{
		player.sendMessage(color + message);
	}
	// For messages with negative results (events cancelled, etc)
	public void messageNo(Player player, String message)
	{
		player.sendMessage(COLOR_NO + message);
	}
	
	// Send message to all players on server
	public void broadcast(String message)
	{
		broadcast(message, BROADCAST_COLOR);
	}
	public void broadcast(String message, ChatColor color)
	{
		message = process(message, color);
		
		for( Player onlinePlayer : plugin.getServer().getOnlinePlayers() )
		{
			onlinePlayer.sendMessage(message);
		}
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
	    return matcher.replaceAll("$1" + ChatColor.DARK_GREEN + "$2" + color);
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
	private String highlightURL(String message, ChatColor color)
	{
	    Matcher matcher = urlPattern.matcher(message);
	    return matcher.replaceAll(ChatColor.AQUA + "$1" + color);
	}


}
