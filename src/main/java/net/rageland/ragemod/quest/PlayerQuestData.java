package net.rageland.ragemod.quest;

import java.util.ArrayList;

public class PlayerQuestData
{
	private Quest quest;
	private int objectiveCounter;
	private boolean playerOnQuest;
	private ArrayList<Integer> completedQuests;

	public PlayerQuestData()
	{
		
	}
	
	public Quest getQuest()
	{
		return quest;
	}
	
	public void questCompleted(int questId)
	{
		if(!completedQuests.contains(questId))
		{
			completedQuests.add(questId);
		}
	}
	
	public boolean isQuestCompleted(int questId)
	{
		if(completedQuests.contains(questId))
			return true;
		else
			return false;
	}
	
	public int getObjectiveCounter()
	{
		return objectiveCounter;
	}
	
	public boolean isPlayerOnQuest()
	{
		return playerOnQuest;
	}
	
	/**
	 * Will increment the objectiveCounter if it is less then what is required
	 * by the quest.
	 */
	public void incrementObjectiveCounter()
	{
		if(objectiveCounter < quest.getQuestData().getObjectiveCounter())
			objectiveCounter++;
	}

	/**
	 * Starts a new quest for the player.
	 * 
	 * @param quest
	 * @param questCounter
	 */
	public void startNewQuest(Quest quest, int objectiveCounter)
	{
		this.quest = quest;
		this.objectiveCounter = objectiveCounter;
		this.playerOnQuest = true;
	}

	/**
	 * Resets the quest and questCounter. Called when a quest is finished.
	 */
	public void endQuest()
	{
		playerOnQuest = false;
	}

}
