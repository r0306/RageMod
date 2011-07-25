package net.rageland.ragemod.quest;

import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.PlayerData;

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
	public  String questTargetCreature;
	private int killNeededCounter;

	public KillCreatureQuest( 
						int questId, 
						String questName, 
						String questText,						
						String questFinishedText, 
						ItemStack rewardItem, 
						int rewardItemAmount,
						double coinRewardAmount, 
						boolean isRandomNPC,
						boolean isActiveQuestNPC,
						String questTargetCreature,
						int killNeededCounter) 
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
	}
	
	@Override
	public void questEnd(Player player, PlayerData playerData) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void questStart(Player player, PlayerData playerData) 
	{
		
	}

	@Override
	public boolean isQuestFinished(PlayerData playerData) 
	{
		// TODO Auto-generated method stub
		return false;
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
	public String getQuestText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void questUpdate(Player player, PlayerData playerData) {
		Util.message(player, "You have killed " + playerData.activeQuestData.questCounter + " of " + killNeededCounter + " " + questTargetCreature + "s.");
	}

}
