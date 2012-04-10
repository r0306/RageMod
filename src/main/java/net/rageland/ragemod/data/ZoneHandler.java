package net.rageland.ragemod.data;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.config.ZonesConfig;
import net.rageland.ragemod.world.Location2D;
import net.rageland.ragemod.world.Region3D;
import net.rageland.ragemod.world.Zone;

public class ZoneHandler {
	
	private final ArrayList<Zone> zones = new ArrayList<Zone>();
	private Location2D worldSpawn;
    public World world;
    public World nether;
    
    // Travel Zone <- TODO not sure how this looks like
    public Location TZ_Center;
    public Region3D TZ_Region;
	
	public static enum Quadrant 
	{
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
				this.zones.add(new Zone(plugin,temp[i]));
			}else{
				//I'm an error that gets thrown then
			}
		}
	}
	
	public int getoutestline(){
		return this.zones.get(this.zones.size()).getOuterLine();
	}
	
	public Zone isInside(Location loc){
		for (int i= 0;i < this.zones.size();i++){
			if (this.zones.get(i).isInside(loc)) return this.zones.get(i);
		}
		return null;
		//should not happen ...
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
		return "U no Zone aka Error";
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
