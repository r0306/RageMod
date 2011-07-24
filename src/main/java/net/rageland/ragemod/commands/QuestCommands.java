package net.rageland.ragemod.commands;

import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.PlayerData;

import org.bukkit.entity.Player;

public class QuestCommands {
	
	public void onQuestCommand(Player player, PlayerData playerData, String[] split) 
	{
		if(split.length != 2) 
		{
			Util.message(player, "Quest commands: ");
			Util.message(player, "    /quest abandon (Abandons your current quest)");
			Util.message(player, "    /quest view    (Views your current quest)");
		}
		else if(split[1].equalsIgnoreCase("abandon")) 
		{
			if(playerData.activeQuestData.quest == null) 
			{
				Util.message(player, "ERROR: You're not currently on a quest.");
			} 
			else
			{
				Util.message(player, "Quest " + playerData.activeQuestData.quest.getQuestName() + " abandoned. You currently have no active quest.");
				playerData.activeQuestData.quest = null;
				playerData.activeQuestData.questCounter = 0;				
			}
		} 
		else if(split[1].equalsIgnoreCase("view"))
		{
			if(playerData.activeQuestData.quest == null)
			{
				Util.message(player, "ERROR: You're not currently on a quest.");
			}
			else 
			{
				Util.message(player, "Active quest: " + playerData.activeQuestData.quest.getQuestName());
				Util.message(player, "Quest text: ");
				Util.message(player, playerData.activeQuestData.quest.getQuestText());
			}
		}
	}

}
