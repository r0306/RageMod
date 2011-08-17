package net.rageland.ragemod.data;

import org.bukkit.Location;
import org.bukkit.World;

public class NPCLocation extends Location
{
	private int id_NPCLocation;
	private int id_NPCTown;		// A value of 0 indicates not inside an NPC town
	private int id_NPCRace;		// A value of 0 indicates no preference for NPC race
	
	// Default constructor
	public NPCLocation(World world, double x, double y, double z, float yaw, float pitch)
	{
		super(world, x, y, z, yaw, pitch);
	}
	
	// Create a new NPCLocation based on a Location object
	public NPCLocation(Location location)
	{
		super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}
	
	// Used when loading NPCLocations from the database
	public void setIDs(int id_NPCLocation, int id_NPCTown, int id_NPCRace)
	{
		this.id_NPCLocation = id_NPCLocation;
		this.id_NPCTown = id_NPCTown;
		this.id_NPCRace = id_NPCRace;
	}
	
	// Returns the ID
	public int getID()
	{
		return id_NPCLocation;
	}

}
