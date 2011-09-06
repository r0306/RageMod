package net.rageland.ragemod.commands;

// TODO: Add removal commands

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;

import com.iConomy.iConomy;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Location2D;
import net.rageland.ragemod.data.Lot;
import net.rageland.ragemod.data.NPCData;
import net.rageland.ragemod.data.NPCInstance;
import net.rageland.ragemod.data.NPCLocation;
import net.rageland.ragemod.data.NPCTown;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.data.NPCInstance.NPCType;
import net.rageland.ragemod.data.Town;
import net.rageland.ragemod.quest.Quest;
import net.rageland.ragemod.quest.QuestImplementation;

public class NPCTownCommands {
	
	private RageMod plugin;
	
	public NPCTownCommands(RageMod plugin)
	{
		this.plugin = plugin;
	}
	
	public void onNPCTownCommand(Player player, PlayerData playerData, String[] split) 
	{
		if( split.length < 2 || split.length > 5 )
		{
			plugin.message.parse(player, "NPCTown commands: <required> [optional]");
			if( playerData.isSteward )
				plugin.message.parse(player, "   /npctown allow <player> (allow player to build in your town)");
			if( RageMod.permissionHandler.has(player, "ragemod.npctown") )
				plugin.message.parse(player, "   /npctown create <name> <raceID> <x,z,x,z>   (new NPCTown)");
			if( playerData.isSteward )
				plugin.message.parse(player, "   /npctown disallow <player/all> (removes permissions)");
			if( true )
				plugin.message.parse(player, "   /npctown list   (lists all NPCTowns)");
			if( true )
				plugin.message.parse(player, "   /npctown info <town_name>   (gives info on NPCTown)");
			if( playerData.isSteward )
				plugin.message.parse(player, "   /npctown resident <name> [sex]  (creates a new NPC)");
			if( RageMod.permissionHandler.has(player, "ragemod.npctown") )
				plugin.message.parse(player, "   /npctown setsteward <town> <player>   (sets steward)");
			
		}
		else if( split[1].equalsIgnoreCase("allow") )
		{
			if( split.length == 3 )
				this.allow(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /npctown allow <player_name>"); 
		}
		else if( split[1].equalsIgnoreCase("create") )
		{
			if( split.length == 5 )
				this.create(player, split[2], split[3], split[4]); 
			else
    			plugin.message.parse(player, "Usage: /npctown create <name> <lvl> <x,z,x,z>"); 
		}
		else if( split[1].equalsIgnoreCase("disallow") )
		{
			if( split.length == 3 )
				this.disallow(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /npctown disallow <player_name/all>"); 
		}
		else if( split[1].equalsIgnoreCase("info") )
		{
			if( split.length == 2 && !playerData.npcTownName.equals("") )
				this.info(player, playerData.npcTownName);
			else if( split.length == 3 )
				this.info(player, split[2]);
    		else
    			plugin.message.parse(player, "Usage: /npctown info <town_name>");
		}
		else if( split[1].equalsIgnoreCase("list") )
		{
			this.list(player, "");
    	}
		else if( split[1].equalsIgnoreCase("resident") )
		{
			if( split.length == 3 ) 
				this.resident(player, split[2], "M"); 
			else if( split.length == 4 )
				this.resident(player, split[2], split[3]); 
			else
			{
				plugin.message.parse(player, "Usage: /npctown resident <name> [gender]");
			}
		}
		else if( split[1].equalsIgnoreCase("setsteward") )
		{
			if( split.length == 4 )
    			this.setsteward(player, split[2], split[3]); 
    		else
    			plugin.message.parse(player, "Usage: /npctown setsteward <town_name> <player_name>");
		}

		else
			plugin.message.parse(player, "Type /npctown to see a list of available commands.");
		
	}


	// Creates a new NPC to live in the town
	private void resident(Player player, String name, String gender) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		
		// Make sure the player is a steward
		if( !playerData.isSteward || playerData.npcTownName.equals("") )
		{
			plugin.message.sendNo(player, "Only NPCTown Stewards can use this command.");
			return;
		}
		
		NPCTown town = (NPCTown)plugin.towns.get(playerData.npcTownName);
		NPCCommands.newNPC(plugin, player, name, town.id_NPCRace, gender, town.getID());
		
	}

	// Allows a player to build in the NPCTown
	private void allow(Player player, String targetPlayerName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		PlayerData targetPlayerData = plugin.players.get(targetPlayerName);

		// Make sure the player is a steward
		if( !playerData.isSteward )
		{
			plugin.message.sendNo(player, "Only NPCTown Stewards can use this command.");
			return;
		}
		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			plugin.message.sendNo(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}
		
		NPCTown town = (NPCTown)plugin.towns.get(playerData.npcTownName);
		
		// Check to see if the target player already has permission
		if( town.buildPermissions.contains(targetPlayerData.name) )
		{
			plugin.message.parseNo(player, targetPlayerData.getCodedName() + " already has permission to build in " + town.getCodedName() + ".");
			return;
		}
		
		// All checks have succeeded - update the DB
		plugin.database.npcTownQueries.allow(town.getID(), targetPlayerData.id_Player);
		
		// Update the town data
		town.buildPermissions.add(targetPlayerData.name);
		
		// Notify both players
		plugin.message.parse(player, targetPlayerData.getCodedName() + " is now allowed to build in " + town.getCodedName() + ".");
		Player targetPlayer = plugin.getServer().getPlayer(targetPlayerData.name);
		if( targetPlayer != null && targetPlayer.isOnline() )
			plugin.message.parse(targetPlayer, playerData.getCodedName() + " has given you permission to build in " + town.getCodedName() + ".");
	}
	
	// Removes building permissions
	private void disallow(Player player, String targetPlayerName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		
		// Make sure the player is a steward
		if( !playerData.isSteward )
		{
			plugin.message.sendNo(player, "Only NPCTown Stewards can use this command.");
			return;
		}
		
		NPCTown town = (NPCTown)plugin.towns.get(playerData.npcTownName);

		if( targetPlayerName.equalsIgnoreCase("all") )
		{
			if( town.buildPermissions.size() == 0 )
			{
				plugin.message.sendNo(player, "You have no permissions to disallow.");
				return;
			}
			
			// All checks have succeeded - update the database
			plugin.database.npcTownQueries.disallow(town.getID(), 0);
			
			// Update the town
			town.buildPermissions.clear();
			
			plugin.message.send(player, "All of your NPCTown build permissions have been cleared.");
		}
		else
		{
			PlayerData targetPlayerData = plugin.players.get(targetPlayerName);
			
			// Check to see if target player exists
			if( targetPlayerData == null )
			{
				plugin.message.sendNo(player, "Player " + targetPlayerName + " does not exist.");
				return;
			}
			// Check to see if the target player already has permission
			if( !town.buildPermissions.contains(targetPlayerData.name) )
			{
				plugin.message.parseNo(player, targetPlayerData.getCodedName() + " does not have permission to build in your NPCTown.");
				return;
			}
			
			// All checks have succeeded - update the database
			plugin.database.npcTownQueries.disallow(town.getID(), targetPlayerData.id_Player);
			
			// Update the town
			town.buildPermissions.remove(targetPlayerData.name);
			
			plugin.message.parse(player, targetPlayerData.getCodedName() + " is no longer allowed to build in your NPCTown.");
		}
	}

	// Sets the steward for an NPCTown
	private void setsteward(Player player, String townName, String targetPlayerName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		PlayerData targetPlayerData = plugin.players.get(targetPlayerName);
		Town town = plugin.towns.get(townName);
		
		// Make sure the player has permission to perform this command
		if( !RageMod.permissionHandler.has(player, "ragemod.npctown") )
		{
			plugin.message.sendNo(player, "You do not have permission to perform that command.");
			return;
		}
		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			plugin.message.sendNo(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}
		// town will be null if name is invalid
		if( town == null || !(town instanceof NPCTown) )
		{	
			plugin.message.parseNo(player, townName + " is not a valid NPCTown.");
			return;
		}
		
		NPCTown npcTown = (NPCTown) town;
		
		// Clear the existing steward, if any
		if( !npcTown.steward.equals("") )
		{
			PlayerData stewardData = plugin.players.get(npcTown.steward);
			stewardData.isSteward = false;
			stewardData.npcTownName = "";
			stewardData.update();
		}
		
		// Update the NPCTown
		npcTown.steward = targetPlayerData.name;
		npcTown.update();
		
		// Update the player
		targetPlayerData.isSteward = true;
		targetPlayerData.npcTownName = town.getName();
		targetPlayerData.update();
		
		// Notify both players
		plugin.message.parse(player, targetPlayerData.getCodedName() + " is now the Steward of " + town.getCodedName() + ".");
		Player targetPlayer = plugin.getServer().getPlayer(targetPlayerData.name);
		if( targetPlayer != null && targetPlayer.isOnline() )
			plugin.message.parse(targetPlayer, playerData.getCodedName() + " has assigned you as the Steward of " + town.getCodedName() + ".");
		
	}

	// Creates a new town
	private void create(Player player, String name, String raceString, String coords) 
	{
		String[] split = coords.split(",");
		try
		{
			int id_Race = Integer.parseInt(raceString);
			if( id_Race < 1 || id_Race > 5 )
				throw new Exception("Invalid Race ID (1-5)");
			
			int id_NPCTown = plugin.database.npcTownQueries.create(player, name, 
					Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]),
					1, id_Race);
			
			NPCTown town = new NPCTown(plugin, id_NPCTown, name, player.getLocation().getWorld(), "");
			town.id_NPCRace = id_Race;	
    		town.townLevel = plugin.config.townLevels.get(1);
    		
			town.createRegion(coords);
			plugin.towns.add(town);
			
			plugin.message.send(player, "Successfully created NPCTown " + name + ".");
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Error: " + ex.getMessage());
			return;
		}	
	}
	
	// Gives info on the specified NPCTown
	public void info(Player player, String townName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		
		Town town = plugin.towns.get(townName);
		
		// Check to see if specified town exists
		if( town == null )
		{
			plugin.message.sendNo(player, "The town '" + townName + "' does not exist.");
			return;
		}
		
		if( town instanceof PlayerTown )
		{
			plugin.message.parseNo(player, town.getCodedName() + " is a Player Town (use /town info).");
		}
		else if( town instanceof NPCTown )
		{
			NPCTown npcTown = (NPCTown)town;
			
			plugin.message.parse(player, "Info for " + npcTown.getCodedName() + ":");
			plugin.message.parse(player, "   Level: " + npcTown.townLevel.name + " (" + npcTown.townLevel.level + ")");
			if( !npcTown.steward.equals("") )
				plugin.message.parse(player, "   Steward: " + plugin.players.get(npcTown.steward).getCodedName());
			
			String builders = "";
			for( String builder : npcTown.buildPermissions )
				builders += ", " + plugin.players.get(builder).getCodedName();
			if( !builders.equals("") )
				plugin.message.parse(player, "   Builders: " + builders.substring(2));
				
			if( RageMod.permissionHandler.has(player, "ragemod.npctown") || playerData.npcTownName.equalsIgnoreCase(town.getName()) )
			{
				for( NPCLocation location : npcTown.getNPCLocations() )
				{
					plugin.message.parse(player, "   Loc. #" + location.getID() + 
							" (" + (int)location.getX() + ", " + (int)location.getY() + ", " + (int)location.getZ() + ")" +
							" " + (location.isActivated() ? location.getInstance().getCodedName() : ""));
				}	
			}
		}
	}
	
	// Lists all NPCTowns
	public void list(Player player, String factionName) 
	{
		ArrayList<NPCTown> npcTowns = plugin.towns.getAllNPCTowns();
		
		// TODO: Implement page # functionality 
		
		plugin.message.send(player, "List of all NPC Towns:");
		for( NPCTown town : npcTowns )
		{
			plugin.message.parse(player, "   " + town.getCodedName() + " " + ChatColor.YELLOW + town.getQuadrant().toString() + 
					 " (" + town.townLevel.name + ") " +
					 town.getNPCLocations().size() + ChatColor.WHITE + " locations");
		}
		return;
	
	}


}
