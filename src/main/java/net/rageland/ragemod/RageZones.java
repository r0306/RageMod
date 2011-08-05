package net.rageland.ragemod;

import net.rageland.ragemod.data.Location2D;
import net.rageland.ragemod.data.Region2D;
import net.rageland.ragemod.data.Region3D;

import org.bukkit.Location;
import org.bukkit.World;

// TODO: Calculate locations with y = 64  >_<

// Contains static info and utility methods for processing zone code
public class RageZones {
	
    public String ZoneA_Name;
    public int ZoneA_Border; 
    public String ZoneB_Name;
    public int ZoneB_Border; 
    public String ZoneC_Name;
    public int ZoneC_Border;
    
    public World world;
    public World nether;
    public Location2D WorldSpawn;
    
    private Region2D Capitol_RegionA;
    private Region2D Capitol_RegionB;
    public static Region3D Capitol_SandLot;
    
    // Travel Zone
    public Location TZ_Center;
    
    
	public enum Action {
		TOWN_CREATE;
	}
	
	public static enum Zone {
		A,
		B,
		C,
		OUTSIDE,
		UNKNOWN;
	}
	
	private RageMod plugin;
	
    
    public RageZones (RageMod ragemod, RageConfig config)
    {
    	plugin = ragemod;
    	world = plugin.getServer().getWorld("world");
    	nether = plugin.getServer().getWorld("world_nether");
    	
    	// TODO: This feels redundant.  Maybe it will make more sense when the config is loading from a file.
    	ZoneA_Name = config.Zone_NAME_A;
    	ZoneA_Border = config.Zone_BORDER_A;
    	ZoneB_Name = config.Zone_NAME_B;
    	ZoneB_Border = config.Zone_BORDER_B;
    	ZoneC_Name = config.Zone_NAME_C;
    	ZoneC_Border = config.Zone_BORDER_C;
    	
    	// Load the capitol regions
    	Capitol_RegionA = new Region2D(config.Capitol_X1a, config.Capitol_Z1a, config.Capitol_X2a, config.Capitol_Z2a);
    	Capitol_RegionB = new Region2D(config.Capitol_X1b, config.Capitol_Z1b, config.Capitol_X2b, config.Capitol_Z2b);
    	Capitol_SandLot = new Region3D(world, config.Capitol_SANDLOT);
    	
    	// Load the Travel Zone
    	TZ_Center = Util.getLocationFromCoords(nether, config.Zone_TZ_CENTER);
    	
    	WorldSpawn = new Location2D(world.getSpawnLocation());
    	
    }
    
    // Returns the name of the zone the Location is currently in
    // TODO: Remove this and make calls to it use a combination of GetCurrentZone and GetName
    public String getName(Location location) 
    {
    	if(!(location.getWorld().getName().equals("world")))
    		return location.getWorld().getName();
    		
    	double distanceFromSpawn = WorldSpawn.distance(location);
    	
    	if( distanceFromSpawn >= 0 && distanceFromSpawn <= ZoneA_Border )
    		return ZoneA_Name;
    	else if( distanceFromSpawn <= ZoneB_Border )
    		return ZoneB_Name;
    	else if( distanceFromSpawn <= ZoneC_Border )
    		return ZoneC_Name;
    	else if( distanceFromSpawn > ZoneC_Border )
    		return "Outside All Zones";
    	else
    		return "Error: Distance from spawn returned negative";
    }
    
    // Return the name of the Zone matching the Zone enum
    public String getName(Zone zone)
    {
    	if( zone == Zone.A )
    		return ZoneA_Name;
    	else if( zone == Zone.B )
    		return ZoneB_Name;
    	else if( zone == Zone.C )
    		return ZoneC_Name;
    	else if( zone == Zone.OUTSIDE )
    		return "Outside All Zones";
    	else if( zone == Zone.UNKNOWN)
    		return "Unknown";
    	else
    		return "Error: Zone unrecognized";
    }
    
    // Calculates the player's current zone based on their location
    public Zone getCurrentZone(Location location)
    {
    	if(!(location.getWorld().getName().equals("world")))
    		return Zone.UNKNOWN;
    
    	
		double distanceFromSpawn = WorldSpawn.distance(location);
    	
    	if( distanceFromSpawn >= 0 && distanceFromSpawn <= ZoneA_Border )
    		return Zone.A;
    	else if( distanceFromSpawn <= ZoneB_Border )
    		return Zone.B;
    	else if( distanceFromSpawn <= ZoneC_Border )
    		return Zone.C;
    	else if( distanceFromSpawn > ZoneC_Border )
    		return Zone.OUTSIDE;
    	else
    		return null;
    }
    
    public double getDistanceFromSpawn(Location location)
    {
    	return WorldSpawn.distance(location);
    }
    
    // Returns whether or not the location is in Zone A
    public boolean isInZoneA(Location location)
    {
    	return ( location.getWorld().getName().equals("world") && 
				WorldSpawn.distance(location) >= 0 && 
				WorldSpawn.distance(location) <= ZoneA_Border );
    }
    
    // Returns whether or not the location is in Zone A
    public boolean isInZoneB(Location location)
    {
		return ( location.getWorld().getName().equals("world") && 
				WorldSpawn.distance(location) > ZoneA_Border && 
				WorldSpawn.distance(location) <= ZoneB_Border );
    }
    
    // Returns whether or not the location is in Zone A
    public boolean isInZoneC(Location location)
    {
    	return ( location.getWorld().getName().equals("world") && 
				WorldSpawn.distance(location) > ZoneB_Border && 
				WorldSpawn.distance(location) <= ZoneC_Border );
    }
    
    // Returns whether the player is in the world capitol
    public boolean isInCapitol(Location location)
    {
		return (
				(location.getWorld().getName().equals("world")) && 
				( Capitol_RegionA.isInside(location) || Capitol_RegionB.isInside(location) ) 
				);
    }
    
    // Checks whether a specified action is allowed in the zone specified by 'location'
    public boolean checkPermission(Location location, Action action)
    {
    	// Put the most frequently called checks at the beginning.  On that note, would it be 
    	// better to split this method into multiple methods to prevent having to do so many comparisons?
    	if(action == Action.TOWN_CREATE)
    		return (isInZoneB(location) && location.getWorld().getName().equals("world"));
    	
    	// If we haven't recognized the action, return false.  Should this throw an exception?
    	return false;
    }
  
    // Checks to see whether the location is inside the sand lot
    public boolean isInSandlot(Location location)
    {
    	return ( Capitol_SandLot.isInside(location) && location.getWorld().getName().equals("world"));
    }
    

}
