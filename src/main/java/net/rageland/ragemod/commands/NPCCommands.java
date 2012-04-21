package net.rageland.ragemod.commands;

// TODO: Add removal commands

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.npc.NPCData;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.npc.NPCInstance;
import net.rageland.ragemod.npc.NPCLocation;
import net.rageland.ragemod.npc.NPCInstance.NPCType;
import net.rageland.ragemod.world.Town;

@SuppressWarnings("unused")
public class NPCCommands {
	
	private RageMod plugin;
	private Random random;
	
	public NPCCommands(RageMod plugin)
	{
		this.plugin = plugin;
		this.random = new Random();
	}
	
	public void onNPCCommand(Player player, PlayerData playerData, String[] split) 
	{
		if( split.length < 2 || split.length > 5 )
		{
			plugin.message.parse(player, "NPC commands: <required> [optional]");
			if( true )
				plugin.message.parse(player, "   /npc create <name> [raceID] [sex]  (creates a new NPC)");
			if( true )
				plugin.message.parse(player, "   /npc despawnall   (clears all NPCs on server)");
			if( true )
				plugin.message.parse(player, "   /npc list   (lists all active NPCs)");
			if( true )
				plugin.message.parse(player, "   /npc listloc   (lists all NPCLocations)");
			if( true )
				plugin.message.parse(player, "   /npc newloc   (creates a new NPCLocation at your point)");
			if( true )
				plugin.message.parse(player, "   /npc spawn <locID> [npcID]  (spawns an NPC at location)");
			if( true )
				plugin.message.parse(player, "   /npc spawnq <locID> <q.type> [q.id]  (spawns quest NPC)");
			if( true )
				plugin.message.parse(player, "   /npc spawnall   (forces server-wide NPC spawning)");
			if( true )
				plugin.message.parse(player, "   /npc tploc <locID>  (teleports to the NPCLocation)");
		}
		else if( split[1].equalsIgnoreCase("create") )
		{
			if( split.length == 3 ) 
				this.create(player, split[2], "5", "M"); 
			else if( split.length == 4 )
				this.create(player, split[2], split[3], "M"); 
			else if(split.length == 5) 
				this.create(player, split[2], split[3], split[4]); 
			else
			{
				plugin.message.parse(player, "Usage: /npc create <name> [raceID] [gender]");
				plugin.message.send(player, "   Races: 1-Creep, 2-Pigman, 3-Benali, 4-Avian, 5-Human");
			}
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
		else if( split[1].equalsIgnoreCase("spawn") )
		{
			if( split.length == 3 )
				this.spawn(player, split[2], "random"); 
			else if(split.length == 4) 
				this.spawn(player, split[2], split[3]); 
			else
    			plugin.message.parse(player, "Usage: /npc spawn <locID> [npcID]");  
		}
		else if( split[1].equalsIgnoreCase("spawnq") )
		{
			if(split.length == 5) 
			{
				String name = split[2];
				String questNpcType = split[3];
				String questId = split[4];
				createQuestNPC(player, name, questNpcType, questId);
			}
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
		
	}

	private void createQuestNPC(Player player, String name, String questNpcType, String questId)
	{
		
		if (!(RageMod.perms.has(player, "ragemod.npc"))) {
			plugin.message.parse(player, plugin.noPerms);
			return;
		}
		
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
			/*
			npc.isQuestNPC = true;
			npc.quest = plugin.questManager.quests.get(questId);
			
			if(questNpcType.equals("0"))
				npc.questNPCType = 0;
			else if(questNpcType.equals("1"))
				npc.questNPCType = 1;
			else
				npc.questNPCType = 2;
				*/
			
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
			return true;
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
	private void spawn(Player player, String id_NPCLocation, String npcString) 
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
			
			System.out.println("Activated NPCLocation #" + location.getID());
			
			// Activate a random NPC from the pool
			if( npcString.equalsIgnoreCase("random") )
				npc = plugin.npcManager.activateRandomFloatingNPC();
			else
				npc = plugin.npcManager.activateNPC(Integer.parseInt(npcString));
			
			if( npc == null )
			{
				plugin.npcManager.deactivateLocation(location);
				throw new Exception("There are no more NPCs in the pool to activate.");
			}
			
			System.out.println("Activated NPC #" + npc.id_NPC);
			
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
	private void create(Player player, String name, String race, String gender) 
	{	
		int raceID = Integer.parseInt(race);
		if( raceID < 1 || raceID > 5 )
		{
			plugin.message.sendNo(player, "Invalid race ID (1-5)");
			return;
		}

		NPCCommands.newNPC(plugin, player, name, raceID, gender, 0);
	}
	
	// Creates a new NPC - used by multiple commands
	public static void newNPC(RageMod plugin, Player player, String name, int raceID, String gender, int id_NPCTown)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		boolean isMale;
		
		Random random = new Random();
		
		try
		{
			isMale = gender.equalsIgnoreCase("M");
			
			if (name.length() > 14) 
			{
				String tmp = name.substring(0, 14);
				plugin.getServer().getLogger().log(Level.WARNING, "NPCs can't have names longer than 14 characters,");
				plugin.getServer().getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
				name = tmp;
			}
			
			// Add the NPC to memory
			NPCData npc = new NPCData();
			npc.id_NPCRace = raceID;
			npc.id_NPCTown = id_NPCTown;		// 0 = no town
			npc.name = name;
			npc.isBilingual = false;
			npc.isMale = isMale;
			
			// Determine the default affinity randomly
			int affinityRoll = random.nextInt(100);
			if( affinityRoll < 10 )
				npc.defaultAffinityCode = -2;
			else if( affinityRoll < 30 )
				npc.defaultAffinityCode = -1;
			else if( affinityRoll < 50 )
				npc.defaultAffinityCode = 1;
			else
				npc.defaultAffinityCode = 0;
			
			// Add the NPC to the database
			npc.id_NPC = plugin.database.npcQueries.createNPC(npc, playerData.id_Player);
			
			plugin.npcManager.addNPC(npc);
			
			plugin.message.send(player, "Successfully added NPC #" + npc.id_NPC + ".");
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
				plugin.message.parse(player, " " + location.getID() + ": " + plugin.zones.getName(location) + " (" + 
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
				String locationStr;
				Town town = plugin.towns.getCurrentTown(instance.getLocation());
				if( town != null )
					locationStr = town.getCodedName();
				else
					locationStr = plugin.zones.getName(instance.getLocation()) + " (" + 
							instance.getLocation().getQuadrant().toString() + ")";
				plugin.message.parse(player, " " + instance.getCodedName() + ": Loc. " + instance.getLocation().getID() + ", " + locationStr);
			}
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
