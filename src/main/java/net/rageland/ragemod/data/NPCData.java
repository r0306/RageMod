package net.rageland.ragemod.data;

public class NPCData 
{
	public int id_NPC;
	public int id_NPCRace;
	public String name;
	public boolean isBilingual;
	public int id_NPCTown;
	
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
	
}
