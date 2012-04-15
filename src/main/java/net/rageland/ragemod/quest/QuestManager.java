package net.rageland.ragemod.quest;

import java.util.HashMap;

import net.rageland.ragemod.RageMod;

@SuppressWarnings("unused")
public class QuestManager
{
	private RageMod plugin;
	public HashMap<Integer, Quest> quests = new HashMap<Integer, Quest>();
	public static HashMap<Integer, Quest> squests = new HashMap<Integer, Quest>();

	public QuestManager()
	{
		this.plugin = RageMod.getInstance();
		
		
//		QuestData testRewardQuestData = new QuestData("TestReward", "reward1", "start text reward1", "end text reward1", 0, "");
//		RewardData testRewardData = new RewardData(new ItemStack(Material.getMaterial("stone")), 10, 50);
		Flags testFlags = new Flags(true, false, false);		
//		RewardQuest testRewardQuest = new RewardQuest(testRewardQuestData, testRewardData, testFlags);
//		quests.put(testRewardQuest.getQuestData().getId(), testRewardQuest);
		
		
//		QuestData testTravelQuestData = new QuestData("TestTravel", "travel1", "start text travel1", "end text travel1", 0, "");
//		RewardData testTravelData = new RewardData(new ItemStack(Material.getMaterial("coal")), 15, 50);
		Flags testTravelFlags = new Flags(true, false, false);		
//		TravelQuest testTravelQuest = new TravelQuest(testTravelQuestData, testTravelData, testTravelFlags);
//		quests.put(testTravelQuest.getQuestData().getId(), testTravelQuest);
	}
}