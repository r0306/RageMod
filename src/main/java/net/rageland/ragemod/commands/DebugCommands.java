package net.rageland.ragemod.commands;

import java.util.ArrayList;

import net.rageland.ragemod.Build;
import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Factions;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.Players;
import net.rageland.ragemod.language.Language;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class DebugCommands 
{
	
	private RageMod plugin;
	
	public DebugCommands(RageMod plugin) 
	{
		this.plugin = plugin;
	}
	
	public void onDebugCommand(Player player, PlayerData playerData, String[] split) 
	{
		if( split.length < 2 )
		{
			plugin.text.parse(player, "Debug commands: <required> [optional]");
			if( true )
				plugin.text.parse(player, "   /debug colors   (displays all chat colors)");
			if( true )
				plugin.text.parse(player, "   /debug donation  (displays amount of donations)");
			if( true )
				plugin.text.parse(player, "   /debug sanctum <level> (attempts to build sanctum floor)");
			if( true )
				plugin.text.parse(player, "   /debug translate [text] (translates the entered text)");
			if( true )
				plugin.text.parse(player, "   /debug transcast <text> (translates/broadcasts the text)");
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
    			plugin.text.parse(player, "Usage: /debug sanctum <level>"); 
		}
		else if( split[1].equalsIgnoreCase("translate") )
		{
			this.translate(player, split); 
		}
		else if( split[1].equalsIgnoreCase("transcast") )
		{
			if( split.length > 2 )
				this.transcast(player, split); 
			else
    			plugin.text.parse(player, "Usage: /debug transcast <text>"); 
		}
		else
			plugin.text.parse(player, "Type /debug to see a list of available commands.");
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
		
		plugin.text.message(player, "The database records you with a total donation of $" + donation + " in the last month.");
	}

	// /debug sanctum <level>
	private void sanctum(Player player, String levelString) 
	{
		plugin.text.messageNo(player, "This command has been disabled.");
		return;
		
//		int level;
//		World world = player.getWorld();
//		PlayerData playerData = plugin.players.get(player.getName());
//		
//		try
//		{
//			level = Integer.parseInt(levelString);
//		}
//		catch( Exception ex )
//		{
//			plugin.text.messageNo(player, "Invalid level.");
//			return;
//		}
//		
//		if( level < 1 || level > 5 )
//		{
//			plugin.text.messageNo(player, "Invalid level.");
//			return;
//		}
//		
//		// Pinpoint the top-left corner
//		int cornerX = (int)player.getLocation().getX() - 2; 
//		int cornerY = (int)player.getLocation().getY() - 1;
//		int cornerZ = (int)player.getLocation().getZ() - 10; 
//		
//		Build.sanctumFloor(plugin, world, cornerX, cornerY, cornerZ, level, playerData.id_Faction);
		
	}
	
	// Translates the text typed by the player
	private void translate(Player player, String[] split) 
	{
		String message = new String();
		ArrayList<String> results;
		Language language = new Language();
		
		if( split.length == 2 )
		{
			plugin.text.parse(player, "Usage: /debug translate [text]");
			plugin.text.parse(player, "Translating sample message...");
			message = "Greetings, fellow traveler.  Would you like a cup of ale?";
		}
		else
		{
			// Pull the commands out of the string
			for( int i = 2; i < split.length; i++ )
				message += split[i] + " ";
		}
		
		results = language.translate(message);
		plugin.text.parse(player, "[100%] " + message);
		
		try
		{
			plugin.text.parse(player, "[75%] " + results.get(0));
			plugin.text.parse(player, "[50%] " + results.get(1));
			plugin.text.parse(player, "[25%] " + results.get(2));
			plugin.text.parse(player, "[0%] " + results.get(3));
		}
		catch( Exception ex )
		{
			plugin.text.message(player, "Error: " + ex.getMessage());
		}
	}
	
	// Translates and broadcasts the text typed by the player
	private void transcast(Player player, String[] split) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		String message = new String();
		ArrayList<String> results;
		Language language = new Language();
		
		// Pull the commands out of the string
		for( int i = 2; i < split.length; i++ )
			message += split[i] + " ";
		
		results = language.translate(message);
		plugin.text.broadcast(playerData.getCodedName() + " has initiated a translation test:");
		plugin.text.broadcast("[100%] " + message, ChatColor.DARK_AQUA);
		
		try
		{
			plugin.text.broadcast("[0%] " + results.get(3), ChatColor.DARK_AQUA);
		}
		catch( Exception ex )
		{
			plugin.text.message(player, "Error: " + ex.getMessage());
		}
	}

}
