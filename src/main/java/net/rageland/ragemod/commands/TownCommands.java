package net.rageland.ragemod.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.RageZones;
import net.rageland.ragemod.RageZones.Action;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Factions;
import net.rageland.ragemod.data.Location2D;
import net.rageland.ragemod.data.NPCLocation;
import net.rageland.ragemod.data.NPCTown;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.data.Town;
import net.rageland.ragemod.data.Towns;
import net.rageland.ragemod.data.Players;
import net.rageland.ragemod.data.TownLevel;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.iCo6.iConomy;
import com.iCo6.system.Accounts;
import com.iCo6.system.Holdings;

// TODO: Keep towns from being created on zone borders

// The Commands classes handle all the state checking for typed commands, then send them to the database if approved
public class TownCommands 
{
	
	private RageMod plugin;
	
	public TownCommands(RageMod plugin)
	{
		this.plugin = plugin;
	}
	
	public void onTownCommand(Player player, PlayerData playerData, String[] split) 
	{
		if( split.length < 2 || split.length > 3 )
		{
			plugin.message.parse(player, "Town commands: <required> [optional]");
			if( playerData.isMayor && !playerData.townName.equals("") )
				plugin.message.parse(player, "   /town add <player_name>   (adds a new resident)");
			if( playerData.townName.equals("") )
				plugin.message.parse(player, "   /town create [town_name]   (creates a new town)");
			if( !playerData.townName.equals("") )
				plugin.message.parse(player, "   /town deposit <amount>   (deposits into town treasury)");
			if( playerData.isMayor && !playerData.townName.equals(""))
				plugin.message.parse(player, "   /town evict <player_name>   (removes a resident)");
			if( playerData.townName.equals("") )
				plugin.message.parse(player, "   /town info <town_name>   (gives info on selected town)");
			else
				plugin.message.parse(player, "   /town info [town_name]   (gives info on selected town)");
			if( !playerData.isMayor && !playerData.townName.equals("") )
				plugin.message.parse(player, "   /town leave   (leaves your current town)");
			if( true )
				plugin.message.parse(player, "   /town list [npc/faction]   (lists all towns in the world)");
			if( playerData.isMayor && !playerData.townName.equals("") )
				plugin.message.parse(player, "   /town minimum <amount>   (sets the min. treasury balance)");
			if( playerData.townName.equals("") )
				plugin.message.parse(player, "   /town residents <town_name>   (lists all residents of town)");
			else
				plugin.message.parse(player, "   /town residents [town_name]   (lists all residents of town)");
			if( playerData.isMayor && !playerData.townName.equals("") )
				plugin.message.parse(player, "   /town upgrade [confirm]   (upgrades your town)");
			if( !playerData.townName.equals("") )
				plugin.message.parse(player, "   /town withdrawl <amount>   (withdrawls from town treasury)");
		}
		else if( split[1].equalsIgnoreCase("add") )
		{
			if( split.length == 3 )
    			this.add(player, split[2]); 
    		else
    			plugin.message.parse(player, "Usage: /town add <player_name>");
		}
		else if( split[1].equalsIgnoreCase("create") )
		{
	
			// TODO: Support 2+ word town names
			
			if( split.length == 2 )
				this.create(player, "");
			else if( split.length == 3 )
				this.create(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /town create [town_name] (use 'quotes' for multiple-word town names)"); 
		}
		else if( split[1].equalsIgnoreCase("deposit") )
		{
			if( split.length == 3 )
				this.deposit(player, split[2]); 
    		else
    			plugin.message.parse(player, "Usage: /town deposit <amount>");
		}
		else if( split[1].equalsIgnoreCase("evict") )
		{
			if( split.length == 3 )
				this.evict(player, split[2]); 
    		else
    			plugin.message.parse(player, "Usage: /town evict <player_name>");
		}
		else if( split[1].equalsIgnoreCase("info") )
		{
			if( split.length == 2 && !playerData.townName.equals("") )
				this.info(player, playerData.townName);
			else if( split.length == 3 )
				this.info(player, split[2]);
    		else
    			plugin.message.parse(player, "Usage: /town info <town_name>");
		}
		else if( split[1].equalsIgnoreCase("leave") )
		{
			this.leave(player); 	 
		}
		else if( split[1].equalsIgnoreCase("list") )
		{
			if( split.length == 2 )
				this.list(player, "");
			else if( split.length == 3 )
				this.list(player, split[2]);
    		else
    			plugin.message.parse(player, "Usage: /town list [faction]");
		}
		else if( split[1].equalsIgnoreCase("minimum") )
		{
			if( split.length == 3 )
				this.minimum(player, split[2]); 
    		else
    			plugin.message.parse(player, "Usage: /town minimum <amount>");
		}
		else if( split[1].equalsIgnoreCase("residents") )
		{
			if( split.length == 2 && !playerData.townName.equals("") )
				this.residents(player, playerData.townName);
			else if( split.length == 3 )
				this.residents(player, split[2]);
    		else
    			plugin.message.parse(player, "Usage: /town residents <town_name>");
		}
		else if( split[1].equalsIgnoreCase("upgrade") )
		{
			if( split.length == 2 )
    			this.upgrade(player, false);
    		else if( split.length == 3 && split[2].equalsIgnoreCase("confirm"))
    			this.upgrade(player, true);
    		else
    			plugin.message.parse(player, "Usage: /town upgrade [confirm]");
		}
		else if( split[1].equalsIgnoreCase("withdrawl") )
		{
			if( split.length == 3 )
				this.withdrawl(player, split[2]); 
    		else
    			plugin.message.parse(player, "Usage: /town withdrawl <amount>");
		}
		else
			plugin.message.parse(player, "Type /town to see a list of available commands.");
	}
	
	// /town add <player_name>
	public void add(Player player, String targetPlayerName)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		PlayerData targetPlayerData = plugin.players.get(targetPlayerName);
		PlayerTown playerTown = (PlayerTown)plugin.towns.get(playerData.townName);
		
		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			plugin.message.sendNo(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}
		// Ensure that the current player is the mayor
		if( !playerData.isMayor || playerTown == null )
		{
			plugin.message.sendNo(player, "Only town mayors can use '/town add'.");
			return;
		}		
		// Ensure that the target player is not currently a resident of a town
		if( !targetPlayerData.townName.equals("") )
		{
			plugin.message.parseNo(player, targetPlayerData.getCodedName() + " is already a resident of '" + playerTown.getCodedName() + "'.");
			return;
		}		
		// Ensure that the target player is the same faction as the mayor
		if( playerData.id_Faction != targetPlayerData.id_Faction )
		{
			plugin.message.sendNo(player, "You can only add players that are the same faction as you.");
			return;
		}
		if( playerTown.isFull() )
		{
			plugin.message.sendNo(player, "Your town already has the maximum number of residents for its level.");
			return;
		}
		
		// Add the target to the player's town
		plugin.database.townQueries.townAdd(targetPlayerName, playerData.townName);
		
		// Update the playerData
		targetPlayerData.townName = playerData.townName;
		// This will give the player's balance back if they were a previous resident of the town
		targetPlayerData.treasuryBalance = plugin.database.playerQueries.getPlayerTreasuryBalance(targetPlayerData.id_Player, playerTown.getID());
		
		playerTown.residents.add(targetPlayerData.name);
		
		plugin.message.parse(player, targetPlayerData.getCodedName() + " is now a resident of " + playerTown.getCodedName() + ".");		
	}
	
