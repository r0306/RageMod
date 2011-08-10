package net.rageland.ragemod.dbqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.database.RageDB;

public class FactionQueries {
	
	private RageDB rageDB;
	private RageMod plugin;
	
	public FactionQueries(RageDB rageDB, RageMod plugin)
	{
		this.rageDB = rageDB;
		this.plugin = plugin;
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
			conn = rageDB.getConnection();
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
			rageDB.close(rs, preparedStatement, conn);
		}
		
		return null;
	}

}
