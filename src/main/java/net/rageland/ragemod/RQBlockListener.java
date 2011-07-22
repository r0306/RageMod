package net.rageland.ragemod;

import org.bukkit.event.block.BlockListener;

public class RQBlockListener extends BlockListener {
	private final RageQuest plugin;

	public RQBlockListener(RageQuest plugin) {
		this.plugin = plugin;
	}
}