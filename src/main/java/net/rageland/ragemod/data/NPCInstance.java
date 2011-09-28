package net.rageland.ragemod.data;

import java.sql.Timestamp;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.getspout.spoutapi.SpoutManager;
import org.martin.bukkit.npclib.BServer;
import org.martin.bukkit.npclib.BWorld;

import net.minecraft.server.ItemInWorldManager;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.npcentities.RageEntity;
//import net.rageland.ragemod.npcentities.QuestNPC;
import net.rageland.ragemod.npcentities.SpeechNPC;

public class NPCInstance 
{
	// Basic data
	private int id_NPCInstance;
	private Timestamp despawnTime;
	
	// Data collections
	private NPCData data;	
	private NPCLocation location;
	private RageEntity entity;
	// QUEST DATA GOES HERE :)
	
	// Server infrastructure
	public RageMod plugin;
	public BServer server;
	public BWorld world;
	
	// Quest, if questNPC
	public String questId;
	public int questNPCType; // START, END or STARTEND
	
	
	// NPC Types
	public enum NPCType
	{
		BASIC (0),
		SPEECH (1),
		QUEST (2);		// Combine all quest into one?
		
		private int value;
		
		NPCType(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return this.value;
		}
		
		// All of this fancy int-enum conversion is for database storage
		public static NPCType getType(int value)
		{
			switch( value )
			{
				case 1:
					return SPEECH;
				default:
					return BASIC;
			}
		}
	}

	private NPCType type;
	
	public NPCInstance(RageMod plugin, int id, NPCType type, Timestamp despawnTime)
	{
		this.plugin = plugin;
		this.server = BServer.getInstance(plugin);
		this.id_NPCInstance = id;
		this.type = type;
		this.despawnTime = despawnTime;
	}
	
	// Activates the instance by claiming the NPC and Location from the pool
	public boolean activate(int id_NPC, int id_NPCLocation) throws Exception
	{
		data = plugin.npcManager.activateNPC(id_NPC);
		location = plugin.npcManager.activateLocation(id_NPCLocation);
		
		// Make sure the activate was valid
		if( data == null || location == null )
		{
			plugin.npcManager.deactivateNPC(id_NPC);
			plugin.npcManager.deactivateLocation(id_NPCLocation);
			return false;
		}

		this.world = new BWorld(location.getWorld());
		
		return true;
	}
	
	// Sets the NPC ID and Location ID, in case they have already been activated
	public void setIDs(int id_NPC, int id_NPCLocation)
	{
		data = plugin.npcManager.getNPC(id_NPC);
		location = plugin.npcManager.getLocation(id_NPCLocation);
		this.world = new BWorld(location.getWorld());
	}
	
	// Sets the NPC and Location objects, in case they have already been activated
	public void setData(NPCData data, NPCLocation location) 
	{
		this.data = data;
		this.location = location;
		this.world = new BWorld(location.getWorld());
	}
	
	public NPCData getData()
	{
		return data;
	}
	
	// Returns the INSTANCE id
	public int getID()
	{
		return this.id_NPCInstance;
	}

	// Returns the NPC name
	public String getName() 
	{
		return data.name;
	}
	
	// Returns the NPC name with the appropriate color, for displaying above the heads
	public String getColorName()
	{
		// Quest NPCs will use AQUA, speech will use DARK_AQUA
		return ChatColor.DARK_AQUA + data.name;
	}
	
	// Returns the NPC name with coded tags for parsing
	public String getCodedName()
	{
		return "<pn>" + data.name + "</pn>";
	}

	// Returns the NPC (NOT the instance) id
	public int getNPCid() 
	{
		return data.id_NPC;
	}

	// Return the NPCLocation object
	public NPCLocation getLocation() 
	{
		return location;
	}
	
	// Gets the NPC entity
	public RageEntity getEntity()
	{
		return entity;
	}
	
	// Spawns the NPC in the world
	public void spawn() throws Exception
	{
		if( data == null || location == null )
			throw new Exception("NPCInstance.spawn() called on non-activated instance");
		
		BWorld world = new BWorld(location.getWorld());
		
		// Spawn the type of NPC 
		switch(type)
		{
			case SPEECH:
				entity = new SpeechNPC(this); 
				break;
//			case QUEST:
//				entity = new QuestNPC(this);
			default:
				entity = new RageEntity(this);
		}

		// Set the position and put the entity in the world
		entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		world.getWorldServer().addEntity(entity);
		plugin.npcManager.addInstance(this.id_NPCInstance, this);
		location.setInstance(this);
		
		this.setSkin();
	}

	// Returns whether the despawnTime is in the past
	public boolean isExpired() 
	{
		return Util.now().getTime() > despawnTime.getTime();
	}
	
	// Returns the race of this NPC
	public int getRaceID()
	{
		return data.id_NPCRace;
	}
	
	// Returns the NPC Town ID, if any
	public int getTownID()
	{
		return location.getTownID();
	}
	
	// Sets up the skin of the entity
	public void setSkin()
	{
		SpoutManager.getAppearanceManager().setGlobalSkin((HumanEntity)entity.getBukkitEntity(), plugin.config.CUSTOM_SKIN_PATH + data.skinPath);
	}
	
	// Gets the default affinity
	public float getDefaultAffinity()
	{
		return data.defaultAffinityCode * 4;
	}
	
	// Gets the NPCData object
	public NPCData getNPCData()
	{
		return this.data;
	}
	
	


	

	
	
	
	
}
