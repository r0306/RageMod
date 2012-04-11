package net.rageland.ragemod.database.queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.World;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.database.RageDB;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.world.Lot;
import net.rageland.ragemod.world.Region2D;

/**
 * Contains the lot queries used for database communication.
 * @author Jorgen
 *
 */
public class LotQueries {
	
	private RageDB rageDB;
	private RageMod plugin;

	public LotQueries(RageDB rageDB, RageMod plugin) 
	{
		this.rageDB = rageDB;
		this.plugin = plugin;
	}
	
	/**
	 *  Make a separate query to get all lots owned by the player
	 */
	public ArrayList<Lot> getLots(int id_Player) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;  
		ArrayList<Lot> playerLots = new ArrayList<Lot>();
	
    	try
    	{
    		conn = rageDB.getConnection();
    		
        	String selectQuery = 
        		"SELECT ID_Lot FROM Lots WHERE ID_Player = " + id_Player;
    		
    		preparedStatement = conn.prepareStatement(selectQuery);	        		        	
        	rs = preparedStatement.executeQuery();
        	
        	while( rs.next() )
        	{
        		playerLots.add(plugin.lots.get(rs.getInt("ID_Lot")));	// We already have the lot info in memory; save the DB some work
        	}
        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in RageDB.getLots(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return playerLots;
	}
	
	// Return all players who are allowed to build in the player's lots
	public ArrayList<String> getLotPermissions(int id_Player)
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		ArrayList<String> lotPermissions = new ArrayList<String>();
	
    	try
    	{
    		conn = rageDB.getConnection();
        	String selectQuery = 
        		"SELECT p.Name as Name FROM LotPermissions lp " +
        		"INNER JOIN Players p ON p.ID_Player = lp.ID_Player_Builder " +
        		"WHERE lp.ID_Player_Owner = " + id_Player;
    		
    		preparedStatement = conn.prepareStatement(selectQuery);	        		        	
        	rs = preparedStatement.executeQuery();
        	
            while( rs.next() )
        	{
        		lotPermissions.add(rs.getString("Name"));	
        	}
            return lotPermissions;
        		        	
    	} catch (SQLException e) {
    		System.out.println("Error in LotQueries.getLotPermissions(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
    	
    	return null;
	}
	
	// Loads all of the lots in the city into memory
	public HashMap<String, Lot> loadLots() 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		HashMap<String, Lot> lots = new HashMap<String, Lot>();
		Lot currentLot = null;
		int mult = plugin.config.Lot_MULTIPLIER;
		World world = plugin.getServer().getWorld("world");
		
    	try
    	{
    		conn = rageDB.getConnection();
        	preparedStatement = conn.prepareStatement(
        		"SELECT l.ID_Lot, l.Category, l.Number, IFNULL(p.Name, '') as Owner, l.XCoord, l.ZCoord, " +
        		"l.Width, l.Height " +
        		"FROM Lots l " +
        		"LEFT JOIN Players p ON l.ID_Player = p.ID_Player");	
        	
        	rs = preparedStatement.executeQuery();
        	
        	while ( rs.next() ) 
        	{
        		currentLot = new Lot(plugin);
        		
        		currentLot.id_Lot = rs.getInt("ID_Lot");
        		currentLot.setCategory(rs.getString("Category"));
        		currentLot.number = rs.getInt("Number");
        		currentLot.owner = rs.getString("Owner");
        		// X and Z are reversed >:(
        		currentLot.region = new Region2D(world, 
        				((rs.getInt("ZCoord")-1) * mult) + plugin.config.Lot_X_OFFSET,
        				((rs.getInt("XCoord")-1) * mult * -1) + plugin.config.Lot_Z_OFFSET,
        				((rs.getInt("ZCoord")-1) * mult) + plugin.config.Lot_X_OFFSET + (rs.getInt("Height") * mult),
        				((rs.getInt("XCoord")-1) * mult * -1) + plugin.config.Lot_Z_OFFSET - (rs.getInt("Width") * mult));
    	        
        		lots.put(currentLot.getLotCode(), currentLot);	        		
        	}
        		
        	return lots;				
        		        	
    	} catch (Exception e) {
    		System.out.println("Error in RageDB.loadLots(): " + e.getMessage());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
		
		return null;
	}
	
	// Assign a lot to a player
	public void lotClaim(PlayerData playerData, Lot lot) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		try
    	{
			conn = rageDB.getConnection();
    		// Update the Lots table to assign the owner
    		preparedStatement = conn.prepareStatement(
    				"UPDATE Lots SET ID_Player = " + playerData.id_Player + ", DateClaimed = NOW() WHERE ID_Lot = " + lot.id_Lot);
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in RageDB.lotClaim(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
	}

	// Reset a lot's owner
	public void lotUnclaim(Lot lot) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		try
    	{
			conn = rageDB.getConnection();
    		// Update the Lots table to assign the owner
    		preparedStatement = conn.prepareStatement(
    				"UPDATE Lots SET ID_Player = NULL, DateClaimed = NULL WHERE ID_Lot = " + lot.id_Lot);
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in RageDB.LotUnclaim(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}
	}
	
	// Sets permission for collaborative lot building
	public void allow(int id_Player_Owner, int id_Player_Builder) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
	    
		try
    	{
			conn = rageDB.getConnection();
			preparedStatement = conn.prepareStatement(
    				"INSERT INTO LotPermissions (ID_Player_Owner, ID_Player_Builder) VALUES (" + id_Player_Owner + ", " + id_Player_Builder + ")");
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in LotQueries.allow(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}		
	}

	// Clears permissions for collaborative lot building
	public void disallow(int id_Player_Owner, int id_Player_Builder) 
	{
		Connection conn = null;
	    PreparedStatement preparedStatement = null;
	    ResultSet rs = null;
		
		try
    	{
			conn = rageDB.getConnection();
			// An ID of 0 indicates all players
			if( id_Player_Builder == 0 )
    		{
				preparedStatement = conn.prepareStatement(
	    				"DELETE FROM LotPermissions WHERE ID_Player_Owner = " + id_Player_Owner);
    		}
			else
			{
				preparedStatement = conn.prepareStatement(
	    				"DELETE FROM LotPermissions WHERE ID_Player_Owner = " + id_Player_Owner + " AND ID_Player_Builder = " + id_Player_Builder);
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
