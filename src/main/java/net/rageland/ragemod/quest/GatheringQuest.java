package net.rageland.ragemod.quest;

import net.rageland.ragemod.data.PlayerData;

import org.bukkit.entity.Player;

public class GatheringQuest extends Quest
{
	
	public GatheringQuest(QuestData questData, 
			RewardData rewardData, 
			Flags flags) {
		super(questData, rewardData, flags);
	}

	@Override
	public void end(Player player, PlayerData playerData)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void start(Player player, PlayerData playerData)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFinished(PlayerData playerData)
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void statusUpdate(Player player, PlayerData playerData)
	{
		// TODO Auto-generated method stub

	}

}
