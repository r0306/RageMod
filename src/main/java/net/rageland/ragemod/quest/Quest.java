package net.rageland.ragemod.quest;

import org.bukkit.entity.Player;

public abstract interface Quest {
	public abstract void questEnd(Player paramPlayer);

	public abstract void questStart(Player paramPlayer);

	public abstract boolean isQuestFinished(Player paramPlayer);

	public abstract void presentQuest(Player paramPlayer);
}