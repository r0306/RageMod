package net.rageland.ragemod.quest;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.utilities.InventoryUtilities;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GatheringQuest extends Quest
{
	private ItemStack itemToGather;
	
	public GatheringQuest(QuestData questData, 
			RewardData rewardData, 
			Flags flags,
			ItemStack itemToGather) {
		super(questData, rewardData, flags, id_NPCInstance_Source);
		this.itemToGather = itemToGather;
	}
	
	@Override
	public void end(Player player, PlayerData playerData) {
		int additionalFreespace = questData.getObjectiveCounter() - (questData.getObjectiveCounter() % itemToGather.getMaxStackSize());
				
		if (InventoryUtilities.checkHasEnoughOfItem(player.getInventory().getContents(), itemToGather, questData.getObjectiveCounter())
				&& InventoryUtilities.checkFreeSpace(player.getInventory(),rewardData.getItem(), rewardData.getAmountOfItems() - additionalFreespace))
		{
			InventoryUtilities.removeItemFromInventory(player.getInventory(), itemToGather, questData.getObjectiveCounter());
			
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
				player.sendMessage(ChatColor.GOLD + " " + RageMod.getInstance().economy.format(rewardData.getCoins()));
				RageMod.getInstance().economy.bankDeposit(player.getName(), rewardData.getCoins());
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

	@Override
	public boolean isFinished(PlayerData playerData)
	{
		
		return false;
	}


	@Override
	public void statusUpdate(Player player, PlayerData playerData)
	{
		// TODO Auto-generated method stub

	}

}
