package net.rageland.ragemod.data;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.RageZones.Quadrant;
import net.rageland.ragemod.RageZones.Zone;

import org.bukkit.Location;
import org.bukkit.World;

public class NPCLocation extends Location
{
	private int id_NPCLocation;
	private int id_NPCTown = 0;			// A value of 0 indicates not inside an NPC town
	private int id_NPCRace;				// A value of 0 indicates no preference for NPC race
	private RageMod plugin;
	private Zone zone;	
	private Quadrant quadrant;
	private boolean inUse = false;
	private NPCInstance instance;
	
	// Default constructor
	public NPCLocation(World world, double x, double y, double z, float yaw, float pitch, RageMod plugin)
	{
		super(world, x, y, z, yaw, pitch);
		this.plugin = plugin;
	}
	
	// Create a new NPCLocation based on a Location object
	public NPCLocation(Location location, RageMod plugin)
	{
		super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		this.plugin = plugin;
	}
	
	// Used when loading NPCLocations from the database
	public void setIDs(int id_NPCLocation, int id_NPCTown, int id_NPCRace)
	{
		this.id_NPCLocation = id_NPCLocation;
		this.id_NPCTown = id_NPCTown;
		this.id_NPCRace = id_NPCRace;
	}
	
	// Sets the current location to "in use"
	public void activate()
	{
		this.inUse = true;
	}
	
	// Sets the current location to "in reserve"
	public void deactivate()
	{
		this.inUse = false;
	}
	
	// Returns whether the location is in use
	public boolean isActivated()
	{
		return inUse;
	}
	
	// Returns the ID
	public int getID()
	{
		return id_NPCLocation;
	}
	
	// Returns the NPCTown ID
	public int getTownID()
	{
		return id_NPCTown;
	}
	
	// Returns the NPCTown, if any
	public NPCTown getTown()
	{
		if( this.id_NPCTown != 0 )
			return plugin.towns.getNPCTown(id_NPCTown);
		else
			return null;
	}
	
	// Gets the Zone that this location is in
	public Zone getZone()
	{
		if( this.zone == null )
		{
			if( plugin.zones.isInCapitol(this))
				this.zone = Zone.CAPITOL;
			else
				this.zone = plugin.zones.getZone(this);
		}
			
		
		return this.zone;
	}
	
	// Gets the Quadrant that this location is in
	public Quadrant getQuadrant()
	{
		if( this.quadrant == null )
			this.quadrant = plugin.zones.getQuadrant(this);
		
		return this.quadrant;
	}
	
	// Sets the attached instance
	public void setInstance(NPCInstance instance)
	{
		this.instance = instance;
	}
	
	// Gets the attached instance, if any
	public NPCInstance getInstance()
	{
		return instance;
	}

}
