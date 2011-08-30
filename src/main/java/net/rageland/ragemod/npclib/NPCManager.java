package net.rageland.ragemod.npclib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.WorldServer;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCData;
import net.rageland.ragemod.data.NPCInstance;
import net.rageland.ragemod.data.NPCLocation;
import net.rageland.ragemod.data.NPCLocationPool;
import net.rageland.ragemod.data.NPCPool;
import net.rageland.ragemod.data.NPCTown;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.data.NPCInstance.NPCType;
import net.rageland.ragemod.npcentities.NPCEntity;
import net.rageland.ragemod.npcentities.SpeechData;
import net.rageland.ragemod.npcentities.SpeechNPC;
import net.rageland.ragemod.quest.Quest;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.plugin.java.JavaPlugin;

public class NPCManager 
{
	private BServer server;
	private int taskid;
	private RageMod plugin;
	//private NPCSpawner npcSpawner;
	private Random random;
	
	// Data storage
	private HashMap<Integer, NPCInstance> activeNPCs = new HashMap<Integer, NPCInstance>();
	private NPCLocationPool npcLocationPool;
	private NPCPool npcPool;	

	public NPCManager(RageMod plugin) 
	{
		this.server = BServer.getInstance(plugin);
		this.plugin = plugin;
		random = new Random();
		
		// On startup, pull all the NPC data from the DB into memory 		
		this.npcLocationPool = plugin.database.npcQueries.loadNPCLocations();
		this.npcPool = plugin.database.npcQueries.loadNPCs();
		
		this.taskid = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() 
		{
			public void run() 
			{
				HashSet<Integer> toRemove = new HashSet<Integer>();
				for (int i : RageMod.getInstance().npcManager.activeNPCs.keySet()) 
				{
					net.minecraft.server.Entity j = (net.minecraft.server.Entity)RageMod.getInstance().npcManager.activeNPCs.get(i).getEntity();
					j.R();
					if (j.dead) 
					{
						toRemove.add(i);
					}
				}
				for (int n : toRemove)
					NPCManager.this.despawnById(n);
			}
		}, 100L, 100L);
		plugin.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, new SL(), Priority.Normal, plugin);
		plugin.getServer().getPluginManager().registerEvent(Event.Type.CHUNK_LOAD, new WL(), Priority.Normal, plugin);
	}
	
	// Used on startup to create NPC entities for all stored instances
	public void spawnAllInstances() 
	{
		ArrayList<NPCInstance> npcInstances = plugin.database.npcQueries.loadInstances();
		
		// Spawn all NPCs with active Instances
		for( NPCInstance instance : npcInstances )
		{
			try
			{
				instance.spawn();
			}
			catch( Exception ex )
			{
				System.out.println("Error spawning NPC Instance #" + instance.getID() + ": " + ex.getMessage());
			}
		}
	}

	// Adds a new NPCLocation
	public void addLocation(NPCLocation npcLocation)
	{
		npcLocationPool.add(npcLocation);
	}
	
	// Gets the NPCLocation from memory.  Returns NULL for non-existent IDs
    public NPCLocation getLocation(int id)
    {       	
    	return npcLocationPool.get(id);
    }
    
    // TODO: Remove this and wrap its functionality up locally
    public NPCLocation activateLocation(int id)
    {
    	return npcLocationPool.activate(id);
    }
    public void deactivateLocation(NPCLocation location)
    {
    	npcLocationPool.deactivate(location.getID());
    }
    public void deactivateLocation(int id)
    {
    	npcLocationPool.deactivate(id);
    }
    
    // Return all locations
    public ArrayList<NPCLocation> getAllLocations()
    {
    	return npcLocationPool.getAllLocations();
    }
    
	// Adds a new NPC
	public void addNPC(NPCData npc)
	{
		npcPool.add(npc);
	}
	
	// Gets the NPC record from memory.  Returns NULL for non-existent IDs
    public NPCData getNPC(int id)
    {       	
    	return npcPool.get(id);
    }
    
    // TODO: Remove this and wrap its functionality up locally
    public NPCData activateNPC(int id)
    {
    	return npcPool.activate(id);
    }
    public NPCData activateRandomFloatingNPC()
    {
    	return npcPool.activateRandomFloating(0);
    }
    public void deactivateNPC(int id)
    {
    	npcPool.deactivate(id);
    }
	
//	public NPCSpawner getSpawner()
//	{
//		return npcSpawner;
//	}

//	public NPCEntity spawnNPC(String name, Location l, int id) 
//	{
//		if (activeNPCs.containsKey(id)) 
//		{
//			this.server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
//			return (NPCEntity) activeNPCs.get(id).getEntity();
//		}
//		if (name.length() > 14) 
//		{
//			String tmp = name.substring(0, 14);
//			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 14 characters,");
//			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
//			name = tmp;
//		}
//		BWorld world = new BWorld(l.getWorld());
//		NPCEntity npcEntity = new NPCEntity(this.server.getMCServer(), world.getWorldServer(), name, new ItemInWorldManager( world.getWorldServer()), plugin, l);
//		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
//		world.getWorldServer().addEntity(npcEntity);
//		activeNPCs.put(id, npcEntity);
//		return npcEntity;
//	}
	
