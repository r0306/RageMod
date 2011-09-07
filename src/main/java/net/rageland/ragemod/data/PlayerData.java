package net.rageland.ragemod.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.World;

import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.RageZones;
import net.rageland.ragemod.RageZones.Zone;
import net.rageland.ragemod.quest.PlayerQuestData;
import net.rageland.ragemod.quest.KillCreatureQuest;

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
	
	// NPCTown info
	public String npcTownName = "";
	public boolean isSteward = false;
	
	// Lot info
	public ArrayList<Lot> lots = new ArrayList<Lot>();
	public ArrayList<String> lotPermissions = new ArrayList<String>();	// array of player names allowed to build
	
	// Messages
	public String logonMessageQueue = "";
	
	// Permits
	public Permits permits = new Permits();
	
	// NPC-related info
	private HashMap<Integer, Integer> languageSkill;		// Skill in each language (id 1-4), up to 100
	private HashSet<Integer> npcInteractions;				// List of which NPCInstances the player has interacted with
	private HashSet<Integer> newNPCInteractions;			// List of interactions from this session
	private HashMap<Integer, Float> npcAffinity;			// NPCs' friendliness towards player
	
	// ***** STATE (Non-DB) VALUES *****
	
	// Current location
	public Zone currentZone;
	public Town currentTown;
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
		this.languageSkill = new HashMap<Integer, Integer>();
		this.npcInteractions = new HashSet<Integer>();
		this.newNPCInteractions = new HashSet<Integer>();
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
	public boolean isInsideOwnLot(Location location)
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
	
	// Gets the skill level for the specified language
	public int getLanguageSkill(int id)
	{
		return this.languageSkill.get(id);
	}
	
	// Sets the skill level for the specified language
	public void setLanguageSkill(int id, int value)
	{
		this.languageSkill.put(id, value);
	}
	
	// Records a player's interaction with an NPC instance
	// Returns true if language skill was increased
	public boolean recordNPCInteraction(NPCInstance instance)
	{
		if( !this.npcInteractions.contains(instance.getID()) )
		{
			// Record the interaction
			this.npcInteractions.add(instance.getID());
			this.newNPCInteractions.add(instance.getID());
			
			// Increase the language skill, if applicable
			if( instance.getRaceID() != plugin.config.NPC_HUMAN_ID && this.languageSkill.get(instance.getRaceID()) < 100 )
			{
				this.languageSkill.put(instance.getRaceID(), this.languageSkill.get(instance.getRaceID()) + 1);
				return true;
			}
			
			// Increase the affinity
			int npcID = instance.getNPCid();
			if( npcAffinity.containsKey(npcID) )
			{
				npcAffinity.put(npcID, npcAffinity.get(npcID) + plugin.config.NPC_AFFINITY_GAIN_TALK);
				if( npcAffinity.get(npcID) > plugin.config.NPC_AFFINITY_MAX )
					npcAffinity.put(npcID, plugin.config.NPC_AFFINITY_MAX);
			}
			else
				npcAffinity.put(npcID, instance.getDefaultAffinity() + plugin.config.NPC_AFFINITY_GAIN_TALK);
		}
		
		return false;
	}
	
	// Gets all NPC interactions
	public HashSet<Integer> getNewInteractions()
	{
		return this.newNPCInteractions;
	}
	
	// Sets the instance list
	public void setInteractions(HashSet<Integer> interactions)
	{
		this.npcInteractions = interactions;
	}
	
	// Sets the instance list
	public void setAffinities(HashMap<Integer, Float> affinities)
	{
		this.npcAffinity = affinities;
	}
	
	public HashMap<Integer, Float> getAffinity()
	{
		return this.npcAffinity;
	}
	
	
}
