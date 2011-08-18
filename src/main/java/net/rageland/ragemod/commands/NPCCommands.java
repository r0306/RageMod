package net.rageland.ragemod.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCData;
import net.rageland.ragemod.data.NPCLocation;
import net.rageland.ragemod.data.PlayerData;
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
		if( split.length < 2 || split.length > 3 )
		{
			plugin.message.parse(player, "NPC commands: <required> [optional]");
			if( true )
				plugin.message.parse(player, "   /npc create <name>   (enters a new NPC into the database)");
			if( true )
				plugin.message.parse(player, "   /npc newloc   (creates a new NPCLocation at your point)");
			if( true )
				plugin.message.parse(player, "   /npc spawn <locID>   (spawns a random NPC at location)");
		}
		else if( split[1].equalsIgnoreCase("create") )
		{
			if( split.length == 3 )
				this.create(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /npc create <name>");  
		}
		else if( split[1].equalsIgnoreCase("newloc") )
		{
			this.newloc(player); 
		}
		else if( split[1].equalsIgnoreCase("spawn") )
		{
			if( split.length == 3 )
				this.spawn(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /npc spawn <locID>");  
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



	private void spawnQuestStartNPC(Player player, QuestImplementation quest, String npcName, int npcId) 
	{
		Location l = player.getLocation();		
		plugin.npcManager.getSpawner().questStartNPC(npcName, npcId, l, quest);
		// Store data to database
	}
	
	private void spawnRewardNPC(Player player, QuestImplementation quest, String npcName, int npcId)
	{
		Location l = player.getLocation();
		plugin.npcManager.getSpawner().rewardNPC(npcName, npcId, l, quest);
		// Store data to database
	}
	
	private void spawnSpeechNPC(Player player, String npcName, int npcId)
	{
		Location l = player.getLocation();
		plugin.npcManager.getSpawner().speechNPC(npcName, npcId, l);
		// Store data to database
	}
	
	private void spawnQuestEndNPC(Player player, QuestImplementation quest, String npcName, int npcId)
	{
		Location l = player.getLocation();
		plugin.npcManager.getSpawner().questEndNPC(npcName, npcId, l, quest);
		// Store data to database
	}
	
	private void spawnQuestStartEndNPC(Player player, QuestImplementation quest, String npcName, int npcId)
	{
		Location l = player.getLocation();
		plugin.npcManager.getSpawner().questStartEndNPC(npcName, npcId, l, quest);
		// Store data to database
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
			// Add the NPCLocation to the database
			int id_NPCLocation = plugin.database.npcQueries.createNPCLocation(player.getLocation(), 0, 0, playerData.id_Player);
			
			// Add the NPCLocation to memory
			NPCLocation npcLocation = new NPCLocation(player.getLocation());
			npcLocation.setIDs(id_NPCLocation, 0, 0);
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
		
		try
		{
			// Get the NPC location
			location = plugin.npcManager.activateLocation(Integer.parseInt(id_NPCLocation));
			if( location == null )
				throw new Exception(id_NPCLocation + " is already in use or is an invalid location ID.");
			
			// Activate a random NPC from the pool
			npc = plugin.npcManager.activateRandomNPC();
			if( npc == null )
				throw new Exception("There are no more NPCs in the pool to activate.");
			
			// Register the NPC instance and spawn the NPC
			plugin.database.npcQueries.createInstance(npc.id_NPC, location.getID(), 30, playerData.id_Player);
			plugin.npcManager.getSpawner().speechNPC(ChatColor.DARK_AQUA + npc.name, npc.id_NPC, location);
			
			// Get two new phrases for the speech NPC to say
			ArrayList<String> phrases = plugin.database.npcQueries.getPhrases(2);
			for( String phrase : phrases )
				plugin.npcManager.getNPCEntity(npc.id_NPC).addSpeechMessage(phrase);
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
	

}
