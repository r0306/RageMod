package net.rageland.ragemod.commands;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Lot;
import net.rageland.ragemod.data.Lots;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.data.PlayerTowns;

import org.bukkit.entity.Player;

public class CompassCommands 
{
	
	private RageMod plugin;
	
	public CompassCommands(RageMod plugin) 
	{
		this.plugin = plugin;
	}
	
	public void onCompassCommand(Player player, PlayerData playerData, String[] split) 
	{
		if( split.length < 2 || split.length > 3 )
		{
			Util.message(player, "Compass commands: <required> [optional]");
			if( true )
				Util.message(player, "   /compass lot <lot_code>   (points compass to specified lot)");
			if( true )
				Util.message(player, "   /compass spawn   (points compass to world spawn)");
			if( playerData.townName.equals("") )
				Util.message(player, "   /compass town <town_name>   (points compass to specified town)");
			else
				Util.message(player, "   /compass town [town_name]   (points compass to town)");
		}
		else if( split[1].equalsIgnoreCase("lot") )
		{
			if( split.length == 3 )
				this.lot(player, split[2]); 
			else
    			Util.message(player, "Usage: /compass lot <lot_code>"); 
		}
		else if( split[1].equalsIgnoreCase("spawn") )
		{
			this.spawn(player);
		}
		else if( split[1].equalsIgnoreCase("town") )
		{
			if( split.length == 2 && !playerData.townName.equals("") )
				this.town(player, playerData.townName);
			else if( split.length == 3 )
				this.town(player, split[2]);
    		else
    			Util.message(player, "Usage: /town info <town_name>");
		}
		else
			Util.message(player, "Type /compass to see a list of available commands.");
	}

	// /compass lot <lot_code>
	public void lot(Player player, String lotCode) 
	{
		Lot lot = plugin.lots.get(lotCode);
		
		// lot will be null if code is invalid
		if( lot == null )
		{	
			Util.message(player, lotCode + " is not a valid lot code.  (consult the online map)");
			return;
		}
		
		player.setCompassTarget(lot.getCenter());
		Util.message(player, "Compass target set to lot " + lot.getLotCode() + ".");
	}

	// /compass spawn
	public void spawn(Player player) 
	{
		player.setCompassTarget(player.getServer().getWorld("world").getSpawnLocation());
		Util.message(player, "Compass target set to world spawn.");
	}

	// /compass town <town_name>
	public void town(Player player, String townName) 
	{
		PlayerTown playerTown = plugin.playerTowns.get(townName);
		
		// Check to see if specified town exists
		if( playerTown == null )
		{
			Util.message(player, "The town '" + townName + "' does not exist.");
			return;
		}
		
		player.setCompassTarget(playerTown.getCenter());
		Util.message(player, "Compass target set to " + playerTown.townName + ".");
		
	}

}
