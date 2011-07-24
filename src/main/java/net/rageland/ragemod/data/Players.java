package net.rageland.ragemod.data;

import java.util.HashMap;

import net.rageland.ragemod.RageMod;

public class Players {
		
	private HashMap<String, PlayerData> players = new HashMap<String, PlayerData>();
	private RageMod plugin;
	
	public Players(RageMod plugin) 
	{
		this.plugin = plugin;
	}
	
    
    // Retrieves the player's data from memory and updates last login time
    // Creates a new Player record if one does not exist
    public PlayerData playerLogin(String playerName)
    {
    	PlayerData playerData = plugin.database.playerQueries.playerLogin(playerName);
    	players.put(playerName.toLowerCase(), playerData);
    	
    	return playerData;
    }
    
    // Gets the player from memory, or pulls from DB if not present.  Returns NULL for non-existent players
    public PlayerData get(String playerName)
    {       	
    	if( players.containsKey(playerName.toLowerCase()) )
    		return players.get(playerName.toLowerCase());
    	else
    	{
    		System.out.println("DB fetch called for player: " + playerName);
    		PlayerData playerData = plugin.database.playerQueries.playerFetch(playerName);
    		if( playerData == null )
    			return null;
    		else
    		{
    			players.put(playerName.toLowerCase(), playerData);
    			return playerData;
    		}
    	}
    }
    
    // Updates the player's info in memory
    public void update(PlayerData playerData)
    {
    	if( players.containsKey(playerData.name.toLowerCase()) )
    	{
    		players.put(playerData.name.toLowerCase(), playerData);
    	}
    	else
    	{
    		System.out.println("Players.update called on invalid value: " + playerData.name);
    	}
    }
    
    // For debugging - returns the number of players loaded into memory
    public int size()
    {
    	return players.size();
    }
		

}
