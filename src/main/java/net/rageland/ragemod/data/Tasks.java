package net.rageland.ragemod.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.iConomy.iConomy;
import com.iConomy.system.Holdings;

import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.RageZones;
import net.rageland.ragemod.Util;

// TODO: Create a task to check for imbalance between player treasuryBlock levels and actual gold blocks in the treasury.
//		 If there is a mismatch, either delete the extra block or pick a resident to deduct the block level from (give them equivalent money)

public class Tasks {
		
	private HashMap<String, Timestamp> tasks;
	private RageMod plugin;
	
	public Tasks(RageMod plugin)
	{
		this.plugin = plugin;
	}
	
	
	// On startup, pull all records of when tasks ran last
	public void loadTaskTimes()
	{
		tasks = plugin.database.taskQueries.loadTaskTimes();
		if(tasks == null)
			tasks = new HashMap<String, Timestamp>();
	}
	
	// Log task as complete
	public void setComplete(String taskName)
	{
		tasks.put(taskName, Util.now());
		plugin.database.taskQueries.setComplete(taskName);
	}
	
	// Get number of seconds since last task run
	public int getSeconds(String taskName)
	{
		// If we have a null value, the task has never been run, so return a huge value to force it to run
		if( !tasks.containsKey(taskName) || tasks.get(taskName) == null )
			return Integer.MAX_VALUE;
		else
			return (int)((Util.now().getTime() - tasks.get(taskName).getTime()) / 1000);
	}
	
	
	
	// *****  TASK CODE  *****
	
	// Charge taxes for player towns and give money for treasury blocks
	public void processTownUpkeeps()
	{
		double remaining;
		double cost = plugin.config.Town_UPKEEP_PER_PLAYER;
		int totalBlocks = 0;
		int actualBlocks;
		int income;
		Holdings holdings;
		PlayerData playerData;
		ArrayList<String> evictList;
		
		if( plugin.config.DISABLE_NON_LOT_CODE )
			return;
		
		System.out.println("Beginning town upkeep processing...");
		
		// Notify players of incoming lag
		plugin.text.broadcast("Beginning Ragemod upkeep tasks, hold on tight...", ChatColor.DARK_GREEN); // TODO: Store colors in config
		
		for( PlayerTown town : plugin.playerTowns.getAll() )
		{
			// *****  INCOME  *****
			// Move through each town resident to give money
			for( String playerName : town.residents )
			{
				playerData = plugin.players.get(playerName);
				income = playerData.treasuryBlocks * plugin.config.INCOME_PER_BLOCK;
				playerData.treasuryBalance += income;
				playerData.update();
				town.treasuryBalance += income;
				plugin.database.townQueries.townDeposit(town.id_PlayerTown, playerData.id_Player, (income));
				totalBlocks += playerData.treasuryBlocks;
			}
			
			if( totalBlocks > 0 )
				System.out.println("Awarded " + town.townName + " " + iConomy.format(totalBlocks * plugin.config.INCOME_PER_BLOCK) + " for treasury blocks.");
			
			// *****  SANCTUM CLEANUP  *****
			// Make sure the number of physical blocks in the treasury matches the database values
			actualBlocks = town.countTreasuryBlocks();
			
			if( actualBlocks > totalBlocks )
			{
				System.out.println("WARNING: " + town.townName + " has " + actualBlocks + " gold blocks in its sanctum, yet only " + totalBlocks + 
						" are recorded in the database.  Deleting " + (actualBlocks - totalBlocks) + " blocks to correct this.");
				town.removeTreasuryBlocks(actualBlocks - totalBlocks);
			}
			else if( actualBlocks < totalBlocks )
			{
				int difference = totalBlocks - actualBlocks;
				System.out.println("WARNING: " + town.townName + " has " + actualBlocks + " gold blocks in its sanctum, but " + totalBlocks + 
						" are recorded in the database.  Removing " + difference + " blocks from database to correct this.");
				
				// Go through the residents one by one, lowering their block values until the discrepancy is resolved.
				// It will go through players that appear earliest in the database first, but as this situation should never
				// happen in actual play, it shouldn't be a problem
				for( String playerName : town.residents )
				{
					playerData = plugin.players.get(playerName);
					if( playerData.treasuryBlocks > 0 )
					{
						playerData.treasuryBlocks--;
						iConomy.getAccount(playerData.name).getHoldings().add(plugin.config.PRICE_GOLD * 9);
						playerData.update();
						difference--;
						if( difference == 0 )
							break;
					}
				}
			}
			
			// *****  TAXES  *****
			
			// Set the total amount needed to be collected
			remaining = town.getLevel().upkeepCost;
			
			evictList = new ArrayList<String>();
			
			// Move through each town resident to collect money
			for( String playerName : town.residents )
			{
				holdings = iConomy.getAccount(playerName).getHoldings();
				playerData = plugin.players.get(playerName);
				
				// Attempt to collect from player's iConomy balance first
				if( holdings.hasEnough(cost) )
				{
					holdings.subtract(cost);
					remaining -= cost;
				}
				// If they don't have enough on hand, see if they have deposits in the treasury
				else if( playerData.treasuryBalance >= cost )
				{
					playerData.treasuryBalance -= cost;
					town.treasuryBalance -= cost;
					remaining -= cost;
					playerData.update();
					plugin.database.townQueries.townDeposit(town.id_PlayerTown, playerData.id_Player, (cost * -1));
				}
				// If the player doesn't have the funds in either area, evict their freeloading ass
				else if( !playerData.isMayor )
				{
					System.out.println("Automatically evicting " + playerData.name + " from " + town.townName + ".");
					playerData.clearSpawn();
					playerData.treasuryBalance = 0;
					playerData.logonMessageQueue += "You have been automatically evicted from " + 
							plugin.playerTowns.get(playerData.townName).getCodedName() + " for inability to pay taxes.<br>";
					playerData.townName = "";
					
					evictList.add(playerName);
					playerData.update();
					
					// TODO: Delete the player's treasury blocks, give them money
					//				... wait a minute.  If the player had a treasury block, they wouldn't get evicted, would they :P
					//				This DOES need to get written for town.leave and town.evict though.
				}
			}
			
			// Evict queued residents, if any
			for( String evictName : evictList )
			{
				town.removeResident(evictName);
			}
			
			// At this point we will have collected an amount of money less, equal to, or more than the town's upkeep
			town.treasuryBalance -= remaining;
			
			// If the treasury balance is in the negative, the town is bankrupt
			if( town.treasuryBalance < 0 )
			{
				if( town.bankruptDate == null )
				{
					System.out.println("The town of " + town.townName + " is now bankrupt.");
					town.bankruptDate = Util.now();
				}
				else
				{
					// At this point the town has been bankrupt for some time - delete after specified time
					if( Util.daysBetween(Util.now(), town.bankruptDate) > plugin.config.Town_MAX_BANKRUPT_DAYS )
					{
						PlayerData ownerData = plugin.players.get(plugin.config.OWNER_NAME);
						String ownerMessages = ownerData.logonMessageQueue;
						String bankruptMessage = "The town of " + town.getCodedName() + " has been bankrupt for over " + plugin.config.Town_MAX_BANKRUPT_DAYS +
								" days and has been set to deleted.<br>";
						
						if( !ownerMessages.contains(bankruptMessage) )
						{
							ownerMessages += bankruptMessage;
							ownerData.update();
						}
						
						town.isDeleted = true;
						plugin.playerTowns.remove(town);
					}
				}
			}
			else
			{
				town.bankruptDate = null;
			}
			
			town.update();		// Update town info in database
		}
	}

