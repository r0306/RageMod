package net.rageland.ragemod.npcentities;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.npc.NPCInstance;

import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class WL implements Listener 
	{
		private WL() 
		{
			
		}

		public void onChunkLoad(ChunkLoadEvent event) 
		{
			for (NPCInstance npcInstance : RageMod.getInstance().npcManager.activeNPCs.values())
			{
				RageEntity npc = null;
				if( npcInstance != null )
					npc = npcInstance.getEntity();
				
				if ((npc != null) && (event.getChunk() == npc.getBukkitEntity().getLocation().getBlock().getChunk())) 
				{   // TODO FIX THIS!
					//World world = new Location(event.getWorld(), taskid, taskid, taskid);
					//world.getWorldServer().addEntity(npc);
				}
			}
		}
	}