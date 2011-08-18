package net.rageland.ragemod.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

// Storage class for all data relating to NPCLocation pool
public class NPCLocationPool 
{
	private HashSet<Integer> reserveNPCLocations;		// A list of all npcLocations that are not currently spawned
	private HashMap<Integer, NPCLocation> npcLocations;
	
	private Random random;

	public NPCLocationPool()
	{
		 npcLocations = new HashMap<Integer, NPCLocation>();
		 reserveNPCLocations = new HashSet<Integer>();
		 random = new Random();
	}
	
	// Adds an NPCLocation to the lists
	public void add(NPCLocation NPCLocation)
	{
		npcLocations.put(NPCLocation.getID(), NPCLocation);
		reserveNPCLocations.add(NPCLocation.getID());
	}
	
	// Gets data on specified NPCLocation
	// Gets the NPCLocation record from memory.  Returns NULL for non-existent IDs
    public NPCLocation get(int id)
    {       	
    	if( npcLocations.containsKey(id) )
    		return npcLocations.get(id);
    	else
    	{
    		System.out.println("Warning: NPCLocationPool.get() called on non-existent id '" + id + "'");
    		return null;
    	}
    }
    
    // Gets an NPCLocation from the pool and sets it as active
    public NPCLocation activate(int id)
    {
    	if( !reserveNPCLocations.contains(id) )
    		return null;

    	reserveNPCLocations.remove(id);
    	return npcLocations.get(id);
    }
	
	
}
