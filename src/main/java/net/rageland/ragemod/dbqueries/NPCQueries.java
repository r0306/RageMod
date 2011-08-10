package net.rageland.ragemod.dbqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.bukkit.Location;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.database.RageDB;
import net.rageland.ragemod.text.Language;

public class NPCQueries 
{
	private RageDB rageDB;
	private RageMod plugin;

	public NPCQueries(RageDB rageDB, RageMod plugin) 
	{
		this.rageDB = rageDB;
		this.plugin = plugin;
	}
	
	// Load all words for languages
	public HashMap<Integer, Language> loadDictionaries()
    {
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    HashMap<Integer, Language> languages = new HashMap<Integer, Language>();
	    Language currentLanguage = null;
	    int id_Language = 0;
	    
		PlayerTown currentTown = null;
		
    	try
    	{
    		conn = rageDB.getConnection();
        	preparedStatement = conn.prepareStatement(
				"SELECT ID_Language, Word, Length FROM Words ORDER BY ID_Language, Length, Word");
        	
        	rs = preparedStatement.executeQuery();
        	
        	while ( rs.next() ) 
        	{	
        		if( id_Language != rs.getInt("ID_Language") )
        		{
        			// New language
        			if( currentLanguage != null )
        				languages.put(id_Language, currentLanguage);
        			
        			currentLanguage = new Language();
        			id_Language = rs.getInt("ID_Language");
        		}
        		
        		currentLanguage.addWord(rs.getString("Word"), rs.getInt("Length"));
        	}			
        	
        	// Add the last language
        	languages.put(id_Language, currentLanguage);
        	
        	return languages;
        		        	
    	} catch (Exception e) {
    		System.out.println("Error in NPCQueries.loadDictionaries: " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
    }
}
