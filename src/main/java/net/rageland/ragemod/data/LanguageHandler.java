package net.rageland.ragemod.data;

import java.util.ArrayList;
import java.util.HashMap;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.text.Language;

// Stores all languages for access
public class LanguageHandler 
{
	private HashMap<Integer, Language> languages = new HashMap<Integer, Language>();
	private RageMod plugin;
	
	public LanguageHandler(RageMod plugin) 
	{
		this.plugin = plugin;
		languages = new HashMap<Integer, Language>();
	}
	
	// On startup, pull all the language dictionaries 
	public void loadDictionaries()
	{
		languages = plugin.database.npcQueries.loadDictionaries();	
	}
	
	// Translate a sentence into the specified language at all comprehension levels
	public ArrayList<String> translate(String source, int id_Language)
	{
		return languages.get(id_Language).translate(source);
	}

	public String getAbbreviation(int id_Language) 
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
	
	
	
	
	
	
	
	
	
	
}
