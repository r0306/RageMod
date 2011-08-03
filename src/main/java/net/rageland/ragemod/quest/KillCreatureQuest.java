package net.rageland.ragemod.quest;

import net.rageland.ragemod.NPCUtilities;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KillCreatureQuest implements Quest
{

	private String questName;
	private int questId;
	private String questText;
	private String questFinishedText;
	private ItemStack rewardItem;
	private int rewardItemAmount;
	private double coinRewardAmount;
	private boolean isQuestAttachedToRandomNPC;
	private boolean isActiveQuestNPC;
	public String questTargetCreature;
	private int killNeededCounter;
	private RageMod plugin;

	public KillCreatureQuest(int questId, String questName, String questText,
			String questFinishedText, ItemStack rewardItem,
			int rewardItemAmount, double coinRewardAmount, boolean isRandomNPC,
			boolean isActiveQuestNPC, String questTargetCreature,
			int killNeededCounter, RageMod plugin)
	{
		this.questId = questId;
		this.questName = questName;
		this.questText = questText;
		this.questFinishedText = questFinishedText;
		this.rewardItem = rewardItem;
		this.rewardItemAmount = rewardItemAmount;
		this.coinRewardAmount = coinRewardAmount;
		this.isQuestAttachedToRandomNPC = isRandomNPC;
		this.isActiveQuestNPC = isActiveQuestNPC;
		this.questTargetCreature = questTargetCreature;
		this.killNeededCounter = killNeededCounter;
		this.plugin = plugin;
	}

	@Override
	/**
	 * Executed when a player is finished with the quest.
	 */
	public void questEnd(Player player, PlayerData playerData)
	{
		if (NPCUtilities.checkFreeSpace(player.getInventory(), this.rewardItem,
				this.rewardItemAmount))
		{
			plugin.text.message(player, this.questFinishedText);
			plugin.text.message(player, "Received: ");

			if (this.rewardItemAmount > 0)
			{
				plugin.text.message(player,
						Integer.toString(this.rewardItemAmount)
								+ this.rewardItem.getType().toString());
				NPCUtilities.addItemToInventory(player.getInventory(),
						this.rewardItem, this.rewardItemAmount);
			}

			if (this.coinRewardAmount > 0.0D)
			{
				plugin.text.message(player,
						Double.toString(this.coinRewardAmount) + " Coins");
			}

			plugin.text.message(player, "for finishing " + this.questName);

			if (isQuestAttachedToRandomNPC)
			{
				isActiveQuestNPC = false;
			}
		}
		else
		{
			player.sendMessage(NPCUtilities.notEnoughSpaceMessage);
		}
	}

	@Override
	public void questStart(Player player, PlayerData playerData)
	{
		if (playerData.activeQuestData != null
				|| playerData.activeQuestData.quest != null)
		{
			plugin.text
					.message(player,
							"You are already on a quest. To abandon the quest write /quest abandon");
		}
		else
		{
			plugin.text.message(player, "Accepted quest: " + questName);
			plugin.text.message(player, questText);

			ActiveQuestData questData = new ActiveQuestData();
			questData.quest = this;
			questData.questCounter = 0;
		}
	}

	@Override
	public boolean isQuestFinished(PlayerData playerData)
	{
		if (playerData.activeQuestData.questCounter >= killNeededCounter)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void presentQuest(Player player, PlayerData playerData)
	{

	}

	@Override
	public String getQuestName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getQuestId()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getQuestText()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void questUpdate(Player player, PlayerData playerData)
	{
		plugin.text.message(player, "You have killed "
				+ playerData.activeQuestData.questCounter + " of "
				+ killNeededCounter + " " + questTargetCreature + "s.");
	}

}
