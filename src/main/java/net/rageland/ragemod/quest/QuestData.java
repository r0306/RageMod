package net.rageland.ragemod.quest;

/**
 * Contains quest data, each quest contains an instance of this class.
 * @author Jorgen
 *
 */
public class QuestData
{
	private String name;
	private String id;
	private String startText;
	private String endText;
	private int objectiveCounter;
	private String preRequiredQuest;
	
	/**
	 * 
	 * @param name Name of quest
	 * @param id Quest ID
	 * @param startText Text to be displayed when the quest is started
	 * @param endText Text to be displayed when the quest is ended
	 */
	public QuestData(
				String name,
				String id,
				String startText,
				String endText,
				int objectiveCounter,
				String preRequiredQuest)
	{
		this.name = name;
		this.id = id;
		this.startText = startText;
		this.endText = endText;
		this.objectiveCounter = objectiveCounter;
		this.preRequiredQuest = preRequiredQuest;
	}
	
	/**
	 * Returns the quest name
	 * @return Quest name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the quest id
	 * @return Quest id
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * Returns start text of a quest
	 * @return Start text
	 */
	public String getStartText()
	{
		return startText;
	}
	
	/**
	 * Returns end text of a quest
	 * @return End text
	 */
	public String getEndText()
	{
		return endText;
	}
	
	public int getObjectiveCounter()
	{
		return objectiveCounter;
	}
	
	public String getPreRequiredQuest()
	{
		return preRequiredQuest;
	}
}
