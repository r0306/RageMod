package net.rageland.ragemod.npcentities;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.quest.Quest;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestStartNPCEntity extends NPCEntity
{
	private Quest quest;

	public QuestStartNPCEntity(MinecraftServer minecraftserver, World world,
			String name, ItemInWorldManager iteminworldmanager, Quest quest)
	{
		super(minecraftserver, world, name, iteminworldmanager);
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
		
		if(!playerData.activeQuestData.isPlayerOnQuest())
		{
			player.sendMessage("Quest: " + quest.getQuestData().getName());
			player.sendMessage(quest.getQuestData().getStartText());
			quest.start(player, plugin.players.get(player.getName()));
		}			
		else
		{
			player.sendMessage("You are currently busy with another quest. If you want to abandon it, write '/quest abandon' and then talk to me again.");
		}			
	}

}