	// /town  create <town_name>
	public void create(Player player, String townName)
	{		
		PlayerData playerData = plugin.players.get(player.getName());
		HashMap<String, Integer> nearbyTowns = plugin.towns.checkForNearbyTowns(player.getLocation());
		Holdings holdings = Accounts.getAccount(player.getName()).getHoldings();
		int cost = plugin.config.townLevels.get(1).initialCost;

		// Ensure that the player is not currently a resident of a town
		if( !playerData.townName.equals("") )
		{
			plugin.message.parseNo(player, "You are already a resident of '" + plugin.towns.get(playerData.townName).getCodedName() + "'; you must use '/town leave' before you can create a new town.");
			return;
		}		
		// Ensure that the town name is not taken
		if( plugin.towns.get(townName) != null )
		{
			plugin.message.sendNo(player, "A town named " + townName + " already exists!");
			return;
		}
		// Ensure that the current zone is allowed to create towns
		if( !plugin.zones.checkPermission(player.getLocation(), Action.TOWN_CREATE) )
		{
			plugin.message.parseNo(player, "You cannot create a town in this zone.");
			return;
		}
		// Check for any towns that are too close to the current point - list all
		if( nearbyTowns.size() > 0 )
		{
			String message = "You are too close to the following towns: ";
			for( String nearbyTownName : nearbyTowns.keySet() )
			{
				message += plugin.towns.get(nearbyTownName).getCodedName() + " (" + nearbyTowns.get(nearbyTownName) + "m) ";
			}
			plugin.message.parseNo(player, message);
			plugin.message.parse(player, "Towns must be a minimum distance of " + plugin.config.Town_MIN_DISTANCE_BETWEEN + "m apart.");
			return;
		}
		// Check to see if the player has enough money to join the specified faction
		if( !holdings.hasEnough(cost) )
		{
			plugin.message.parseNo(player, "You need at least " + iConomy.format(cost) + " to create a " + plugin.config.townLevels.get(1).name + ".");
			return;
		}
		
		// TODO: Check against NPC town names
		
		// Create the town if name selected, otherwise return message
		if( !townName.equals("") )
		{
			// Add the new town to the database
			int townID = plugin.database.townQueries.townCreate(player, townName);
			
			// Update PlayerTowns
			PlayerTown playerTown = new PlayerTown(plugin, townID, townName, player.getWorld());
			playerTown.centerPoint = player.getLocation();
					
			playerTown.id_Faction = playerData.id_Faction;
			playerTown.bankruptDate = null;
			playerTown.townLevel = plugin.config.townLevels.get(1);
			playerTown.treasuryBalance = plugin.config.townLevels.get(1).minimumBalance;
			playerTown.minimumBalance = plugin.config.townLevels.get(1).minimumBalance;
			playerTown.mayor = playerData.name;
			playerTown.residents.add(playerData.name);
			
			playerTown.createRegions();
			playerTown.build();
			
			plugin.towns.add(playerTown);
			
			// Update the playerData
			playerData.townName = townName;
			playerData.isMayor = true;
			playerData.currentTown = playerTown;
			playerData.treasuryBalance = cost;
			
			// Subtract from player balance
			holdings.subtract(cost);
			
			plugin.message.parse(player, "Congratulations, you are the new mayor of " + playerTown.getCodedName() + "!");		
		}
		else
		{
			plugin.message.parse(player, "This location is valid for a new town - to create one for " + iConomy.format(cost) + ", type '/town create <town_name>'");
		}
	}
	
