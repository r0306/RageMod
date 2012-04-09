package net.rageland.ragemod.utilities;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtilities {
	public static String notEnoughSpaceMessage = "$dERROR: $6Not enough free space in your inventory. Clear out some space and try again.";

	public static boolean checkFreeSpace(ItemStack[] contents, ItemStack item,
			int spaceNeeded) {
		Material itemType = item.getType();
		short durability = item.getDurability();
		int maxStack = item.getType().getMaxStackSize();
		int spaceAvailable = 0;

		ItemStack[] arrayOfItemStack = contents;
		int j = contents.length;
		for (int i = 0; i < j; i++) {
			ItemStack curItem = arrayOfItemStack[i];
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
	
	public static boolean checkHasEnoughOfItem(ItemStack[] contents, ItemStack item, int amountNeeded){
		Material itemType = item.getType();
		int countedAmount = 0;
		double durability = item.getDurability();
		
		int j = contents.length;
		ItemStack[] arrayOfItemStack = contents;
		
		for(int i = 0; i < j; i++) {
			ItemStack curItem = arrayOfItemStack[i];
			if(countedAmount >= amountNeeded) {
				return true;
			}
			if(curItem == null) {
				continue;
			} else {
				if((curItem.getType() != itemType)
						|| ((curItem.getDurability() != durability) && (curItem
								.getDurability() != -1))) {
					continue;
				} else {
					countedAmount += curItem.getAmount();
				}	
			}
		}
		
		return false;
	}
	
	public static void removeItemFromInventory(Inventory inv, ItemStack item, int amount ) {
		int removedAmount = 0;
		Material itemType = item.getType();
		
		while(removedAmount < amount) {
			int indexToRemove = inv.first(itemType);
			removedAmount += inv.getItem(indexToRemove).getAmount();
			inv.clear(indexToRemove);
		}		
		addItemToInventory(inv, item, removedAmount - amount);		
	}
}
