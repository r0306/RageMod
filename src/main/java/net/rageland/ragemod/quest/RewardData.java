package net.rageland.ragemod.quest;

import org.bukkit.inventory.ItemStack;

/**
 * Contains data about the reward of a quest. Each quest
 * consist of a RewardData object.
 * @author Jorgen
 *
 */
public class RewardData
{

	private ItemStack item;
	private int amountOfItems;
	private double coins;
	
	public RewardData(
				ItemStack item,
				int amountOfItems,
				double coins)
	{
		this.item = item;
		this.amountOfItems = amountOfItems;
		this.coins = coins;
	}
	
	public ItemStack getItem()
	{
		return item;
	}
	
	public int getAmountOfItems() 
	{
		return amountOfItems;
	}
	
	public double getCoins()
	{
		return coins;
	}
}
