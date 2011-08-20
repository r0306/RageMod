package net.rageland.ragemod.commands;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Lot;
import net.rageland.ragemod.data.Lots;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.data.Town;
import net.rageland.ragemod.data.Towns;

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
			plugin.message.parse(player, "Compass commands: <required> [optional]");
			if( true )
				plugin.message.parse(player, "   /compass lot <lot_code>   (points compass to specified lot)");
			if( true )
				plugin.message.parse(player, "   /compass spawn   (points compass to world spawn)");
			if( !plugin.config.PRE_RELEASE_MODE )
			{
				if( playerData.townName.equals("") )
					plugin.message.parse(player, "   /compass town <town_name>   (points compass to specified town)");
				else
					plugin.message.parse(player, "   /compass town [town_name]   (points compass to town)");
			}	
		}
		else if( split[1].equalsIgnoreCase("lot") )
		{
			if( split.length == 3 )
				this.lot(player, split[2]); 
			else
    			plugin.message.parse(player, "Usage: /compass lot <lot_code>"); 
		}
		else if( split[1].equalsIgnoreCase("spawn") )
		{
			this.spawn(player);
		}
		else if( split[1].equalsIgnoreCase("town") && !plugin.config.PRE_RELEASE_MODE )
		{
			if( split.length == 2 && !playerData.townName.equals("") )
				this.town(player, playerData.townName);
			else if( split.length == 3 )
				this.town(player, split[2]);
    		else
    			plugin.message.parse(player, "Usage: /town info <town_name>");
		}
		else
			plugin.message.parse(player, "Type /compass to see a list of available commands.");
	}

	// /compass lot <lot_code>
	public void lot(Player player, String lotCode) 
	{
		Lot lot = plugin.lots.get(lotCode);
		
		// lot will be null if code is invalid
		if( lot == null )
		{	
			plugin.message.parseNo(player, lotCode + " is not a valid lot code.  (consult the online map)");
			return;
		}
		
		player.setCompassTarget(lot.getCenter());
		plugin.message.send(player, "Compass target set to lot " + lot.getLotCode() + ".");
	}

	// /compass spawn
	public void spawn(Player player) 
	{
		player.setCompassTarget(player.getServer().getWorld("world").getSpawnLocation());
		plugin.message.send(player, "Compass target set to world spawn.");
	}

	// /compass town <town_name>
	public void town(Player player, String townName) 
	{
		Town town = plugin.towns.get(townName);
		
		// Check to see if specified town exists
		if( town == null )
		{
			plugin.message.sendNo(player, "The town '" + townName + "' does not exist.");
			return;
		}
		
		player.setCompassTarget(town.getCenter());
		plugin.message.parse(player, "Compass target set to " + town.getCodedName() + ".");
		
	}

}
