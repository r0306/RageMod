package net.rageland.ragemod.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.npc.NPCInstance;
import net.rageland.ragemod.npc.NPCLocation;
import net.rageland.ragemod.npc.NPCTown;
import net.rageland.ragemod.utilities.Util;
import net.rageland.ragemod.world.PlayerTown;


public class TaskHandler 
{
		
	private HashMap<String, Timestamp> tasks;
	private RageMod plugin;
	
	public TaskHandler(RageMod plugin)
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
		PlayerData playerData;
		ArrayList<String> evictList;
		
		if( plugin.config.PRE_RELEASE_MODE )
			return;
		
		System.out.println("Beginning town upkeep processing...");
		
		// Notify players of incoming lag
		plugin.message.broadcast("Beginning Ragemod upkeep tasks, hold on tight...", ChatColor.DARK_GREEN); // TODO: Store colors in config
		
		for( PlayerTown town : plugin.towns.getAllPlayerTowns() )
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
				plugin.database.townQueries.townDeposit(town.getId(), playerData.id_Player, (income));
				totalBlocks += playerData.treasuryBlocks;
			}
			
			if( totalBlocks > 0 )
				System.out.println("Awarded " + town.getName() + " " + RageMod.econ.format(totalBlocks * plugin.config.INCOME_PER_BLOCK) + " for treasury blocks.");
			
			// *****  SANCTUM CLEANUP  *****
			// Make sure the number of physical blocks in the treasury matches the database values
			actualBlocks = town.countTreasuryBlocks();
			
			if( actualBlocks > totalBlocks )
			{
				System.out.println("WARNING: " + town.getName() + " has " + actualBlocks + " gold blocks in its sanctum, yet only " + totalBlocks + 
						" are recorded in the database.  Deleting " + (actualBlocks - totalBlocks) + " blocks to correct this.");
				town.removeTreasuryBlocks(actualBlocks - totalBlocks);
			}
			else if( actualBlocks < totalBlocks )
			{
				int difference = totalBlocks - actualBlocks;
				System.out.println("WARNING: " + town.getName() + " has " + actualBlocks + " gold blocks in its sanctum, but " + totalBlocks + 
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
						RageMod.econ.bankDeposit(playerData.name, plugin.config.PRICE_GOLD);
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
				playerData = plugin.players.get(playerName);
				
				// Attempt to collect from player's iConomy balance first
				if( RageMod.econ.has(playerName, cost) )
				{
					remaining -= cost;
				}
				// If they don't have enough on hand, see if they have deposits in the treasury
				else if( playerData.treasuryBalance >= cost )
				{
					playerData.treasuryBalance -= cost;
					town.treasuryBalance -= cost;
					remaining -= cost;
					playerData.update();
					plugin.database.townQueries.townDeposit(town.getId(), playerData.id_Player, (cost * -1));
				}
				// If the player doesn't have the funds in either area, evict their freeloading ass
				else if( !playerData.isMayor )
				{
					System.out.println("Automatically evicting " + playerData.name + " from " + town.getName() + ".");
					playerData.clearSpawn();
					playerData.treasuryBalance = 0;
					playerData.logonMessageQueue += "You have been automatically evicted from " + 
							plugin.towns.get(playerData.townName).getCodedName() + " for inability to pay taxes.<br>";
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
					System.out.println("The town of " + town.getName() + " is now bankrupt.");
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
						plugin.towns.remove(town);
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


	// Spawn and despawn NPCs in the world
	public void spawnNPCs() 
	{
		// Spawn non-town NPCs
		ArrayList<NPCInstance> floatingInstances = plugin.npcManager.getFloatingInstances();
		
		for( int i = floatingInstances.size(); i < plugin.config.NPC_TOTAL_FLOATING; i++ )
		{
			NPCInstance instance = plugin.npcManager.spawnRandomFloating();
			if( instance != null )
				System.out.println("Spawned NPC " + instance.getName() + " at location #" + instance.getLocation().getID() + ".");
		}
		
		// Spawn town NPCs
		for( NPCTown town : plugin.towns.getAllNPCTowns() )
		{
			ArrayList<NPCLocation> townLocations = plugin.npcManager.getActiveTownLocations(town.getID());
			for( int i = townLocations.size(); i < town.townLevel.maxNPCs; i++ )
			{
				NPCInstance instance = plugin.npcManager.spawnRandomInTown(town.getID());
				if( instance != null )
					System.out.println("Spawned NPC " + instance.getName() + " in town " + town.getName() + 
							" at location #" + instance.getLocation().getID() + ".");
			}
		}
		
		// Despawn NPCs
		plugin.npcManager.despawnExpired();
	}
	
	
}

