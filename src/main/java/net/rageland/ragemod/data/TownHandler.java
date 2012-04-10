package net.rageland.ragemod.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.npc.NPCTown;
import net.rageland.ragemod.world.Location2D;
import net.rageland.ragemod.world.PlayerTown;
import net.rageland.ragemod.world.Town;

import org.bukkit.Location;

// TODO: Consider making this an Integer hash map for speed - implement a method to return PlayerTown by searching for name
// 		 I could also store a separate hash/array that stores ID to string for fast lookup (would this actually be faster?)

public class TownHandler {
		
	private HashMap<String, PlayerTown> playerTowns;
	private HashMap<String, NPCTown> npcTowns;
	private HashSet<NPCTown> outsideNPCTowns;				// A collection of NPCTowns outside Zone A (subset of npcTowns)
	private HashMap<Integer, String> playerTownIDs;
	private HashMap<Integer, String> npcTownIDs;
	private RageMod plugin;
	
	public TownHandler(RageMod plugin) 
	{
		this.plugin = plugin;
		playerTowns = new HashMap<String, PlayerTown>();
		npcTowns = new HashMap<String, NPCTown>();
		outsideNPCTowns = new HashSet<NPCTown>();
		playerTownIDs = new HashMap<Integer, String>();
		npcTownIDs = new HashMap<Integer, String>();
	}
		
	// On startup, pull all the PlayerTown data from the DB into memory 
	public void loadTowns()
	{
		playerTowns = plugin.database.townQueries.loadPlayerTowns();	
		npcTowns = plugin.database.npcTownQueries.loadAll();
		
		// Populate the ID-name hashes (reverse these?)
		for( PlayerTown town : playerTowns.values() )
			playerTownIDs.put(town.getID(), town.getName().toLowerCase());
		for( NPCTown town : npcTowns.values() )
		{
			npcTownIDs.put(town.getID(), town.getName().toLowerCase());
			//TODO config stuff
				outsideNPCTowns.add(town);
		}
	}
	
	// Insert/update town info
	public void add(Town town)
	{
		if( town instanceof PlayerTown )
			playerTowns.put(town.getName().toLowerCase(), (PlayerTown)town);
		else if( town instanceof NPCTown )
			npcTowns.put(town.getName().toLowerCase(), (NPCTown)town);
		else
			System.out.println("ERROR: Invalid town type in Towns.add()");
	}
	
	// Gets the town from memory.  Returns NULL for non-existent towns
    public Town get(String townName)
    {       	
    	if( playerTowns.containsKey(townName.toLowerCase()) )
    		return playerTowns.get(townName.toLowerCase());
    	else if( npcTowns.containsKey(townName.toLowerCase()) )
    		return npcTowns.get(townName.toLowerCase());
    	else
    	{
    		System.out.println("Warning: Towns.Get called on non-existent town '" + townName + "'");
    		return null;
    	}
    }
    
    // Returns all towns
    public ArrayList<Town> getAll()
    {
    	ArrayList<Town> allTowns = new ArrayList<Town>(playerTowns.values());
 		allTowns.addAll(npcTowns.values());
    	return allTowns;
    }
    
    // Returns all player towns
    public ArrayList<PlayerTown> getAllPlayerTowns()
    {
    	return new ArrayList<PlayerTown>(playerTowns.values());
    }
    
    // Returns all NPC towns
    public ArrayList<NPCTown> getAllNPCTowns()
    {
    	return new ArrayList<NPCTown>(npcTowns.values());
    }
    
    
    // Check for all nearby towns within minimum distance (for creating new towns)
 	public HashMap<String, Integer> checkForNearbyTowns(Location location)
 	{
 		HashMap<String, Integer> townList = new HashMap<String, Integer>();
 		double distance;
 		Location2D location2D = new Location2D(location);
 		ArrayList<Town> allTowns = new ArrayList<Town>(playerTowns.values());
 		allTowns.addAll(npcTowns.values());
 		
 		for( Town town : allTowns )
 		{
 			distance = location2D.distance(town.centerPoint);
 			if( distance < plugin.config.Town_MIN_DISTANCE_BETWEEN )
 				townList.put(town.getName(), (int)distance);
 		}
 		
 		return townList;
 	}
    
    // Checks to see if the selected faction already has a capitol; used by /townupgrade
    public boolean doesFactionCapitolExist(int faction)
    {
    	for( PlayerTown town : playerTowns.values () )
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
    	
    	for( PlayerTown town : playerTowns.values () )
    	{
    		if( town.isCapitol() && location2D.distance(town.centerPoint) < plugin.config.Town_MIN_DISTANCE_ENEMY_CAPITOL )
    		{
    			return true;
    		}
    	}
    	
    	return false;	// No too-close capitols found
    }

	// Gets the town that the location is in, if any
    public Town getCurrentTown(Location location) 
	{
 		// Zone A - NPC towns
    	if( plugin.zones.isInside((location)).getConfig().isNpcTown())
    	{
        	for( NPCTown town : npcTowns.values() )
        	{
        		if( town.isInside(location) )
        			return town;
        	}
    	}
    	// Zone B - Player towns
    	else if(plugin.zones.isInside((location)).getConfig().isPlayerCity() )
    	{
        	for( PlayerTown town : playerTowns.values() )
        	{
        		if( town.isInside(location) )
        			return town;
        	}
        	for( NPCTown town : outsideNPCTowns )
        	{
        		if( town.isInside(location) )
        			return town;
        	}
    	}
	
		// Location not inside any town; return null
		return null;
	}
    
    // Removes the town from the list for deletions
    public void remove(PlayerTown playerTown)
    {
    	playerTowns.remove(playerTown);
    }

	// Gets the NPC town by ID
    public NPCTown getNPCTown(int id_NPCTown) 
    {
		if( npcTownIDs.containsKey(id_NPCTown) )
			return npcTowns.get(npcTownIDs.get(id_NPCTown));
		else
			return null;
	}
    

}

