package net.rageland.ragemod.quest;

public class ActiveQuestData
{

	public int questCounter;
	public Quest quest;

	public ActiveQuestData()
	{

	}

	/**
	 * Set parameters needed for a active quest.
	 * 
	 * Does not check if quest == null or not, must be checked earlier.
	 * 
	 * @param quest
	 * @param questCounter
	 */
	public void questStart(Quest quest, int questCounter)
	{
		this.quest = quest;
		this.questCounter = questCounter;
	}

	/**
	 * Resets the quest and questCounter. Called when a quest is finished.
	 */
	public void questFinished()
	{
		quest = null;
		questCounter = 0;
	}

}
