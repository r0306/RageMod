package net.rageland.ragemod.database;

// TODO: Prevent null pointer exception when database not found (!)

// TODO: Consider refactoring the player and town update code into a few generic update methods.
//		 When Players.update() is called, add the update to a queue.
//		 Every minute or so, process all the updates.  This would keep multiple updates from going to the database multiple times.

import java.sql.*;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;

import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.Location2D;
import net.rageland.ragemod.data.Lot;
import net.rageland.ragemod.data.Lots;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.data.Towns;
import net.rageland.ragemod.data.Players;
import net.rageland.ragemod.data.Region2D;
import net.rageland.ragemod.dbqueries.FactionQueries;
import net.rageland.ragemod.dbqueries.LotQueries;
import net.rageland.ragemod.dbqueries.NPCQueries;
import net.rageland.ragemod.dbqueries.PlayerQueries;
import net.rageland.ragemod.dbqueries.TaskQueries;
import net.rageland.ragemod.dbqueries.TownQueries;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RageDB {

	private JDCConnectionDriver connectionDriver;
    protected String url;
    protected String databaseName;
    protected String driver;
    protected String user;
    protected String password;
    protected long timeout;
             
    private RageMod plugin;
    public TownQueries townQueries;
    public LotQueries lotQueries;
    public PlayerQueries playerQueries;
    public FactionQueries factionQueries;
    public TaskQueries taskQueries;
    public NPCQueries npcQueries;

    public RageDB(RageMod instance, RageConfig config)
    {
    	plugin = instance;
    	townQueries = new TownQueries(this, this.plugin);
    	lotQueries = new LotQueries(this, this.plugin);
    	playerQueries = new PlayerQueries(this, this.plugin);
    	factionQueries = new FactionQueries(this, this.plugin);
    	taskQueries = new TaskQueries(this, this.plugin);
    	npcQueries = new NPCQueries(this, this.plugin);
    	
    	url = config.DB_URL;
    	databaseName = config.DB_NAME;
    	driver = config.DB_DRIVER;
    	user = config.DB_USER;
    	password = config.DB_PASSWORD;
    	timeout = config.DB_TIMEOUT;
    	
        try
        {
        	connectionDriver = new JDCConnectionDriver(driver, url + databaseName, user, password, timeout);
        }
        catch(Exception e)
        {
           System.out.println(e);
        }
    }

    public Connection getConnection() throws SQLException
    {
    	return connectionDriver.getConnectionPool().getConnection();
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
				connectionDriver.getConnectionPool().returnConnection((JDCConnection)conn);
			}
		} catch (Exception e) {

		}
	}		
}
