package net.rageland.ragemod;

import net.rageland.ragemod.data.Location2D;
import net.rageland.ragemod.utilities.Util;
import net.rageland.ragemod.world.Region2D;
import net.rageland.ragemod.world.Region3D;

import org.bukkit.Location;
import org.bukkit.World;

// TODO: Add messages for other worlds

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
    public Location2D worldSpawn;
    
    // Capitol
    private Region2D Capitol_RegionA;
    private Region2D Capitol_RegionB;
    public static Region3D Capitol_SandLot;
    public Location Capitol_Portal;
    
    // Travel Zone
    public Location TZ_Center;
    public Region3D TZ_Region;
    
	public enum Action {
		TOWN_CREATE;
	}
	
	public static enum Zone 
	{
		CAPITOL,
		A,
		B,
		C,
		OUTSIDE,
		UNKNOWN;
	}
	
	public static enum Quadrant 
	{
		NW,
		NE,
		SW,
		SE;
	}
	
	private RageMod plugin;
	
    
    public RageZones (RageMod ragemod, RageConfig config)
    {
    	plugin = ragemod;
    	world = plugin.getServer().getWorld("world");
    	nether = plugin.getServer().getWorld("world_nether");
    	
    	ZoneA_Name = config.Zone_NAME_A;
    	ZoneA_Border = config.Zone_BORDER_A;
    	ZoneB_Name = config.Zone_NAME_B;
    	ZoneB_Border = config.Zone_BORDER_B;
    	ZoneC_Name = config.Zone_NAME_C;
    	ZoneC_Border = config.Zone_BORDER_C;
    	
    	// Load the capitol regions
    	Capitol_RegionA = new Region2D(world, config.Capitol_X1a, config.Capitol_Z1a, config.Capitol_X2a, config.Capitol_Z2a);
    	Capitol_RegionB = new Region2D(world, config.Capitol_X1b, config.Capitol_Z1b, config.Capitol_X2b, config.Capitol_Z2b);
    	Capitol_SandLot = new Region3D(world, config.Capitol_SANDLOT);
    	Capitol_Portal = Util.getLocationFromCoords(world, config.Capitol_PORTAL_LOCATION);
    	
    	// Load the Travel Zone
    	TZ_Center = Util.getLocationFromCoords(nether, config.Zone_TZ_CENTER);
    	TZ_Region = new Region3D(nether, config.Zone_TZ_REGION);
    	
    	worldSpawn = new Location2D(world.getSpawnLocation());
    	
    }
    
    // Returns the name of the zone the Location is currently in
    // TODO: Remove this and make calls to it use a combination of GetCurrentZone and GetName
    public String getName(Location location) 
    {
    	if( location.getWorld().getName().equals("world") )
    	{
    		double distanceFromSpawn = worldSpawn.distance(location);
        	
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
    	else
    	{
    		if( isInTravelZone(location) )
    			return "Travel Zone";
    		else
    			return location.getWorld().getName();
    	}
    	
    	
    }
    
    // Return the name of the Zone matching the Zone enum
    public String getName(Zone zone)
    {
    	if( zone == Zone.A )
    		return ZoneA_Name;
    	else if( zone == Zone.CAPITOL )
    		return plugin.config.Capitol_Name;
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
    
    // Calculates the location's current zone based on their location
    public Zone getZone(Location location)
    {
    	if(!(location.getWorld().getName().equals("world")))
    		return Zone.UNKNOWN;
    	
		double distanceFromSpawn = worldSpawn.distance(location);
    	
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
    
    // Calculates the locations current quadrant based on their location
    // Uses the main world's spawn coords as a center point, regardless of actual world
    public Quadrant getQuadrant(Location location)
    {
    	double x = location.getX() - worldSpawn.getX();
    	double z = location.getZ() - worldSpawn.getZ();
    	
    	if( z >= 0 && x < 0 )
    		return Quadrant.NW;
    	else if( z < 0 && x < 0 )
    		return Quadrant.NE;
    	else if( z >= 0 && x >= 0 )
    		return Quadrant.SW;
    	else if( z < 0 && x >= 0 )
    		return Quadrant.SE;
    	else
    		return null;
    }
    
    public double getDistanceFromSpawn(Location location)
    {
    	return worldSpawn.distance(location);
    }
    
    // Returns whether or not the location is in Zone A
    public boolean isInZoneA(Location location)
    {
    	return ( location.getWorld().getName().equals("world") && 
				worldSpawn.distance(location) >= 0 && 
				worldSpawn.distance(location) <= ZoneA_Border );
    }
    
 // Returns whether or not the location is in Zone A
    public boolean isOutsideZoneA(Region2D region)
    {
    	return (worldSpawn.distance(region.nwCorner) > 1000 || 
    			worldSpawn.distance(region.seCorner) > 1000 ||
    			worldSpawn.distance(new Location2D(region.nwCorner.getX(), region.seCorner.getZ())) > 1000 ||
    			worldSpawn.distance(new Location2D(region.nwCorner.getZ(), region.seCorner.getX())) > 1000 );
    }
    
    // Returns whether or not the location is in Zone A
    public boolean isInZoneB(Location location)
    {
		return ( location.getWorld().getName().equals("world") && 
				worldSpawn.distance(location) > ZoneA_Border && 
				worldSpawn.distance(location) <= ZoneB_Border );
    }
    
    // Returns whether or not the location is in Zone A
    public boolean isInZoneC(Location location)
    {
    	return ( location.getWorld().getName().equals("world") && 
				worldSpawn.distance(location) > ZoneB_Border && 
				worldSpawn.distance(location) <= ZoneC_Border );
    }
    
    // Returns whether the player is in the world capitol
    public boolean isInCapitol(Location location)
    {
		return Capitol_RegionA.isInside(location) || Capitol_RegionB.isInside(location);
    }
    
    // Checks whether a specified action is allowed in the zone specified by 'location'
    public boolean checkPermission(Location location, Action action)
    {
    	// Put the most frequently called checks at the beginning.  On that note, would it be 
    	// better to split this method into multiple methods to prevent having to do so many comparisons?
    	if(action == Action.TOWN_CREATE)
    		return (isInZoneB(location));
    	
    	// If we haven't recognized the action, return false.  Should this throw an exception?
    	return false;
    }
  
    // Checks to see whether the location is inside the sand lot
    public boolean isInSandlot(Location location)
    {
    	return Capitol_SandLot.isInside(location);
    }
    
    // Checks to see whether the location is inside the travel zone
    public boolean isInTravelZone(Location location)
    {
    	return this.TZ_Region.isInside(location);    	
    }
    
    // Calculates the coordinates of a town's travel node
    public Location getTravelNode(Location centerPoint)
    {
    	return new Location(this.nether, 
    			((centerPoint.getX() - worldSpawn.getX()) / 8) + TZ_Center.getX(),
    			TZ_Center.getY(),
    			((centerPoint.getZ() - worldSpawn.getZ()) / 8) + TZ_Center.getZ());
    }

//  enum.toString().  Who knew?  XD
//	public String quadrantName(Quadrant quadrant) 
//	{
//		switch(quadrant)
//		{
//			case NW:
//				return "NW"; 
//			case NE:
//				return "NE"; 
//			case SW:
//				return "SW"; 
//			case SE:
//				return "SE"; 
//			default:
//				return "Error";
//				
//		}
//	}
    

}
