package net.rageland.ragemod.commands;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import net.rageland.ragemod.Build;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.config.RageConfig;
import net.rageland.ragemod.data.TownHandler;
import net.rageland.ragemod.data.PlayerHandler;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.npc.NPCTown;
import net.rageland.ragemod.text.Message;
import net.rageland.ragemod.utilities.Util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
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
		plugin.message.parse(player, "Your current zone is " + plugin.zones.getName(player.getLocation()) 
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
				plugin.message.sendNo(player, "You do not have a home to clear.");
				return;
			}
			else
			{
				// Remove any nearby beds to the home location
				Build.clearNearbyBeds(playerData.getHome());
				playerData.clearHome();
				playerData.update();
				plugin.message.send(player, "Your home has now been cleared.");
				return;
			}
		}
		
		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			plugin.message.sendNo(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}
		
		// Check permissions so only mods and admins can go to another player's home
		if( targetPlayerData.id_Player != playerData.id_Player && !RageMod.perms.has(player, "ragemod.referee.inspectspawn") )
		{
			plugin.message.sendNo(player, "Only mods and admins can teleport to other players' home points.");
			return;
		}
		
		// See if player has an active membership
		if( !playerData.isMember )
		{
			plugin.message.parseNo(player, "Only active Rageland members can use /home.");
			return;
		}
		
		// Check to see if home is on cooldown
		if( playerData.home_LastUsed != null )
		{
			int secondsSinceLastUse = (int)((now.getTime() - playerData.home_LastUsed.getTime()) / 1000);
			if(secondsSinceLastUse < plugin.config.Cooldown_Home)
			{
				plugin.message.parseNo(player, "Spell /home is not ready yet (" + Util.formatCooldown(plugin.config.Cooldown_Home - secondsSinceLastUse) + " left)");
				return;
			}
		}
		// Make sure the player has set a home
		if( targetPlayerData.getHome() == null )			
		{
			plugin.message.parseNo(player, "You have not yet set a /home (place a bed inside your lot).");
			return;
		}
		
		plugin.message.send(player, "Teleporting...");
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
				plugin.message.sendNo(player, "You do not have a spawn to clear.");
				return;
			}
			else
			{
				// Remove any nearby beds to the home location
				Build.clearNearbyBeds(playerData.getSpawn());
				playerData.clearSpawn();
				playerData.update();
				plugin.message.send(player, "Your spawn has now been cleared.");
				return;
			}
		}
		
		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			plugin.message.sendNo(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}
		
		// Check permissions so only mods and admins can go to another player's spawn
		if( targetPlayerData.id_Player != playerData.id_Player && !RageMod.perms.has(player, "ragemod.referee.inspectspawn") )
		{
			plugin.message.sendNo(player, "Only mods and admins can teleport to other players' spawn points.");
			return;
		}
		
		// Check to see if spawn is on cooldown
		if( playerData.spawn_LastUsed != null )
		{
			int secondsSinceLastUse = (int)((now.getTime() - playerData.spawn_LastUsed.getTime()) / 1000);
			if( secondsSinceLastUse < plugin.config.Cooldown_Spawn)
			{
				plugin.message.parseNo(player, "Spell /spawn is not ready yet (" + Util.formatCooldown(plugin.config.Cooldown_Spawn - secondsSinceLastUse) + " left)");
				return;
			}
		}
		
		if( targetPlayerData.getSpawn() != null )			
			destination = targetPlayerData.getSpawn();
		else
			destination = plugin.zones.world.getSpawnLocation();
		
		plugin.message.send(player, "Teleporting...");
		player.teleport(destination);
		playerData.spawn_LastUsed = now;
		plugin.database.playerQueries.updatePlayer(playerData);
	}
	
	// /language or /lang
	
	// /affinity or /aff
	public void affinity(Player player)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		HashMap<Integer, Integer> affinities = new HashMap<Integer, Integer>();
		affinities.put(-2, 0);
		affinities.put(-1, 0);
		affinities.put(0, 0);
		affinities.put(1, 0);
		affinities.put(2, 0);
		
		for( float affinity : playerData.getAffinities().values() )
		{
			int affinityCode = Util.getAffinityCode(affinity);
			affinities.put(affinityCode, affinities.get(affinityCode) + 1);
		}
		
		plugin.message.send(player, ChatColor.GOLD + "NPC affinity totals:");
		for( int i = 2; i >= -2; i-- )
		{
			plugin.message.send(player, "   " + plugin.config.NPC_AFFINITY_CODED_NAMES.get(i) + ": " + ChatColor.WHITE + affinities.get(i));
		}
		plugin.message.send(player, "   " + ChatColor.WHITE + "Total: " + playerData.getAffinities().size());
	}
}
