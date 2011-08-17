package net.rageland.ragemod.dbqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPC;
import net.rageland.ragemod.data.NPCLocation;
import net.rageland.ragemod.data.NPCPool;
import net.rageland.ragemod.data.PlayerData;
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

	// Loads all NPCLocations in the database into memory
	public HashMap<Integer, NPCLocation> loadNPCLocations() 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    HashMap<Integer, NPCLocation> locations = new HashMap<Integer, NPCLocation>();
	    
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
        				rs.getFloat("Pitch"));
        		
        		location.setIDs(rs.getInt("ID_NPCLocation"), rs.getInt("ID_NPCTown"), rs.getInt("ID_NPCRace"));
        		locations.put(location.getID(), location);
        	}			
        	
        	return locations;
        		        	
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
		NPC npc = null;
		
    	try
    	{
    		conn = rageDB.getConnection();
        	preparedStatement = conn.prepareStatement(
				"SELECT ID_NPC, ID_NPCRace, Name, IsBilingual, ID_NPCTown " +
				"FROM NPCs");
        	
        	rs = preparedStatement.executeQuery();
        	
        	while ( rs.next() ) 
        	{	        		
        		npc = new NPC();
        		npc.id_NPC = rs.getInt("ID_NPC");
        		npc.id_NPCRace = rs.getInt("ID_NPC");
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
	public int createNPC(NPC npc, int id_Player_Creator) 
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
//	public int createInstance(int id_NPC, int id_NPCLocation, int ttlMinutes, int id_Player_Creator) 
//	{
//		Connection conn = null;
//	    PreparedStatement preparedStatement = null;
//	    ResultSet rs = null;
//		
//    	try
//    	{    		
//    		conn = rageDB.getConnection();
//    		// Insert the new town into the PlayerTowns table
//    		preparedStatement = conn.prepareStatement(
//    				"INSERT INTO NPCInstancess (ID_NPC, ID_NPCLocation, IsBilingual, ID_NPCTown, CreateDate, ID_Player_Creator) " +
//    				"VALUES (" + npc.id_NPCRace + ", '" + npc.name + "', " + npc.isBilingual + ", " +
//    						npc.id_NPCTown + ", NOW(), " + id_Player_Creator + ")",
//    				Statement.RETURN_GENERATED_KEYS);   
//    		preparedStatement.executeUpdate();
//    		
//    		// Retrieve the new auto-increment town ID 
//    		rs = preparedStatement.getGeneratedKeys();
//    		rs.next();
//    		int npcID = rs.getInt(1);
//    		
//    		return npcID;
//        		        		        	
//    	} catch (SQLException e) {
//    		System.out.println("Error in NPCQueries.createNPC(): " + e.getMessage());
//		} finally {
//			rageDB.close(rs, preparedStatement, conn);
//		}
//    	
//    	System.out.println("Error: NPCQueries.createNPC() returned -1");
//    	return -1;
//	}
	

}
