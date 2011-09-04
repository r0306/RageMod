package net.rageland.ragemod.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

// Storage class for all data relating to NPC pool
public class NPCPool 
{
	// Master list
	private HashMap<Integer, NPCData> npcs;
	
	// Reserve lists
	private HashSet<Integer> reserveNPCs;		// A list of all NPCs that are not currently spawned
	private HashMap<Integer, HashSet<Integer>> floatingNPCs;		// <ID_NPCRace, ID_NPC>  all NPCs that can spawn anywhere
	private HashMap<Integer, HashSet<Integer>> residentNPCs;		// <ID_NPCTown, ID_NPC>  all NPCs that are fixed to a specific town
	
	private Random random;

	public NPCPool()
	{
		 npcs = new HashMap<Integer, NPCData>();
		 random = new Random();
		 
		 // Set up the reserve lists
		 reserveNPCs = new HashSet<Integer>();
		 floatingNPCs = new HashMap<Integer, HashSet<Integer>>();
		 residentNPCs = new HashMap<Integer, HashSet<Integer>>();
	}
	
	// Adds an NPC to the lists
	public void add(NPCData npc)
	{
		npcs.put(npc.id_NPC, npc);
		reserveNPCs.add(npc.id_NPC);
		
		if( npc.id_NPCTown == 0 )
		{
			if( !floatingNPCs.containsKey(npc.id_NPCRace) )
				floatingNPCs.put(npc.id_NPCRace, new HashSet<Integer>());
			floatingNPCs.get(npc.id_NPCRace).add(npc.id_NPC);
		}
    	else
    	{
    		if( !residentNPCs.containsKey(npc.id_NPCTown) )
    			residentNPCs.put(npc.id_NPCTown, new HashSet<Integer>());
    		residentNPCs.get(npc.id_NPCTown).add(npc.id_NPC);
    	}
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

    	NPCData npc = npcs.get(id);
    	
    	// Remove the NPC from all appropriate lists
    	reserveNPCs.remove(id);
    	if( npc.id_NPCTown == 0 )
    		floatingNPCs.get(npc.id_NPCRace).remove(id);
    	else
    		residentNPCs.get(npc.id_NPCTown).remove(id);
    	
    	npc.activate();
    	return npc;
    }
    
    // Finds a random NPC from the pool and sets it as active
    public NPCData activateRandomFloating(int id_NPCRace)	// 0 = any race
    {
    	if( reserveNPCs.size() == 0 )
    		return null;
    	
    	System.out.println("ReserveNPCs size: " + reserveNPCs.size());
    	
    	ArrayList<Integer> removeList = new ArrayList<Integer>();
    	
    	if( id_NPCRace == 0 )
    	{
    		// Add all races
    		for( HashSet<Integer> set : floatingNPCs.values() )
    			removeList.addAll(set);
    	}
    	else
    		removeList.addAll(floatingNPCs.get(id_NPCRace));
    				
    	if( removeList.size() == 0 )
    		return null;

    	int id_NPC = removeList.get(random.nextInt(removeList.size()));
    	return activate(id_NPC);    
    }

    // Returns an NPC to the pool
    public void deactivate(int id)
    {
    	if( reserveNPCs.contains(id) || npcs.get(id) == null )
    		return;
    	
    	NPCData npc = npcs.get(id);
    	
    	reserveNPCs.add(id);
    	if( npc.id_NPCTown == 0 )
			floatingNPCs.get(npc.id_NPCRace).add(npc.id_NPC);
    	else
    		residentNPCs.get(npc.id_NPCTown).add(npc.id_NPC);
    	
    	npcs.get(id).deactivate();
    }

	// Activates a random NPC in a town
    public NPCData activateRandomInTown(int id) 
	{
    	if( reserveNPCs.size() == 0 || residentNPCs.get(id) == null || residentNPCs.get(id).size() == 0 )
    		return null;
    	
    	ArrayList<Integer> removeList = new ArrayList<Integer>();
    	removeList.addAll(residentNPCs.get(id));

    	int id_NPC = removeList.get(random.nextInt(removeList.size()));
    	return activate(id_NPC);  
	}

	
	
}
