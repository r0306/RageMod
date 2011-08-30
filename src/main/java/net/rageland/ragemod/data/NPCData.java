package net.rageland.ragemod.data;

import net.rageland.ragemod.quest.QuestImplementation;

public class NPCData 
{
	public int id_NPC;
	public int id_NPCRace;
	public String name;
	public boolean isBilingual;
	public int id_NPCTown;
	public boolean isQuestNPC;
	public QuestImplementation quest;
	public int questNPCType; // 0 = START, 1 = END, 2 = STARTEND
	
	private boolean inUse = false;
	
	// Sets the current location to "in use"
	public void activate()
	{
		this.inUse = true;
	}
	
	// Sets the current location to "in reserve"
	public void deactivate()
	{
		this.inUse = false;
	}
	
}
