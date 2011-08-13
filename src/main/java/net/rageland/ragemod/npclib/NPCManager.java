package net.rageland.ragemod.npclib;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.WorldServer;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.npcentities.NPCEntity;
import net.rageland.ragemod.npcentities.QuestStartNPCEntity;
import net.rageland.ragemod.npcentities.SpeechData;
import net.rageland.ragemod.quest.Quest;

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

public class NPCManager {
	public HashMap<String, NPCEntity> npcs = new HashMap<String, NPCEntity>();
	private BServer server;
	private int taskid;
	private RageMod plugin;

	public NPCManager(RageMod plugin) 
	{
		this.server = BServer.getInstance(plugin);
		this.plugin = plugin;
		this.taskid = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() 
		{
					public void run() 
					{
						HashSet<String> toRemove = new HashSet<String>();
						for (String i : RageMod.getInstance().npcManager.npcs.keySet()) 
						{
							net.minecraft.server.Entity j = (net.minecraft.server.Entity)RageMod.getInstance().npcManager.npcs.get(i);
							j.R();
							if (j.dead) 
							{
								toRemove.add(i);
							}
						}
						for (String n : toRemove)
							NPCManager.this.despawnById(n);
					}
				}, 100L, 100L);
		plugin.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, new SL(), Priority.Normal, plugin);
		plugin.getServer().getPluginManager().registerEvent(Event.Type.CHUNK_LOAD, new WL(), Priority.Normal, plugin);
	}

	public NPCEntity spawnNPC(String name, Location l, String id) 
	{
		if (npcs.containsKey(id)) 
		{
			this.server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
			return (NPCEntity) npcs.get(id);
		}
		if (name.length() > 16) 
		{
			String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		BWorld world = new BWorld(l.getWorld());
		NPCEntity npcEntity = new NPCEntity(this.server.getMCServer(), world.getWorldServer(), name, new ItemInWorldManager( world.getWorldServer()));
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		world.getWorldServer().addEntity(npcEntity);
		npcs.put(id, npcEntity);
		return npcEntity;
	}
	
	public QuestStartNPCEntity spawnQuestStartNPC(String name, Location l, Quest quest)
	{
		String id = String.valueOf(npcs.size() + 1);
		if (npcs.containsKey(id)) 
		{
			this.server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
			return (QuestStartNPCEntity) npcs.get(id);
		}
		if (name.length() > 16) 
		{
			String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		BWorld world = new BWorld(l.getWorld());
		QuestStartNPCEntity npcEntity = new QuestStartNPCEntity(this.server.getMCServer(), world.getWorldServer(), name, new ItemInWorldManager( world.getWorldServer()), quest);
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		world.getWorldServer().addEntity(npcEntity);
		npcs.put(String.valueOf(npcs.size() + 1), npcEntity);
		return npcEntity;
	}
	
	public NPCEntity spawnQuestStartNPC(String name, Location l, String id, Quest quest) 
	{
		if (npcs.containsKey(id)) 
		{
			this.server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
			return (NPCEntity) npcs.get(id);
		}
		if (name.length() > 16) 
		{
			String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		BWorld world = new BWorld(l.getWorld());
		QuestStartNPCEntity npcEntity = new QuestStartNPCEntity(
													this.server.getMCServer(), 
													world.getWorldServer(), 
													name, 
													new ItemInWorldManager( 
															world.getWorldServer()),  
															quest
															);
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		world.getWorldServer().addEntity(npcEntity);
		npcs.put(id, npcEntity);
		return npcEntity;
	}
	
	public void addSpeechMessage(String npcname, String message)
	{
		if(npcs.containsKey(npcname))
		{
			npcs.get(npcname).addSpeechMessage(message);
		}
		else
		{
			
		}
	}

	public void despawnById(String id) 
	{
		NPCEntity npc = (NPCEntity) npcs.get(id);
		if (npc != null) 
		{
			npcs.remove(id);
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
		{
			npcName = npcName.substring(0, 16);
		}
		HashSet<String> toRemove = new HashSet<String>();
		for (String n : npcs.keySet()) 
		{
			NPCEntity npc = (NPCEntity) npcs.get(n);
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
		for (String n : toRemove)
			npcs.remove(n);
	}

	public void despawnAll() 
	{
		for (NPCEntity npc : npcs.values()) 
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

		npcs.clear();
	}
	
	public void storeNpcInDatabase(NPCEntity npcEntity)
	{
		// TODO Add NPC data to database. (Type, name, quest assigned etc.)
	}

	public void moveNPC(String id, Location l) 
	{
		NPCEntity npc = (NPCEntity) npcs.get(id);
		if (npc != null)
			npc.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
	}

	public void moveNPCStatic(String id, Location l) 
	{
		NPCEntity npc = (NPCEntity) npcs.get(id);
		if (npc != null)
			npc.setPosition(l.getX(), l.getY(), l.getZ());
	}

	public void putNPCinbed(String id, Location bed) 
	{
		NPCEntity npc = (NPCEntity) npcs.get(id);
		if (npc != null) 
		{
			npc.setPosition(bed.getX(), bed.getY(), bed.getZ());
			npc.a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
		}
	}

	public void getNPCoutofbed(String id) 
	{
		NPCEntity npc = (NPCEntity) npcs.get(id);
		if (npc != null)
			npc.a(true, true, true);
	}

	public void setSneaking(String id, boolean flag) 
	{
		NPCEntity npc = (NPCEntity) npcs.get(id);
		if (npc != null)
			npc.setSneak(flag);
	}

	public NPCEntity getNPC(String id) 
	{
		return (NPCEntity) npcs.get(id);
	}

	public static boolean isNPC(org.bukkit.entity.Entity e) 
	{
		return ((CraftEntity) e).getHandle() instanceof NPCEntity;
	}

	public String getNPCIdFromEntity(org.bukkit.entity.Entity e) 
	{
		if ((e instanceof HumanEntity)) 
		{
			for (String i : npcs.keySet()) 
			{
				if (((NPCEntity) npcs.get(i)).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) 
				{
					return i;
				}
			}
		}
		return null;
	}

	public static NPCEntity getNPCFromEntity(org.bukkit.entity.Entity e) 
	{
		if ((e instanceof HumanEntity)) 
		{
			for (String i : RageMod.getInstance().npcManager.npcs.keySet()) 
			{
				if (((NPCEntity) RageMod.getInstance().npcManager.npcs.get(i)).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId()) 
				{
					return (NPCEntity) RageMod.getInstance().npcManager.npcs.get(i);
				}
			}
		}
		return null;
	}

	public void rename(String id, String name) 
	{
		if (name.length() > 16) 
		{ // Check and nag if name is too long, spawn
		  // NPC anyway with shortened name.
			String tmp = name.substring(0, 16);
			server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		NPCEntity npc = getNPC(id);
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
			for (NPCEntity npc : RageMod.getInstance().npcManager.npcs.values())
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