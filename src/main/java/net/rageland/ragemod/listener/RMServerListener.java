package net.rageland.ragemod.listener;

import net.rageland.ragemod.RageMod;

import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class RMServerListener implements Listener {

	private RageMod plugin = RageMod.getInstance();
	private Server server = plugin.getServer();
	
	public RMServerListener(RageMod instance, Server server) {
		this.plugin = instance;
		this.server = server;
	}
}
