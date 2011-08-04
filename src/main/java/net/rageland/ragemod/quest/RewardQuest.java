package net.rageland.ragemod.quest;

import net.rageland.ragemod.NPCUtilities;
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
	private boolean isQuestAttachedToRandomNPC;
	private boolean isActiveQuestNPC;

	public RewardQuest(
				QuestData questData, 
				RewardData rewardData, 
				boolean isRandomNPC,
				boolean isActiveQuestNPC)
	{
		this.questData = questData;
		this.rewardData = rewardData;
		this.isQuestAttachedToRandomNPC = isRandomNPC;
		this.isActiveQuestNPC = isActiveQuestNPC;
	}

	public void questEnd(Player player)
	{

	}

	public void questStart(Player player, PlayerData playerData)
	{
		presentQuest(player, playerData);

		if (this.isActiveQuestNPC)
		{
			if (NPCUtilities.checkFreeSpace(
							player.getInventory(),
							rewardData.getItem(), 
							rewardData.getAmountOfItems()
						)
				)
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
					player.sendMessage(ChatColor.DARK_GREEN
							+ Double.toString(rewardData.getCoins())
							+ ChatColor.GOLD + " Coins");		// DC: Check out iConomy.format() - I'm thinking of changing the currency name
				}

				player.sendMessage(" ");
				player.sendMessage("for finishing &a" + questData.getName());

				if (isQuestAttachedToRandomNPC)
				{
					isActiveQuestNPC = false;
				}

			}
			else
			{
				player.sendMessage(NPCUtilities.notEnoughSpaceMessage);
			}
		}
	}

	public void presentQuest(Player player, PlayerData playerData)
	{
		if (this.isActiveQuestNPC)
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
	public boolean isQuestFinished(PlayerData playerData)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getQuestId()
	{
		return questData.getId();
	}

	@Override
	public String getQuestName()
	{
		return questData.getName();
	}

	@Override
	public String getQuestText()
	{
		return questData.getStartText();
	}

	@Override
	public void questEnd(Player player, PlayerData playerData)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void questUpdate(Player player, PlayerData playerData)
	{
		// TODO Auto-generated method stub

	}

}