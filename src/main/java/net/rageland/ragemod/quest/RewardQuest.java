package net.rageland.ragemod.quest;

import net.rageland.ragemod.data.PlayerData;

import org.bukkit.entity.Player;

public class RewardQuest extends Quest
{
	public RewardQuest(	QuestData questData, RewardData rewardData, Flags flags, int id_NPCInstance_Source)
	{
		super(questData, rewardData, flags, id_NPCInstance_Source);
	}

	/**
	 * For a RewardQuest type, the quest is finished when it starts. 
	 */
	public void start(Player player, PlayerData playerData)
	{		
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
		// No status update for this quest type
	}

}