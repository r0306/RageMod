package net.rageland.ragemod.dbqueries;

// TODO: Consider fetching all players that have logged in in the last 24 hours on server startup.  May save some lag

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Permits;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.Towns;
import net.rageland.ragemod.database.RageDB;

public class PlayerQueries {

	private RageDB rageDB;
	private RageMod plugin;
	
	private String playerQuery = 
			"SELECT p.ID_Player, p.Name, IFNULL(p.ID_Faction, 0) as ID_Faction, p.IsMember, p.MemberExpiration, p.Bounty, p.ExtraBounty, " +
    		"   (p.Home_XCoord IS NOT NULL) AS Home_IsSet, p.Home_XCoord, p.Home_YCoord, p.Home_ZCoord, p.Home_LastUsed, " +
    		"	(p.Spawn_XCoord IS NOT NULL) AS Spawn_IsSet, p.Spawn_XCoord, p.Spawn_YCoord, p.Spawn_ZCoord, p.Spawn_LastUsed, " +
    		"	IFNULL(pt.TownName, '') as TownName, p.IsMayor, IFNULL(p.LogonMessageQueue, '') as LogonMessageQueue, p.TreasuryBlocks," +
    		"	p.LanguageSkill1, p.LanguageSkill2, p.LanguageSkill3, p.LanguageSkill4, IFNULL(nt.Name, '') as NPCTown " +
    		"FROM Players p " +
    		"LEFT JOIN PlayerTowns pt ON p.ID_PlayerTown = pt.ID_PlayerTown " +
    		"LEFT JOIN NPCTowns nt ON p.ID_Player = nt.ID_Player_Steward ";
	
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
		PlayerData playerData = new PlayerData(plugin);
		
		// Load basic values
		playerData.id_Player = rs.getInt("ID_Player");
		playerData.name = rs.getString("Name");
		playerData.id_Faction = rs.getInt("ID_Faction");
		playerData.memberExpiration = rs.getDate("MemberExpiration");
		playerData.bounty = rs.getFloat("Bounty");
		playerData.extraBounty = rs.getFloat("ExtraBounty");
		playerData.townName = rs.getString("TownName");
		playerData.isMayor = rs.getBoolean("IsMayor");
		playerData.treasuryBlocks = rs.getInt("TreasuryBlocks");
		playerData.home_LastUsed = rs.getTimestamp("Home_LastUsed");
		playerData.spawn_LastUsed = rs.getTimestamp("Spawn_LastUsed");
		playerData.logonMessageQueue = rs.getString("LogonMessageQueue");
		
		// Set the player's isMember boolean based on the expiration date
		if( playerData.memberExpiration == null )
			playerData.isMember = false;
		else
			playerData.isMember = playerData.memberExpiration.getTime() > Util.now().getTime();
		
		if( rs.getBoolean("Home_IsSet") )
		{
			playerData.setHome(new Location(plugin.getServer().getWorld("world"), rs.getInt("Home_XCoord") + .5, rs.getInt("Home_YCoord"), rs.getInt("Home_ZCoord") + .5));
		}
		if( rs.getBoolean("Spawn_IsSet") )
		{
			playerData.setSpawn(new Location(plugin.getServer().getWorld("world"), rs.getInt("Spawn_XCoord") + .5, rs.getInt("Spawn_YCoord"), rs.getInt("Spawn_ZCoord") + .5));
		}
		
		// Load NPCTown, if any
		if( !rs.getString("NPCTown").equals("") )
		{
			playerData.isSteward = true;
			playerData.npcTownName = rs.getString("NPCTown");
		}
		
		// Load arrays of data
		playerData.lots = rageDB.lotQueries.getLots(playerData.id_Player);
        playerData.lotPermissions = rageDB.lotQueries.getLotPermissions(playerData.id_Player);
        playerData.permits = getPermits(playerData.id_Player);
        playerData.setInteractions(getInteractions(playerData.id_Player));
        playerData.setAffinities(getAffinities(playerData.id_Player));
        
