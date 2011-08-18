package net.rageland.ragemod.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

// Storage class for all data relating to NPC pool
public class NPCPool 
{
	private HashMap<Integer, NPCData> npcs;
	private HashSet<Integer> reserveNPCs;		// A list of all NPCs that are not currently spawned
	// TODO: List of NPCs for each NPC town
	
	private Random random;

	public NPCPool()
	{
		 npcs = new HashMap<Integer, NPCData>();
		 reserveNPCs = new HashSet<Integer>();
		 random = new Random();
	}
	
	// Adds an NPC to the lists
	public void add(NPCData npc)
	{
		npcs.put(npc.id_NPC, npc);
		reserveNPCs.add(npc.id_NPC);
	}
	
	// Gets data on specified NPC
	// Gets the NPC record from memory.  Returns NULL for non-existent IDs
    public NPCData get(int id)
    {       	
    	if( npcs.containsKey(id) )
    		return npcs.get(id);
    	else
    	{
    		System.out.println("Warning: NPCPool.get() called on non-existent id '" + id + "'");
    		return null;
    	}
    }
    
    // Gets an NPCLocation from the pool and sets it as active
    public NPCData activate(int id)
    {
    	if( !reserveNPCs.contains(id) )
    		return null;

    	reserveNPCs.remove(id);
    	return npcs.get(id);
    }
    
    // Finds a random NPC from the pool and sets it as active
    public NPCData activateRandom()
    {
    	if( reserveNPCs.size() == 0 )
    		return null;
    	
    	ArrayList<Integer> removeList = new ArrayList<Integer>(reserveNPCs);

    	int id_NPC = removeList.remove(random.nextInt(reserveNPCs.size()));
    	reserveNPCs.remove(id_NPC);
    	return npcs.get(id_NPC);
    }
	
	
}
