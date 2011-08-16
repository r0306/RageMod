package net.rageland.ragemod.quest;

import net.rageland.ragemod.NPCUtilities;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.iConomy.iConomy;

public class RewardQuest implements Quest
{
	private QuestData questData;
	private RewardData rewardData;
	private Flags flags;

	public RewardQuest(
				QuestData questData, 
				RewardData rewardData, Flags flags)
	{
		this.questData = questData;
		this.rewardData = rewardData;
		this.flags = flags;
	}

	public void questEnd(Player player)
	{

	}

	/**
	 * For a RewardQuest type, the quest is finished when it starts. 
	 */
	public void start(Player player, PlayerData playerData)
	{
		present(player, playerData);

		if (flags.isActive())
		{
			if (NPCUtilities.checkFreeSpace(player.getInventory(),rewardData.getItem(), rewardData.getAmountOfItems()))
			{
				player.sendMessage(ChatColor.LIGHT_PURPLE + questData.getEndText());
				player.sendMessage("Received: ");

				if (rewardData.getAmountOfItems() > 0)
				{
					player.sendMessage(ChatColor.DARK_GREEN
							+ Integer.toString(rewardData.getAmountOfItems())
							+ ChatColor.GOLD
							+ rewardData.getItem().getType().toString());
					NPCUtilities.addItemToInventory(player.getInventory(),
							rewardData.getItem(), rewardData.getAmountOfItems());
				}

				if (rewardData.getCoins() > 0.0D)
				{
					player.sendMessage(ChatColor.GOLD + " " + RageMod.getInstance().iConomy.format(rewardData.getCoins()));
					RageMod.getInstance().iConomy.getAccount(player.getName()).getHoldings().add(rewardData.getCoins());
				}

				player.sendMessage(" ");
				player.sendMessage("for finishing &a" + questData.getName());

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
	}

	public void present(Player player, PlayerData playerData)
	{
		if (flags.isActive())
		{
			player.sendMessage(ChatColor.DARK_GREEN + "Quest: "
					+ ChatColor.YELLOW + "[" + questData.getName() + "]");
			player.sendMessage(ChatColor.GREEN + questData.getStartText());
		}
		else
		{
			player.sendMessage(ChatColor.GOLD
					+ "Someone has already finished this quest.");
		}
	}

	@Override
	public boolean isFinished(PlayerData playerData)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void end(Player player, PlayerData playerData)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void statusUpdate(Player player, PlayerData playerData)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public QuestData getQuestData()
	{
		return questData;
	}

}