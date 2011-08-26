package net.rageland.ragemod.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

// Storage class for all data relating to NPCLocation pool
public class NPCLocationPool 
{
	private HashMap<Integer, NPCLocation> npcLocations;
	
	// Reserve lists
	private HashSet<Integer> reserveNPCLocations;					// A list of all npcLocations that are not currently spawned
	private HashSet<Integer> floatingLocations;						// <ID_NPC>  NPCLocations not tied to any NPCTown
	private HashMap<Integer, HashSet<Integer>> residentLocations;	// <ID_NPCTown, ID_NPC>  all NPCLocations that are fixed to a specific town
	
	private Random random;

	public NPCLocationPool()
	{
		 npcLocations = new HashMap<Integer, NPCLocation>();
		 random = new Random();
		 
		 // Set up the reserve lists
		 reserveNPCLocations = new HashSet<Integer>();
		 floatingLocations = new HashSet<Integer>();
		 residentLocations = new HashMap<Integer, HashSet<Integer>>();
	}
	
	// Adds an NPCLocation to the lists
	public void add(NPCLocation npcLocation)
	{
		npcLocations.put(npcLocation.getID(), npcLocation);
		reserveNPCLocations.add(npcLocation.getID());
		
		if( npcLocation.getTownID() == 0 )
		{
			floatingLocations.add(npcLocation.getID());
		}
		else
		{
			if( !residentLocations.containsKey(npcLocation.getTownID()) )
    			residentLocations.put(npcLocation.getTownID(), new HashSet<Integer>());
    		residentLocations.get(npcLocation.getTownID()).add(npcLocation.getID());
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
    	
    	NPCLocation location = npcLocations.get(id);

    	// Remove the NPC from all appropriate lists
    	reserveNPCLocations.remove(id);
    	
    	if( location.getTownID() == 0 )
    		floatingLocations.remove(id);
    	else
    		residentLocations.get(location.getTownID()).remove(id);
    	
    	location.activate();
    	
    	return location;
    }
    
    // Finds a random NPCLocation from the pool without a town
    public NPCLocation activateRandomFloating()
    {
    	if( floatingLocations.size() == 0 )
    		return null;
    	
    	ArrayList<Integer> removeList = new ArrayList<Integer>(floatingLocations);
    	int id_NPCLocation = removeList.get(random.nextInt(removeList.size()));

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
			floatingLocations.add(id);
    	else
    		residentLocations.get(npcLocations.get(id).getTownID()).add(npcLocations.get(id).getID());
    }
    
    // Returns a list of all locations
    public ArrayList<NPCLocation> getAllLocations()
    {
    	return new ArrayList<NPCLocation>(npcLocations.values());
    }

    // Returns a list of all locations for a given town
	public ArrayList<NPCLocation> getActiveTownLocations(int id) 
	{
		ArrayList<NPCLocation> locations = new ArrayList<NPCLocation>();
		
		for( NPCLocation location : npcLocations.values() )
			if( location.getTownID() == id && location.isActivated() )
				locations.add(location);
				
		return locations;
	}

	// Pulls a random NPCLocation from a town
	public NPCLocation activateRandomInTown(int id) 
	{
		if( residentLocations.get(id) == null || residentLocations.get(id).size() == 0 )
    		return null;
    	
    	ArrayList<Integer> removeList = new ArrayList<Integer>(residentLocations.get(id));
    	int id_NPCLocation = removeList.get(random.nextInt(removeList.size()));

    	return this.activate(id_NPCLocation);
	}

	
	
}
