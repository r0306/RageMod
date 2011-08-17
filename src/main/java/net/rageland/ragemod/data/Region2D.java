package net.rageland.ragemod.data;

import org.bukkit.Location;
import org.bukkit.World;

// Stores a rectangular area from bedrock to sky.  Used for Towns and Lots
public class Region2D extends Region
{
	public Location2D nwCorner;
	public Location2D seCorner;
	
	public Region2D (World world, Location2D _nwCorner, Location2D _seCorner)
	{
		this.world = world;
		nwCorner = _nwCorner;
		seCorner = _seCorner;
	}
	
	public Region2D (World world, int x1, int z1, int x2, int z2)
	{
		this.world = world;
		nwCorner = new Location2D((double)x1, (double)z1);
		seCorner = new Location2D((double)x2, (double)z2);
	}
	
	public Region2D (World world, double x1, double z1, double x2, double z2)
	{
		this.world = world;
		nwCorner = new Location2D(x1, z1);
		seCorner = new Location2D(x2, z2);
	}
	
	public Region2D (World world, Location l1, Location l2)
	{
		this.world = world;
		nwCorner = new Location2D(l1);
		seCorner = new Location2D(l2);
	}
	
	// Tests to see whether the current Location is inside the region
	public boolean isInside(Location loc)
	{
		return (loc.getWorld() == this.world) && ((loc.getX() >= nwCorner.getX() && loc.getX() < (seCorner.getX())) &&
				(loc.getZ() < nwCorner.getZ() && loc.getZ() >= (seCorner.getZ())));   // Stupid Z axis is reversed
	}
	


}
