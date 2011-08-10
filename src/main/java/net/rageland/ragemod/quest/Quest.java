package net.rageland.ragemod.quest;

import net.rageland.ragemod.data.PlayerData;

import org.bukkit.entity.Player;

public abstract interface Quest
{

	public void end(Player player, PlayerData playerData);

	public void start(Player player, PlayerData playerData);

	public boolean isFinished(PlayerData playerData);

	public void present(Player player, PlayerData playerData);
	
	public QuestData getQuestData();

	public void statusUpdate(Player player, PlayerData playerData);

}