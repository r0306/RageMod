package net.rageland.ragemod.world;

import org.bukkit.Location;
import org.bukkit.World;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.config.ZonesConfig;

public class Zone {
	private ZonesConfig config;
	private RageMod plugin;
	private int Beginning;
	
    public World world;
    public World nether;
    
    private Location2D worldSpawn;
    private Region2D Capitol_Region;
    
    public Location Capitol_Portal;
    public Location TZ_Center;
    public Region3D TZ_Region;
    
    public Zone(RageMod Plugin, ZonesConfig config){
    	this.config= config;
    	this.plugin=Plugin;
    	this.worldSpawn= new Location2D(world.getSpawnLocation());
    	this.Beginning = this.plugin.zones.getoutestline();
    }
    
    public boolean isInside(Location loc){
    	if ((int)worldSpawn.distance(loc) >= this.Beginning && (int)worldSpawn.distance(loc)<= (this.Beginning+this.config.getWidth())){
        	return true;	
    	}else{
    		return false;
    	}
    }
    
    public boolean isInsideCapitol(Location loc){
    	return (this.Capitol_Region.isInside(loc));

    }
    
    public ZonesConfig getConfig(){
    	return this.config;
    }
    
    public int getOuterLine(){
    	return this.Beginning+this.config.getWidth();
    }
    

}
