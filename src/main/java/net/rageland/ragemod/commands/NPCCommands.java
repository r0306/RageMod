package net.rageland.ragemod.commands;

// TODO: Add removal commands

import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Location2D;
import net.rageland.ragemod.data.NPCData;
import net.rageland.ragemod.data.NPCInstance;
import net.rageland.ragemod.data.NPCLocation;
import net.rageland.ragemod.data.NPCTown;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.NPCInstance.NPCType;
import net.rageland.ragemod.data.Town;
import net.rageland.ragemod.quest.Quest;
import net.rageland.ragemod.quest.QuestImplementation;

public class NPCCommands {
	
	private RageMod plugin;
	
	public NPCCommands(RageMod plugin)
	{
		this.plugin = plugin;
	}
	
	public void onNPCCommand(Player player, PlayerData playerData, String[] split) 
	{
		if( split.length < 2 || split.length > 5 )
		{
			plugin.message.parse(player, "NPC commands: <required> [optional]");
			if( true )
				plugin.message.parse(player, "   /npc create <name>   (enters a new NPC into the database)");
			if( true )
				plugin.message.parse(player, "   /npc despawnall   (clears all NPCs on server)");
			if( true )
				plugin.message.parse(player, "   /npc list   (lists all active NPCs)");
			if( true )
				plugin.message.parse(player, "   /npc listloc   (lists all NPCLocations)");
			if( true )
				plugin.message.parse(player, "   /npc newloc   (creates a new NPCLocation at your point)");
			if( true )
				plugin.message.parse(player, "   /npc newtown <name> <lvl> <x,z,x,z>   (new NPCTown)");
			if( true )
				plugin.message.parse(player, "   /npc spawn <locID>   (spawns a random NPC at location)");
			if( true )
				plugin.message.parse(player, "   /npc spawnall   (forces server-wide NPC spawning)");
			if( true )
				plugin.message.parse(player, "   /npc tploc <locID>  (teleports to the NPCLocation)");
		}
		else if( split[1].equalsIgnoreCase("create") )
		{
			if( split.length == 3 )
				this.create(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /npc create <name>");  
		}
		else if( split[1].equalsIgnoreCase("despawnall") )
		{
			this.despawnall(player); 
		}
		else if( split[1].equalsIgnoreCase("listloc") )
		{
			this.listloc(player); 
		}
		else if( split[1].equalsIgnoreCase("list") )
		{
			this.list(player); 
		}
		else if( split[1].equalsIgnoreCase("newloc") )
		{
			this.newloc(player); 
		}
		else if( split[1].equalsIgnoreCase("newtown") )
		{
			if( split.length == 5 )
				this.newtown(player, split[2], split[3], split[4]); 
			else
    			plugin.message.parse(player, "Usage: /npc newtown <name> <lvl> <x,z,x,z>");  
		}
		else if( split[1].equalsIgnoreCase("spawn") )
		{
			if( split.length == 3 )
				this.spawn(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /npc spawn <locID>");  
		}
		else if( split[1].equalsIgnoreCase("spawnall") )
		{
			this.spawnall(player); 
		}
		else if( split[1].equalsIgnoreCase("tploc") )
		{
			if( split.length == 3 )
				this.tploc(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /npc tploc <locID>");  
		}

		else
			plugin.message.parse(player, "Type /npc to see a list of available commands.");
		
		
		
//		
//		
//		
//		if( isSpawnCommand(split) ) 
//		{
//			String npcType = split[2];
//			String npcName = split[3];
//			String npcId = split[4];
//			String questId;
//			
//			if( split.length > 5 )
//				questId = split[5];
//			else
//				questId = "0";
//			
//			
//			if(npcType.equalsIgnoreCase("queststartnpc"))
//			{
//				spawnQuestStartNPC(player, plugin.questManager.quests.get(questId), npcName, npcId);
//			} 
//			else if(npcType.equalsIgnoreCase("questrewardnpc"))
//			{
//				spawnRewardNPC(player, plugin.questManager.quests.get(questId), npcName, npcId);
//			}
//			else if(npcType.equalsIgnoreCase("speechnpc"))
//			{
//				spawnSpeechNPC(player, npcName, npcId);
//			}
//			else if(npcType.equalsIgnoreCase("questendnpc"))
//			{
//				spawnQuestEndNPC(player, plugin.questManager.quests.get(questId), npcName, npcId);
//			}
//			else if(npcType.equalsIgnoreCase("queststartendnpc"))
//			{
//				spawnQuestStartEndNPC(player, plugin.questManager.quests.get(questId), npcName, npcId);
//			}				
//		} 
//		else if( isDespawnCommand(split) )
//		{
//			String npcName = split[2];
//			plugin.npcManager.despawn(npcName);
//			// Will despawn all NPC's with npcName as name. Might need to add despawn by ID, which is unique.
//		}
//		else
//		{
//			player.sendMessage("Usage: /npc spawn <npctype> <npcname> <npcid> [questid]");
//		}
	}




//
//	private void spawnQuestStartNPC(Player player, QuestImplementation quest, String npcName, int npcId) 
//	{
//		Location l = player.getLocation();		
//		plugin.npcManager.getSpawner().questStartNPC(npcName, npcId, l, quest);
//		// Store data to database
//	}
//	
//	private void spawnRewardNPC(Player player, QuestImplementation quest, String npcName, int npcId)
//	{
//		Location l = player.getLocation();
//		plugin.npcManager.getSpawner().rewardNPC(npcName, npcId, l, quest);
//		// Store data to database
//	}
//	
//	private void spawnSpeechNPC(Player player, String npcName, int npcId)
//	{
//		Location l = player.getLocation();
//		plugin.npcManager.getSpawner().speechNPC(npcName, npcId, l);
//		// Store data to database
//	}
//	
//	private void spawnQuestEndNPC(Player player, QuestImplementation quest, String npcName, int npcId)
//	{
//		Location l = player.getLocation();
//		plugin.npcManager.getSpawner().questEndNPC(npcName, npcId, l, quest);
//		// Store data to database
//	}
//	
//	private void spawnQuestStartEndNPC(Player player, QuestImplementation quest, String npcName, int npcId)
//	{
//		Location l = player.getLocation();
//		plugin.npcManager.getSpawner().questStartEndNPC(npcName, npcId, l, quest);
//		// Store data to database
//	}
	






	/**
	 * To be a valid spawn command, format is:
	 * 
	 * npc spawn <npctype> <npcname> [questid]
	 * @param split
	 * @return
	 */
	private boolean isSpawnCommand(String[] split)
	{
		if(split.length > 4 && split.length < 7)
		{
			if(split[1].equalsIgnoreCase("spawn"))
			{
				if(split[2].equalsIgnoreCase("speechnpc") && split.length == 5)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * To be a valid despawn command format is:
	 * 
	 * npc despawn <npcname>
	 * @param split
	 * @return
	 */
	private boolean isDespawnCommand(String[] split)
	{
		if(split.length == 3 && split[1].equalsIgnoreCase("despawn"))
		{
			
		}
		return false;
	}
	
	/**
	 * Command format: /npc addspeechmessage <npcname> <message>
	 */
	private boolean isAddSpeechCommand(String[] split)
	{
		if(split.length == 4 && split[1].equalsIgnoreCase("addspeechmessage"))
			return true;
		else
			return false;
	}	
	
	/**
	 * Creates a new NPCLocation and adds it to the database
	 */
	private void newloc(Player player)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		
		try
		{
			NPCLocation npcLocation = new NPCLocation(player.getLocation(), plugin);
			int townID = 0;
			
			// See if the location is inside a town
			Town town = plugin.towns.getCurrentTown(npcLocation);
			if( town != null )
			{
				townID = town.getID();
				town.addNPCLocation(npcLocation); 
			}
			
			// Add the NPCLocation to the database
			int id_NPCLocation = plugin.database.npcQueries.createNPCLocation(player.getLocation(), townID, 0, playerData.id_Player);
			
			// Add the NPCLocation to memory
			npcLocation.setIDs(id_NPCLocation, townID, 0);
			plugin.npcManager.addLocation(npcLocation);
			
			plugin.message.send(player, "Successfully added NPCLocation #" + id_NPCLocation + ".");
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Error: " + ex.getMessage());
		}

	}
	
	/**
	 * Spawns a new NPC at the specified location
	 * @param player
	 * @param id_NPCLocation
	 */
	private void spawn(Player player, String id_NPCLocation) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		NPCLocation location;
		NPCData npc;
		NPCInstance instance;
		
		try
		{
			// Get the NPC location
			location = plugin.npcManager.activateLocation(Integer.parseInt(id_NPCLocation));
			if( location == null )
				throw new Exception(id_NPCLocation + " is already in use or is an invalid location ID.");
			
			// Activate a random NPC from the pool
			npc = plugin.npcManager.activateRandomFloatingNPC();
			if( npc == null )
			{
				plugin.npcManager.deactivateLocation(location);
				throw new Exception("There are no more NPCs in the pool to activate.");
			}
			
			// Register the NPC instance and spawn the NPC
			instance = plugin.database.npcQueries.createInstance(
					npc.id_NPC, location.getID(), 30, playerData.id_Player, NPCType.SPEECH);
			instance.setData(npc, location);
			instance.spawn();
					
			plugin.message.parse(player, "Successfully spawned " + instance.getCodedName() + " at location #" + location.getID() + ".");
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Error: " + ex.getMessage());
			return;
		}	
	}
	
	/**
	 * Creates a new NPC record in the database
	 * @param player
	 * @param name
	 */
	private void create(Player player, String name) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		
		try
		{
			if (name.length() > 14) 
			{
				String tmp = name.substring(0, 14);
				plugin.getServer().getLogger().log(Level.WARNING, "NPCs can't have names longer than 14 characters,");
				plugin.getServer().getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
				name = tmp;
			}
			
			// Add the NPC to memory
			NPCData npc = new NPCData();
			npc.id_NPCRace = 5;		// temp: 5 for humans
			npc.id_NPCTown = 0;		// 0 = no town
			npc.name = name;
			npc.isBilingual = false;
			plugin.npcManager.addNPC(npc);
			
			// Add the NPC to the database
			int id_NPC = plugin.database.npcQueries.createNPC(npc, playerData.id_Player);
			
			plugin.message.send(player, "Successfully added NPC #" + id_NPC + ".");
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Error: " + ex.getMessage());
		}
	}
	
	// Lists all NPC locations
	// TODO: add granularity
	private void listloc(Player player) 
	{
		plugin.message.send(player, "List of all non-town NPC locations:");
		
		for( NPCLocation location : plugin.npcManager.getAllLocations() )
		{
			if( location.getTown() == null )
				plugin.message.parse(player, " " + location.getID() + ": " + plugin.zones.getName(location.getZone()) + " (" + 
						location.getQuadrant().toString() + ") " + 
						(location.isActivated() ? ChatColor.GOLD + "ACTIVE" : ""));
		}
	}
	
	// Teleports the player to the specified location
	private void tploc(Player player, String id_NPCLocation) 
	{
		NPCLocation location;
		
		try
		{
			// Get the NPC location
			location = plugin.npcManager.getLocation(Integer.parseInt(id_NPCLocation));
			if( location == null )
				throw new Exception(id_NPCLocation + " is an invalid location ID.");
			
			plugin.message.send(player, "Teleporting...");
			player.teleport(location);
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Error: " + ex.getMessage());
			return;
		}	
	}
	
	// Lists all currently spawned NPCs
	private void list(Player player) 
	{
		ArrayList<NPCInstance> instances = plugin.npcManager.getAllInstances();
		
		if( instances.size() == 0 )
			plugin.message.send(player, "There are no currently active NPCs.");
		else
		{
			plugin.message.send(player, "List of all active NPCs:");
			
			for( NPCInstance instance : plugin.npcManager.getAllInstances() )
			{
				plugin.message.parse(player, " " + instance.getCodedName() + ": Loc. " + instance.getLocation().getID() + ", " + 
						plugin.zones.getName(instance.getLocation().getZone()) + " (" + 
						instance.getLocation().getQuadrant().toString() + ")");
			}
		}
	}
	
	// Creates a new town
	private void newtown(Player player, String name, String levelString, String coords) 
	{
		String[] split = coords.split(",");
		try
		{
			int level = Integer.parseInt(levelString);
			int id_NPCTown = plugin.database.npcQueries.createNPCTown(player, name, 
					Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]),
					level);
			
			NPCTown town = new NPCTown(plugin, id_NPCTown, name, player.getLocation().getWorld());
			town.id_NPCRace = 5;	// temp
    		town.townLevel = plugin.config.townLevels.get(level);
    		
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
	
	// Kicks off the server-wide NPC random generation and cleanup task
	private void spawnall(Player player) 
	{
		 plugin.tasks.spawnNPCs();
		 plugin.message.send(player, "Spawned " + plugin.npcManager.getAllInstances().size() + " NPCs.");
	}
	
	// Clears all NPCs on the server
	private void despawnall(Player player) 
	{
		plugin.npcManager.despawnAll(true);
		plugin.message.send(player, "Cleared all NPCs.");
	}
	

}
