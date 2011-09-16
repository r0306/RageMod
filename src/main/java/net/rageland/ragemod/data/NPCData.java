package net.rageland.ragemod.data;

import net.rageland.ragemod.quest.Quest;

public class NPCData 
{
	public int id_NPC;
	public int id_NPCRace;
	public String name;
	public boolean isBilingual;
	public int id_NPCTown;
	public boolean isMale = true;
	public String skinPath;
	public int defaultAffinityCode;
	
	// Quest data
	// DC: This should be moved to NPCInstance - NPCs should be able to spawn with or without quests
	public boolean isQuestNPC;
    public Quest quest;
    public int questNPCType; // 0 = START, 1 = END, 2 = STARTEND
	
	private boolean inUse = false;
	
	// Sets the current NPC to "in use"
	public void activate()
	{
		this.inUse = true;
	}
	
	// Sets the current NPC to "in reserve"
	public void deactivate()
	{
		this.inUse = false;
	}
	
	// Returns the NPC's name with the appropriate code
	public String getCodedName()
	{
		return "<pn>" + name + "</pn>";
	}
	
}
