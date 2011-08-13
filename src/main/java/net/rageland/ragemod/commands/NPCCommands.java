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
			String questId = split[4];
			
			
			if(npcType.equalsIgnoreCase("queststartnpc"))
			{
				spawnQuestStartNPC(player, plugin.questManager.quests.get(questId), npcName);
			} 
			else if(npcType.equalsIgnoreCase("questrewardnpc"))
			{
				
			}
			else if(npcType.equalsIgnoreCase("speechnpc"))
			{
				
			}
			else if(npcType.equalsIgnoreCase("questendnpc"))
			{
				
			}
			else if(npcType.equalsIgnoreCase("queststartendnpc"))
			{
				
			}				
		} 
		else if( isDespawnCommand(split) )
		{
			
		}
	}
	
	private void spawnQuestStartNPC(Player player, Quest quest, String npcName) 
	{
		Location l = player.getLocation();
		
		plugin.npcManager.spawnQuestStartNPC(npcName, l, quest);
	}
	
	private void spawnQuestRewardNPC(Player player, Quest quest, String npcName)
	{
		
	}
	
	private void spawnQuestSpeechNPC(Player player, String npcName)
	{
		
	}
	
	private void spawnQuestEndNPC(Player player, Quest quest, String npcName)
	{
		
	}
	
	private void spawnQuestStartEndNPC(Player player, Quest quest, String npcName)
	{
		
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
		if(split.length > 3 && split.length < 6)
		{
			if(split[1].equalsIgnoreCase("spawn"))
			{
				if(split[2].equalsIgnoreCase("speechnpc") && split.length == 4)
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
		if(split.length == 3)
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
	
	private void spawnAlotOfNPCS(Player player) 
	{
		Location l = player.getLocation();
		
		for(int i = 0; i < 20; i++)
		{
			for(int j = 0; j < 5; j++)
			{
				l.setZ(l.getZ() + 1);
				plugin.npcManager.spawnQuestStartNPC("TheIcarusKid", l, plugin.questManager.quests.get(1));
			}
			l.setZ(l.getZ() - 5);
			l.setX(l.getX() + 1);
		}
	}

}
