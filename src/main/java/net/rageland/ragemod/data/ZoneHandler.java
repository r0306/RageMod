package net.rageland.ragemod.data;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.config.RageConfig;
import net.rageland.ragemod.config.WarZoneConfig;
import net.rageland.ragemod.config.ZonesConfig;
import net.rageland.ragemod.utilities.Util;
import net.rageland.ragemod.world.Location2D;
import net.rageland.ragemod.world.Region2D;
import net.rageland.ragemod.world.Region3D;
import net.rageland.ragemod.world.Zone;

@SuppressWarnings("unused")
public class ZoneHandler {
	
	public String ZoneA_Name;
	public int ZoneA_Border; 
	public String ZoneB_Name;
	public int ZoneB_Border; 
	public String ZoneC_Name;
	public int ZoneC_Border;
	
	private final ArrayList<Zone> zones = new ArrayList<Zone>();
	private Location2D worldSpawn;
    public World world;
    public World nether;
    
    private Region2D Capitol_RegionA;	  	
    private Region2D Capitol_RegionB;
    public static Region3D Capitol_SandLot;
    public Location Capitol_Portal;
    
    private RageConfig config;
    
    // Travel Zone <- TODO not sure how this looks like
    public Location TZ_Center;
    public Region3D TZ_Region;
    
    // War Zone <- TODO Probably not correct.
    public Location WZ_Center;
    public Region3D WZ_Region;
	
    private WarZoneConfig wzConfig;
    
	public static enum Quadrant {
		NW,
		NE,
		SW,
		SE;
	}

	//TODO Create an infinite long zone
	@SuppressWarnings("null")
	public ZoneHandler(RageMod plugin){
		ZonesConfig[] configs = null;
		ZonesConfig[] temp = new ZonesConfig[configs.length];
		for (int i= 0;i < configs.length;i++){
			if (temp[configs[i].getPosition()] == null){
				temp[configs[i].getPosition()] = configs[i];
			}else{
				//I'm an error that gets thrown then
			}
		}
		for (int i= 0;i < temp.length;i++){
			if (temp[i]!= null){
				this.zones.add(new net.rageland.ragemod.world.Zone(plugin,temp[i], wzConfig)); //wzConfig may be wrong here.
			}else{
				//I'm an error that gets thrown then
			}
		}
		// Perdemot, ATM it doesn't load the capitol regions, I'm adding it temporarily, as it's done been implemented another way yet..
		      ZoneA_Name = config.Zone_NAME_A;	
		      ZoneA_Border = config.Zone_BORDER_A;			  	
		      ZoneB_Name = config.Zone_NAME_B;			  	
		      ZoneB_Border = config.Zone_BORDER_B;			  	
		      ZoneC_Name = config.Zone_NAME_C;			  	
		      ZoneC_Border = config.Zone_BORDER_C;

		      Capitol_RegionA = new Region2D(world, config.Capitol_X1a, config.Capitol_Z1a, config.Capitol_X2a, config.Capitol_Z2a);  	
		      Capitol_RegionB = new Region2D(world, config.Capitol_X1b, config.Capitol_Z1b, config.Capitol_X2b, config.Capitol_Z2b);	  	
		      Capitol_SandLot = new Region3D(world, config.Capitol_SANDLOT);
		      Capitol_Portal = Util.getLocationFromCoords(world, config.Capitol_PORTAL_LOCATION);
		      
		      Capitol_RegionA = new Region2D(world, config.Capitol_X1a, config.Capitol_Z1a, config.Capitol_X2a, config.Capitol_Z2a);
		      Capitol_RegionB = new Region2D(world, config.Capitol_X1b, config.Capitol_Z1b, config.Capitol_X2b, config.Capitol_Z2b);
		      Capitol_SandLot = new Region3D(world, config.Capitol_SANDLOT);
		      Capitol_Portal = Util.getLocationFromCoords(world, config.Capitol_PORTAL_LOCATION);
		      
		      
		      // Is the travel zone supposed to be in the nether?
		      TZ_Center = Util.getLocationFromCoords(nether, config.Zone_TZ_CENTER);
		      TZ_Region = new Region3D(nether, config.Zone_TZ_REGION);
		      
		      WZ_Center = Util.getLocationFromCoords(world, wzConfig.Zone_WZ_CENTER);
		      WZ_Region = new Region3D(world, wzConfig.Zone_WZ_REGION);
		      
		      worldSpawn = new Location2D(world.getSpawnLocation());
	}
	
	public int getoutestline(){
		return this.zones.get(this.zones.size()).getOuterLine();
	}
	
	public Zone isInside(Location loc){
		for (int i= 0;i < this.zones.size();i++){
			if (this.zones.get(i).isInside(loc)) return this.zones.get(i);
		}
		return null;
		//should not happen ... Das ist error
	}
	
	public Zone[] getZones(){
		return (Zone[]) this.zones.toArray();
	}
	
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
    
    public Location getTravelNode(Location centerPoint)
    {
    	return new Location(this.nether, 
    			((centerPoint.getX() - worldSpawn.getX()) / 8) + TZ_Center.getX(),
    			TZ_Center.getY(),
    			((centerPoint.getZ() - worldSpawn.getZ()) / 8) + TZ_Center.getZ());
    }
    
    public String getName(Location loc) {
		for (int i= 0;i < this.zones.size();i++){
			if (this.zones.get(i).isInside(loc)) return this.zones.get(i).getConfig().getName();
		}
		return "An error has ocurred. You appear to not be in a zone!";
		//should not happen
    }

	public int getDistanceFromSpawn(Location location) {
		return (int)this.worldSpawn.distance(location);
	}

	public boolean isInSandlot(Location location) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInTravelZone(Location location) {
		// TODO Auto-generated method stub
		return false;
	}

}
