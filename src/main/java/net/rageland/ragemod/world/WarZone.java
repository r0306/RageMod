package net.rageland.ragemod.world;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import uk.co.oliwali.HawkEye.HawkEye;

import de.diddiz.LogBlock.CommandsHandler;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.config.WarZoneConfig;
import net.rageland.ragemod.database.RageDB;

@SuppressWarnings("unused")
public class WarZone {
	
	private WarZoneConfig wConfig;
	private WarZone wz;
	Region2D region;
	public RageDB rdb;
	private RageMod plugin;

	public WarZoneConfig getwConfig() {
		return wConfig;
	}
	
	public class WarZoneRollback {
		
	}
	
	public WarZone() {
		 //constructor here
	}
	
	public boolean isInside(Location2D loc, Region2D r2d) {
		if ((r2d.nwCorner.x<loc.x && r2d.seCorner.x > loc.x) && (r2d.nwCorner.z > loc.z &&  r2d.seCorner.z < loc.z)) 
			return true;
		return false;
	}
	
	PluginManager pm = plugin.getServer().getPluginManager();
	
	public void createWarZone(Town t, Region2D r2d) {
		
		double xArea = (r2d.nwCorner.x-r2d.seCorner.x)/2*wConfig.WarZone_X_Factor;
		double zArea = (r2d.nwCorner.z-r2d.seCorner.z)/2*wConfig.WarZone_Z_Factor;
		double townArea = zArea+xArea;
		// TODO Expand the area - May be left out.
		// TODO Use it for the warzone
		// TODO Save around which town it is
	}
	
	CommandSender cmdSender;
	
	public void destroyWarZone(Town t, Region2D r2d) {
		
		double xArea = (r2d.nwCorner.x-r2d.seCorner.x)/2*wConfig.WarZone_X_Factor;
		double zArea = (r2d.nwCorner.z-r2d.seCorner.z)/2*wConfig.WarZone_Z_Factor;
		double townArea = zArea+xArea;
		
		// TODO Get this to work.
	}

}
