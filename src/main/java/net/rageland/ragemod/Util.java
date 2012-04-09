package net.rageland.ragemod;

import java.sql.Timestamp;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

// Misc. methods

@SuppressWarnings("deprecation")
public class Util 
{	
	// Formats the cooldown time into a lovely string
	public static String formatCooldown(int totalSeconds)
	{
		int minutes, seconds;
		
		minutes = totalSeconds / 60;
		seconds = totalSeconds % 60;
		
		java.text.DecimalFormat nft = new java.text.DecimalFormat("#00.###");
		nft.setDecimalSeparatorAlwaysShown(false);
		
		return minutes + ":" + nft.format(seconds);
	}
	
	// Returns the current time
	public static Timestamp now()
	{
		Date today = new java.util.Date();
		Timestamp now = new java.sql.Timestamp(today.getTime());
		return now;
	}
	
	// Returns the number of days between the two Timestamps
	public static int daysBetween(Timestamp time1, Timestamp time2)
	{
		return (int)((time1.getTime() - time2.getTime()) / 86400000);
	}
	
	// Returns the number of seconds since the Timestamp occurred
	public static int secondsSince(Timestamp timestamp)
	{
		return (int)((now().getTime() - timestamp.getTime()) / 1000);
	}
	
	// Returns a Timestamp representing minutes from now
	public static Timestamp minutesFromNow(int minutes)
	{
		return new Timestamp(now().getTime() + (minutes * 60000));
	}
	
	public static CreatureType getCreatureTypeFromEntity(Creature creature)
	{
		
		if(creature instanceof Spider)
		{
			return CreatureType.SPIDER;
		}
		else if(creature instanceof Wolf)
		{
			return CreatureType.WOLF;
		}
		else if(creature instanceof Creeper)
		{
			return CreatureType.CREEPER;
		}
		else if(creature instanceof Slime)
		{
			return CreatureType.SLIME;
		}
		else if(creature instanceof Skeleton)
		{
			return CreatureType.SKELETON;
		}
		else if(creature instanceof Zombie)
		{
			return CreatureType.ZOMBIE;
		}
		else if(creature instanceof PigZombie)
		{
			return CreatureType.PIG_ZOMBIE;
		}
		else if(creature instanceof Squid)
		{
			return CreatureType.SQUID;
		}
		else if(creature instanceof Chicken)
		{
			return CreatureType.CHICKEN;
		}
		else if(creature instanceof Cow)
		{
			return CreatureType.COW;
		}
		else if(creature instanceof Ghast)
		{
			return CreatureType.GHAST;
		}
		else if(creature instanceof Pig)
		{
			return CreatureType.PIG;
		}
		else if(creature instanceof Sheep)
		{
			return CreatureType.SHEEP;
		}

		else if(creature instanceof Giant)
		{
			return CreatureType.GIANT;
		}
		else if (creature instanceof EnderDragon)
		{
			return CreatureType.ENDER_DRAGON;
		} else if (creature instanceof Enderman) {
			return CreatureType.ENDERMAN;
		} else {
			return CreatureType.valueOf("Unknown");
		}
	}
	
	// Parses a set of coordinates from the config file
	public static Location getLocationFromCoords(World world, String coords)
	{
		String[] split = coords.split(",");
		try
		{
			return new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
		}
		catch( Exception ex )
		{
			System.out.println("ERROR: Invalid coordinate string passed to Util.getLocationFromCoords(): " + coords);
			return null;
		}
	}
	
	// Returns the point between the two points
	public static double between(double x1, double x2)
	{
		return x1 - ((x1 - x2) / 2) ;
	}
	
	// Checks to see whether the number is between the two extremes
	public static boolean isBetween(double test, double a1, double a2)
	{
		return (test >= a1 && test <= a2) || (test <= a1 && test >= a2);
	}
	
	// Finds the nearest spot to teleport to by looking upwards
	public static Location findTeleportLocation(Location location)
	{
		Block blockA, blockB;
		
		for( int i = (int)location.getY(); i < 127; i++ )
		{
			blockA = location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX(), i, location.getZ()));
			blockB = location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX(), i-1, location.getZ()));
			
			if( blockA.getType() == Material.AIR && blockB.getType() == Material.AIR )
				return new Location(location.getWorld(), location.getX(), i-1, location.getZ());
		}
		
		return null;
	}
	
	// Returns the appropriate affinity code (-2 -> 2) for the affinity value (-10 -> 10)
	public static int getAffinityCode(float affinity)
	{
		int affinityCode = Math.round(affinity / 4);
		if( affinityCode > 2 )
			affinityCode = 2;
		else if( affinityCode < -2 )
			affinityCode = -2;
		
		return affinityCode;
	}
	
	
	
}
