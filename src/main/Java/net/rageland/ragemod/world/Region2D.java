package net.rageland.ragemod.world;

import net.rageland.ragemod.utilities.Util;

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
	
	public Region2D( World world, String coords )
	{
		String[] split = coords.split(",");
		try
		{
			nwCorner = new Location2D(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
			seCorner = new Location2D(Double.parseDouble(split[2]), Double.parseDouble(split[3]));
			this.world = world;
		}
		catch( Exception ex )
		{
			System.out.println("ERROR: Invalid coordinate string passed to Region2D constructor");
		}
	}
	
	// Tests to see whether the current Location is inside the region
	public boolean isInside(Location loc)
	{
		return (loc.getWorld() == this.world) && (Util.isBetween(loc.getX(), nwCorner.getX(), seCorner.getX()) &&
				(Util.isBetween(loc.getZ(), nwCorner.getZ(), seCorner.getZ())));   
	}
	


}
