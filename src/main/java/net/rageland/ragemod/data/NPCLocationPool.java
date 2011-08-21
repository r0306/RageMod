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
	private HashSet<Integer> reserveNonTownLocations;	// Non-associated locations
	
	private Random random;

	public NPCLocationPool()
	{
		 npcLocations = new HashMap<Integer, NPCLocation>();
		 reserveNPCLocations = new HashSet<Integer>();
		 reserveNonTownLocations = new HashSet<Integer>();
		 random = new Random();
	}
	
	// Adds an NPCLocation to the lists
	public void add(NPCLocation npcLocation)
	{
		npcLocations.put(npcLocation.getID(), npcLocation);
		reserveNPCLocations.add(npcLocation.getID());
		if( npcLocation.getTownID() == 0 )
		{
			reserveNonTownLocations.add(npcLocation.getID());
		}
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
    	npcLocations.get(id).activate();
    	if( npcLocations.get(id).getTownID() == 0 )
    		reserveNonTownLocations.remove(id);
    	
    	return npcLocations.get(id);
    }
    
    // Finds a random NPCLocation from the pool and sets it as active
    public NPCLocation activateRandom()
    {
    	if( reserveNPCLocations.size() == 0 )
    		return null;
    	
    	ArrayList<Integer> removeList = new ArrayList<Integer>(reserveNPCLocations);
    	int id_NPCLocation = removeList.remove(random.nextInt(reserveNPCLocations.size()));

    	return this.activate(id_NPCLocation);
    }
    
    // Finds a random NPCLocation from the pool without a town
    public NPCLocation activateRandomNonTown()
    {
    	if( reserveNonTownLocations.size() == 0 )
    		return null;
    	
    	ArrayList<Integer> removeList = new ArrayList<Integer>(reserveNonTownLocations);
    	int id_NPCLocation = removeList.remove(random.nextInt(reserveNonTownLocations.size()));

    	return this.activate(id_NPCLocation);
    }
    
    // Returns an NPCLocation to the pool
    public void deactivate(int id)
    {
    	if( reserveNPCLocations.contains(id) || npcLocations.get(id) == null )
    		return;
    	
    	reserveNPCLocations.add(id);
    	npcLocations.get(id).deactivate();
    	if( npcLocations.get(id).getTownID() == 0 )
			reserveNonTownLocations.add(id);
    }
    
    // Returns a list of all locations
    public ArrayList<NPCLocation> getAllLocations()
    {
    	return new ArrayList<NPCLocation>(npcLocations.values());
    }

	
	
}
