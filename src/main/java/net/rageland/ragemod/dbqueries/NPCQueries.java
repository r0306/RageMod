package net.rageland.ragemod.dbqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.NPCData;
import net.rageland.ragemod.data.NPCInstance;
import net.rageland.ragemod.data.NPCInstance.NPCType;
import net.rageland.ragemod.data.Location2D;
import net.rageland.ragemod.data.NPCLocation;
import net.rageland.ragemod.data.NPCLocationPool;
import net.rageland.ragemod.data.NPCPool;
import net.rageland.ragemod.data.NPCPhrase;
import net.rageland.ragemod.data.NPCTown;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.database.RageDB;
import net.rageland.ragemod.text.Language;

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
				"SELECT ID_NPC, ID_NPCRace, Name, IsBilingual, ID_NPCTown " +
				"FROM NPCs");
        	
        	rs = preparedStatement.executeQuery();
        	
        	while ( rs.next() ) 
        	{	        		
        		npc = new NPCData();
        		npc.id_NPC = rs.getInt("ID_NPC");
        		npc.id_NPCRace = rs.getInt("ID_NPCRace");
        		npc.name = rs.getString("Name");
        		npc.isBilingual = rs.getBoolean("IsBilingual");
        		npc.id_NPCTown = rs.getInt("ID_NPCTown");

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
		
    	try
    	{    		
    		conn = rageDB.getConnection();
    		// Insert the new town into the PlayerTowns table
    		preparedStatement = conn.prepareStatement(
    				"INSERT INTO NPCs (ID_NPCRace, Name, IsBilingual, ID_NPCTown, CreateDate, ID_Player_Creator) " +
    				"VALUES (" + npc.id_NPCRace + ", '" + npc.name + "', " + npc.isBilingual + ", " +
    						npc.id_NPCTown + ", NOW(), " + id_Player_Creator + ")",
    				Statement.RETURN_GENERATED_KEYS);   
    		preparedStatement.executeUpdate();
    		
    		// Retrieve the new auto-increment town ID 
    		rs = preparedStatement.getGeneratedKeys();
    		rs.next();
    		int npcID = rs.getInt(1);
    		
    		return npcID;
        		        		        	
    	} catch (SQLException e) {
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
    		// Insert the new town into the PlayerTowns table
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
	public ArrayList<NPCPhrase> getPhrases(int id_NPCRace, int id_NPCTown, int id_NPC) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    ArrayList<NPCPhrase> phrases = new ArrayList<NPCPhrase>();
	    ArrayList<NPCPhrase> phrasePool = new ArrayList<NPCPhrase>();
	    String updateQuery = "";
		
    	try
    	{
    		conn = rageDB.getConnection();
        	preparedStatement = conn.prepareStatement(
				"SELECT ID_NPCPhrase, Text " +
				"FROM NPCPhrases " +
				"WHERE (ID_NPCRace = 0 OR ID_NPCRace = " + id_NPCRace + ") AND " +
						"(ID_NPCTown = 0 OR ID_NPCTown = " + id_NPCTown + ") AND " +
						"(ID_NPC = 0 OR ID_NPC = " + id_NPC + ") " +
				"ORDER BY Uses " +
				"LIMIT " + plugin.config.NPC_PHRASE_POOL);
        	
        	rs = preparedStatement.executeQuery();
        	
        	// Load all possible phrases into the pool for random selection
        	while( rs.next() )
        	{
        		phrasePool.add(new NPCPhrase(rs.getString("Text"), rs.getInt("ID_NPCPhrase")));
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
        	
        	if( !updateQuery.equals("") )
        	{
        		// Update the database to show that the phrases have been used
            	preparedStatement = conn.prepareStatement(
        				"UPDATE NPCPhrases SET Uses = Uses + 1 WHERE " + updateQuery);
        		preparedStatement.executeUpdate();
        		
        		return phrases;
        	}
        		        	
    	} catch (Exception e) {
    		System.out.println("Error in NPCQueries.getPhrases(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
	}

	// Creates a new NPC town
	public int createNPCTown(Player player, String name, int x1, int z1, int x2, int z2, int level) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		
    	try
    	{    		
    		conn = rageDB.getConnection();
    		// Insert the new town into the PlayerTowns table
    		preparedStatement = conn.prepareStatement(
    				"INSERT INTO NPCTowns (Name, XCoordA, ZCoordA, XCoordB, ZCoordB, ID_NPCRace, TownLevel) " +
    				"VALUES ('" + name + "', " + x1 + ", " + z1 + ", " + x2 + ", " + z2 + ", 5, " + level + ")",
    				Statement.RETURN_GENERATED_KEYS);   
    		preparedStatement.executeUpdate();
    		
    		// Retrieve the new auto-increment town ID 
    		rs = preparedStatement.getGeneratedKeys();
    		rs.next();
    		int townID = rs.getInt(1);
    		
       		return townID;
        		        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in NPCQueries.createNPCTown(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return -1;
	}
	
	// Loads all NPC towns from the database
	public HashMap<String, NPCTown> loadNPCTowns() 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    HashMap<String, NPCTown> towns = new HashMap<String, NPCTown>();
	    
		NPCTown currentTown = null;
		
    	try
    	{
    		conn = rageDB.getConnection();
        	preparedStatement = conn.prepareStatement(
				"SELECT ID_NPCTown, Name, XCoordA, ZCoordA, XCoordB, ZCoordB, ID_NPCRace, TownLevel " +
				"FROM NPCTowns");
        	
        	rs = preparedStatement.executeQuery();
        	
        	while ( rs.next() ) 
        	{	        		
        		currentTown = new NPCTown(plugin, rs.getInt("ID_NPCTown"), rs.getString("Name"), plugin.getServer().getWorld("world"));
        		currentTown.id_NPCRace = rs.getInt("ID_NPCRace");
        		currentTown.townLevel = plugin.config.townLevels.get(rs.getInt("TownLevel"));
        		
        		currentTown.createRegion((double)rs.getInt("XCoordA"), (double)rs.getInt("ZCoordA"),
        				(double)rs.getInt("XCoordB"),(double)rs.getInt("ZCoordB"));
        		
        		towns.put(currentTown.getName().toLowerCase(), currentTown);
        	}			
        	
        	return towns;
        		        	
    	} catch (Exception e) {
    		System.out.println("Error in TownQueries.loadNPCTowns(): " + e.getMessage());
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
    		// Insert the new town into the PlayerTowns table
    		preparedStatement = conn.prepareStatement(
    				"UPDATE NPCInstances SET IsDisabled = 1 WHERE ID_NPCInstance = " + id);;   
    		preparedStatement.executeUpdate();
        		        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in NPCQueries.disableInstance(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
	}
	

}
