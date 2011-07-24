package net.rageland.ragemod.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

import net.rageland.ragemod.RageMod;

// Stores a HashMap of all the Lots in the capitol
public class Lots 
{
	
	// The String key is Category (single letter) + ID; eg. W12
	private HashMap<String, Lot> lots;
	private RageMod plugin;
	
	public Lots(RageMod plugin)
	{
		this.plugin = plugin;
		lots = new HashMap<String, Lot>();
	}
	
	// On startup, pull all the Lot data from the DB into memory 
	public void loadLots()
	{
		lots = plugin.database.lotQueries.loadLots();
		
		if(lots == null) 
		{
			lots = new HashMap<String, Lot>();
		}
	}
	
	// Insert/update town info
	public void put(Lot lot)
	{
		lots.put(lot.getLotCode(), lot);
	}
	
	// Gets the lot from memory.  Returns NULL for non-existent lot
    public Lot get(String lotCode)
    {       	
    	if( lots.containsKey(lotCode.toUpperCase()) )
    		return lots.get(lotCode.toUpperCase());
    	else
    	{
    		System.out.println("Error: Lots.Get called on non-existent lot code " + lotCode);
    		return null;
    	}
    }
    
    // Gets the lot by database ID
    public Lot get(int id)
    {
    	for( Lot lot : lots.values() )
		{
    		if( lot.id_Lot == id )
			{
				return lot;
			}
		}
    	
    	// If we reach this point, we did not find a lot
    	System.out.println("Error: Lots.Get called on non-existent lot ID " + id);
    	return null;
    }
    
    // Returns all lots
    public ArrayList<Lot> getAll()
    {
    	ArrayList<Lot> lotsList = new ArrayList<Lot>();
    	
    	for(Lot lot : lots.values()) 
    	{
    		lotsList.add(lot);
    	}
    	
    	return lotsList;
    }
    
    // Find which lot the player is standing in, if any
    public Lot findCurrentLot(Location loc)
    {
    	for( Lot lot : lots.values() )
		{
    		if( lot.isInside(loc) )
			{
				return lot;
			}
		}
    	
    	// If we reach this point, we did not find a lot
    	return null;
    }
    
	    

}
