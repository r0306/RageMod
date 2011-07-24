package net.rageland.ragemod.commands;

// TODO: Should the faction populations/costs be loaded upon startup, and updated in memory?

import java.util.HashMap;

import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Factions;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.Players;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Holdings;

public class FactionCommands 
{
	
	private RageMod plugin;
	
	public FactionCommands(RageMod plugin) 
	{
		this.plugin = plugin;
	}
	
	public void onFactionCommand(Player player, PlayerData playerData, String[] split)
	{
		if( split.length < 2 || split.length > 3 )
		{
			Util.message(player, "Faction commands: <required> [optional]");
			if( playerData.id_Faction == 0 )
				Util.message(player, "   /faction join     (used to join a faction)");
			if( playerData.id_Faction != 0 )
				Util.message(player, "   /faction leave    (leaves your faction)");
			if( true )
				Util.message(player, "   /faction stats    (displays stats on each faction)");
		}
		else if( split[1].equalsIgnoreCase("join") )
		{
			if( split.length == 2 )
				this.join(player, "");
			else if( split.length == 3 )
				this.join(player, split[2]); 
			else
    			Util.message(player, "Usage: /faction join [faction_name]"); 
		}
		else if( split[1].equalsIgnoreCase("leave") )
		{
			if( split.length == 2 )
				this.leave(player, false);
    		else if( split.length == 3 && split[2].equalsIgnoreCase("confirm"))
    			this.leave(player, true);
    		else
    			Util.message(player, "Usage: /faction leave [confirm]");
		}
		else if( split[1].equalsIgnoreCase("stats") )
		{
			this.stats(player);
		}
		else
			Util.message(player, "Type /faction to see a list of available commands.");
	}
	
	// /faction join
	public void join(Player player, String factionName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		int id_Faction;
		Holdings balance = iConomy.getAccount(player.getName()).getHoldings();
		
		// Ensure the player is not already a member of a faction
		if( playerData.id_Faction != 0 )
		{
			Util.message(player, "You are already a member of a faction.");
			return;
		}
		
		// Calculate the cost to join each faction
		HashMap<Integer, Integer> populations = plugin.database.factionQueries.getFactionPopulations();
		int lowestPopulation = 9999;
		for( int faction : populations.keySet() )
		{
			if( lowestPopulation > populations.get(faction) )
				lowestPopulation = populations.get(faction);
		}
		for( int faction : populations.keySet() )
		{
			populations.put(faction, (plugin.config.Faction_BaseJoinCost + ((populations.get(faction) - lowestPopulation) * plugin.config.Faction_JoinCostIncrease)));
		}
		
		// If the player did not type a faction name, return the cost to join each faction
		if( factionName.equals("") )
		{
			Util.message(player, "Current costs to join each faction (based on population):");
			for( int faction : populations.keySet() )
			{
				Util.message(player, "   " + plugin.factions.getName(faction) + ": " + iConomy.format(populations.get(faction)));
			}
			return;
		}
		// Check to make sure the typed faction exists
		id_Faction = plugin.factions.getID(factionName);
		if( id_Faction == 0 )
		{
			Util.message(player, "Faction '" + factionName + "' does not exist.");
			return;
		}
		// Check to see if the player has enough money to join the specified faction
		if( !balance.hasEnough(populations.get(id_Faction)) )
		{
			Util.message(player, "You need at least " + iConomy.format(populations.get(id_Faction)) + " to join the " + plugin.factions.getName(id_Faction) + " faction.");
			return;
		}
		
		// Subtract from player balance
		balance.subtract(populations.get(id_Faction));
		
		// Set the player's faction
		playerData.id_Faction = id_Faction;
		plugin.players.update(playerData);
		plugin.database.playerQueries.updatePlayer(playerData);
		
		Util.message(player, "Congratulations, you are now a member of the " + plugin.factions.getName(id_Faction) + " faction!");
	}
	
	// /faction leave
	public void leave(Player player, boolean isConfirmed) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		
		// Ensure the player is a member of a faction
		if( playerData.id_Faction == 0 )
		{
			Util.message(player, "You are not a member of a faction.");
			return;
		}
		
		// See if the player typed "confirm" or not
		if( !isConfirmed )
		{
			Util.message(player, "Are you sure?  You will need to pay the join fee again if you change your mind.");
			Util.message(player, "Type /faction leave confirm to leave your faction.");
		}
		else
		{
			// Reset the player's faction
			Util.message(player, "You are no longer a member of the " + plugin.factions.getName(playerData.id_Faction) + " faction.");
			playerData.id_Faction = 0;
			plugin.players.update(playerData);
			plugin.database.playerQueries.updatePlayer(playerData);
		}
	}
	
	// /faction stats
	public void stats(Player player)
	{
		HashMap<Integer, Integer> populations = plugin.database.factionQueries.getFactionPopulations();
		
		Util.message(player, "Current faction populations (excluding inactive players):");
		for( int faction : populations.keySet() )
		{
			Util.message(player, "   " + plugin.factions.getName(faction) + ": " + populations.get(faction) + " players");
		}
	}

}
