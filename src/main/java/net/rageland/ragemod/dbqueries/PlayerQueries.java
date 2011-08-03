package net.rageland.ragemod.dbqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Location;

import net.rageland.ragemod.RageDB;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTowns;

public class PlayerQueries {

	private RageDB rageDB;
	private RageMod plugin;
	
	private String playerQuery = 
			"SELECT p.ID_Player, p.Name, IFNULL(p.ID_Faction, 0) as ID_Faction, p.IsMember, p.MemberExpiration, p.Bounty, p.ExtraBounty, " +
    		"   (p.Home_XCoord IS NOT NULL) AS Home_IsSet, p.Home_XCoord, p.Home_YCoord, p.Home_ZCoord, p.Home_LastUsed, " +
    		"	(p.Spawn_XCoord IS NOT NULL) AS Spawn_IsSet, p.Spawn_XCoord, p.Spawn_YCoord, p.Spawn_ZCoord, p.Spawn_LastUsed, " +
    		"	IFNULL(pt.TownName, '') as TownName, p.IsMayor, IFNULL(p.LogonMessageQueue, '') as LogonMessageQueue " +
    		"FROM Players p " +
    		"LEFT JOIN PlayerTowns pt ON p.ID_PlayerTown = pt.ID_PlayerTown ";
	
	public PlayerQueries(RageDB rageDB, RageMod plugin)
	{
		this.rageDB = rageDB;
		this.plugin = plugin;
	}
	