	// /town deposit <amount>
	public void deposit(Player player, String amountString)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		double amount;
		Holdings holdings = iConomy.getAccount(player.getName()).getHoldings();
		PlayerTown playerTown = (PlayerTown)plugin.towns.get(playerData.townName);
		
		// Make sure the player is a resident of a town
		if( playerData.townName.equals("") )
		{
			plugin.message.sendNo(player, "Only town residents can use the deposit command.");
			return;
		}
		// Ensure that the typed amount is a valid number
		try
		{
			amount = Double.parseDouble(amountString);
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Invalid amount.");
			return;
		}
		// Ensure that the amount is greater than 0 (no sneaky withdrawls!)
		if( amount <= 0 )
		{
			plugin.message.sendNo(player, "Invalid amount.");
			return;	
		}
		// Make sure the player has enough money to make the deposit
		if( !holdings.hasEnough(amount) )
		{
			plugin.message.parseNo(player, "You only have " + iConomy.format(holdings.balance()) + ".");
			return;
		}
		
		// Subtract the amount from the player's balance
		holdings.subtract(amount);
		
		// Update the database
		plugin.database.townQueries.townDeposit(playerTown.getID(), playerData.id_Player, amount);
		
		// Update the town data
		playerTown.treasuryBalance += amount; 
		plugin.towns.add(playerTown);
		
