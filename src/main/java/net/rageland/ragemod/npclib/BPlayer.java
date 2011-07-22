package net.rageland.ragemod.npclib;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.InventoryLargeChest;
import net.minecraft.server.TileEntityChest;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BPlayer {
	private CraftPlayer cPlayer;
	private EntityPlayer ePlayer;

	public BPlayer(Player player) {
		try {
			this.cPlayer = ((CraftPlayer) player);
			this.ePlayer = this.cPlayer.getHandle();
		} catch (Exception ex) {
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
	}

	public void openVirtualChest(TileEntityChest chest) {
		this.ePlayer.a(chest);
	}

	public void openVirtualChest(InventoryLargeChest lChest) {
		this.ePlayer.a(lChest);
	}
}