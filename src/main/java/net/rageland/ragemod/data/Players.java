package net.rageland.ragemod.data;

import java.util.Date;
import java.util.HashMap;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.RageZones;

public class Players {
		
	private HashMap<String, PlayerData> players = new HashMap<String, PlayerData>();
	private RageMod plugin;
	
	public Players(RageMod plugin) 
	{
		this.plugin = plugin;
		players = new HashMap<String, PlayerData>();
	}
	
    
    // Retrieves the player's data from memory and updates last login time
    // Creates a new Player record if one does not exist
    public PlayerData playerLogin(String playerName)
    {
    	PlayerData playerData = plugin.database.playerQueries.playerLogin(playerName);
    	
    	
    	if(playerData == null) 
    	{
    		// No contact with DB
    		playerData = generateDefaultPlayer(playerName);
    		
    	} 
    	else 
    	{
    		playerData.persistantInDatabase = true;
    	}
    	
    	players.put(playerName.toLowerCase(), playerData);
    	
    	return playerData;
    }
    
    private PlayerData generateDefaultPlayer(String playerName) 
    {
    	PlayerData playerData = new PlayerData();
    	playerData.name = playerName;
    	playerData.id_Player = -1;
    	playerData.id_Faction = -1;
    	playerData.isMember = false;
    	playerData.memberExpiration = new Date();
    	playerData.bounty = 0;
    	playerData.extraBounty = 0;
    	playerData.persistantInDatabase = false;
    	playerData.townName = "";
    	
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
    		if( playerData != null )
    			players.put(playerName.toLowerCase(), playerData);
    		
    		return playerData;    		
    	}
    }
    
    // Updates the player's info in memory
    public void update(PlayerData playerData)
    {
    	// No point in doing this, in the hashmap is pointer to the object. If you change values
    	// This will also be changed in the object the hashmap points to.
    	/*
    	if( players.containsKey(playerData.name.toLowerCase()) )
    	{
    		players.put(playerData.name.toLowerCase(), playerData);
    	}
    	else
    	{
    		System.out.println("Players.update called on invalid value: " + playerData.name);
    	}
    	*/
    }
    
    // For debugging - returns the number of players loaded into memory
    public int size()
    {
    	return players.size();
    }
		

}
