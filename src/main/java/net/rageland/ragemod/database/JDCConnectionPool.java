package net.rageland.ragemod.database;

import java.sql.*;
import java.util.*;

class ConnectionReaper extends Thread {

    private JDCConnectionPool pool;
    private final long delay=3600000;		// TODO: this was 300000

    ConnectionReaper(JDCConnectionPool pool) {
        this.pool=pool;
    }

    public void run() {
        while(true) {
           try {
              sleep(delay);
           } catch( InterruptedException e) { }
           //System.out.println("[RAGE] Attempting to reap connections... (pool size: " + pool.connections.size() + ")");
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
@SuppressWarnings("unused")
public class JDCConnectionPool {

   public Vector<JDCConnection> connections;
   private String url, user, password;
   private long timeout = 190000; 			// TODO: This was 20000000
   private ConnectionReaper reaper;
   private ConnectionKeepAlive pinger;
   final private int poolsize=20;

   public JDCConnectionPool(String url, String user, String password, long timeout) {
      this.url = url;
      this.user = user;
      this.password = password;
      this.timeout = timeout;
      connections = new Vector<JDCConnection>(poolsize);
      reaper = new ConnectionReaper(this);
      reaper.start();
      //pinger = new ConnectionKeepAlive(connections);
      //pinger.start();
   }

   public synchronized void reapConnections() {

      long stale = System.currentTimeMillis() - timeout;
      Enumeration<JDCConnection> connlist = connections.elements();
    
      while( (connlist != null) && (connlist.hasMoreElements()) ) 
      {
          JDCConnection conn = (JDCConnection)connlist.nextElement();

          //System.out.println("[RAGE] Conn. " + connections.indexOf(conn) + ": inUse: " + conn.inUse() + ", stale: " + ((stale-conn.getLastUse())/1000) + " validate(): " + conn.validate());
          
          if((conn.inUse()) && !conn.validate()) // Reap all connections that fail validate() (DC)
          {
        	  removeConnection(conn);
        	  System.out.println("[RAGE] Connection pool reaped broken connection.  Pool size: " + connections.size());
          }
          // Also reap ALL older connections - the pool isn't going to try to use them anyway
          else if((!conn.inUse() && (stale > conn.getLastUse())) || (!conn.validate())) // Reap all connections that fail validate() or are too old (DC)
          {
        	  removeConnection(conn);
        	  System.out.println("[RAGE] Connection pool reaped idle connection.  Pool size: " + connections.size());
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
	   long stale = System.currentTimeMillis() - timeout;   // DC - added stale checking to getConnection - don't even try with old connections
	   
	   for(JDCConnection connection : connections) {    	   
           if(stale < connection.getLastUse() && (connection.validate() && connection.lease())) 
           {
               //System.out.println("[RAGE] Getting connection " + connections.indexOf(connection));
        	   return connection;
           } 
       }

       //System.out.println("[RAGE] Creating new connection");
       
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
