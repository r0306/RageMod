package net.rageland.ragemod.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// Storage class for all data relating to NPC pool
public class NPCPool 
{
	private HashMap<Integer, NPC> npcs;
	private ArrayList<Integer> reserveNPCs;		// A list of all NPCs that are not currently spawned
	// TODO: List of NPCs for each NPC town
	
	private Random random;

	public NPCPool()
	{
		 npcs = new HashMap<Integer, NPC>();
		 reserveNPCs = new ArrayList<Integer>();
		 random = new Random();
	}
	
	// Adds an NPC to the lists
	public void add(NPC npc)
	{
		npcs.put(npc.id_NPC, npc);
		reserveNPCs.add(npc.id_NPC);
	}
	
	// Gets data on specified NPC
	// Gets the NPC record from memory.  Returns NULL for non-existent IDs
    public NPC get(int id)
    {       	
    	if( npcs.containsKey(id) )
    		return npcs.get(id);
    	else
    	{
    		System.out.println("Warning: NPCPool.get() called on non-existent id '" + id + "'");
    		return null;
    	}
    }
    
    // Finds a random NPC from the pool and sets it as active
    public NPC activate()
    {
    	if( reserveNPCs.size() == 0 )
    		return null;

    	int id_NPC = reserveNPCs.remove(random.nextInt(reserveNPCs.size()));
    	return npcs.get(id_NPC);
    }
	
	
}