        // Load language skill
        playerData.setLanguageSkill(1, rs.getInt("LanguageSkill1"));
        playerData.setLanguageSkill(2, rs.getInt("LanguageSkill2"));
        playerData.setLanguageSkill(3, rs.getInt("LanguageSkill3"));
        playerData.setLanguageSkill(4, rs.getInt("LanguageSkill4"));
        playerData.setLanguageSkill(5, 100);		// Because everyone can speak English! :D
        
		
		if( playerData.townName.equals("") )
			playerData.treasuryBalance = 0;
		else
		{
			if(plugin.towns.get(playerData.townName) != null)
				playerData.treasuryBalance = this.getPlayerTreasuryBalance(playerData.id_Player, plugin.towns.get(playerData.townName).getID());
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
    		System.out.println("Error in PlayerQueries.getPlayerTreasuryBalance(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return -1;
	}    
	
	// Return all permits acquired by the player
	public Permits getPermits(int id_Player)
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null; 
	    Permits permits = new Permits();
	
    	try
    	{
    		conn = rageDB.getConnection();
        	String selectQuery = 
        		"SELECT Type FROM Permits WHERE ID_Player_Holder = " + id_Player + " AND Expiration > NOW()";
    		
    		preparedStatement = conn.prepareStatement(selectQuery);	        		        	
        	rs = preparedStatement.executeQuery();
        	
        	while( rs.next() )
        	{
        		if( rs.getString("Type").equals("CAPITOL") )
        			permits.capitol = true;
        	}	
        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in PlayerQueries.getPermits(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return permits;
	} 
	
	// Return all permits acquired by the player
	public HashSet<Integer> getInteractions(int id_Player)
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null; 
	    HashSet<Integer> interactions = new HashSet<Integer>();
	
    	try
    	{
    		conn = rageDB.getConnection();
        	String selectQuery = 
        		"SELECT pni.ID_NPCInstance FROM Player_NPCInstance pni " +
        		"INNER JOIN NPCInstances ni " +
        		"WHERE pni.ID_Player = " + id_Player + 
        		" AND (ni.DespawnTime IS NULL OR ni.DespawnTime > NOW()) AND ni.IsDisabled = 0";
    		
    		preparedStatement = conn.prepareStatement(selectQuery);	        		        	
        	rs = preparedStatement.executeQuery();
        	
        	while( rs.next() )
        		interactions.add(rs.getInt("ID_NPCInstance"));
        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in PlayerQueries.getInteractions(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return interactions;
	} 
	
	// Return all permits acquired by the player
	public HashMap<Integer, Float> getAffinities(int id_Player)
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null; 
	    HashMap<Integer, Float> affinities = new HashMap<Integer, Float>();
	
    	try
    	{
    		conn = rageDB.getConnection();
        	String selectQuery = 
        		"SELECT ID_NPC, Affinity FROM Player_NPC " +
        		"WHERE ID_Player = " + id_Player;
    		
    		preparedStatement = conn.prepareStatement(selectQuery);	        		        	
        	rs = preparedStatement.executeQuery();
        	
        	while( rs.next() )
        		affinities.put(rs.getInt("ID_NPC"), rs.getFloat("Affinity"));
        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in PlayerQueries.getAffinities(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return affinities;
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
    		System.out.println("Error in PlayerQueries.PlayerFetch(): " + e.getMessage());
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
	    Location home = playerData.getHome();
	    Location spawn = playerData.getSpawn();
		
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
				"TreasuryBlocks = " + playerData.treasuryBlocks + ", " +
				"LanguageSkill1 = " + playerData.getLanguageSkill(1) + ", " +
				"LanguageSkill2 = " + playerData.getLanguageSkill(2) + ", " +
				"LanguageSkill3 = " + playerData.getLanguageSkill(3) + ", " +
				"LanguageSkill4 = " + playerData.getLanguageSkill(4) + ", " +
				
    			"Home_LastUsed = " + (playerData.home_LastUsed == null ? "null" : "'" + playerData.home_LastUsed + "'") + ", " +
    			"Spawn_LastUsed = " + (playerData.spawn_LastUsed == null ? "null" : "'" + playerData.spawn_LastUsed + "'") + ", ";
    		
    		if( home != null )
    		{
    			updateString += "Home_XCoord = " + (int)(home.getX()-.1) + ", " +		// The -.1 is to keep .5 from rounding up over and over
					"Home_YCoord = " + (int)(home.getY()-.1) + ", " +
					"Home_ZCoord = " + (int)(home.getZ()-.1) + ", ";
    		}
    		else
    		{
    			updateString += "Home_XCoord = NULL, " +
					"Home_YCoord = NULL, " +
					"Home_ZCoord = NULL, ";
    		}
    		if( spawn != null )
    		{
    			updateString += "Spawn_XCoord = " + (int)(spawn.getX()-.1) + ", " +
					"Spawn_YCoord = " + (int)(spawn.getY()-.1) + ", " +
					"Spawn_ZCoord = " + (int)(spawn.getZ()-.1) + " ";
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
    		System.out.println("Error in PlayerQueries.updatePlayer(): " + e.getMessage());
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
    		System.out.println("Error in PlayerQueries.getSpawnLocations(): " + e.getMessage());
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
    		System.out.println("Error in PlayerQueries.getRecentDonations(): " + e.getMessage());
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
    		System.out.println("Error in PlayerQueries.playerLogoff(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
	}

	// Gives a permit to a player
	public void grantPermit(int id_Player_Granter, int id_Player_Holder, String type, int numberOfDays) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    
		try
    	{
			conn = rageDB.getConnection();
    		
    		preparedStatement = conn.prepareStatement(
    				"INSERT INTO Permits (ID_Player_Holder, ID_Player_Granter, Type, Expiration) VALUES (" +
    				id_Player_Holder + ", " + id_Player_Granter + ", '" + type + "', ADDDATE(NOW(), " + numberOfDays + "))");
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in PlayerQueries.grantPermit(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
		
	}

	// Records all NPC interactions the player had during their session
	public void recordInstances(PlayerData playerData) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    boolean firstEntry = true;
	    String query = "";
	    
		try
    	{
			// If there are no new interactions, no need for update
			if( playerData.getNewInteractions().size() == 0 )
				return;
			
			conn = rageDB.getConnection();
			query = "INSERT INTO Player_NPCInstance (ID_Player, ID_NPCInstance, Timestamp) VALUES ";
			
			// Build a list of all new interactions
			for( int instanceID : playerData.getNewInteractions() )
			{
				if( !firstEntry )
					query += ", ";
				else
					firstEntry = false;
				query += "(" + playerData.id_Player + ", " + instanceID + ", NOW())";
			}
			
    		preparedStatement = conn.prepareStatement(query);
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in PlayerQueries.recordInstances(): " + e.getMessage() + " Query: " + query);
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}	
	}
	
	// Records all NPC interactions the player had during their session
	public void recordAffinity(PlayerData playerData) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    boolean firstEntry = true;
	    String query = "";
	    
		try
    	{
			// If there is no affinity, no need for update
			if( playerData.getAffinities().size() == 0 )
				return;
			
			conn = rageDB.getConnection();
			
			// Clear the existing affinity data
			preparedStatement = conn.prepareStatement("DELETE FROM Player_NPC WHERE ID_Player = " + playerData.id_Player);
    		preparedStatement.executeUpdate();	
			
			query = "INSERT INTO Player_NPC (ID_Player, ID_NPC, Affinity) VALUES ";
			
			// Build a list of all new interactions
			for( int npcID : playerData.getAffinities().keySet() )
			{
				if( !firstEntry )
					query += ", ";
				else
					firstEntry = false;
				query += "(" + playerData.id_Player + ", " + npcID + ", " + playerData.getAffinities().get(npcID) + ")";
			}
			
    		preparedStatement = conn.prepareStatement(query);
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in PlayerQueries.recordAffinity(): " + e.getMessage() + " Query: " + query);
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}	
	}
	
}
