package net.rageland.ragemod.data;

// Represents a single phrase uttered by an NPC
public class NPCPhrase
{
	private String message;
	private int id;
	
	public NPCPhrase(String message, int id)
	{
		this.message = message;
		this.id = id;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public int getID()
	{
		return id;
	}

}
