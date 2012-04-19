package net.rageland.ragemod.npcentities;

import net.rageland.ragemod.RageMod;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class SL implements Listener {
	
	private RageMod plugin;
	
		private SL() 
		{
			
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPluginDisable(PluginDisableEvent event) 
		{
			if (event.getPlugin() == plugin) 
			{
				RageNPCManager.sDespawnAll(false);
				plugin.getServer().getScheduler().cancelTask(RageNPCManager.staskid);
			}
		}
	}