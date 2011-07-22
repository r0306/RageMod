package net.rageland.ragemod;

// TODO: Refactor into connection pooling

// TODO: Prevent null pointer exception when database not found (!)

// TODO: Consider refactoring the player and town update code into a few generic update methods.
//		 When Players.update() is called, add the update to a queue.
//		 Every minute or so, process all the updates.  This would keep multiple updates from going to the database multiple times.

import java.sql.*;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;

import net.rageland.ragemod.data.Location2D;
import net.rageland.ragemod.data.Lot;
import net.rageland.ragemod.data.Lots;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.data.PlayerTowns;
import net.rageland.ragemod.data.Players;
import net.rageland.ragemod.data.Region2D;
import net.rageland.ragemod.database.JDCConnection;
import net.rageland.ragemod.database.JDCConnectionDriver;
import net.rageland.ragemod.database.JDCConnectionPool;
import net.rageland.ragemod.dbqueries.LotQueries;
import net.rageland.ragemod.dbqueries.TownQueries;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RageDB {

	protected JDCConnectionPool connectionPool;
    protected String url;
    protected String databaseName;
    protected String driver;
    protected String user;
    protected String password;
             
    private RageMod plugin;
    public TownQueries townQueries;
    public LotQueries lotQueries;

    public RageDB(RageMod instance)
    {
    	plugin = instance;
    	townQueries = new TownQueries(this, this.plugin);
    	lotQueries = new LotQueries(this, this.plugin);
    	
    	url = RageConfig.DB_URL;
    	databaseName = RageConfig.DB_NAME;
    	driver = RageConfig.DB_DRIVER;
    	user = RageConfig.DB_USER;
    	password = RageConfig.DB_PASSWORD;
    	
        try
        {
        	JDCConnectionDriver connectionDriver = new JDCConnectionDriver(driver, url, user, password);
        	connectionPool = connectionDriver.getConnectionPool();
        }
        catch(Exception e)
        {
           System.out.println(e);
        }
    }

    public Connection getConnection() throws SQLException
    {
    	return connectionPool.getConnection();
    }
        
    
    
    // You need to close the resultSet
	public void close(ResultSet rs, PreparedStatement preparedStatement, Connection conn) {
		try {
			if (rs != null) 
			{
				rs.close();
			}

			if (preparedStatement != null) 
			{
				preparedStatement.close();
			}

			if (conn != null) 
			{
				connectionPool.returnConnection((JDCConnection)conn);
			}
		} catch (Exception e) {

		}
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
    		conn = getConnection();
        	String selectQuery = 
        		"SELECT p.ID_Player, p.Name, IFNULL(p.ID_Faction, 0) as ID_Faction, p.IsMember, p.MemberExpiration, p.Bounty, p.ExtraBounty, " +
        		"   (p.Home_XCoord IS NOT NULL) AS Home_IsSet, p.Home_XCoord, p.Home_YCoord, p.Home_ZCoord, p.Home_LastUsed, " +
        		"	(p.Spawn_XCoord IS NOT NULL) AS Spawn_IsSet, p.Spawn_XCoord, p.Spawn_YCoord, p.Spawn_ZCoord, p.Spawn_LastUsed, " +
        		"	IFNULL(pt.TownName, '') as TownName, p.IsMayor " +
        		"FROM Players p " +
        		"LEFT JOIN PlayerTowns pt ON p.ID_PlayerTown = pt.ID_PlayerTown " +
        		"WHERE p.Name = '" + playerName + "'";
    		
    		preparedStatement = conn.prepareStatement(selectQuery);	        		        	
        	rs = preparedStatement.executeQuery();
        	
        	// Test to see if result set was empty
        	if( rs.next() )
        	{
        		playerData = fillPlayerData(rs);
        		
        		// Set LastLogin time
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
        	
        	return playerData;				
        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in RageDB.PlayerLogin(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			close(rs, preparedStatement, conn);
		}
		
		return null;
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
    		conn = getConnection();
        	String selectQuery = 
        		"SELECT p.ID_Player, p.Name, IFNULL(p.ID_Faction, 0) as ID_Faction, p.IsMember, p.MemberExpiration, p.Bounty, p.ExtraBounty, " +
        		"   (p.Home_XCoord IS NOT NULL) AS Home_IsSet, p.Home_XCoord, p.Home_YCoord, p.Home_ZCoord, p.Home_LastUsed, " +
        		"	(p.Spawn_XCoord IS NOT NULL) AS Spawn_IsSet, p.Spawn_XCoord, p.Spawn_YCoord, p.Spawn_ZCoord, p.Spawn_LastUsed, " +
        		"	IFNULL(pt.TownName, '') as TownName, p.IsMayor " +
        		"FROM Players p " +
        		"LEFT JOIN PlayerTowns pt ON p.ID_PlayerTown = pt.ID_PlayerTown " +
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
			close(rs, preparedStatement, conn);
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
		playerData.isMember = rs.getBoolean("IsMember");
		playerData.memberExpiration = rs.getDate("MemberExpiration");
		playerData.bounty = rs.getFloat("Bounty");
		playerData.extraBounty = rs.getFloat("ExtraBounty");
		playerData.townName = rs.getString("TownName");
		playerData.isMayor = rs.getBoolean("IsMayor");
		
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
		
		playerData.lots = lotQueries.getLots(playerData.id_Player);
        playerData.lotPermissions = lotQueries.getLotPermissions(playerData.id_Player);
		
		if( playerData.townName.equals("") )
			playerData.treasuryBalance = 0;
		else
			playerData.treasuryBalance = this.getPlayerTreasuryBalance(playerData.id_Player, PlayerTowns.get(playerData.townName).id_PlayerTown);
		
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
    		conn = getConnection();
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
			close(rs, preparedStatement, conn);
		}
    	
    	return -1;
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
			conn = getConnection();
    		// Update the Players table to create the town association
    		updateString = 
				"UPDATE Players SET " +
				"ID_Faction = " + (playerData.id_Faction == 0 ? "null" : playerData.id_Faction) + ", " +
				"IsMember = " + (playerData.isMember ? 1 : 0) + ", " +
				"MemberExpiration = " + playerData.memberExpiration + ", " +
				"Bounty = " + playerData.bounty + ", " +
				"ExtraBounty = " + playerData.extraBounty + ", " +
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
			close(rs, preparedStatement, conn);
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
    		conn = getConnection();
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
			close(rs, preparedStatement, conn);
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
			conn = getConnection();
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
			close(rs, preparedStatement, conn);
		}
		
		return 0;
	}
	
	// Return the population of each faction
	public HashMap<Integer, Integer> getFactionPopulations()
	{
		HashMap<Integer, Integer> populations = new HashMap<Integer, Integer>();
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		
		try
    	{
			conn = getConnection();
        	preparedStatement = conn.prepareStatement(
        		"SELECT p.ID_Faction, COUNT(*) as Population FROM Factions f " +
        		"INNER JOIN Players p ON p.ID_Faction = f.ID_Faction " +
        		"WHERE DATE_SUB(CURDATE(),INTERVAL 30 DAY) <= p.LastLogin " +
        		"GROUP BY f.ID_Faction");
        		
        	rs = preparedStatement.executeQuery();
        	while ( rs.next() ) 
        	{
        		populations.put(rs.getInt("ID_Faction"), rs.getInt("Population"));       		
        	}
        	
        	return populations;
    	} 
		catch (Exception e) {
    		System.out.println("Error in RageDB.getRecentDonations(): " + e.getMessage());
		} finally {
			close(rs, preparedStatement, conn);
		}
		
		return null;
	}

	// Load the latest ran task times for all tasks
	public HashMap<String, Timestamp> loadTaskTimes() 
	{
		HashMap<String, Timestamp> tasks = new HashMap<String, Timestamp>();
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		
		try
    	{
			conn = getConnection();
        	preparedStatement = conn.prepareStatement("SELECT Name, MAX(Timestamp) as Timestamp FROM Tasks GROUP BY Name");
        		
        	rs = preparedStatement.executeQuery();
        	while ( rs.next() ) 
        	{
        		tasks.put(rs.getString("Name"), rs.getTimestamp("Timestamp"));       		
        	}
        	
        	return tasks;
    	} 
		catch (Exception e) {
    		System.out.println("Error in RageDB.loadTaskTimes(): " + e.getMessage());
		} finally {
			close(rs, preparedStatement, conn);
		}
		
		return null;
	}

	// Log a task as complete in the database
	public void setComplete(String taskName) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		try
    	{
			conn = getConnection();
    		preparedStatement = conn.prepareStatement(
    				"INSERT INTO Tasks (Name, Timestamp) VALUES ('" + taskName + "',NOW())");
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in RageDB.setComplete(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			close(rs, preparedStatement, conn);
		}	
	}
        
}
