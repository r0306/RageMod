package net.rageland.ragemod.commands;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;

import org.bukkit.entity.Player;

// Allows staff to give temporary permissions to players
public class PermitCommands 
{
	private RageMod plugin;
	private int DEFAULT_LENGTH = 7;		// Length of permit in days
	
	public PermitCommands(RageMod plugin) 
	{
		this.plugin = plugin;
	}
	
	public void onCommand(Player player, PlayerData playerData, String[] split) 
	{
		if( split.length < 2 || split.length > 3 )
		{
			plugin.message.parse(player, "Permit commands: <required> [optional]");
			if( true )
				plugin.message.parse(player, "   /permit capitol <player_name>   (edit city infrastructure)");	
		}
		else if( split[1].equalsIgnoreCase("capitol") )
		{
			if( split.length == 3 )
				this.capitol(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /permit capitol <player_name>"); 
		}
		else
			plugin.message.parse(player, "Type /permit to see a list of available commands.");
	}
	
	
	
	// /permit capitol <player_name>
	public void capitol(Player player, String targetPlayerName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		PlayerData targetPlayerData = plugin.players.get(targetPlayerName);
		
		// Make sure the player has permission to perform this command
		if( !RageMod.perms.has(player, "ragemod.permit.capitol") )
		{
			plugin.message.sendNo(player, "You do not have permission to perform that command.");
			return;
		}
		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			plugin.message.sendNo(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}
		
		// All checks have succeeded - assign the permit
		plugin.database.playerQueries.grantPermit(playerData.id_Player, targetPlayerData.id_Player, "CAPITOL", DEFAULT_LENGTH);
		
		// Update the playerData
		targetPlayerData.permits.capitol = true;
		
		// Notify both players
		plugin.message.parse(player, targetPlayerData.getCodedName() + " has been granted a one-week permit to build in the city.");
		Player targetPlayer = plugin.getServer().getPlayer(targetPlayerData.name);
		if( targetPlayer != null && targetPlayer.isOnline() )
			plugin.message.parse(targetPlayer, playerData.getCodedName() + " has granted you a one-week permit to build in the city.");
	}
}
