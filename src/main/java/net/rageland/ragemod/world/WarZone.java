package net.rageland.ragemod.world;

import net.rageland.ragemod.config.WarZoneConfig;
import net.rageland.ragemod.database.RageDB;

@SuppressWarnings("unused")
public class WarZone {
	
	private WarZoneConfig wConfig;
	private WarZone wz;
	Region2D region;
	public RageDB rdb;

	public WarZoneConfig getwConfig() {
		return wConfig;
	}
	
	public WarZone() {
		 //constructor here
	}
	
	public boolean isInside(Location2D loc, Region2D r2d) {
		if ((r2d.nwCorner.x<loc.x && r2d.seCorner.x > loc.x) && (r2d.nwCorner.z > loc.z &&  r2d.seCorner.z < loc.z)) 
			return true;
		return false;
	}
	
	public void createWarzone(Town t, Region2D r2d) {
		
		double xArea = (r2d.nwCorner.x-r2d.seCorner.x)/2*wConfig.WarZone_X_Factor;
		double zArea = (r2d.nwCorner.z-r2d.seCorner.z)/2*wConfig.WarZone_Z_Factor;
		double townArea = zArea+xArea;
		// Expand the area
		//use it for the warzone
		//save around which town it is
		// save all blocks in the zone
	}
	
	public void destroyWarZone() {
		//roll everything back
	}

}
