package net.rageland.ragemod.world;

import net.rageland.ragemod.utilities.Util;

import org.bukkit.Location;
import org.bukkit.World;

public class Region3D extends Region
{
	public Location nwCorner;
	public Location seCorner;
	
	public Region3D( World world, String coords )
	{
		String[] split = coords.split(",");
		try
		{
			nwCorner = new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
			seCorner = new Location(world, Double.parseDouble(split[3]), Double.parseDouble(split[4]), Double.parseDouble(split[5]));
			this.world = world;
		}
		catch( Exception ex )
		{
			System.out.println("ERROR: Invalid coordinate string passed to Region3D constructor");
		}
	}
	
	public Region3D( World world, double x1, double y1, double z1, double x2, double y2, double z2 )
	{
		this.world = world;
		nwCorner = new Location(world, x1, y1, z1);
		seCorner = new Location(world, x2, y2, z2);
	}
	
	// Tests to see whether the current Location is inside the region
	public boolean isInside(Location loc)
	{
		return (loc.getWorld() == this.world) && 
				(Util.isBetween(loc.getX(), nwCorner.getX(), seCorner.getX()) &&
				Util.isBetween(loc.getY(), nwCorner.getY(), seCorner.getY()) &&
				Util.isBetween(loc.getZ(), nwCorner.getZ(), seCorner.getZ()));

	}
	

	




}
