package net.rageland.ragemod.database;

import java.sql.*;
import java.util.*;

class ConnectionReaper extends Thread {

    private JDCConnectionPool pool;
    private final long delay=300000;

    ConnectionReaper(JDCConnectionPool pool) {
        this.pool=pool;
    }

    public void run() {
        while(true) {
           try {
              sleep(delay);
           } catch( InterruptedException e) { }
           pool.reapConnections();
        }
    }
}

/**
 * To keep the connections in the pool alive. This was what caused our problems.
 * No good way to track if a connection is closed or not without just keeping
 * it alive by using ping.
 * @author Jorgen
 *
 */
class ConnectionKeepAlive extends Thread
{
	private Vector<JDCConnection> connections;
	private final int interval = 60000;
	
	public ConnectionKeepAlive(Vector<JDCConnection> connections)
	{
		this.connections = connections;
	}
	
	public void run()
	{
		while(true)
		{			
			try {
				sleep(interval);				
			} 
			catch (InterruptedException e) { }
			for(JDCConnection conn : connections)
			{
				PreparedStatement ps;
				
				try {
					ps = conn.prepareStatement("/* ping */ SELECT 1");
					ps.executeQuery();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
				
			}
		}
	}
	
}

public class JDCConnectionPool {

   public Vector<JDCConnection> connections;
   private String url, user, password;
   final private long timeout=60000;
   private ConnectionReaper reaper;
   private ConnectionKeepAlive pinger;
   final private int poolsize=10;

   public JDCConnectionPool(String url, String user, String password) {
      this.url = url;
      this.user = user;
      this.password = password;
      connections = new Vector<JDCConnection>(poolsize);
      reaper = new ConnectionReaper(this);
      reaper.start();
      pinger = new ConnectionKeepAlive(connections);
      pinger.start();
   }

   public synchronized void reapConnections() {

      long stale = System.currentTimeMillis() - timeout;
      Enumeration<JDCConnection> connlist = connections.elements();
    
      while((connlist != null) && (connlist.hasMoreElements())) {
          JDCConnection conn = (JDCConnection)connlist.nextElement();

          if((conn.inUse()) && (stale >conn.getLastUse()) && 
                                            (!conn.validate())) {
 	      removeConnection(conn);
         }
      }
   }

   public synchronized void closeConnections() {
        
      Enumeration<JDCConnection> connlist = connections.elements();

      while((connlist != null) && (connlist.hasMoreElements())) {
          JDCConnection conn = (JDCConnection)connlist.nextElement();
          removeConnection(conn);
      }
   }

   private synchronized void removeConnection(JDCConnection conn) {
       connections.removeElement(conn);
   }


   public synchronized Connection getConnection() throws SQLException {
       for(JDCConnection connection : connections) {    	   
           if(connection.validate() && connection.lease()) 
           {
              return connection;
           } 
       }

       Connection conn = DriverManager.getConnection(url, user, password);
       JDCConnection newConnection = new JDCConnection(conn, this);
       newConnection.lease();
       connections.addElement(newConnection);
       
       return newConnection;
  } 

   public synchronized void returnConnection(JDCConnection conn) {
      conn.expireLease();
   }
}
