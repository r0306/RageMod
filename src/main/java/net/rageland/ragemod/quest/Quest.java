package net.rageland.ragemod.quest;

import net.rageland.ragemod.InventoryUtilities;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class Quest
{
	protected QuestData questData;
	protected RewardData rewardData;
	protected Flags flags;
	protected int id_NPCInstance_Source;
	
	public static final int TYPE_REWARD = 1;
	
	public Quest(QuestData questData, RewardData rewardData, Flags flags, int id_NPCInstance_Source)
	{
		this.questData = questData;
		this.rewardData = rewardData;
		this.flags = flags;
		this.id_NPCInstance_Source = id_NPCInstance_Source;
	}

	public void end(Player player, PlayerData playerData)
	{
		if (InventoryUtilities.checkFreeSpace(player.getInventory(),rewardData.getItem(), rewardData.getAmountOfItems()))
		{
			player.sendMessage(ChatColor.LIGHT_PURPLE + questData.getEndText());
			player.sendMessage("Received: ");

			if (rewardData.getAmountOfItems() > 0)
			{
				player.sendMessage(ChatColor.DARK_GREEN
						+ Integer.toString(rewardData.getAmountOfItems())
						+ ChatColor.GOLD
						+ rewardData.getItem().getType().toString());
				InventoryUtilities.addItemToInventory(player.getInventory(),
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
		}
		else
		{
			player.sendMessage(InventoryUtilities.notEnoughSpaceMessage);
		}		
	}

	public void start(Player player, PlayerData playerData)
	{
		playerData.activeQuestData.startNewQuest(this, 0);
		player.sendMessage("You have accepted the quest.");		
	}

	public abstract boolean isFinished(PlayerData playerData);

	public QuestData getQuestData()
	{
		return questData;
	}

	public abstract void statusUpdate(Player player, PlayerData playerData);
	
	public boolean isRepeatable()
	{
		return flags.isRepeatable();
	}

}
