package net.rageland.ragemod.npcentities;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.quest.Quest;
import net.rageland.ragemod.quest.QuestImplementation;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestStartNPCEntity extends NPCEntity
{
	private QuestImplementation quest;

	public QuestStartNPCEntity(MinecraftServer minecraftserver, World world,
			String name, ItemInWorldManager iteminworldmanager, QuestImplementation quest, RageMod plugin)
	{
		super(minecraftserver, world, name, iteminworldmanager, plugin);
		this.quest = quest;
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
		player.sendMessage("Quest: " + quest.getQuestData().getName());
		player.sendMessage(quest.getQuestData().getStartText());
		player.sendMessage("[Left click npc to accept]");
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
		PlayerData playerData = plugin.players.get(player.getName());
		
		if(playerData.activeQuestData.getQuest() == quest) 
		{
			player.sendMessage("You are already on this quest.");
		} 
		else if(playerData.activeQuestData.isPlayerOnQuest())
		{
			player.sendMessage("When you are finished with your current quest, come back to see me.");
		}
		else
		{
			if(playerData.activeQuestData.isQuestCompleted(quest.getQuestData().getId()) && !quest.isRepeatable())
			{
				player.sendMessage("You have already finished this quest ");
			}
			else
			{
				player.sendMessage("Quest: " + quest.getQuestData().getName());
				player.sendMessage(quest.getQuestData().getStartText());
				quest.start(player, playerData);
			}			
		}		
	}

}
