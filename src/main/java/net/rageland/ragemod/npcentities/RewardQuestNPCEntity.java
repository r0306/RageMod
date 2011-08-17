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

public class RewardQuestNPCEntity extends NPCEntity
{
	private QuestImplementation quest;

	public RewardQuestNPCEntity(MinecraftServer minecraftserver, World world,
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
		player.sendMessage("[Left click npc to finish]");
	}

	/**
	 * This method starts the quest instantly, because it doesn't matter if
	 * the player is on a quest or not when finishing a reward quest.
	 */
	public void leftClickAction(Player player)
	{
		PlayerData playerData = this.plugin.players.get(player.getName());
		
		if(playerData.activeQuestData.isQuestCompleted(quest.getQuestData().getId()) && !quest.isRepeatable())
		{
			player.sendMessage("You have already finished this quest ");
		}
		else
		{
			player.sendMessage("Quest: " + quest.getQuestData().getName());
			player.sendMessage(quest.getQuestData().getStartText());
			quest.start(player, this.plugin.players.get(player.getName()));
		}
	}
}