	// Fill a specified area with sand for a public mine
	public void processFillSandlot(RageMod plugin) 
	{
		World world = plugin.getServer().getWorld("world");
		Random random = new Random();
		
		System.out.println("Refilling sand lot...");
		
		// Look for players who are currently in the area and evacuate them
		for( Player player : plugin.getServer().getOnlinePlayers() )
		{
			if( plugin.zones.isInSandlot(player.getLocation()) )
			{
				plugin.text.parse(player, "Automatically refilling sand lot - get out of the way!");
				player.teleport(world.getSpawnLocation());
			}
		}
		
		Block currentBlock;
		
		for( int x = (int)RageZones.Capitol_SandLot.nwCorner.getX(); x <= (int)RageZones.Capitol_SandLot.seCorner.getX(); x++ )
		{
			for( int y = (int)RageZones.Capitol_SandLot.nwCorner.getY(); y <= (int)RageZones.Capitol_SandLot.seCorner.getY(); y++ )
			{
				for( int z = (int)RageZones.Capitol_SandLot.nwCorner.getZ(); z >= (int)RageZones.Capitol_SandLot.seCorner.getZ(); z-- )
				{
					currentBlock = world.getBlockAt(x, y, z);
					if( random.nextInt( plugin.config.Capitol_SANDLOT_GOLD_ODDS ) == 0 )
						currentBlock.setType(Material.GOLD_ORE);
					else if( random.nextInt( plugin.config.Capitol_SANDLOT_DIAMOND_ODDS ) == 0 ) 
						currentBlock.setType(Material.DIAMOND_ORE);
					else
						currentBlock.setType(Material.SAND);
				}
			}
		}
		
	}
	
	
}

