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
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.npcentities.NPCEntity;
import net.rageland.ragemod.npcentities.QuestStartNPCEntity;
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
	private NPCSpawner npcSpawner;
	private Random random;
	
	// Data storage
	public HashMap<Integer, NPCEntity> activeNPCs = new HashMap<Integer, NPCEntity>();
	private NPCLocationPool npcLocationPool;
	private NPCPool npcPool;	
	private ArrayList<NPCInstance> npcInstances;

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
					net.minecraft.server.Entity j = (net.minecraft.server.Entity)RageMod.getInstance().npcManager.activeNPCs.get(i);
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
		npcSpawner = new NPCSpawner();
	}
	
	// Used on startup to create NPC entities for all stored instances
	public void spawnAllInstances() 
	{
		this.npcInstances = plugin.database.npcQueries.loadInstances();
		
		// Retrieve a number of phrases based on the number of NPC instances
		ArrayList<String> phrases = plugin.database.npcQueries.getPhrases(this.npcInstances.size() * 2);
		
		// Spawn all NPCs with active Instances
		for( NPCInstance instance : this.npcInstances )
		{
			// TODO: Support multiple NPC types
			plugin.npcManager.getSpawner().speechNPC(instance.getColorName(), instance.getNPCid(), instance.getLocation());
			
			// Add two phrases to the NPC
			plugin.npcManager.getNPCEntity(instance.getNPCid()).addSpeechMessage(phrases.remove(random.nextInt(phrases.size())));
			plugin.npcManager.getNPCEntity(instance.getNPCid()).addSpeechMessage(phrases.remove(random.nextInt(phrases.size())));
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
    public NPCData activateRandomNPC()
    {
    	return npcPool.activateRandom();
    }
	
	public NPCSpawner getSpawner()
	{
		return npcSpawner;
	}

	public NPCEntity spawnNPC(String name, Location l, int id) 
	{
		if (activeNPCs.containsKey(id)) 
		{
			this.server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
			return (NPCEntity) activeNPCs.get(id);
		}
		if (name.length() > 16) 
		{
			String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		BWorld world = new BWorld(l.getWorld());
		NPCEntity npcEntity = new NPCEntity(this.server.getMCServer(), world.getWorldServer(), name, new ItemInWorldManager( world.getWorldServer()), plugin, l);
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		world.getWorldServer().addEntity(npcEntity);
		activeNPCs.put(id, npcEntity);
		return npcEntity;
	}
	
	public HashMap<Integer, NPCEntity> getNpcs()
	{
		return activeNPCs;
	}
	
	public void addSpeechMessage(String npcname, String message)
	{
		if(activeNPCs.containsKey(npcname))
		{
			activeNPCs.get(npcname).addSpeechMessage(message);
		}
		else
		{
			this.server.getLogger().log(Level.WARNING, "Invalid npc name, could not add speech message.");
		}
	}

	public void despawnById(int id) 
	{
		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
		if (npc != null) 
		{
			activeNPCs.remove(id);
			try 
			{
				npc.world.removeEntity(npc);
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void despawn(String npcName) 
	{
		if (npcName.length() > 16) 
			npcName = npcName.substring(0, 16);
		
		HashSet<Integer> toRemove = new HashSet<Integer>();
		
		for (int n : activeNPCs.keySet()) 
		{
			NPCEntity npc = (NPCEntity) activeNPCs.get(n);
			if ((npc != null) && (npc.name.equals(npcName))) 
			{
				toRemove.add(n);
				try 
				{
					npc.world.removeEntity(npc);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		for (int n : toRemove)
			activeNPCs.remove(n);
	}

	public void despawnAll() 
	{
		for (NPCEntity npc : activeNPCs.values()) 
		{
			if (npc == null)
				continue;
			try 
			{
				npc.world.removeEntity(npc);
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

		activeNPCs.clear();
	}
	
	public void storeNpcInDatabase(NPCEntity npcEntity)
	{
		// TODO Add NPC data to database. (Type, name, quest assigned etc.)
	}

	public void moveNPC(int id, Location l) 
	{
		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
		if (npc != null)
			npc.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
	}

	public void moveNPCStatic(int id, Location l) 
	{
		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
		if (npc != null)
			npc.setPosition(l.getX(), l.getY(), l.getZ());
	}

	public void putNPCinbed(int id, Location bed) 
	{
		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
		if (npc != null) 
		{
			npc.setPosition(bed.getX(), bed.getY(), bed.getZ());
			npc.a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
		}
	}

	public void getNPCoutofbed(int id) 
	{
		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
		if (npc != null)
			npc.a(true, true, true);
	}

	public void setSneaking(int id, boolean flag) 
	{
		NPCEntity npc = (NPCEntity) activeNPCs.get(id);
		if (npc != null)
			npc.setSneak(flag);
	}

	public NPCEntity getNPCEntity(int id) 
	{
		return (NPCEntity) activeNPCs.get(id);
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
				if (((NPCEntity) activeNPCs.get(i)).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) 
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
				if (((NPCEntity) RageMod.getInstance().npcManager.activeNPCs.get(i)).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) 
				{
					return (NPCEntity) RageMod.getInstance().npcManager.activeNPCs.get(i);
				}
			}
		}
		return null;
	}

	public void rename(int id, String name) 
	{
		if (name.length() > 16) 
		{ // Check and nag if name is too long, spawn
		  // NPC anyway with shortened name.
			String tmp = name.substring(0, 16);
			server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		NPCEntity npc = getNPCEntity(id);
		npc.setName(name);
		BWorld b = new BWorld(npc.getBukkitEntity().getLocation().getWorld());
		WorldServer s = b.getWorldServer();
		try 
		{
			Method m = s.getClass().getDeclaredMethod("d", new Class[] { Entity.class });
			m.setAccessible(true);
			m.invoke(s, (Entity) npc);
			m = s.getClass().getDeclaredMethod("c", new Class[] { Entity.class });
			m.setAccessible(true);
			m.invoke(s, (Entity) npc);
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		s.everyoneSleeping();
	}

	private class SL extends ServerListener 
	{
		private SL() 
		{
			
		}

		public void onPluginDisable(PluginDisableEvent event) 
		{
			if (event.getPlugin() == NPCManager.this.plugin) 
			{
				NPCManager.this.despawnAll();
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
			for (NPCEntity npc : RageMod.getInstance().npcManager.activeNPCs.values())
			{
				if ((npc != null) && (event.getChunk() == npc.getBukkitEntity().getLocation().getBlock().getChunk())) 
				{
					BWorld world = new BWorld(event.getWorld());
					world.getWorldServer().addEntity(npc);
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}