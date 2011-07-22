package net.rageland.ragemod;

import org.bukkit.event.player.PlayerListener;

public class RQPlayerListener extends PlayerListener {
	private final RageQuest plugin;

	public RQPlayerListener(RageQuest instance) {
		this.plugin = instance;
	}
}