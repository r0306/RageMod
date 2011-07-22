package net.rageland.ragemod.quest;

import net.rageland.ragemod.data.PlayerData;

import org.bukkit.entity.Player;

public abstract interface Quest {
	
	public void questEnd(Player player);
	public void questStart(Player player);
	public boolean isQuestFinished(PlayerData playerData);
	public void presentQuest(Player player);	
	public String getQuestName();
	public int getQuestId();
	public String getQuestText();
	
}