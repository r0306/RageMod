package net.rageland.ragemod.data;

import java.util.ArrayList;

import net.rageland.ragemod.RageMod;

// Represents a single phrase uttered by an NPC
public class NPCPhrase
{
	private String message;
	private ArrayList<String> translations;
	private int id;
	private NPCData npcData;
	private RageMod plugin;
	private boolean isDynamic;
	
	public NPCPhrase(String message, int id, RageMod plugin, boolean isDynamic, NPCData npcData)
	{
		this.message = message;
		this.id = id;
		this.plugin = plugin;
		this.isDynamic = isDynamic;
		this.npcData = npcData;
	}
	
	public String getMessage(PlayerData playerData)
	{
		if( npcData.id_NPCRace == plugin.config.NPC_HUMAN_ID || playerData.getLanguageSkill(npcData.id_NPCRace) == 100 )
			return parse(message, playerData);
		else
			return getTranslation(playerData);
	}
	
	public int getID()
	{
		return id;
	}
	
	private String getTranslation(PlayerData playerData)
	{
		// Check to see if the translations have been set up yet
		if( translations == null )
			translations = plugin.languages.translate(message, npcData.id_NPCRace);
		
		int skill = playerData.getLanguageSkill(npcData.id_NPCRace);
		
		if( skill >= 100 )
			return parse(message, playerData);
		else
			return parse(translations.get((int)(((skill / 25) - 3) * -1)), playerData);	// This maps 0 to 3, 25 to 2, 50 to 1, and 75 to 0
	}
	
	// Returns whether the phrase needs to have its XML parsed
//	public boolean isDynamic()
//	{
//		return isDynamic;
//	}
	
	// Parse XML
	private String parse(String toParse, PlayerData playerData)
	{
		if( !isDynamic )
			return toParse;
		
		String result = toParse;
		result = result.replace("<playerName/>", playerData.getCodedName());
		result = result.replace("<selfName/>", npcData.getCodedName());
		return result;
	}
	

}
