package net.rageland.ragemod.quest;

import net.rageland.ragemod.data.PlayerData;

import org.bukkit.entity.Player;

public abstract interface Quest
{

	public void questEnd(Player player, PlayerData playerData);

	public void questStart(Player player, PlayerData playerData);

	public boolean isQuestFinished(PlayerData playerData);

	public void presentQuest(Player player, PlayerData playerData);
	
	public QuestData getQuestData();

	public void questUpdate(Player player, PlayerData playerData);

}