package net.rageland.ragemod.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
		
		if( split.length == 5 && split[1].equals("spawn")) 
		{
			if(split[2].equalsIgnoreCase("queststartnpc"))
			{
				spawnQuestStartNPC(player, playerData, plugin.questManager.quests.get(Integer.parseInt(split[3])), split[4]);
			}
		} 
		else if(split.length == 2) 
		{
			spawnQuestStartNPC(player, playerData, plugin.questManager.quests.get(1), split[1]);
		}
		else if(split.length == 1)
		{
			spawnAlotOfNPCS(player);
		}
	}
	
	private void spawnQuestStartNPC(Player player, PlayerData playerData, Quest quest, String npcName) {
		Location l = player.getLocation();
		
		plugin.npcManager.spawnQuestStartNPC(npcName, l, quest);
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
