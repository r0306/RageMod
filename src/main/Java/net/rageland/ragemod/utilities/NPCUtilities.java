package net.rageland.ragemod.utilities;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NPCUtilities {
	public static String notEnoughSpaceMessage = "$dERROR: $6Not enough free space in your inventory. Clear out some space and try again.";

	public static boolean checkFreeSpace(ItemStack[] contents, ItemStack item,
			int spaceNeeded) {
		Material itemType = item.getType();
		short durability = item.getDurability();
		int maxStack = item.getType().getMaxStackSize();
		int spaceAvailable = 0;
		System.out.println("ItemType: " + itemType.getId() + " Dur: "
				+ durability + " spaceNeeded: " + spaceNeeded + " MaxStack: "
				+ maxStack);

		ItemStack[] arrayOfItemStack = contents;
		int j = contents.length;
		for (int i = 0; i < j; i++) {
			ItemStack curItem = arrayOfItemStack[i];
			System.out.println("Space Avail: " + spaceAvailable);
			if (spaceAvailable >= spaceNeeded) {
				return true;
			}
			if (curItem == null) {
				spaceAvailable += maxStack;
			} else {
				if ((curItem.getType() != itemType)
						|| ((curItem.getDurability() != durability) && (curItem
								.getDurability() != -1))) {
					continue;
				}
				int amount = curItem.getAmount();
				if (amount < maxStack) {
					spaceAvailable += maxStack - amount;
				}
			}
		}
		return spaceAvailable >= spaceNeeded;
	}

	public static boolean checkFreeSpace(Inventory inv, ItemStack item,
			int spaceNeeded) {
		return checkFreeSpace(inv.getContents(), item, spaceNeeded);
	}

	public static void addItemToInventory(Inventory inv, ItemStack item,
			int amount) {
		int maxStackSize = item.getType().getMaxStackSize();
		int iterations = amount / maxStackSize;
		int amountLeft = amount;

		for (int i = 0; i <= iterations; i++) {
			if (amountLeft < maxStackSize) {
				item.setAmount(amountLeft);
			} else {
				item.setAmount(maxStackSize);
				amountLeft -= maxStackSize;
			}
			inv.addItem(new ItemStack[] { item });
		}
	}
	
	public static Location findValidRandomNPCSpawnInLocation(Location min, Location max, int standardHeight)
	{
		int maxRetries = 20;
		Random rand = new Random();
		if(min.getWorld() == max.getWorld()) 
		{
			World world = min.getWorld();
			int randomX = rand.nextInt(max.getBlockX() - min.getBlockX()) + min.getBlockX();
			int randomZ = rand.nextInt(max.getBlockZ() - min.getBlockZ()) + min.getBlockZ();
			
			@SuppressWarnings("unused")
			Location possibleLocation;
			
			for(int i = 0; i < maxRetries; i++) {
				possibleLocation = new Location(world, randomX, standardHeight, randomZ);
				
				
				for(int x = -1; x < 2; i++)
				{					
					for(int y = -1; y < 2; y++) 
					{
						for(int z = -1; z < 2; z++)
						{
//							if(possibleLocation.getBlock().getRelative(x, y, z).isEmpty())  // TODO: Commented out by Icarus, preventing compilation
							{
								
							}
						}
					}					
				}
				
				
			}	
			
			return new Location(min.getWorld(), 2, 2, 2);
		}
		else
		{
			// Cannot find position between different worlds
			return null;
		}
	}
}
