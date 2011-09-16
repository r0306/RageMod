package net.rageland.ragemod.quest;

import net.rageland.ragemod.data.PlayerData;

public class QuestRequirements
{
	private String preQuest;
	private int level;
	private int affinity;
	private int race;
	
	public QuestRequirements(String preQuest) {
		this.preQuest = preQuest;
	}
	
	public QuestRequirements(String preQuest, int level, int affinity, int race) {
		this.preQuest = preQuest;
		this.level = level;
		this.affinity = affinity;
		this.race = race;
	}
	
	public boolean requirementsMet(PlayerData playerData) {
		// Perform check for wether a quest can be started.
		return true;
	}
	
}
