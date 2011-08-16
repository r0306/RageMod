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

public class QuestEndNPCEntity extends NPCEntity
{
	private QuestImplementation quest;

	public QuestEndNPCEntity(MinecraftServer minecraftserver, World world,
			String name, ItemInWorldManager iteminworldmanager, QuestImplementation quest)
	{
		super(minecraftserver, world, name, iteminworldmanager);
		this.quest = quest;
	}

	public void rightClickAction(Player player)
	{
		leftClickAction(player);
	}

	public void leftClickAction(Player player)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		if (playerData.activeQuestData.getQuest() == quest)
		{
			if (quest.isFinished(playerData))
			{
				quest.end(player, playerData);
			}
			else
			{
				player.sendMessage("You havent finished your quest. What are you waiting for?");
			}
		}
		else
		{
			player.sendMessage("Sorry, I can't help you with anything.");
		}

	}
}
