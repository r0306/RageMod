package net.rageland.ragemod;

import java.sql.Timestamp;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
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
		else 
		{
			return CreatureType.MONSTER;
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
	
	
}
