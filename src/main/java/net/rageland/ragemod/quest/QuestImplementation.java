package net.rageland.ragemod.quest;

import net.rageland.ragemod.NPCUtilities;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class QuestImplementation implements Quest
{
	
	protected QuestData questData;
	protected RewardData rewardData;
	protected Flags flags;
	
	public QuestImplementation(QuestData questData, RewardData rewardData, Flags flags)
	{
		this.questData = questData;
		this.rewardData = rewardData;
		this.flags = flags;
	}

	@Override
	public void end(Player player, PlayerData playerData)
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

			
			playerData.activeQuestData.questCompleted(questData.getId());
			
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
		playerData.activeQuestData.startNewQuest(this, 0);
		player.sendMessage("You have accepted the quest.");		
	}

	@Override
	public abstract boolean isFinished(PlayerData playerData);

	@Override
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
	public QuestData getQuestData()
	{
		return questData;
	}

	@Override
	public abstract void statusUpdate(Player player, PlayerData playerData);
	
	public boolean isRepeatable()
	{
		return flags.isRepeatable();
	}

}
