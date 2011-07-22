package net.rageland.ragemod.dbqueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import net.rageland.ragemod.RageDB;
import net.rageland.ragemod.RageMod;

public class TaskQueries {
	
	private RageDB rageDB;
	private RageMod plugin;
	
	public TaskQueries(RageDB rageDB, RageMod plugin)
	{
		this.rageDB = rageDB;
		this.plugin = plugin;
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
			conn = rageDB.getConnection();
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
			rageDB.close(rs, preparedStatement, conn);
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
			conn = rageDB.getConnection();
    		preparedStatement = conn.prepareStatement(
    				"INSERT INTO Tasks (Name, Timestamp) VALUES ('" + taskName + "',NOW())");
    		preparedStatement.executeUpdate();	
    	} 
    	catch (SQLException e) {
    		System.out.println("Error in RageDB.setComplete(): " + e.getMessage());
		    System.out.println("SQLState: " + e.getSQLState());
		    System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			rageDB.close(rs, preparedStatement, conn);
		}	
	}

}
