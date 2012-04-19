package net.rageland.ragemod.data;

import java.util.HashMap;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.text.Language;

// Stores all languages for access
public class LanguageHandler 
{
	private HashMap<Integer, Language> languages = new HashMap<Integer, Language>();
	private HashMap<String, Integer> langids = new HashMap<String, Integer>();
	private RageMod plugin;
	
	public LanguageHandler(RageMod plugin) 
	{
		this.plugin = plugin;
		languages = new HashMap<Integer, Language>();
	}
	
	// On startup, pull all the language dictionaries TODO make this for the new system
	public void loadDictionaries()
	{
		languages = plugin.database.npcQueries.loadDictionaries();	
	}
	
	// Translate a sentence into the specified language at all comprehension levels
	public String translate(String source, int id_Language)
	{
		return languages.get(id_Language).translateEnLa(source);
	}

	public String getAbbreviation(int id_Language) //TODO what is this? A Short thing for the language names?
	{
		switch( id_Language )
		{
			case 1:
				return "Cr";
			case 2:
				return "Gh";
			case 3:
				return "Be";
			case 4:
				return "Av";
			default:
				return "XX";
		}
	}

	public int getlangid(String lang) {
		return this.langids.get(lang);
	}

	public Language getLanguage(int isSpeaking) {
		return this.languages.get(isSpeaking);
	}

	public String translate(String message, String name) {
		
		return this.translate(message, this.getlangid(name));
	}	
	
}