		// Update the player data
		playerData.treasuryBalance += amount;
		
		plugin.message.parse(player, "Deposited " + iConomy.format(amount) + " into town treasury.");
	}
	
	// /town evict <player_name>
	public void evict(Player player, String targetPlayerName)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		PlayerData targetPlayerData = plugin.players.get(targetPlayerName);
		
		// Ensure that the current player is the mayor
		if( !playerData.isMayor || playerData.townName.equals("") )
		{
			plugin.message.parseNo(player, "Only town mayors can use /town evict.");
			return;
		}	
		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			plugin.message.sendNo(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}	
		// Ensure that the target player is a resident of the mayor's town
		if( !targetPlayerData.townName.equals(playerData.townName) )
		{
			plugin.message.parseNo(player, targetPlayerData.getCodedName() + " is not a resident of " + plugin.towns.get(playerData.townName).getCodedName() + ".");
			return;
		}
		
		// Remove the target from the player's town
		plugin.database.townQueries.townLeave(targetPlayerName);
		
		// TODO: This causes two updates to the same table in the database.  Expand updatePlayer() to include town info
		
		// Update the playerData
		targetPlayerData.townName = "";
		targetPlayerData.clearSpawn();
		targetPlayerData.logonMessageQueue += "You have been evicted from " + plugin.towns.get(playerData.townName).getCodedName() + " by " + 
				playerData.getCodedName() + ".<br>";
		targetPlayerData.update();
		
		plugin.message.parse(player, targetPlayerData.getCodedName() + " is no longer a resident of " + plugin.towns.get(playerData.townName).getCodedName() + ".");		
	}
	
	// /town info [town_name]
	public void info(Player player, String townName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		
		Town town = plugin.towns.get(townName);
		
		// Check to see if specified town exists
		if( town == null )
		{
			plugin.message.sendNo(player, "The town '" + townName + "' does not exist.");
			return;
		}
		
		if( town instanceof PlayerTown )
		{
			PlayerTown playerTown = (PlayerTown)town;
			
			plugin.message.parse(player, "Info for " + playerTown.getCodedName() + ":");
			plugin.message.parse(player, "   Faction: " + plugin.factions.getName(playerTown.id_Faction));
			plugin.message.parse(player, "   Level: " + playerTown.getLevel().name + " (" + playerTown.townLevel.level + ")");
			plugin.message.parse(player, "   Mayor: " + plugin.players.get(playerTown.mayor).getCodedName());
			if( playerData.townName.equalsIgnoreCase(townName) )
			{
				plugin.message.parse(player, "   Total Balance: " + iConomy.format(playerTown.treasuryBalance));
				plugin.message.parse(player, "   Minimum Balance:  " + iConomy.format(playerTown.minimumBalance));
				plugin.message.parse(player, "   Your Balance:  " + iConomy.format(playerData.treasuryBalance));
			}
		}
		else if( town instanceof NPCTown )
		{
			plugin.message.parseNo(player, town.getCodedName() + " is an NPCTown (use /npctown info).");
		}
	}
	
	// /town leave
	public void leave(Player player)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		String townName = playerData.townName;

		// Ensure that the player is currently a resident of a town
		if( townName.equals("") )
		{
			plugin.message.sendNo(player, "You do not have a town to leave.");
			return;
		}		
		// Ensure that the player is not the mayor
		if( playerData.isMayor )
		{
			plugin.message.parseNo(player, "Town mayors cannot use '/town leave'; contact an admin to shut down your town.");
			return;
		}
		
		// Remove the player from the town in the database
		plugin.database.townQueries.townLeave(playerData.name);
		
		// Update the playerData
		playerData.townName = "";
		playerData.isMayor = false;
		playerData.clearSpawn();
		playerData.treasuryBalance = 0;
		
		plugin.message.parse(player, "You are no longer a resident of " + plugin.towns.get(townName).getCodedName() + ".");		
	}
	
	// /town list [faction]
	public void list(Player player, String factionName) 
	{
		ArrayList<PlayerTown> playerTowns = plugin.towns.getAllPlayerTowns();
		
		// TODO: Implement page # functionality 
		
		// Sorts the towns by level
		Collections.sort(playerTowns);
		
		if( factionName.equals("") )
			plugin.message.parse(player, "List of all player towns: (" + playerTowns.size() + ")");
		else
			plugin.message.parse(player, "List of all towns for " + factionName + " faction:");
		
		for( PlayerTown town : playerTowns )
		{
			if( plugin.factions.getName(town.id_Faction).equalsIgnoreCase(factionName) || factionName.equals("") )
				plugin.message.parse(player, town.getCodedName() + " (" + town.getLevel().name + ")");
		}
	}
	
	// /town minimum <amount>
	public void minimum(Player player, String amountString)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		double amount;
		PlayerTown playerTown = (PlayerTown)plugin.towns.get(playerData.townName);
		
		// Ensure that the current player is the mayor
		if( !playerData.isMayor || playerData.townName.equals("") )
		{
			plugin.message.parseNo(player, "Only town mayors can use /town minimum.");
			return;
		}	
		// Ensure that the typed amount is a valid number
		try
		{
			amount = Double.parseDouble(amountString);
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Invalid amount.");
			return;
		}
		// Ensure that the amount is greater than 0
		if( amount <= 0 )
		{
			plugin.message.sendNo(player, "Invalid amount.");
			return;	
		}
		// Make sure the amount is not lower than the server-defined minimum balances
		if( amount < playerTown.townLevel.minimumBalance )
		{
			plugin.message.parseNo(player, "The lowest minimum balance allowed for a " + playerTown.townLevel.name + " is " + 
								iConomy.format(playerTown.townLevel.minimumBalance) + ".");
			return;
		}
		
		// Update the database
		plugin.database.townQueries.townSetMinimumBalance(playerTown.getID(), amount);
		
		// Update the town data
		playerTown.minimumBalance = amount; 
		plugin.towns.add(playerTown);
		
		plugin.message.parse(player, "Your town's treasury minimum balance is now " + iConomy.format(amount) + ".");
	}
	
	// /town residents [town_name]
	public void residents(Player player, String townName) 
	{
		PlayerTown playerTown = (PlayerTown)plugin.towns.get(townName);
		boolean isMayor = true;
		
		// Check to see if specified town exists
		if( playerTown == null )
		{
			plugin.message.sendNo(player, "The town '" + townName + "' does not exist.");
			return;
		}
		
		plugin.message.parse(player, "Residents of " + playerTown.getCodedName() + ":");
		
		for( String resident : playerTown.residents )
		{
			if( isMayor )
			{
				plugin.message.parse(player, "   " + plugin.players.get(resident).getCodedName() + " (mayor)");
				isMayor = false;
			}
			else
			{
				plugin.message.parse(player, "   " + plugin.players.get(resident).getCodedName());
			}
		}
	}
	
	// /town upgrade <confirm>
	public void upgrade(Player player, boolean isConfirmed)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		PlayerTown playerTown = (PlayerTown)plugin.towns.get(playerData.townName);
		
		// Ensure that the current player is the mayor
		if( !playerData.isMayor || playerData.townName.equals("") )
		{
			plugin.message.parseNo(player, "Only town mayors can use '/town upgrade'.");
			return;
		}
		// Ensure that the town is not at its maximum level
		if( playerTown.isAtMaxLevel() )
		{
			plugin.message.sendNo(player, "Your town is already at its maximum level.");
			return;
		}
		
		// Load the data for the target town level
		TownLevel targetLevel = plugin.config.townLevels.get(playerTown.townLevel.level + 1);
		
		// If the upgrade would make the current town a capitol...
		if( targetLevel.isCapitol )
		{
			// ...check to see if the player's faction already has a capitol...
			if( plugin.towns.doesFactionCapitolExist(playerData.id_Faction) )
			{
				plugin.message.sendNo(player, "Your faction already has a capitol; your town cannot be upgraded further.");
				return;
			}
			// ...and make sure it is not too close to enemy capitols.
			if( plugin.towns.areEnemyCapitolsTooClose(playerTown) )
			{
				plugin.message.sendNo(player, "Your town is ineligible to be your faction's capitol; it is too close to an enemy capitol.");
				return;
			}
		}
		// Check treasury balance
		if( playerTown.treasuryBalance < targetLevel.initialCost )
		{
			plugin.message.parseNo(player, "You need at least " + iConomy.format(targetLevel.initialCost) + " in your treasury to upgrade your town to a " + targetLevel.name + ".");
			return;
		}
		
		// Make the updates if confirm was typed
		if( isConfirmed ) 
		{
			// Update PlayerTowns; subtract balance from treasury; also add minimum balance
			playerTown.townLevel = plugin.config.townLevels.get(playerTown.townLevel.level + 1);
			playerTown.treasuryBalance = playerTown.treasuryBalance - targetLevel.initialCost + targetLevel.minimumBalance;
			playerTown.minimumBalance = targetLevel.minimumBalance;
			playerTown.createRegions();
			playerTown.build();
			plugin.towns.add(playerTown);
			
			plugin.database.townQueries.townUpgrade(playerTown.getName(), (targetLevel.initialCost - targetLevel.minimumBalance));
			
			plugin.message.parse(player, "Congratulations, " + playerTown.getCodedName() + " has been upgraded to a " + targetLevel.name + "!");
			plugin.message.parse(player, " " + iConomy.format(targetLevel.initialCost) + " has been deducted from the town treasury.");
		}
		else
		{
			plugin.message.parse(player, "Your town is ready to be upgraded to a " + targetLevel.name + " for " + iConomy.format(targetLevel.initialCost) + 
					"; type '/town upgrade confirm' to complete the upgrade.");
		}
		
	}
	
	// /town withdrawl <amount>
	public void withdrawl(Player player, String amountString)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		double amount;
		Holdings holdings = iConomy.getAccount(player.getName()).getHoldings();
		PlayerTown playerTown = (PlayerTown)plugin.towns.get(playerData.townName);
		
		// Make sure the player is a resident of a town
		if( playerData.townName.equals("") )
		{
			plugin.message.sendNo(player, "Only town residents can use the deposit command.");
			return;
		}
		// Ensure that the typed amount is a valid number
		try
		{
			amount = Double.parseDouble(amountString);
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Invalid amount.");
			return;
		}
		// Ensure that the amount is greater than 0
		if( amount <= 0 )
		{
			plugin.message.sendNo(player, "Invalid amount.");
			return;	
		}
		// Make sure the player has a high enough balance to make the withdrawl
		if( playerData.treasuryBalance < amount )
		{
			plugin.message.parseNo(player, "You only have " + iConomy.format(playerData.treasuryBalance) + " in the treasury.");
			return;
		}
		// Make sure the withdrawl wouldn't put the town below its minimum balance
		if( playerTown.treasuryBalance - amount < playerTown.minimumBalance )
		{
			plugin.message.sendNo(player, "This transaction would put the town below its minimum balance.");
			return;
		}
		
		// Add the amount to the player's balance
		holdings.add(amount);
		
		// Update the database
		plugin.database.townQueries.townDeposit(playerTown.getID(), playerData.id_Player, (amount * -1));
		
		// Update the town data
		playerTown.treasuryBalance -= amount; 
		plugin.towns.add(playerTown);
		
		// Update the player data
		playerData.treasuryBalance -= amount;
		
		plugin.message.parse(player, "Withdrew " + iConomy.format(amount) + " from town treasury.");
	}
	
	

	



	
	
	
	
	
	
}
