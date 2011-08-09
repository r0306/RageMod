package net.rageland.ragemod.data;

import java.util.ArrayList;
import java.util.HashMap;

import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;

import org.bukkit.Location;

// TODO: Consider making this an Integer hash map for speed - implement a method to return PlayerTown by searching for name
// 		 I could also store a separate hash/array that stores ID to string for fast lookup (would this actually be faster?)

public class PlayerTowns {
		
	private HashMap<String, PlayerTown> towns;
	private RageMod plugin;
	
	public PlayerTowns(RageMod plugin) 
	{
		this.plugin = plugin;
		towns = new HashMap<String, PlayerTown>();
	}
		
	// On startup, pull all the PlayerTown data from the DB into memory 
	public void loadPlayerTowns()
	{
		towns = plugin.database.townQueries.loadPlayerTowns();	
	}
	
	// Insert/update town info
	public void put(PlayerTown playerTown)
	{
		towns.put(playerTown.townName.toLowerCase(), playerTown);
	}
	
	// Gets the town from memory.  Returns NULL for non-existent towns
    public PlayerTown get(String townName)
    {       	
    	if( towns.containsKey(townName.toLowerCase()) )
    		return towns.get(townName.toLowerCase());
    	else
    	{
    		System.out.println("Warning: PlayerTowns.Get called on non-existent town '" + townName + "'");
    		return null;
    	}
    }
    
    // Returns all towns
    public ArrayList<PlayerTown> getAll()
    {
 		return new ArrayList<PlayerTown>(towns.values());
    }
    
    
    // Check for all nearby towns within minimum distance (for creating new towns)
 	public HashMap<String, Integer> checkForNearbyTowns(Location location)
 	{
 		HashMap<String, Integer> townList = new HashMap<String, Integer>();
 		double distance;
 		Location2D location2D = new Location2D(location);
 		
 		for( PlayerTown town : towns.values() )
 		{
 			distance = location2D.distance(town.centerPoint);
 			if( distance < plugin.config.Town_MIN_DISTANCE_BETWEEN )
 				townList.put(town.townName, (int)distance);
 		}
 		
 		return townList;
 	}
    
    // Checks to see if the selected faction already has a capitol; used by /townupgrade
    public boolean doesFactionCapitolExist(int faction)
    {
    	for( PlayerTown town : towns.values () )
    	{
    		if( town.id_Faction == faction && town.isCapitol() )
    			return true;
    	}
    	
    	return false;	// No faction capitols found
    }
    
    // Checks to see if nearby enemy capitols are too close; used by /townupgrade
    public boolean areEnemyCapitolsTooClose(PlayerTown playerTown)
    {
    	Location2D location2D = new Location2D(playerTown.centerPoint);
    	
    	for( PlayerTown town : towns.values () )
    	{
    		if( town.isCapitol() && location2D.distance(town.centerPoint) < plugin.config.Town_MIN_DISTANCE_ENEMY_CAPITOL )
    		{
    			return true;
    		}
    	}
    	
    	return false;	// No too-close capitols found
    }

	// Gets the town that the location is in, if any
    public PlayerTown getCurrentTown(Location location) 
	{
		for( PlayerTown town : towns.values() )
    	{
    		if( town.isInside(location) )
    		{
    			return town;
    		}
    	}
	
		// Location not inside any town; return null
		return null;
	}
    
    // Removes the town from the list for deletions
    public void remove(PlayerTown playerTown)
    {
    	towns.remove(playerTown);
    }
    

}

