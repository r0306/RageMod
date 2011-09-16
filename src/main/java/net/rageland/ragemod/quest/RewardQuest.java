package net.rageland.ragemod.quest;

import net.rageland.ragemod.NPCUtilities;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.iConomy.iConomy;

public class RewardQuest extends Quest
{
	public RewardQuest(	QuestData questData, RewardData rewardData, Flags flags)
	{
		super(questData, rewardData, flags);
	}

	/**
	 * For a RewardQuest type, the quest is finished when it starts. 
	 */
	public void start(Player player, PlayerData playerData)
	{
		present(player, playerData);
		end(player, playerData);
	}

	@Override
	public boolean isFinished(PlayerData playerData)
	{		
		return true;
	}

	@Override
	public void statusUpdate(Player player, PlayerData playerData)
	{
		// No status update for this method.
	}

}