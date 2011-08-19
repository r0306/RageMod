package net.rageland.ragemod.npcentities;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCInstance;

public class SpeechNPC extends NPCEntity
{	
	
	
	public SpeechNPC(NPCInstance instance)
	{
		super(instance);
	}
	
	/**
	 * Method called when a right click action on the NPC is performed by a
	 * player. *
	 * 
	 * @param player
	 *            Player that right clicked the entity
	 */
	public void rightClickAction(Player player)
	{
		plugin.message.talk(player, this.name, this.speechData.getNextMessage());
		//plugin.message.talk(player, this.name, "My yaw: " + this.yaw + "; Your yaw: " + player.getLocation().getYaw());
		this.facePlayer(player);
	}

	/**
	 * Method called when a left click action on the NPC is performed by a
	 * player. *
	 * 
	 * @param player
	 *            Player that left clicked the entity
	 */
	public void leftClickAction(Player player)
	{
		
	}

}
