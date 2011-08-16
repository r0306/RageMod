package net.rageland.ragemod.quest;

import net.rageland.ragemod.data.PlayerData;

import org.bukkit.entity.Player;

public class TravelQuest implements Quest
{

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

	/**
	 * A travel quest does not have any requirements to be fulfilled.
	 * This quest type should not be attached to a startendquest.
	 */
	@Override
	public boolean isFinished(PlayerData playerData)
	{
		return true;
	}

	@Override
	public void present(Player player, PlayerData playerData)
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
		return null;
	}

}
