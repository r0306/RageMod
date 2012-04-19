package net.rageland.ragemod.npc;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;

// Represents a single phrase uttered by an NPC
public class NPCPhrase
{
	private String message;
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
		return this.plugin.languages.translate(message, plugin.races.races.get(npcData.id_NPCRace).getLang().getName());
	}
	
	// Returns whether the phrase needs to have its XML parsed
	// Is this right?
	public boolean isDynamic()
	{
		return isDynamic;
	}
	
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
