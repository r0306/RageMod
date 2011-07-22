package net.rageland.ragemod.quest;

import org.bukkit.entity.Player;

public abstract interface Quest {
	
	public void questEnd(Player paramPlayer);
	public void questStart(Player paramPlayer);
	public boolean isQuestFinished(Player paramPlayer);
	public void presentQuest(Player paramPlayer);	
	public String getQuestName();
	public int getQuestId();
	public String getQuestText();
	
}