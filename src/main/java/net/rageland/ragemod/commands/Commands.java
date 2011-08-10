package net.rageland.ragemod.commands;

import java.sql.Timestamp;
import java.util.Date;

import net.rageland.ragemod.Build;
import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.RageZones;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTowns;
import net.rageland.ragemod.data.Players;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Commands 
{
	private RageMod plugin;
	
	public Commands(RageMod plugin)
	{
		this.plugin = plugin;
	}
	// /zone
	public void zone(Player player)
	{
		plugin.text.parse(player, "Your current zone is " + plugin.zones.getName(player.getLocation()) 
				+ " and distance from spawn is " + (int)plugin.zones.getDistanceFromSpawn(player.getLocation()));
	}

	// /home [player_name]
	public void home(Player player, String targetPlayerName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		PlayerData targetPlayerData = plugin.players.get(targetPlayerName);
		Timestamp now = new Timestamp(new Date().getTime());
		Location destination;
		
		// Use /home clear to fix problems with beds
		if( targetPlayerName.equalsIgnoreCase("clear") )
		{
			if( playerData.getHome() == null )
			{
				plugin.text.sendNo(player, "You do not have a home to clear.");
				return;
			}
			else
			{
				// Remove any nearby beds to the home location
				Build.clearNearbyBeds(playerData.getHome());
				playerData.clearHome();
				playerData.update();
				plugin.text.send(player, "Your home has now been cleared.");
				return;
			}
		}
		
		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			plugin.text.sendNo(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}
		
		// Check permissions so only mods and admins can go to another player's home
		if( targetPlayerData.id_Player != playerData.id_Player && !RageMod.permissionHandler.has(player, "ragemod.referee.inspectspawn") )
		{
			plugin.text.sendNo(player, "Only mods and admins can teleport to other players' home points.");
			return;
		}
		
		// See if player has an active membership
		if( !playerData.isMember )
		{
			plugin.text.parseNo(player, "Only active Rageland members can use /home.");
			return;
		}
		
		// Check to see if home is on cooldown
		if( playerData.home_LastUsed != null )
		{
			int secondsSinceLastUse = (int)((now.getTime() - playerData.home_LastUsed.getTime()) / 1000);
			if( secondsSinceLastUse < plugin.config.Cooldown_Home )
			{
				plugin.text.parseNo(player, "Spell /home is not ready yet (" + Util.formatCooldown(plugin.config.Cooldown_Home - secondsSinceLastUse) + " left)");
				return;
			}
		}
		// Make sure the player has set a home
		if( targetPlayerData.getHome() == null )			
		{
			plugin.text.parseNo(player, "You have not yet set a /home (place a bed inside your lot).");
			return;
		}
		
		plugin.text.send(player, "Teleporting...");
		player.teleport(targetPlayerData.getHome());
		playerData.home_LastUsed = now;
		playerData.update();
	}
	
	// /spawn [player_name]
	public void spawn(Player player, String targetPlayerName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		PlayerData targetPlayerData = plugin.players.get(targetPlayerName);
		Timestamp now = new Timestamp(new Date().getTime());
		Location destination;
		
		// Use /home clear to fix problems with beds
		if( targetPlayerName.equalsIgnoreCase("clear") )
		{
			if( targetPlayerData.getSpawn() == null )
			{
				plugin.text.sendNo(player, "You do not have a spawn to clear.");
				return;
			}
			else
			{
				// Remove any nearby beds to the home location
				Build.clearNearbyBeds(playerData.getSpawn());
				playerData.clearSpawn();
				playerData.update();
				plugin.text.send(player, "Your spawn has now been cleared.");
				return;
			}
		}
		
		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			plugin.text.sendNo(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}
		
		// Check permissions so only mods and admins can go to another player's spawn
		if( targetPlayerData.id_Player != playerData.id_Player && !RageMod.permissionHandler.has(player, "ragemod.referee.inspectspawn") )
		{
			plugin.text.sendNo(player, "Only mods and admins can teleport to other players' spawn points.");
			return;
		}
		
		// Check to see if spawn is on cooldown
		if( playerData.spawn_LastUsed != null )
		{
			int secondsSinceLastUse = (int)((now.getTime() - playerData.spawn_LastUsed.getTime()) / 1000);
			if( secondsSinceLastUse < plugin.config.Cooldown_Spawn )
			{
				plugin.text.parseNo(player, "Spell /spawn is not ready yet (" + Util.formatCooldown(plugin.config.Cooldown_Spawn - secondsSinceLastUse) + " left)");
				return;
			}
		}
		
		if( targetPlayerData.getSpawn() != null )			
			destination = targetPlayerData.getSpawn();
		else
			destination = plugin.zones.world.getSpawnLocation();
		
		plugin.text.send(player, "Teleporting...");
		player.teleport(destination);
		playerData.spawn_LastUsed = now;
		plugin.database.playerQueries.updatePlayer(playerData);
	}
	
	
}
