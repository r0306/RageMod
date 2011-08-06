package net.rageland.ragemod.commands;

import java.util.ArrayList;

import net.rageland.ragemod.Build;
import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Factions;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.Players;

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
		if( split.length < 2 || split.length > 3 )
		{
			plugin.text.parse(player, "Debug commands: <required> [optional]");
			if( true )
				plugin.text.parse(player, "   /debug colors   (displays all chat colors)");
			if( true )
				plugin.text.parse(player, "   /debug donation  (displays amount of donations)");
			if( true )
				plugin.text.parse(player, "   /debug sanctum <level> (attempts to build sanctum floor)");
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
		else
			plugin.text.parse(player, "Type /debug to see a list of available commands.");
	}
	
	// /debug colors
	public void colors(Player player) 
	{
		player.sendMessage(ChatColor.DARK_GRAY + "Dark Gray: Player (Tourist)");
		player.sendMessage(ChatColor.GRAY + "Gray: Player (Neutral)");
		player.sendMessage(ChatColor.WHITE + "White: Town (Neutral), Player (Neutral Member)");
		player.sendMessage(ChatColor.YELLOW + "Yellow: Player (Admin)");
		player.sendMessage(ChatColor.GOLD + "Gold: Player (Owner)");
		player.sendMessage(ChatColor.RED + "Red: Player (Red Faction)");
		player.sendMessage(ChatColor.DARK_RED + "Dark Red: Town (Red Faction), Player (Red Member)");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Light Purple: Battle messages");
		player.sendMessage(ChatColor.DARK_PURPLE + "Dark Purple: Important battle messages");
		player.sendMessage(ChatColor.BLUE + "Blue: Player (Blue Faction)");
		player.sendMessage(ChatColor.DARK_BLUE + "Dark Blue: Town Name (Blue Faction), Player (Blue Member)");
		player.sendMessage(ChatColor.AQUA + "Aqua: NPC quests/shops/etc. ");
		player.sendMessage(ChatColor.DARK_AQUA + "Dark Aqua: NPC speech");
		player.sendMessage(ChatColor.GREEN + "Green: Ragemod messages");
		player.sendMessage(ChatColor.DARK_GREEN + "Dark Green: Important messages, Player (Moderator)");
		
	}
	
	// /debug donation
	public void donation(Player player) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		int donation = plugin.database.playerQueries.getRecentDonations(playerData.id_Player);
		
		plugin.text.parse(player, "The database records you with a total donation of $" + donation + " in the last month.");
	}

	public void sanctum(Player player, String levelString) 
	{
		int level;
		World world = player.getWorld();
		PlayerData playerData = plugin.players.get(player.getName());
		
		try
		{
			level = Integer.parseInt(levelString);
		}
		catch( Exception ex )
		{
			plugin.text.parse(player, "Invalid level.");
			return;
		}
		
		if( level < 1 || level > 5 )
		{
			plugin.text.parse(player, "Invalid level.");
			return;
		}
		
		// Pinpoint the top-left corner
		int cornerX = (int)player.getLocation().getX() - 2; 
		int cornerY = (int)player.getLocation().getY() - 1;
		int cornerZ = (int)player.getLocation().getZ() - 10; 
		
		Build.sanctumFloor(plugin, world, cornerX, cornerY, cornerZ, level, playerData.id_Faction);
		
		
		
	}

}