	// Load data from Players table on login if existing player - create new row if not 
	public PlayerData playerLogin(String playerName)
    {
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    
		PlayerData playerData = null;  		
		
    	try
    	{
    		conn = rageDB.getConnection();
        	String selectQuery = playerQuery +
        		"WHERE p.Name = '" + playerName + "'";
    		
    		preparedStatement = conn.prepareStatement(selectQuery);	        		        	
        	rs = preparedStatement.executeQuery();
        	
        	// Test to see if result set was empty
        	if( rs.next() )
        	{
        		playerData = fillPlayerData(rs);
        		
        		// Set LastLogin time  TODO: Does the playerLog table deprecate this?  I'm thinking yes...
        		preparedStatement = conn.prepareStatement("UPDATE Players SET LastLogin = NOW() WHERE Name = '" + playerName + "'");
        		preparedStatement.executeUpdate();
        	}
        	else
        	{
        		// Insert new data into DB - default DB values will take care of the data
        		preparedStatement = conn.prepareStatement("INSERT INTO Players (Name, LastLogin) VALUES ('" + playerName + "', NOW())");
        		preparedStatement.executeUpdate();
        		
        		// Pull the DB-created defaults into memory
        		preparedStatement = conn.prepareStatement(selectQuery);		
        		rs = preparedStatement.executeQuery();
        		
        		rs.next();
        		playerData = fillPlayerData(rs);
        	}	        	
        	
        	// Log the player's logon time
        	preparedStatement = conn.prepareStatement("INSERT INTO PlayerLog (ID_Player, LogonTime) VALUES ('" + playerData.id_Player + "', NOW())");
    		preparedStatement.executeUpdate();
        	
        	return playerData;				
        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in RageDB.PlayerLogin(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
		
		return null;
    }
	
	// Fill the PlayerData class with data from a result set
	private PlayerData fillPlayerData(ResultSet rs) throws SQLException
	{
		PlayerData playerData = new PlayerData();
		
		playerData.id_Player = rs.getInt("ID_Player");
		playerData.name = rs.getString("Name");
		playerData.id_Faction = rs.getInt("ID_Faction");
		playerData.memberExpiration = rs.getDate("MemberExpiration");
		playerData.bounty = rs.getFloat("Bounty");
		playerData.extraBounty = rs.getFloat("ExtraBounty");
		playerData.townName = rs.getString("TownName");
		playerData.isMayor = rs.getBoolean("IsMayor");
		
		// Set the player's isMember boolean based on the expiration date
		if( playerData.memberExpiration == null )
			playerData.isMember = false;
		else
			playerData.isMember = playerData.memberExpiration.getTime() > Util.now().getTime();
		
		playerData.home_IsSet = rs.getBoolean("Home_IsSet");
		playerData.home_X = rs.getInt("Home_XCoord");
		playerData.home_Y = rs.getInt("Home_YCoord");
		playerData.home_Z = rs.getInt("Home_ZCoord");
		playerData.home_LastUsed = rs.getTimestamp("Home_LastUsed");
		
		playerData.spawn_IsSet = rs.getBoolean("Spawn_IsSet");
		playerData.spawn_X = rs.getInt("Spawn_XCoord");
		playerData.spawn_Y = rs.getInt("Spawn_YCoord");
		playerData.spawn_Z = rs.getInt("Spawn_ZCoord");
		playerData.spawn_LastUsed = rs.getTimestamp("Spawn_LastUsed");
		
		playerData.logonMessageQueue = rs.getString("LogonMessageQueue");
		
		playerData.lots = rageDB.lotQueries.getLots(playerData.id_Player);
        playerData.lotPermissions = rageDB.lotQueries.getLotPermissions(playerData.id_Player);
		
		if( playerData.townName.equals("") )
			playerData.treasuryBalance = 0;
		else
		{
			if(plugin.playerTowns.get(playerData.townName) != null)
				playerData.treasuryBalance = this.getPlayerTreasuryBalance(playerData.id_Player, plugin.playerTowns.get(playerData.townName).id_PlayerTown);
			else 
			{
				playerData.townName = "";
				playerData.treasuryBalance = 0;
			}
			
		}
			
		
    	return playerData;
	}	
	
	// Return the result of the player's transaction history with their current town
	public double getPlayerTreasuryBalance(int id_Player, int id_PlayerTown)
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;    
	
    	try
    	{
    		conn = rageDB.getConnection();
        	String selectQuery = 
        		"SELECT SUM(Amount) as Amount FROM TreasuryTransactions WHERE ID_Player = " + id_Player + " AND ID_PlayerTown = " + id_PlayerTown;
    		
    		preparedStatement = conn.prepareStatement(selectQuery);	        		        	
        	rs = preparedStatement.executeQuery();
        	
            rs.next();
        	return rs.getDouble("Amount");	
        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in RageDB.getPlayerTreasuryBalance(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return -1;
	}    
	
	// Load data from Players table for specified player; returns NULL if not found 
	public PlayerData playerFetch(String playerName)
    {
		PlayerData playerData = null;
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;		
		
    	try
    	{
    		conn = rageDB.getConnection();
    		String selectQuery = playerQuery +
            		"WHERE p.Name = '" + playerName + "'";
    		
    		preparedStatement = conn.prepareStatement(selectQuery);	        		        	
        	rs = preparedStatement.executeQuery();
        	
        	// Test to see if result set was empty - return null if not 
        	if( rs.next() )
        	{
        		playerData = fillPlayerData(rs);
	        	return playerData;
        	}
        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in RageDB.PlayerFetch(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
		
		return null;
    }
	
	// Update the database with the player data stored in memory (skips town info)
	public void updatePlayer(PlayerData playerData)
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		
		String updateString = "";
		try
    	{
			conn = rageDB.getConnection();
    		// Update the Players table to create the town association
    		updateString = 
				"UPDATE Players SET " +
				"ID_Faction = " + (playerData.id_Faction == 0 ? "null" : playerData.id_Faction) + ", " +
				"Bounty = " + playerData.bounty + ", " +
				"ExtraBounty = " + playerData.extraBounty + ", " +
				"LogonMessageQueue = '" + playerData.logonMessageQueue + "', " +
    			"Home_LastUsed = " + (playerData.home_LastUsed == null ? "null" : "'" + playerData.home_LastUsed + "'") + ", " +
    			"Spawn_LastUsed = " + (playerData.spawn_LastUsed == null ? "null" : "'" + playerData.spawn_LastUsed + "'") + ", ";
    		
    		if( playerData.home_IsSet )
    		{
    			updateString += "Home_XCoord = " + playerData.home_X + ", " +
					"Home_YCoord = " + playerData.home_Y + ", " +
					"Home_ZCoord = " + playerData.home_Z + ", ";
    		}
    		else
    		{
    			updateString += "Home_XCoord = NULL, " +
					"Home_YCoord = NULL, " +
					"Home_ZCoord = NULL, ";
    		}
    		if( playerData.spawn_IsSet )
    		{
    			updateString += "Spawn_XCoord = " + playerData.spawn_X + ", " +
					"Spawn_YCoord = " + playerData.spawn_Y + ", " +
					"Spawn_ZCoord = " + playerData.spawn_Z + " ";
    		}
    		else
    		{
    			updateString += "Spawn_XCoord = NULL, " +
					"Spawn_YCoord = NULL, " +
					"Spawn_ZCoord = NULL ";
    		}
    		
    		updateString += "WHERE ID_Player = " + playerData.id_Player;
    		
    		preparedStatement = conn.prepareStatement(updateString);
    		preparedStatement.executeUpdate();	
        		        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in RageDB.updatePlayer(): " + e.getMessage());
    		System.out.println("updateString: " + updateString);
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
	}	
	
	// Returns all spawn points held by residents of a town, to see if any are too close
	public HashMap<String, Location> getSpawnLocations(int id_PlayerTown) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;   
		HashMap<String, Location> spawns = new HashMap<String, Location>();
		
    	try
    	{
    		conn = rageDB.getConnection();
    		preparedStatement = conn.prepareStatement(
    				"SELECT p.Name, p.Spawn_XCoord, p.Spawn_YCoord, p.Spawn_ZCoord " +
    				"FROM Players p " +
    				"WHERE p.Spawn_XCoord IS NOT NULL AND ID_PlayerTown = " + id_PlayerTown);
    		rs = preparedStatement.executeQuery();
        	
        	while ( rs.next() ) 
        	{	        	
        		spawns.put(rs.getString("Name"), 
        				new Location(plugin.getServer().getWorld("world"), 
        						rs.getInt("Spawn_XCoord"),
        						rs.getInt("Spawn_YCoord"),
        						rs.getInt("Spawn_ZCoord")));	        		
        	}
        		
        	return spawns;	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in RageDB.getSpawnLocations(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
	}
	
	// Return the amount the player has donated in the past month
	public int getRecentDonations(int id_Player)
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		try
    	{
			conn = rageDB.getConnection();
        	preparedStatement = conn.prepareStatement(
        		"SELECT SUM(Amount) as Amount FROM Donations WHERE ID_Player = " + id_Player + 
        		" AND DATE_SUB(CURDATE(),INTERVAL 30 DAY) <= Date");
        		
        	rs = preparedStatement.executeQuery();
        	rs.next();
        	
        	return rs.getInt("Amount");  			        	
    	} 
		catch (Exception e) {
    		System.out.println("Error in RageDB.getRecentDonations(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
		
		return 0;
	}

	// Log the player's logoff time
	public void playerLogoff(int id_Player) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		try
    	{
			conn = rageDB.getConnection();
			
			// Get the ID of the latest entry
			preparedStatement = conn.prepareStatement(
	        		"SELECT ID_PlayerLog FROM PlayerLog WHERE ID_Player = " + id_Player + " ORDER BY ID_PlayerLog DESC LIMIT 1");
        	rs = preparedStatement.executeQuery();
        	rs.next();
        	int id_PlayerLog = rs.getInt("ID_PlayerLog");
			
        	// Update the database
        	preparedStatement = conn.prepareStatement("UPDATE PlayerLog SET LogoffTime = NOW() WHERE ID_PlayerLog = " + id_PlayerLog);
    		preparedStatement.executeUpdate();       	
    	} 
		catch (Exception e) {
    		System.out.println("Error in RageDB.playerLogoff(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
	}
	
}
