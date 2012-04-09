package net.rageland.ragemod.data;

import java.util.Date;
import java.util.HashMap;

import net.rageland.ragemod.RageMod;

public class PlayerHandler {
		
	private HashMap<String, PlayerData> players;
	private RageMod plugin;
	
	public PlayerHandler(RageMod plugin) 
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
    		playerData.persistentInDatabase = true;
    	}
    	
    	players.put(playerName.toLowerCase(), playerData);
    	
    	return playerData;
    }
    
    private PlayerData generateDefaultPlayer(String playerName) 
    {
    	PlayerData playerData = new PlayerData(plugin);
    	playerData.name = playerName;
    	playerData.id_Player = -1;
    	playerData.id_Faction = -1;
    	playerData.isMember = false;
    	playerData.memberExpiration = new Date();
    	playerData.bounty = 0;
    	playerData.extraBounty = 0;
    	playerData.persistentInDatabase = false;
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

    
    // For debugging - returns the number of players loaded into memory
    public int size()
    {
    	return players.size();
    }
		

}
