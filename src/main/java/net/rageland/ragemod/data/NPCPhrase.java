package net.rageland.ragemod.data;

import java.util.ArrayList;

import net.rageland.ragemod.RageMod;

// Represents a single phrase uttered by an NPC
public class NPCPhrase
{
	private String message;
	private ArrayList<String> translations;
	private int id;
	private int id_Race;
	private RageMod plugin;
	
	public NPCPhrase(String message, int id, int id_Race, RageMod plugin)
	{
		this.message = message;
		this.id = id;
		this.id_Race = id_Race;
		this.plugin = plugin;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getTranslation(int skill)
	{
		// Check to see if the translations have been set up yet
		if( translations == null )
			translations = plugin.languages.translate(message, id_Race);
		
		if( skill >= 100 )
			return message;
		else
			return translations.get((int)(((skill / 25) - 3) * -1));	// This maps 0 to 3, 25 to 2, 50 to 1, and 75 to 0
	}

}
