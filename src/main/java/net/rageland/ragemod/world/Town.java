package net.rageland.ragemod.world;

import java.util.ArrayList;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.ZoneHandler.Quadrant;
import net.rageland.ragemod.npc.NPCLocation;

import org.bukkit.Location;
import org.bukkit.World;

public abstract class Town 
{
	public int id;
	public String name;
	
	protected Region2D region;
	protected World world;
	protected Quadrant quadrant;
	
	public Location centerPoint;
	public TownLevel townLevel;				// Corresponds to the HashMap TownLevels in Config
	protected ArrayList<NPCLocation> npcLocations;
	
	protected RageMod plugin;
	
	
	
	public Town(RageMod plugin, int id, String name, World world)
	{
		this.plugin = plugin;
		this.setId(id);
		this.name = name;
		this.world = world;
		this.npcLocations = new ArrayList<NPCLocation>();
	}
	
	public abstract String getCodedName();
	public abstract void update();				// Updates the town in the database
	
	// Returns a Location at the center of the town
	public Location getCenter()
	{
		return new Location(world, centerPoint.getX(), 65, centerPoint.getZ());
	}
	
	// Get the town ID
	public int getID()
	{
		return getId();
	}
	
	// Get the town name
	public String getName()
	{
		return name;
	}
	
	// Returns whether or not the specified location is inside the region
	public boolean isInside(Location location)
	{
		return region.isInside(location);
	}
	
	// Gets the Quadrant that this location is in
	public Quadrant getQuadrant()
	{
		if( this.quadrant == null )
			this.quadrant = plugin.zones.getQuadrant(this.centerPoint);
		
		return this.quadrant;
	}
	
	// Adds an NPCLocation to the town's list
	public void addNPCLocation(NPCLocation npcLocation)
	{
		this.npcLocations.add(npcLocation);
	}
	
	// Return a list of all NPCLocations
	public ArrayList<NPCLocation> getNPCLocations()
	{
		return npcLocations;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
