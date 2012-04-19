package net.rageland.ragemod.npcentities;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.npc.NPCInstance;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class WL implements Listener 
{
	
	private WL() 
		{
			
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onChunkLoad(ChunkLoadEvent event) 
		{
			for (NPCInstance npcInstance : RageMod.getInstance().npcManager.getActiveNPCs().values())
			{
				RageEntity npc = null;
				if( npcInstance != null )
					npc = npcInstance.getEntity();
				
				if ((npc != null) && (event.getChunk() == npc.getBukkitEntity().getLocation().getBlock().getChunk())) 
				{   // TODO FIX THIS!
					//World world = new World(event.getWorld(), taskid, taskid, taskid);
					//String world1 = config.World_Name;
					//plugin.getServer().getWorld(world1).spawnCreature(loc, npc);
				}
			}
		}
	}