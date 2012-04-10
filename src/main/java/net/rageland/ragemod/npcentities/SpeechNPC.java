package net.rageland.ragemod.npcentities;

import org.bukkit.entity.Player;

import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.npc.NPCInstance;

public class SpeechNPC extends RageEntity
{	
	
	
	private String name;

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
