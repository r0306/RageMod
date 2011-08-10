package net.rageland.ragemod.npcentities;

import org.bukkit.entity.Player;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import net.rageland.ragemod.RageMod;

public class SpeechNPC extends NPCEntity
{	
	
	
	public SpeechNPC(MinecraftServer minecraftserver, World world,
			String name, ItemInWorldManager iteminworldmanager, RageMod plugin)
	{
		super(minecraftserver, world, name, iteminworldmanager, plugin);
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
