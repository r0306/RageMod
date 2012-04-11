package net.rageland.ragemod.npcentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import net.citizensnpcs.lib.NPCSpawner;
import net.citizensnpcs.lib.creatures.CreatureNPC;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.npc.NPCData;
import net.rageland.ragemod.npc.NPCInstance;
import net.rageland.ragemod.npc.NPCLocation;
import net.rageland.ragemod.npc.NPCLocationPool;
import net.rageland.ragemod.npc.NPCPool;
import net.rageland.ragemod.npc.NPCTown;
import net.rageland.ragemod.npc.NPCInstance.NPCType;
import net.rageland.ragemod.npcentities.RageEntity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.PluginManager;

@SuppressWarnings("unused")
public class RageNPCManager
{
	int taskid;
	static int staskid;
	private RageMod plugin;
	private NPCSpawner npcSpawner;
	private Random random;
	private static RageMod pluginS;
	
	// Data storage
	private static HashMap<Integer, NPCInstance> sActiveNPCs = new HashMap<Integer, NPCInstance>();
	private HashMap<Integer, NPCInstance> activeNPCs = new HashMap<Integer, NPCInstance>();
	private NPCLocationPool npcLocationPool;
	private NPCPool npcPool;
	private static NPCPool sNpcPool;
	private static NPCLocationPool sNpcLocationPool;

	public RageNPCManager(RageMod plugin) 
	{
		this.plugin = plugin;
		random = new Random();
		
		// On startup, pull all the NPC data from the DB into memory 		
		this.npcLocationPool = plugin.database.npcQueries.loadNPCLocations();
		this.npcPool = plugin.database.npcQueries.loadNPCs();
		
		this.taskid = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() 
		{
			public void run() 
			{
				// TODO Fix this!
				HashSet<Integer> toRemove = new HashSet<Integer>();
				for (int i : RageMod.getInstance().npcManager.getActiveNPCs().keySet()) 
				{
					
					// TODO This needs porting to Citizens
					
					//net.minecraft.server.Entity j = (net.minecraft.server.Entity)RageMod.getInstance().npcManager.activeNPCs.get(i).getEntity(); // TODO Fix this!
					//j.aA();
					//if (j.dead) 
					{
						toRemove.add(i);
					}
				}
				for (int n : toRemove)
					RageNPCManager.this.despawnById(n);
			}
		}, 100L, 100L);
						Listener listener;
						PluginManager pm = plugin.getServer().getPluginManager();
						SL sl = plugin.sl;
						WL wl = plugin.wl;
						pm.registerEvents(sl, plugin);
						pm.registerEvents(wl, plugin);
						

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

	// Despawn one NPC by ID
    public void despawnById(int id) 
	{
		NPCInstance instance = getActiveNPCs().get(id);
		despawn(instance);
		getActiveNPCs().remove(id);
	}
	
