package net.rageland.ragemod.quest;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class QuestManager {
	public static HashMap<Integer, Quest> quests = new HashMap<Integer, Quest>();

	public QuestManager() {
		RewardQuest testRewardQuest = new RewardQuest(1, "TestRewardQuest",
				"Hooray, you found me. Could you tell me where I am?",
				"Thanks you so much. Enjoy your reward.", new ItemStack(
						Material.COBBLESTONE), 200, 50.0D, true, true);
		quests.put(Integer.valueOf(1), testRewardQuest);
	}
}