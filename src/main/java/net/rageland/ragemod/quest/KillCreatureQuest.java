package net.rageland.ragemod.quest;

import net.rageland.ragemod.NPCUtilities;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.CreatureType;

public class KillCreatureQuest implements Quest
{

	private QuestData questData;
	private RewardData rewardData;
	private Flags flags;
	private CreatureType creatureToBeKilled;
	private int killNeededCounter;
	private RageMod plugin;

	public KillCreatureQuest(
			QuestData questData, 
			RewardData rewardData, 
			Flags flags,
			CreatureType creatureToBeKilled,
			int killNeededCounter)
	{
		this.questData = questData;
		this.rewardData = rewardData;
		this.flags = flags;
		this.creatureToBeKilled = creatureToBeKilled;
		this.killNeededCounter = killNeededCounter;
		this.plugin = RageMod.getInstance();
	}
	
	public CreatureType getCreatureToBeKilled()
	{
		return creatureToBeKilled;
	}

	@Override
	/**
	 * Executed when a player is finished with the quest.
	 */
	public void end(Player player, PlayerData playerData)
	{
		if (NPCUtilities.checkFreeSpace(player.getInventory(), rewardData.getItem(),
				rewardData.getAmountOfItems()))
		{
			plugin.message.parse(player, questData.getEndText());
			plugin.message.parse(player, "Received: ");

			if (rewardData.getAmountOfItems() > 0)
			{
				plugin.message.parse(player,
						Integer.toString(rewardData.getAmountOfItems())
								+ rewardData.getItem().getType().toString());
				NPCUtilities.addItemToInventory(player.getInventory(),
						rewardData.getItem(), rewardData.getAmountOfItems());
			}

			if (rewardData.getCoins() > 0.0D)
			{
				plugin.message.parse(player,
						Double.toString(rewardData.getCoins()) + " Coins");
			}

			plugin.message.parse(player, "for finishing " + questData.getName());

			if (flags.isRandom())
			{
				flags.setActive(false);
			}
		}
		else
		{
			player.sendMessage(NPCUtilities.notEnoughSpaceMessage);
		}
	}

	@Override
	public void start(Player player, PlayerData playerData)
	{
		if (playerData.activeQuestData.isPlayerOnQuest())
		{
			plugin.message
					.parse(player,
							"You are already on a quest. To abandon the quest write /quest abandon");
		}
		else
		{
			plugin.message.parse(player, "Accepted quest: " + questData.getName());
			plugin.message.parse(player, questData.getStartText());
			playerData.activeQuestData.startNewQuest(this, killNeededCounter);
		}
	}

	@Override
	public boolean isFinished(PlayerData playerData)
	{
		if (playerData.activeQuestData.getObjectiveCounter() >= killNeededCounter)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void present(Player player, PlayerData playerData)
	{

	}
	
	@Override
	public QuestData getQuestData()
	{
		return questData;
	}

	@Override
	public void statusUpdate(Player player, PlayerData playerData)
	{
		plugin.message.parse(player, "You have killed "
				+ playerData.activeQuestData.getObjectiveCounter() + " of "
				+ killNeededCounter + " " + this.creatureToBeKilled.getName() + "s.");
	}

}
