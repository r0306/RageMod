package net.rageland.ragemod.commands;

import java.util.ArrayList;

import net.rageland.ragemod.Build;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.utilities.Util;
import net.rageland.ragemod.world.Town;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class DebugCommands 
{
	
	private RageMod plugin;
	int level;
	
	public DebugCommands(RageMod plugin) 
	{
		this.plugin = plugin;
	}
	
	public void onRmdebugCommand(Player player, PlayerData playerData, String[] split) 
	{
		if( split.length < 2 )
		{
			plugin.message.parse(player, "Debug commands: <required> [optional]");
			if( true )
				plugin.message.parse(player, "   /rmdebug colors   (displays all chat colors)");
			if( true )
				plugin.message.parse(player, "   /rmdebug donation  (displays amount of donations)");
			if( false )
				plugin.message.parse(player, "   /rmdebug sanctum <level> (attempts to build sanctum floor)");
			if( true )
				plugin.message.parse(player, "   /rmdebug setlang <1-4> <level> (sets your language skill)");
			if( true )
				plugin.message.parse(player, "   /rmdebug tptown <town_name> (teleports to town)");
			if( true )
				plugin.message.parse(player, "   /rmdebug translate [text] (translates the entered text)");
			if( true )
				plugin.message.parse(player, "   /rmdebug transcast <#> <text> (translates/broadcasts text)");
		}
		else if( split[1].equalsIgnoreCase("colors") )
		{
			this.colors(player);
		}
		else if( split[1].equalsIgnoreCase("donation") )
		{
			this.donation(player);
		}
		else if( split[1].equalsIgnoreCase("sanctum") )
		{
			if( split.length == 3 )
				this.sanctum(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /debug sanctum <level>"); 
		}
		else if( split[1].equalsIgnoreCase("setlang") )
		{
			if( split.length == 4 )
				this.setlang(player, split[2], split[3]); 
			else
			{
    			plugin.message.parse(player, "Usage: /rmdebug setlang <1-4> <level>"); 
				plugin.message.send(player, "   Languages: 1-Creeptongue, 2-Gronuk, 3-Benali, 4-Avialese");
			}
		}
		else if( split[1].equalsIgnoreCase("tptown") )
		{
			if( split.length == 3 )
				this.towntp(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /rmdebug tptown <town_name>"); 
		}
		else if( split[1].equalsIgnoreCase("translate") )
		{
				this.translate(player, split); 
		}
		else if( split[1].equalsIgnoreCase("transcast") )
		{
			if( split.length > 3 )
				this.transcast(player, split); 
			else
    			plugin.message.parse(player, "Usage: /rmdebug transcast 1-4 <text>"); 
		}
		else
			plugin.message.parse(player, "Type /rmdebug to see a list of available commands.");
	}

	// /debug colors
	private void colors(Player player) 
	{
		player.sendMessage(ChatColor.DARK_GRAY + "Dark Gray: Player (Tourist)");
		player.sendMessage(ChatColor.GRAY + "Gray: Player (Neutral)");
		player.sendMessage(ChatColor.WHITE + "White: Town (Neutral), Player (Merchant)");
		player.sendMessage(ChatColor.YELLOW + "Yellow: Player (Admin)");
		player.sendMessage(ChatColor.GOLD + "Gold: Treasury messages, Player (Owner)");
		player.sendMessage(ChatColor.RED + "Red: Town (Red), Player (Red)");
		player.sendMessage(ChatColor.DARK_RED + "Dark Red: Negative messages");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Light Purple: Battle messages");
		player.sendMessage(ChatColor.DARK_PURPLE + "Dark Purple: Important battle messages");
		player.sendMessage(ChatColor.BLUE + "Blue: Player (Blue)");
		player.sendMessage(ChatColor.DARK_BLUE + "Dark Blue: Unused (illegible)  :(");
		player.sendMessage(ChatColor.AQUA + "Aqua: NPC names, NPC towns");
		player.sendMessage(ChatColor.DARK_AQUA + "Dark Aqua: NPC speech, Quest info");
		player.sendMessage(ChatColor.GREEN + "Green: Ragemod messages");
		player.sendMessage(ChatColor.DARK_GREEN + "Dark Green: Important messages, Broadcasts, Player (Moderator)");
		
	}
	
	// /debug donation
	private void donation(Player player) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		int donation = plugin.database.playerQueries.getRecentDonations(playerData.id_Player);
		
		plugin.message.send(player, "The database records you with a total donation of $" + donation + " in the last month.");
	}

	// /debug sanctum <level>
	private void sanctum(Player player, String levelString) 
	{
		//plugin.message.sendNo(player, "This command has been disabled.");
		//return;

		World world = player.getWorld();
		PlayerData playerData = plugin.players.get(player.getName());
		
		try
		{
			level = Integer.parseInt(levelString);
		}
		catch( Exception ex )
		{
			plugin.text.messageNo(player, "Invalid level.");
			return;
		}
		
		if( level < 1 || level > 5 )
		{
			plugin.text.messageNo(player, "Invalid level.");
			return;
		}
		
		// Pinpoint the top-left corner
		int cornerX = (int)player.getLocation().getX() - 2; 
		int cornerY = (int)player.getLocation().getY() - 1;
		int cornerZ = (int)player.getLocation().getZ() - 10; 
		
		Build.sanctumFloor(plugin, world, cornerX, cornerY, cornerZ, level, playerData.id_Faction);	
	}
	
	// Sets the language skill
	@SuppressWarnings("static-access")
	private void setlang(Player player, String langString, String levelString) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		int id_Language;
		int level;
		
		// Parse the language string
		try
		{
			id_Language = Integer.parseInt(langString);
			if( id_Language < 1 || id_Language > 4 )
				throw new Exception();
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Invalid language (1-4).");
			return;
		}
		// Parse the level string
		try
		{
			level = Integer.parseInt(levelString);
			if( level < 0 || level > 100 )
				throw new Exception();
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Invalid skill level (0-100).");
			return;
		}
		
		// Set the language skill
		playerData.setLanguageSkill(id_Language, level);
		playerData.update();
		
		plugin.message.send(player, "Your " + plugin.message.LANGUAGE_NAME_COLOR + plugin.config.NPC_LANGUAGE_NAMES.get(id_Language) + 
				plugin.message.DEFAULT_COLOR + " skill is now " + ChatColor.WHITE + level + ".");
		
	}
	
	// Teleports to selected town
	private void towntp(Player player, String townName)
	{
		Town town = plugin.towns.get(townName);
		
		// Check to see if specified town exists
		if( town == null )
		{
			plugin.message.sendNo(player, "The town '" + townName + "' does not exist.");
			return;
		}
		
		// Teleport to the town
		plugin.message.send(player, "Teleporting...");
		player.teleport(Util.findTeleportLocation(town.centerPoint));
	}
	
	// Translates the text typed by the player
	private void translate(Player player, String[] split) 
	{
		String message = new String();
		ArrayList<String> results;
		
		if( split.length == 2 )
		{
			plugin.message.parse(player, "Usage: /debug translate [text]");
			plugin.message.parse(player, "Translating sample message...");
			message = "Greetings, fellow traveler.  Would you like a cup of ale?";
		}
		else
		{
			// Pull the commands out of the string
			for( int i = 2; i < split.length; i++ )
				message += split[i] + " ";
		}
		
		try
		{
			plugin.message.parse(player,  "[En] " + message);
			
			// Translate into all 4 languages
			for( int i = 1; i <= 4; i++ )
			{
				results = plugin.languages.translate(message, i);
				plugin.message.parse(player,  "[" + plugin.languages.getAbbreviation(i) + "] " + results.get(3), ChatColor.DARK_AQUA);
			}
		}
		catch( Exception ex )
		{
			plugin.message.send(player, "Error: " + ex.getMessage());
		}
	}
	
	// Translates and broadcasts the text typed by the player
	private void transcast(Player player, String[] split) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		String message = new String();
		ArrayList<String> results;
		int id_Language;
		
		try
		{
			id_Language = Integer.parseInt(split[2]);
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Invalid language (1-4).");
			return;
		}
		if( id_Language < 1 || id_Language > 4 )
		{
			plugin.message.sendNo(player, "Invalid language (1-4).");
			return;
		}
		
		// Pull the commands out of the string
		for( int i = 3; i < split.length; i++ )
			message += split[i] + " ";
		
		results = plugin.languages.translate(message, id_Language);
		plugin.message.broadcast(playerData.getCodedName() + " has initiated a translation test:");
		plugin.message.broadcast("[En] " + message, ChatColor.GREEN);
		
		try
		{
			plugin.message.broadcast("[" + plugin.languages.getAbbreviation(id_Language) + "] " + results.get(3), ChatColor.DARK_AQUA);
		}
		catch( Exception ex )
		{
			plugin.message.send(player, "Error: " + ex.getMessage());
		}
	}

}
