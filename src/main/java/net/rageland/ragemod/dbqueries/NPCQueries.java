package net.rageland.ragemod.dbqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCData;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.database.RageDB;
import net.rageland.ragemod.npc.NPCInstance;
import net.rageland.ragemod.npc.NPCLocation;
import net.rageland.ragemod.npc.NPCLocationPool;
import net.rageland.ragemod.npc.NPCPhrase;
import net.rageland.ragemod.npc.NPCPool;
import net.rageland.ragemod.npc.NPCTown;
import net.rageland.ragemod.npc.NPCInstance.NPCType;
import net.rageland.ragemod.npcentities.SpeechData;
import net.rageland.ragemod.text.Language;
import net.rageland.ragemod.utilities.Util;

public class NPCQueries 
{
	private RageDB rageDB;
	private RageMod plugin;
	private Random random;

	public NPCQueries(RageDB rageDB, RageMod plugin) 
	{
		this.rageDB = rageDB;
		this.plugin = plugin;
		this.random = new Random();
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

	// Loads all NPCLocations in the database into memory
	public NPCLocationPool loadNPCLocations() 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    NPCLocationPool locationPool = new NPCLocationPool();
		NPCLocation location = null;
		
    	try
    	{
    		conn = rageDB.getConnection();
        	preparedStatement = conn.prepareStatement(
				"SELECT ID_NPCLocation, ID_NPCTown, ID_NPCRace, XCoord, YCoord, ZCoord, Yaw, Pitch " +
				"FROM NPCLocations");
        	
        	rs = preparedStatement.executeQuery();
        	
        	while ( rs.next() ) 
        	{	        		
        		location = new NPCLocation(plugin.zones.world, 
        				rs.getDouble("XCoord"),
        				rs.getDouble("YCoord"),
        				rs.getDouble("ZCoord"),
        				rs.getFloat("Yaw"),
        				rs.getFloat("Pitch"), plugin);
        		
        		location.setIDs(rs.getInt("ID_NPCLocation"), rs.getInt("ID_NPCTown"), rs.getInt("ID_NPCRace"));
        		locationPool.add(location);
        	}			
        	
        	return locationPool;
        		        	
    	} catch (Exception e) {
    		System.out.println("Error in NPCQueries.loadNPCLocations: " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
	}

	public int createNPCLocation(Location location, int id_NPCTown, int id_NPCRace, int id_Player_Creator) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		
    	try
    	{    		
    		conn = rageDB.getConnection();
    		// Insert the new town into the PlayerTowns table
    		preparedStatement = conn.prepareStatement(
    				"INSERT INTO NPCLocations (ID_NPCTown, ID_NPCRace, XCoord, YCoord, ZCoord, Yaw, Pitch, CreateDate, ID_Player_Creator) " +
    				"VALUES (" + id_NPCTown + ", " + id_NPCRace + ", " + location.getX() + ", " + location.getY() + ", " +
    						location.getZ() + ", " +  location.getYaw() + ", " + location.getPitch() + ", NOW(), " + 
    				id_Player_Creator + ")",
    				Statement.RETURN_GENERATED_KEYS);   
    		preparedStatement.executeUpdate();
    		
    		// Retrieve the new auto-increment town ID 
    		rs = preparedStatement.getGeneratedKeys();
    		rs.next();
    		int locationID = rs.getInt(1);
    		
    		return locationID;
        		        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in NPCQueries.createNPCLocation(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	System.out.println("Error: NPCQueries.createNPCLocation() returned -1");
    	return -1;
	}

	// Load all NPCs into memory
	public NPCPool loadNPCs() 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    NPCPool npcs = new NPCPool();
		NPCData npc = null;
		
    	try
    	{
    		conn = rageDB.getConnection();
        	preparedStatement = conn.prepareStatement(
				"SELECT n.ID_NPC, n.ID_NPCRace, n.Name, n.IsBilingual, n.ID_NPCTown, n.Gender, s.Filename, n.DefaultAffinity " +
				"FROM NPCs n " +
				"LEFT JOIN Skins s ON n.ID_Skin = s.ID_Skin");
        	
        	rs = preparedStatement.executeQuery();
        	
        	while ( rs.next() ) 
        	{	        		
        		npc = new NPCData();
        		npc.id_NPC = rs.getInt("ID_NPC");
        		npc.id_NPCRace = rs.getInt("ID_NPCRace");
        		npc.name = rs.getString("Name");
        		npc.isBilingual = rs.getBoolean("IsBilingual");
        		npc.id_NPCTown = rs.getInt("ID_NPCTown");
        		npc.isMale = rs.getString("Gender").equalsIgnoreCase("M");
        		npc.skinPath = rs.getString("Filename");
        		npc.defaultAffinityCode = rs.getInt("DefaultAffinity");
        		
        		npcs.add(npc);
        	}			
        	
        	return npcs;
        		        	
    	} catch (Exception e) {
    		System.out.println("Error in NPCQueries.loadNPCs(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
	}
	
	// Enter a new NPC into the database
	public int createNPC(NPCData npc, int id_Player_Creator) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    int skinID;
		
    	try
    	{    		
    		conn = rageDB.getConnection();
    		// Insert the new town into the PlayerTowns table
    		preparedStatement = conn.prepareStatement(
    				"INSERT INTO NPCs (ID_NPCRace, Name, IsBilingual, ID_NPCTown, CreateDate, ID_Player_Creator, Gender, DefaultAffinity) " +
    				"VALUES (" + npc.id_NPCRace + ", '" + npc.name + "', " + npc.isBilingual + ", " +
    					npc.id_NPCTown + ", NOW(), " + id_Player_Creator + ", " +
    					(npc.isMale ? "'M'" : "'F'") + ", " + npc.defaultAffinityCode + ")",
    				Statement.RETURN_GENERATED_KEYS);   
    		preparedStatement.executeUpdate();
    		
    		// Retrieve the new auto-increment town ID 
    		rs = preparedStatement.getGeneratedKeys();
    		rs.next();
    		int npcID = rs.getInt(1);
    		
    		// Find a new skin for the NPC
    		preparedStatement = conn.prepareStatement(
    				"SELECT s.ID_Skin, s.Filename FROM Skins s " +
    				"LEFT JOIN NPCs n ON n.ID_Skin = s.ID_Skin " +
    				"WHERE s.ID_NPCRace = " + npc.id_NPCRace + " AND s.Gender = " + (npc.isMale ? "'M'" : "'F'") + "" +
    				"GROUP BY s.ID_Skin " +
    				"ORDER BY COUNT(n.ID_NPC) LIMIT 1");
        	rs = preparedStatement.executeQuery();
        	
        	if( rs.next() ) 
        	{	        		
        		npc.skinPath = rs.getString("Filename");
        		skinID = rs.getInt("ID_Skin");
        		
        		// Update the skin in the DB
        		preparedStatement = conn.prepareStatement(
        				"UPDATE NPCs SET ID_Skin = " + skinID + " WHERE ID_NPC = " + npcID);   
        		preparedStatement.executeUpdate();
        	}		
        	else
        		throw new Exception("Could not locate a suitable skin for " + npc.name);
    		
    		return npcID;
        		        		        	
    	} catch (Exception e) {
    		System.out.println("Error in NPCQueries.createNPC(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	System.out.println("Error: NPCQueries.createNPC() returned -1");
    	return -1;
	}
	
	// Enter a new NPC instance into the database
	public NPCInstance createInstance(int id_NPC, int id_NPCLocation, int ttlMinutes, int id_Player_Creator, NPCType type) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    NPCInstance instance = null;
		
    	try
    	{    		
    		if( id_NPC == 0 )
    			throw new Exception("Attempted to create instance with NPC ID 0");
    		
    		conn = rageDB.getConnection();
    		preparedStatement = conn.prepareStatement(
    				"INSERT INTO NPCInstances (ID_NPC, ID_NPCLocation, SpawnTime, DespawnTime, ID_Player_Creator, Type) " +
    				"VALUES (" + id_NPC + ", '" + id_NPCLocation + "', NOW(), NOW() + INTERVAL " + ttlMinutes + " MINUTE, " +
    						id_Player_Creator + ", " + type.getValue() + ")",
    				Statement.RETURN_GENERATED_KEYS);   
    		preparedStatement.executeUpdate();
    		
    		// Retrieve the new auto-increment town ID 
    		rs = preparedStatement.getGeneratedKeys();
    		rs.next();
    		int instanceID = rs.getInt(1);
    		
    		instance = new NPCInstance(plugin, instanceID, type, Util.minutesFromNow(ttlMinutes));
    		
    		return instance;
        		        		        	
    	} catch (Exception e) {
    		System.out.println("Error in NPCQueries.createInstance(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
	}

	// Loads all active NPC instances
	public ArrayList<NPCInstance> loadInstances() 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    ArrayList<NPCInstance> instances = new ArrayList<NPCInstance>();
		NPCInstance instance = null;
		
    	try
    	{
    		conn = rageDB.getConnection();
        	preparedStatement = conn.prepareStatement(
				"SELECT ID_NPCInstance, ID_NPC, ID_NPCLocation, DespawnTime, Type " +
				"FROM NPCInstances " +
				"WHERE (DespawnTime IS NULL OR DespawnTime > NOW()) AND IsDisabled = 0");
        	
        	rs = preparedStatement.executeQuery();
        	
        	while ( rs.next() ) 
        	{	        		
        		instance = new NPCInstance(plugin, rs.getInt("ID_NPCInstance"), NPCType.getType(rs.getInt("Type")), rs.getTimestamp("DespawnTime"));

        		if( instance.activate(rs.getInt("ID_NPC"), rs.getInt("ID_NPCLocation")) )
        			instances.add(instance);
        		else
        			System.out.println("ERROR: Could not activate NPC instance #" + instance.getID());
        	}			
        	
        	return instances;
        		        	
    	} catch (Exception e) {
    		System.out.println("Error in NPCQueries.loadInstances(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
	}
	
	// Gets a specified number of phrases for speech NPCs 
	public SpeechData getPhrases(NPCData npcData, int id_NPCTown) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    ArrayList<NPCPhrase> phrases = new ArrayList<NPCPhrase>();
	    ArrayList<NPCPhrase> phrasePool = new ArrayList<NPCPhrase>();
	    NPCPhrase greeting;
	    HashMap<Integer, NPCPhrase> followups = new HashMap<Integer, NPCPhrase>();
	    String updateQuery = "";
	    SpeechData speechData;
		
    	try
    	{
    		conn = rageDB.getConnection();
    		String whereClause = "(np.ID_NPCRace = 0 OR np.ID_NPCRace = " + npcData.id_NPCRace + ") AND " +
						"(np.ID_NPCTown = 0 OR np.ID_NPCTown = " + id_NPCTown + ") AND " +
						"(np.ID_NPC = 0 OR np.ID_NPC = " + npcData.id_NPC + ") AND " +
						"(np.IsApproved = 1) ";
    		
        	preparedStatement = conn.prepareStatement(
				"SELECT np.ID_NPCPhrase, np.Text, np.IsDynamic " +
				"FROM NPCPhrases np " +
				"WHERE " + whereClause + " AND " +
						"np.GreetingType = 0 AND np.Affinity = 0 " +	// TODO: Figure out how affinity will work with normal messages, if at all
				"ORDER BY np.Uses " +
				"LIMIT " + plugin.config.NPC_PHRASE_POOL);
        	rs = preparedStatement.executeQuery();
        	
        	// Load all possible phrases into the pool for random selection
        	while( rs.next() )
        	{
        		phrasePool.add(new NPCPhrase(rs.getString("Text"), rs.getInt("ID_NPCPhrase"), 
        				plugin, rs.getBoolean("IsDynamic"), npcData));
        	}
        	
        	// Choose a specified number of phrases from the pool to use in game
        	for( int i = 0; i < plugin.config.NPC_PHRASES; i++ )
        	{
        		if( phrasePool.size() > 0 )
        		{
        			NPCPhrase phrase = phrasePool.remove(random.nextInt(phrasePool.size()));
        			phrases.add(phrase);
        			if( i == 0 )
        				updateQuery = "ID_NPCPhrase = " + String.valueOf(phrase.getID());
        			else
        				updateQuery += " OR ID_NPCPhrase = " + String.valueOf(phrase.getID());
        		}
        	}
        	
        	// Get the initial greeting
        	preparedStatement = conn.prepareStatement(
    				"SELECT np.ID_NPCPhrase, np.Text, np.IsDynamic " +
    				"FROM NPCPhrases np " +
    				"WHERE " + whereClause + " AND " +
    						"np.GreetingType = 1 AND np.Affinity = (SELECT DefaultAffinity FROM NPCs WHERE ID_NPC = " + npcData.id_NPC + ") " +
    				"ORDER BY Uses " +
    				"LIMIT 1");
        	rs = preparedStatement.executeQuery();
        	
        	rs.next();
        	greeting = new NPCPhrase(rs.getString("Text"), rs.getInt("ID_NPCPhrase"), 
        			plugin, rs.getBoolean("IsDynamic"), npcData);
        	updateQuery += " OR ID_NPCPhrase = " + rs.getInt("ID_NPCPhrase");
        	
        	// Get the followup greetings
        	preparedStatement = conn.prepareStatement(
    				"SELECT np.ID_NPCPhrase, np.Text, np.Affinity, np.IsDynamic " +
    				"FROM ( " +
    				"	SELECT Affinity, MIN(Uses) as MinUses " +
				    "	FROM NPCPhrases np WHERE " + whereClause + " AND GreetingType = 2 GROUP BY Affinity " +
        			"	) AS x INNER JOIN NPCPhrases np ON np.Affinity = x.Affinity AND np.Uses = x.MinUses " +
    				"WHERE " + whereClause + " AND " +
    						"np.GreetingType = 2 " +
    				"GROUP BY np.Affinity ");
        	rs = preparedStatement.executeQuery();
        	
        	while( rs.next() )
        	{
        		followups.put(rs.getInt("Affinity"), new NPCPhrase(rs.getString("Text"), rs.getInt("ID_NPCPhrase"), 
        				plugin, rs.getBoolean("IsDynamic"), npcData));
        		updateQuery += " OR ID_NPCPhrase = " + rs.getInt("ID_NPCPhrase");
        		//System.out.println("Loaded followup greeting (Aff. " + rs.getInt("Affinity") + "): " + rs.getString("Text"));
        	}

    		// Update the database to show that the phrases have been used
        	preparedStatement = conn.prepareStatement(
    				"UPDATE NPCPhrases SET Uses = Uses + 1 WHERE " + updateQuery);
    		preparedStatement.executeUpdate();
    		
    		// Build the SpeechData object
    		speechData = new SpeechData(phrases, greeting, followups, npcData, plugin);
    		
    		return speechData;
        		        	
    	} catch (Exception e) {
    		System.out.println("Error in NPCQueries.getPhrases(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
	}


	// Deletes an NPCInstance
	public void disableInstance(int id) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		
    	try
    	{    		
    		conn = rageDB.getConnection();
    		preparedStatement = conn.prepareStatement(
    				"UPDATE NPCInstances SET IsDisabled = 1 WHERE ID_NPCInstance = " + id);;   
    		preparedStatement.executeUpdate();
        		        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in NPCQueries.disableInstance(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
	}

	// Submits a player-written NPCPhrase
	public boolean submitPhrase(NPCTown town, PlayerData playerData, String message) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    int id_NPCTown;
		
    	try
    	{    		
    		// Make sure there is proper capitalization and punctuation
			message = message.trim();
			message = message.substring(0, 1).toUpperCase() + message.substring(1);
			if( !message.substring(message.length()-1).equals(".") && 
				!message.substring(message.length()-1).equals("?") &&
				!message.substring(message.length()-1).equals("!"))
				message += ".";
    					
    		if( town == null )
    			id_NPCTown = 0;
    		else
    			id_NPCTown = town.getID();
    		
    		conn = rageDB.getConnection();
    		preparedStatement = conn.prepareStatement(
    				"INSERT INTO NPCPhrases (Text, ID_NPCTown, IsApproved, ID_Player_Writer, Timestamp) " +
    				"VALUES ('" + message.replace("'", "''") + "', " + id_NPCTown + ", 0, " + playerData.id_Player + ", NOW())");   
    		preparedStatement.executeUpdate();
    		
    		return true;
        		        		        	
    	} catch (Exception e) {
    		System.out.println("Error in NPCQueries.submitPhrase(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return false;
    	
	}
	

}
