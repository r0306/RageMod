package net.rageland.ragemod.dbqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCTown;
import net.rageland.ragemod.database.RageDB;

public class NPCTownQueries 
{
	private RageDB rageDB;
	private RageMod plugin;

	public NPCTownQueries(RageDB rageDB, RageMod plugin) 
	{
		this.rageDB = rageDB;
		this.plugin = plugin;
	}
	
	// Creates a new NPC town
	public int create(Player player, String name, int x1, int z1, int x2, int z2, int level, int id_Race) 
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
    				"VALUES ('" + name + "', " + x1 + ", " + z1 + ", " + x2 + ", " + z2 + ", " + id_Race + ", " + level + ")",
    				Statement.RETURN_GENERATED_KEYS);   
    		preparedStatement.executeUpdate();
    		
    		// Retrieve the new auto-increment town ID 
    		rs = preparedStatement.getGeneratedKeys();
    		rs.next();
    		int townID = rs.getInt(1);
    		
       		return townID;
        		        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in NPCTownQueries.create(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return -1;
	}

	// Loads all NPC towns from the database
	public HashMap<String, NPCTown> loadAll() 
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
				"SELECT n.ID_NPCTown, IFNULL(p.Name, '') as Steward, n.Name, n.XCoordA, n.ZCoordA, n.XCoordB, n.ZCoordB, n.ID_NPCRace, n.TownLevel " +
				"FROM NPCTowns n " +
				"LEFT JOIN Players p ON p.ID_Player = n.ID_Player_Steward");
        	
        	rs = preparedStatement.executeQuery();
        	
        	while ( rs.next() ) 
        	{	        		
        		currentTown = new NPCTown(plugin, rs.getInt("ID_NPCTown"), rs.getString("Name"), plugin.getServer().getWorld("world"), 
        									rs.getString("Steward"));
        		currentTown.id_NPCRace = rs.getInt("ID_NPCRace");
        		currentTown.townLevel = plugin.config.townLevels.get(rs.getInt("TownLevel"));
        		
        		currentTown.createRegion((double)rs.getInt("XCoordA"), (double)rs.getInt("ZCoordA"),
        				(double)rs.getInt("XCoordB"),(double)rs.getInt("ZCoordB"));
        		
        		currentTown.buildPermissions = this.loadBuildPermissions(currentTown.getID());
        		
        		towns.put(currentTown.getName().toLowerCase(), currentTown);
        	}			
        	
        	return towns;
        		        	
    	} catch (Exception e) {
    		System.out.println("Error in NPCTownQueries.loadAll(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
	}

	// Loads a list of build permissions
	private ArrayList<String> loadBuildPermissions(int id_NPCTown) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		ArrayList<String> buildPermissions = new ArrayList<String>();
	
    	try
    	{
    		conn = rageDB.getConnection();
        	String selectQuery = 
        		"SELECT p.Name as Name FROM NPCTownPermissions np " +
        		"INNER JOIN Players p ON p.ID_Player = np.ID_Player " +
        		"WHERE np.ID_NPCTown = " + id_NPCTown;
    		
    		preparedStatement = conn.prepareStatement(selectQuery);	        		        	
        	rs = preparedStatement.executeQuery();
        	
            while( rs.next() )
        	{
            	buildPermissions.add(rs.getString("Name"));	
        	}
            return buildPermissions;
        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in NPCTownQueries.loadBuildPermissions(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
	}

	// Updates all of the town's information in the database
	public void update(NPCTown npcTown) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		try
    	{
			conn = rageDB.getConnection();
    		// Update all town info
    		preparedStatement = conn.prepareStatement(
    				"UPDATE NPCTowns SET " +
    				"ID_Player_Steward = " + plugin.players.get(npcTown.steward).id_Player + ", " + 
    				"ID_NPCRace = " + npcTown.id_NPCRace + ", " + 
    				"TownLevel = " + (npcTown.townLevel.level) + " " + 
    				"WHERE ID_NPCTown = " + npcTown.getID());
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in NPCTownQueries.update(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
		
	}

	// Adds to the NPCTownPermissions table
	public void allow(int id_NPCTown, int id_Player) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    
		try
    	{
			conn = rageDB.getConnection();
			preparedStatement = conn.prepareStatement(
    				"INSERT INTO NPCTownPermissions (ID_NPCTown, ID_Player) VALUES (" + id_NPCTown + ", " + id_Player + ")");
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in NPCTownQueries.allow(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}	
	}

	// Removes from the NPCTownPermissions table
	public void disallow(int id_NPCTown, int id_Player) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		
		try
    	{
			conn = rageDB.getConnection();
			// An ID of 0 indicates all players
			if( id_Player == 0 )
    		{
				preparedStatement = conn.prepareStatement(
	    				"DELETE FROM NPCTownPermissions WHERE ID_NPCTown = " + id_NPCTown);
    		}
			else
			{
				preparedStatement = conn.prepareStatement(
	    				"DELETE FROM NPCTownPermissions WHERE ID_NPCTown = " + id_NPCTown + " AND ID_Player = " + id_Player);
			}
			
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in LotQueries.disallow(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}	
		
	}
}