	// Despawn the NPC
	private void despawn(NPCInstance instance)
	{
		if (instance == null)
			return;
		
		// Return the NPC and Location back to the pool
		npcPool.deactivate(instance.getNPCid());
		npcLocationPool.deactivate(instance.getLocation().getID());
		
		RageEntity npc = instance.getEntity();
		if (npc == null)
			return;
		
		try 
		{
			// TODO Fix this!
			final World world;
			//world.removeEntity(npc);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void sDespawn(NPCInstance instance)
	{
		if (instance == null)
			return;
		
		// Return the NPC and Location back to the pool
		sNpcPool.deactivate(instance.getNPCid());
		sNpcLocationPool.deactivate(instance.getLocation().getID());
		
		RageEntity npc = instance.getEntity();
		if (npc == null)
			return;
		
		try 
		{
			// TODO Fix this!
			//npc.world.removeEntity(npc);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	// Despawn all active NPCs
	public void despawnAll(boolean deleteInstances) 
	{
		for (NPCInstance instance : getActiveNPCs().values()) 
		{
			if( deleteInstances && instance != null ) 
				plugin.database.npcQueries.disableInstance(instance.getID());
			
			despawn(instance);
		}

		getActiveNPCs().clear();
	}
	
	public static void sDespawnAll(boolean deleteInstances) 
	{
		for (NPCInstance instance : sActiveNPCs.values()) 
		{
			if( deleteInstances && instance != null ) 
				pluginS.database.npcQueries.disableInstance(instance.getID());
			
			sDespawn(instance);
		}

		sActiveNPCs.clear();
	}
  // TODO Fix this!
	public void moveNPC(int id, Location l) 
	{
		NPCInstance npc = RageMod.getInstance().npcManager.getActiveNPCs().get(id);
		if (npc != null)
			npc.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
	}

	public void moveNPCStatic(int id, Location l) 
	{
		NPCInstance npc = RageMod.getInstance().npcManager.getActiveNPCs().get(id);
		if (npc != null)
			npc.setPosition(l.getX(), l.getY(), l.getZ());
	}

	// TODO Oy Perdemot, look at these 2 methods below \/. - To fix them you need to look at NPCInstance.
	
	public void putNPCinbed(int id, Location bed) 
	{
		NPCInstance npc = RageMod.getInstance().npcManager.getActiveNPCs().get(id);
		if (npc != null) 
		{
			npc.setPosition(bed.getX(), bed.getY(), bed.getZ());
			npc.a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
		}
	}

	// TODO Oy Perdemot, look at the method above and the one below. - To fix them you need to look at NPCInstance.
	
	public void getNPCoutofbed(int id) 
	{
		NPCInstance npc = RageMod.getInstance().npcManager.getActiveNPCs().get(id);
		if (npc != null)
			npc.a(true, true, true);
	}
	
	// TODO Oy Perdemot, look at these two methods above /\. - To fix them you need to look at NPCInstance.

	public void setSneaking(int id, boolean flag) 
	{
		NPCInstance npc = RageMod.getInstance().npcManager.getActiveNPCs().get(id);
		if (npc != null)
			npc.setSneak(flag);
	}

	public RageEntity getRageEntity(int id) 
	{
		return (RageEntity) activeNPCs.get(id).getEntity();
	}

	public static boolean isNPC1(org.bukkit.entity.Entity e) 
	{
		return ((CreatureNPC) e).getHandle() instanceof RageEntity;
	}

	public int getNPCIdFromEntity(org.bukkit.entity.Entity e) 
	{
		if ((e instanceof HumanEntity)) 
		{
			for (int i : getActiveNPCs().keySet()) 
			{
				if (((RageEntity) getActiveNPCs().get(i).getEntity()).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) 
				{
					return i;
				}
			}
		}
		return -1;
	}

	public static RageEntity getNPCFromEntity(org.bukkit.entity.Entity e) 
	{
		if ((e instanceof HumanEntity)) 
		{
			for (int i : RageMod.getInstance().npcManager.getActiveNPCs().keySet()) 
			{
				if (((RageEntity) RageMod.getInstance().npcManager.getActiveNPCs().get(i).getEntity()).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) 
				{
					return (RageEntity) RageMod.getInstance().npcManager.getActiveNPCs().get(i).getEntity();
				}
			}
		}
		return null;
	}

	// Adds a new instance to the HashMap
	public void addInstance(int id_NPCInstance, NPCInstance instance) 
	{
		this.getActiveNPCs().put(id_NPCInstance, instance);
	}

	// Checks to see if the entity is contained by activeNPCs
	public boolean contains(RageEntity RageEntity) 
	{
		for( NPCInstance instance : getActiveNPCs().values() )
		{
			if( instance.getEntity() == RageEntity )
				return true;
		}
		return false;
	}
	
    // Return all instances
    public ArrayList<NPCInstance> getAllInstances()
    {
    	return new ArrayList<NPCInstance>(getActiveNPCs().values());
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
		
		for( NPCInstance instance : getActiveNPCs().values() )
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
		ArrayList<NPCInstance> instances = new ArrayList<NPCInstance>(getActiveNPCs().values());
		
		for( NPCInstance instance : instances )
		{
			if( instance.isExpired() )
			{
				despawn(instance);
				getActiveNPCs().remove(instance.getID());
				System.out.println("Automatically despawned NPC " + instance.getName());
			}
		}
	}
	
	// Returns all residents of a particular town (if any)
	public HashSet<NPCData> getAllResidents(int id_NPCTown)
	{
		return npcPool.getAllResidents(id_NPCTown);
	}

	public static boolean isNPC(Entity defenderEntity) {
		// TODO Unfinished
		if (!(defenderEntity instanceof Player) && !(defenderEntity instanceof Creature)) {
			return true;
		}
		return false;
	}

	public HashMap<Integer, NPCInstance> getActiveNPCs() {
		return activeNPCs;
	}

	public void setActiveNPCs(HashMap<Integer, NPCInstance> activeNPCs) {
		this.activeNPCs = activeNPCs;
	}
	
	public HashMap<Integer, NPCInstance> getActiveNPCs() {		
	    return activeNPCs;	 	
	  }		
	public void setActiveNPCs(HashMap<Integer, NPCInstance> activeNPCs) {
		this.activeNPCs = activeNPCs;
	 	 	
	}
}