package net.rageland.ragemod.npcentities;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCInstance;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.quest.Quest;

import org.bukkit.entity.Player;

public class QuestNPC extends RageEntity
{
	private Quest quest;
	private int type;
	
	public static final int START = 0;
	public static final int END = 0;
	public static final int STARTEND = 0;

	public QuestNPC(NPCInstance npcInstance)
	{
		super(npcInstance);
		this.quest = RageMod.getInstance().questManager.quests.get(npcInstance.questId);
		this.type = npcInstance.questNPCType;
	}

	/**
	 * Method called when a right click action on the NPC is performed by a
	 * player. *
	 * 
	 * @param player
	 *            Player that right clicked the entity
	 */
	public void rightClickAction(Player player)
	{
		PlayerData playerData = this.plugin.players.get(player.getName());
		if(playerData.activeQuestData.getQuest() == quest && (type == STARTEND || type == END)) 
		{
			player.sendMessage("If you are finished with your quest, left click me.");
		} 
		else if(playerData.activeQuestData.isPlayerOnQuest() && (type == STARTEND || type == START))
		{
			player.sendMessage("When you are finished with your current quest, come back to see me.");
		}
		else if(type == STARTEND || type == START)
		{
			player.sendMessage("Quest: " + quest.getQuestData().getName());
			player.sendMessage(quest.getQuestData().getStartText());
			player.sendMessage("[Left click npc to accept]");	
		}
		
	}

	/**
	 * Method called when a left click action on the NPC is performed by a
	 * player. *
	 * 
	 * @param player
	 *            Player that left clicked the entity
	 */
	public void leftClickAction(Player player)
	{
		PlayerData playerData = this.plugin.players.get(player.getName());
		if(playerData.activeQuestData.getQuest() == quest && (type == STARTEND || type == END)) 
		{
			if(quest.isFinished(playerData))
				quest.end(player, playerData);
			else
				quest.statusUpdate(player, playerData);
		} 
		else if(playerData.activeQuestData.isPlayerOnQuest() && (type == STARTEND || type == START))
		{
			player.sendMessage("When you are finished with your current quest, come back to see me.");
		}
		else if(type == STARTEND || type == START)
		{
			if(playerData.activeQuestData.isQuestCompleted(quest.getQuestData().getId()))
			{
				player.sendMessage("You have already finished this quest ");
			}
			else
			{
				player.sendMessage("Quest: " + quest.getQuestData().getName());
				player.sendMessage(quest.getQuestData().getStartText());
				quest.start(player, playerData);
			}			
		}
		else
		{
			// Say something from phrases maybe? This happens if it is a Quest END NPC, and the player is not on a quest, or a wrong one.
		}
	}
}
