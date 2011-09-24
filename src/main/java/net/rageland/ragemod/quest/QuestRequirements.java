package net.rageland.ragemod.quest;

import net.rageland.ragemod.data.PlayerData;

public class QuestRequirements
{
	private int preQuest;
	private int rank;
	private int affinity;
	private int reputation;
	
	public QuestRequirements(int preQuest) {
		this.preQuest = preQuest;
	}
	
	public QuestRequirements(int preQuest, int rank, int affinity, int reputation) {
		this.preQuest = preQuest;
		this.rank = rank;
		this.affinity = affinity;
		this.reputation = reputation;
	}
	
	public boolean requirementsMet(PlayerData playerData) {
		// Perform check for whether a quest can be started.
		return true;
	}
	
}
