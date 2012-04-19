package net.rageland.ragemod.commands;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.world.Lot;
import net.rageland.ragemod.world.Town;

import org.bukkit.entity.Player;

public class CompassCommands 
{
	
	private RageMod plugin;
	
	public CompassCommands(RageMod plugin) 
	{
		this.plugin = plugin;
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
