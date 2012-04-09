package net.rageland.ragemod.listener;

import net.rageland.ragemod.RageMod;

import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class RMServerListener implements Listener {

	private final RageMod plugin;
	
	public RMServerListener(RageMod instance) {
		this.plugin = instance;
	}
}
