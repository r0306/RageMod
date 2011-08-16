package net.rageland.ragemod.commands;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.quest.Quest;

public class NPCCommands {
	
	private RageMod plugin;
	
	public NPCCommands(RageMod plugin)
	{
		this.plugin = plugin;
	}
	
	public void onNPCCommand(Player player, PlayerData playerData, String[] split) {
		
		if( isSpawnCommand(split) ) 
		{
			String npcType = split[2];
			String npcName = split[3];
			String npcId = split[4];
			String questId = split[5];
			
			
			if(npcType.equalsIgnoreCase("queststartnpc"))
			{
				spawnQuestStartNPC(player, plugin.questManager.quests.get(questId), npcName, npcId);
			} 
			else if(npcType.equalsIgnoreCase("questrewardnpc"))
			{
				spawnRewardNPC(player, plugin.questManager.quests.get(questId), npcName, npcId);
			}
			else if(npcType.equalsIgnoreCase("speechnpc"))
			{
				spawnSpeechNPC(player, npcName, npcId);
			}
			else if(npcType.equalsIgnoreCase("questendnpc"))
			{
				spawnQuestEndNPC(player, plugin.questManager.quests.get(questId), npcName, npcId);
			}
			else if(npcType.equalsIgnoreCase("queststartendnpc"))
			{
				spawnQuestStartEndNPC(player, plugin.questManager.quests.get(questId), npcName, npcId);
			}				
		} 
		else if( isDespawnCommand(split) )
		{
			String npcName = split[2];
			plugin.npcManager.despawn(npcName);
			// Will despawn all NPC's with npcName as name. Might need to add despawn by ID, which is unique.
		}
	}
	
	private void spawnQuestStartNPC(Player player, Quest quest, String npcName, String npcId) 
	{
		Location l = player.getLocation();		
		plugin.npcManager.getSpawner().questStartNPC(npcName, npcId, l, quest);
		// Store data to database
	}
	
	private void spawnRewardNPC(Player player, Quest quest, String npcName, String npcId)
	{
		Location l = player.getLocation();
		plugin.npcManager.getSpawner().rewardNPC(npcName, npcId, l, quest);
		// Store data to database
	}
	
	private void spawnSpeechNPC(Player player, String npcName, String npcId)
	{
		Location l = player.getLocation();
		plugin.npcManager.getSpawner().speechNPC(npcName, npcId, l);
		// Store data to database
	}
	
	private void spawnQuestEndNPC(Player player, Quest quest, String npcName, String npcId)
	{
		Location l = player.getLocation();
		plugin.npcManager.getSpawner().questEndNPC(npcName, npcId, l, quest);
		// Store data to database
	}
	
	private void spawnQuestStartEndNPC(Player player, Quest quest, String npcName, String npcId)
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

}
