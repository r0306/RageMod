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
			plugin.text.send(player, "Quest commands: ");
			plugin.text.parse(player, "    /quest abandon (Abandons your current quest)");
			plugin.text.parse(player, "    /quest view    (Views your current quest)");
		}
		else if(split[1].equalsIgnoreCase("abandon")) 
		{
			if(playerData.activeQuestData.isPlayerOnQuest()) 
			{
				plugin.text.send(player, "Quest " + 
						playerData.activeQuestData.getQuest().getQuestData().getName() + 
						" abandoned. You currently have no active quest.");
				playerData.activeQuestData.endQuest();
			} 
			else
				plugin.text.sendNo(player, "ERROR: You're not currently on a quest.");
		} 
		else if(split[1].equalsIgnoreCase("view"))
		{
			if(playerData.activeQuestData.isPlayerOnQuest())
			{
				plugin.text.send(player, "Active quest: " + playerData.activeQuestData.getQuest().getQuestData().getName());
				plugin.text.send(player, "Quest text: ");
				plugin.text.send(player, playerData.activeQuestData.getQuest().getQuestData().getStartText());
				
			}
			else 
			{
				plugin.text.sendNo(player, "ERROR: You're not currently on a quest.");
			}
		}
	}

}
