package net.rageland.ragemod.text;

import java.util.ArrayList;
import java.util.HashMap;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.PlayerData;

// Stores all languages for access
public class Languages 
{
	private HashMap<Integer, Language> languages = new HashMap<Integer, Language>();
	private RageMod plugin;
	
	public Languages(RageMod plugin) 
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
	
	
	
	
	
	
	
	
	
	
}
