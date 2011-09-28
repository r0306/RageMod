package net.rageland.ragemod.npcentities;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCInstance;
import net.rageland.ragemod.data.PlayerData;

public class SpeechNPC extends RageEntity
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
		PlayerData playerData = plugin.players.get(player.getName());
		
		this.facePlayer(player);
		
		if( playerData.isNewInteraction(instance.getID()) )
		{
			// If  returned true, this is the first encounter with this instance
			if( playerData.isFirstMeeting(instance.getNPCid()) )
				plugin.message.talk(player, this.name, this.speechData.getInitialGreeting(playerData));
			else
				plugin.message.talk(player, this.name, speechData.getFollowupGreeting(playerData));
			
			playerData.recordNPCInteraction(instance);
		}
		else
		{
			plugin.message.talk(player, this.name, this.speechData.getNextMessage(playerData));
		}
			
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
