package net.rageland.ragemod.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.World;

import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.RageZones;
import net.rageland.ragemod.RageZones.Zone;
import net.rageland.ragemod.quest.PlayerQuestData;
import net.rageland.ragemod.quest.KillCreatureQuest;

// TODO: Create a colored player name that takes their data into account to be easily pulled by Commands, etc

// TODO: Should I be storing IDs for towns and such for all player data?  Then I would call the PlayerTowns hash
//		 every time I need to retrieve the name.

public class PlayerData 
{
	// ***** DATABASE VALUES *****
	
	// Basic data
	public int id_Player;
	public String name;
	public int id_Faction;
	public boolean isMember = false;
	public Date memberExpiration;
	public float bounty;
	public float extraBounty;
	public boolean persistentInDatabase;
	
	// Home (used for capitol lots)
	private Location home_Location;
	public Timestamp home_LastUsed;
	
	// Spawn (used for player town beds)
	private Location spawn_Location;
	public Timestamp spawn_LastUsed;
	
	// Town info
	public String townName = "";
	public boolean isMayor = false;
	public double treasuryBalance = 0;
	public int treasuryBlocks;				// Number of gold blocks stored in town's treasury
	
	// Lot info
	public ArrayList<Lot> lots = new ArrayList<Lot>();
	public ArrayList<String> lotPermissions = new ArrayList<String>();	// array of player names allowed to build
	
	// Messages
	public String logonMessageQueue = "";
	
	// Permits
	public Permits permits = new Permits();
	
	
	// ***** STATE (Non-DB) VALUES *****
	
	// Current location
	public Zone currentZone;
	public PlayerTown currentTown;
	public boolean isInCapitol;
	public Timestamp enterLeaveMessageTime = null;		// Prevent message spam by only allowing a message every 10 seconds (while people work on walls, etc)
	
	// Quest data
	public PlayerQuestData activeQuestData = new PlayerQuestData();
	
	// Misc.
	private RageMod plugin;
	
	
	
	// Constructor
	public PlayerData( RageMod plugin )
	{
		this.plugin = plugin;
	}
	
	// Sets the spawn location when bed clicked
	public void setSpawn(Location location)
	{
		spawn_Location = location;
	}
	
	// Returns a Location object of the spawn location
	public Location getSpawn()
	{
		return spawn_Location;
	}
	
	// Sets the home location when bed clicked
	public void setHome(Location location)
	{
		home_Location = location;
	}
	
	// Returns a Location object of the home location
	public Location getHome()
	{
		return home_Location;
	}
	
	// Clears the spawn location when bed is broken	
	public void clearSpawn() 
	{
		spawn_Location = null;
	}
	
	// Clears the home location when bed is broken	
	public void clearHome() 
	{
		home_Location = null;
	}
	
	// Checks whether the current location is inside one of the player's lots
	public boolean isInsideLot(Location location)
	{
		if(!location.getWorld().getName().equalsIgnoreCase("world"))
			return false;
		
		for( Lot lot : this.lots )
		{
			if( lot.isInside(location) )
			{
				return true;
			}
		}
		
		// The location was not in any of the player's lots
		return false;
	}
	
	public boolean isOnKillQuest() {
		if(this.activeQuestData.isPlayerOnQuest() && activeQuestData.getQuest() instanceof KillCreatureQuest)
		{
			return true;
		}
		else
		{
			return false;
		}
			
	}
	
	// Returns the player name with special color tags to be interpreted by the messaging methods
	public String getCodedName() 
	{
		char colorCode;
		
		if( id_Faction != 0 )
			colorCode = plugin.factions.getColorCode(this.id_Faction);
		else if( RageMod.permissionHandler.inGroup("world", this.name, "Owner") )
			colorCode = 'o';
		else if( RageMod.permissionHandler.inGroup("world", this.name, "Admin") )
			colorCode = 'a';
		else if( RageMod.permissionHandler.inGroup("world", this.name, "Moderator") )
			colorCode = 'm';
		else if( RageMod.permissionHandler.inGroup("world", this.name, "Citizen") )
			colorCode = 'c';
		else
			colorCode = 't';	// tourist
			
		return "<p" + colorCode + ">" + this.name + "</p" + colorCode + ">";
	}
	
	// Updates the playerData in the database
	public void update()
	{
		plugin.database.playerQueries.updatePlayer(this);
	}
	
	
}