//	public HashMap<Integer, NPCEntity> getNpcs()
//	{
//		return activeNPCs;
//	}
	
//	public void addSpeechMessage(String npcname, String message)
//	{
//		if(activeNPCs.containsKey(npcname))
//		{
//			activeNPCs.get(npcname).addSpeechMessage(message);
//		}
//		else
//		{
//			this.server.getLogger().log(Level.WARNING, "Invalid npc name, could not add speech message.");
//		}
//	}

	// Despawn one NPC by ID
    public void despawnById(int id) 
	{
		NPCInstance instance = activeNPCs.get(id);
		despawn(instance);
		activeNPCs.remove(id);
	}
	
	// Despawn the NPC
	private void despawn(NPCInstance instance)
	{
		if (instance == null)
			return;
		
		// Return the NPC and Location back to the pool
		npcPool.deactivate(instance.getNPCid());
		npcLocationPool.deactivate(instance.getLocation().getID());
		
		NPCEntity npc = instance.getEntity();
		if (npc == null)
			return;
		
		try 
		{
			npc.world.removeEntity(npc);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	// Despawn all active NPCs
	public void despawnAll(boolean deleteInstances) 
	{
		for (NPCInstance instance : activeNPCs.values()) 
		{
			if( deleteInstances && instance != null ) 
				plugin.database.npcQueries.disableInstance(instance.getID());
			
			despawn(instance);
		}

		activeNPCs.clear();
	}
//
//	public void moveNPC(int id, Location l) 
//	{
//		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
//		if (npc != null)
//			npc.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
//	}
//
//	public void moveNPCStatic(int id, Location l) 
//	{
//		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
//		if (npc != null)
//			npc.setPosition(l.getX(), l.getY(), l.getZ());
//	}
//
//	public void putNPCinbed(int id, Location bed) 
//	{
//		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
//		if (npc != null) 
//		{
//			npc.setPosition(bed.getX(), bed.getY(), bed.getZ());
//			npc.a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
//		}
//	}
//
//	public void getNPCoutofbed(int id) 
//	{
//		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
//		if (npc != null)
//			npc.a(true, true, true);
//	}
//
//	public void setSneaking(int id, boolean flag) 
//	{
//		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
//		if (npc != null)
//			npc.setSneak(flag);
//	}

	public NPCEntity getNPCEntity(int id) 
	{
		return (NPCEntity) activeNPCs.get(id).getEntity();
	}

	public static boolean isNPC(org.bukkit.entity.Entity e) 
	{
		return ((CraftEntity) e).getHandle() instanceof NPCEntity;
	}

	public int getNPCIdFromEntity(org.bukkit.entity.Entity e) 
	{
		if ((e instanceof HumanEntity)) 
		{
			for (int i : activeNPCs.keySet()) 
			{
				if (((NPCEntity) activeNPCs.get(i).getEntity()).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) 
				{
					return i;
				}
			}
		}
		return -1;
	}

	public static NPCEntity getNPCFromEntity(org.bukkit.entity.Entity e) 
	{
		if ((e instanceof HumanEntity)) 
		{
			for (int i : RageMod.getInstance().npcManager.activeNPCs.keySet()) 
			{
				if (((NPCEntity) RageMod.getInstance().npcManager.activeNPCs.get(i).getEntity()).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) 
				{
					return (NPCEntity) RageMod.getInstance().npcManager.activeNPCs.get(i).getEntity();
				}
			}
		}
		return null;
	}

//	public void rename(int id, String name) 
//	{
//		if (name.length() > 16) 
//		{ // Check and nag if name is too long, spawn
//		  // NPC anyway with shortened name.
//			String tmp = name.substring(0, 16);
//			server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
//			server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
//			name = tmp;
//		}
//		NPCEntity npc = getNPCEntity(id);
//		npc.setName(name);
//		BWorld b = new BWorld(npc.getBukkitEntity().getLocation().getWorld());
//		WorldServer s = b.getWorldServer();
//		try 
//		{
//			Method m = s.getClass().getDeclaredMethod("d", new Class[] { Entity.class });
//			m.setAccessible(true);
//			m.invoke(s, (Entity) npc);
//			m = s.getClass().getDeclaredMethod("c", new Class[] { Entity.class });
//			m.setAccessible(true);
//			m.invoke(s, (Entity) npc);
//		} 
//		catch (Exception ex) 
//		{
//			ex.printStackTrace();
//		}
//		s.everyoneSleeping();
//	}

	private class SL extends ServerListener 
	{
		private SL() 
		{
			
		}

		public void onPluginDisable(PluginDisableEvent event) 
		{
			if (event.getPlugin() == NPCManager.this.plugin) 
			{
				NPCManager.this.despawnAll(false);
				NPCManager.this.plugin.getServer().getScheduler().cancelTask(NPCManager.this.taskid);
			}
		}
	}

	private class WL extends WorldListener 
	{
		private WL() 
		{
			
		}

		public void onChunkLoad(ChunkLoadEvent event) 
		{
			for (NPCInstance npcInstance : RageMod.getInstance().npcManager.activeNPCs.values())
			{
				NPCEntity npc = null;
				if( npcInstance != null )
					npc = npcInstance.getEntity();
				
				if ((npc != null) && (event.getChunk() == npc.getBukkitEntity().getLocation().getBlock().getChunk())) 
				{
					BWorld world = new BWorld(event.getWorld());
					world.getWorldServer().addEntity(npc);
				}
			}
		}
	}

	// Adds a new instance to the HashMap
	public void addInstance(int id_NPCInstance, NPCInstance instance) 
	{
		this.activeNPCs.put(id_NPCInstance, instance);
	}

	// Checks to see if the entity is contained by activeNPCs
	public boolean contains(NPCEntity npcEntity) 
	{
		for( NPCInstance instance : activeNPCs.values() )
		{
			if( instance.getEntity() == npcEntity )
				return true;
		}
		return false;
	}
	
    // Return all instances
    public ArrayList<NPCInstance> getAllInstances()
    {
    	return new ArrayList<NPCInstance>(activeNPCs.values());
    }

    // Associates NPCLocations with NPCTowns, called on startup
	public void associateLocations() 
	{
		for( NPCLocation location : this.npcLocationPool.getAllLocations() )
		{
			if( location.getTown() != null )
				location.getTown().addNPCLocation(location);
		}
	}
	
	// Return all active NPCs not in any NPCTown
	public ArrayList<NPCInstance> getFloatingInstances()
	{
		ArrayList<NPCInstance> instances = new ArrayList<NPCInstance>();
		
		for( NPCInstance instance : activeNPCs.values() )
		{
			if( instance.getLocation().getTown() == null )
				instances.add(instance);
		}
		
		return instances;
	}
	
	// Spawns a random NPC at a non-town location
	public NPCInstance spawnRandomFloating()
	{
		NPCLocation location;
		NPCData npc;
		
		try
		{
			// Get a random NPCLocation from the reserve pool
			location = this.npcLocationPool.activateRandomFloating();
			if( location == null )
				throw new Exception("Could not activate an NPCLocation.");
			
			// Activate a random NPC from the pool
			npc = this.npcPool.activateRandomFloating(0);
			if( npc == null )
			{
				this.deactivateLocation(location);
				throw new Exception("There are no more NPCs in the pool to activate.");
			}
			
			return spawn(location, npc);
		}
		catch( Exception ex )
		{
			System.out.println("Error in NPCManager.spawnRandomFloating(): " + ex.getMessage());
		}
		
		return null;
	}
	
	// Generate a random time to live in minutes
	public int generateTTL()
	{
		return random.nextInt(plugin.config.NPC_TTL_MAX - plugin.config.NPC_TTL_MIN) + plugin.config.NPC_TTL_MIN;
	}
	
	// Returns a list of all NPCLocations for a given town
	public ArrayList<NPCLocation> getActiveTownLocations(int id)
	{
		return npcLocationPool.getActiveTownLocations(id);
	}

	// Spawns a random NPC inside of a town
	public NPCInstance spawnRandomInTown(int id) 
	{
		NPCLocation location;
		NPCData npc;
		NPCTown town = plugin.towns.getNPCTown(id); 
		
		try
		{
			// Get a random NPCLocation from the reserve pool
			location = this.npcLocationPool.activateRandomInTown(id);
			if( location == null )
				throw new Exception("Could not activate an NPCLocation.");
			
			// Activate a random NPC from the pool
			// Determine whether it will be a resident or guest based on odds set in config
			if( random.nextInt(100) < plugin.config.NPCTOWN_GUEST_CHANCE )
				npc = npcPool.activateRandomFloating(town.id_NPCRace);
			else
				npc = npcPool.activateRandomInTown(id);
			
			if( npc == null )
			{
				this.deactivateLocation(location);
				throw new Exception("There are no more NPCs in the pool to activate.");
			}
			
			return spawn(location, npc);
		}
		catch( Exception ex )
		{
			System.out.println("Error in NPCManager.spawnRandomInTown(): " + ex.getMessage());
		}
		
		return null;
	}
	
	// Spawns a new NPC Instance
	public NPCInstance spawn(NPCLocation location, NPCData npc) throws Exception
	{
		NPCInstance instance;
		
		// Register the NPC instance and spawn the NPC
		instance = plugin.database.npcQueries.createInstance(
				npc.id_NPC, location.getID(), this.generateTTL(), 0, NPCType.SPEECH);
		instance.setData(npc, location);
		instance.spawn();
				
		return instance;
	}

	// Despawns all NPCs whose time has expired
	public void despawnExpired() 
	{
		ArrayList<NPCInstance> instances = new ArrayList<NPCInstance>(activeNPCs.values());
		
		for( NPCInstance instance : instances )
		{
			if( instance.isExpired() )
			{
				despawn(instance);
				activeNPCs.remove(instance.getID());
				System.out.println("Automatically despawned NPC " + instance.getName());
			}
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}