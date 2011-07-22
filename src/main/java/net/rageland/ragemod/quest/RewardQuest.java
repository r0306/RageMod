package net.rageland.ragemod.quest;

import net.rageland.ragemod.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardQuest implements Quest {
	private String questName;
	private int questId;
	private String questText;
	private String questFinished;
	private ItemStack rewardItem;
	private int rewardItemAmount;
	private double coinRewardAmount;
	private boolean isQuestAttachedToRandomNPC;
	private boolean isActiveQuestNPC;

	public RewardQuest(int questId, String questName, String questText,
			String questFinished, ItemStack rewardItem, int rewardItemAmount,
			double coinRewardAmount, boolean isRandomNPC,
			boolean isActiveQuestNPC) {
		this.questId = questId;
		this.questName = questName;
		this.questText = questText;
		this.questFinished = questFinished;
		this.rewardItem = rewardItem;
		this.rewardItemAmount = rewardItemAmount;
		this.coinRewardAmount = coinRewardAmount;
		this.isQuestAttachedToRandomNPC = isRandomNPC;
		this.isActiveQuestNPC = isActiveQuestNPC;
	}

	public void questEnd(Player player) {
	}

	public void questStart(Player player) {
		presentQuest(player);

		if (this.isActiveQuestNPC) {
			if (Utilities.checkFreeSpace(player.getInventory(),
					this.rewardItem, this.rewardItemAmount)) {
				player.sendMessage(ChatColor.LIGHT_PURPLE + this.questFinished);
				player.sendMessage("Received: ");

				if (this.rewardItemAmount > 0) {
					player.sendMessage(ChatColor.DARK_GREEN
							+ Integer.toString(this.rewardItemAmount)
							+ ChatColor.GOLD
							+ this.rewardItem.getType().toString());
					Utilities.addItemToInventory(player.getInventory(),
							this.rewardItem, this.rewardItemAmount);
				}

				if (this.coinRewardAmount > 0.0D) {
					player.sendMessage(ChatColor.DARK_GREEN
							+ Double.toString(this.coinRewardAmount)
							+ ChatColor.GOLD + " Coins");
				}

				player.sendMessage(" ");
				player.sendMessage("for finishing &a" + this.questName);
				
				if(isQuestAttachedToRandomNPC) {
					isActiveQuestNPC = false;
				}
				
			} else {
				player.sendMessage(Utilities.notEnoughSpaceMessage);
			}
		}
	}

	public void presentQuest(Player player) {
		if (this.isActiveQuestNPC) {
			player.sendMessage(ChatColor.DARK_GREEN + "Quest: "
					+ ChatColor.YELLOW + "[" + this.questName + "]");
			player.sendMessage(ChatColor.GREEN + this.questText);
		} else {
			player.sendMessage(ChatColor.GOLD
					+ "Someone has already finished this quest.");
		}
	}

	public boolean isQuestFinished(Player player) {
		return false;
	}

	public int getQuestId() {
		return this.questId;
	}
}