package net.rageland.ragemod.commands;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.PlayerData;

import org.bukkit.entity.Player;

public class QuestCommands 
{
	private RageMod plugin;
	
	public QuestCommands(RageMod plugin) 
	{
		this.plugin = plugin;
	}
	
	public void onQuestCommand(Player player, PlayerData playerData, String[] split) 
	{
		if(split.length != 2) 
		{
			plugin.text.message(player, "Quest commands: ");
			plugin.text.message(player, "    /quest abandon (Abandons your current quest)");
			plugin.text.message(player, "    /quest view    (Views your current quest)");
		}
		else if(split[1].equalsIgnoreCase("abandon")) 
		{
			if(playerData.activeQuestData.quest == null) 
			{
				plugin.text.message(player, "ERROR: You're not currently on a quest.");
			} 
			else
			{
				plugin.text.message(player, "Quest " + playerData.activeQuestData.quest.getQuestName() + " abandoned. You currently have no active quest.");
				playerData.activeQuestData.quest = null;
				playerData.activeQuestData.questCounter = 0;				
			}
		} 
		else if(split[1].equalsIgnoreCase("view"))
		{
			if(playerData.activeQuestData.quest == null)
			{
				plugin.text.message(player, "ERROR: You're not currently on a quest.");
			}
			else 
			{
				plugin.text.message(player, "Active quest: " + playerData.activeQuestData.quest.getQuestName());
				plugin.text.message(player, "Quest text: ");
				plugin.text.message(player, playerData.activeQuestData.quest.getQuestText());
			}
		}
	}

}
