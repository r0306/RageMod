package net.rageland.ragemod;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

public class RQServerListener extends ServerListener {
	private final RageQuest plugin;

	public RQServerListener(RageQuest instance) {
		this.plugin = instance;
	}

	public void onPluginDisable(PluginDisableEvent event) {
	}

	public void onPluginEnable(PluginEnableEvent event) {
	}
}