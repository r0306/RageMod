package net.rageland.ragemod.commands;

import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.RageZones;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Lot;
import net.rageland.ragemod.data.Lot.LotCategory;
import net.rageland.ragemod.data.Lots;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.Players;

import org.bukkit.entity.Player;

//The Commands classes handle all the state checking for typed commands, then send them to the database if approved
public class LotCommands 
{
	
	private RageMod plugin;
	
	public LotCommands(RageMod plugin) 
	{
		this.plugin = plugin;
	}
	
	public void onLotCommand(Player player, PlayerData playerData, String[] split)
	{
		if( split.length < 2 || split.length > 4 )
		{
			Util.message(player, "Lot commands: <required> [optional]");
			if( playerData.lots.size() > 0 )
				Util.message(player, "   /lot allow <player_name> (allow player to build in your lots)");
			if( RageMod.permissionHandler.has(player, "ragemod.lot.assign") )
				Util.message(player, "   /lot assign <lot_code> <player_name>  (gives lot to player)");
			if( true )
				Util.message(player, "   /lot check   (returns info on the current lot)");
			if( true )
				Util.message(player, "   /lot claim [lot_code]   (claims the specified or current lot)");
			if( playerData.lots.size() > 0 )
				Util.message(player, "   /lot disallow <player_name/all> (removes permissions)");
			if( RageMod.permissionHandler.has(player, "ragemod.lot.evict") )
				Util.message(player, "   /lot evict <lot_code>   (sets specified lot to 'unclaimed')");
			if( playerData.lots.size() > 0 )
				Util.message(player, "   /lot list   (lists all lots you own)");
			if( playerData.lots.size() > 0 )
				Util.message(player, "   /lot unclaim [lot_code]  (unclaims the specified lot)");
		}
		else if( split[1].equalsIgnoreCase("allow") )
		{
			if( split.length == 3 )
				this.allow(player, split[2]); 
			else
    			Util.message(player, "Usage: /lot allow <player_name>"); 
		}
		else if( split[1].equalsIgnoreCase("assign") )
		{
			if( split.length == 4 )
				this.assign(player, split[2], split[3]); 
			else
    			Util.message(player, "Usage: /lot assign <lot_code> <player_name>"); 
		}
		else if( split[1].equalsIgnoreCase("check") )
		{
			this.check(player);
		}
		else if( split[1].equalsIgnoreCase("claim") )
		{
			if( split.length == 2 )
				this.claim(player, "");
			else if( split.length == 3 )
				this.claim(player, split[2]); 
			else
    			Util.message(player, "Usage: /lot claim [lot_code]"); 
		}
		else if( split[1].equalsIgnoreCase("disallow") )
		{
			if( split.length == 3 )
				this.disallow(player, split[2]); 
			else
    			Util.message(player, "Usage: /lot disallow <player_name/all>"); 
		}
		else if( split[1].equalsIgnoreCase("evict") )
		{
			if( split.length == 3 )
				this.evict(player, split[2]); 
			else
    			Util.message(player, "Usage: /lot evict <lot_code>"); 
		}
		else if( split[1].equalsIgnoreCase("list") )
		{
			this.list(player);
		}
		else if( split[1].equalsIgnoreCase("unclaim") )
		{
			if( split.length == 2 )
				this.unclaim(player, "");
			else if( split.length == 3 )
				this.unclaim(player, split[2]); 
			else
    			Util.message(player, "Usage: /lot unclaim [lot_code]"); 
		}
		else
			Util.message(player, "Type /lot to see a list of available commands.");
	}
	
	// /lot allow <player_name>
	public void allow(Player player, String targetPlayerName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		PlayerData targetPlayerData = plugin.players.get(targetPlayerName);

		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			Util.message(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}
		// Check to see if the target player already has permission
		if( playerData.lotPermissions.contains(targetPlayerData.name) )
		{
			Util.message(player, targetPlayerData.getNameColor() + " already has permission to build in your lots.");
			return;
		}
		
		// All checks have succeeded - update the DB
		plugin.database.lotQueries.lotAllow(playerData.id_Player, targetPlayerData.id_Player);
		
		// Update the playerData
		playerData.lotPermissions.add(targetPlayerData.name);
		plugin.players.update(playerData);
		
		Util.message(player, targetPlayerData.getNameColor() + " is now allowed to build in your lots.");
	}
	
	
	// /lot assign <lot_code> <player_name>
	public void assign(Player player, String lotCode, String targetPlayerName) 
	{
		PlayerData targetPlayerData = plugin.players.get(targetPlayerName);
		Lot lot = plugin.lots.get(lotCode);
		
		// Make sure the player has permission to perform this command
		if( !RageMod.permissionHandler.has(player, "ragemod.lot.assign") )
		{
			Util.message(player, "You do not have permission to perform that command.");
			return;
		}
		// Check to see if target player exists
		if( targetPlayerData == null )
		{
			Util.message(player, "Player " + targetPlayerName + " does not exist.");
			return;
		}
		// lot will be null if code is invalid
		if( lot == null )
		{	
			Util.message(player, lotCode + " is not a valid lot code.  (consult the online map)");
			return;
		}
		// See if the lot is already claimed
		if( !lot.owner.equals("") )
		{
			Util.message(player, "Lot " + lot.getLotCode() + " is already owned by " + plugin.players.get(lot.owner).getNameColor() + ".");
			return;
		}
		
		// All checks have succeeded - give the lot to the player
		plugin.database.lotQueries.lotClaim(targetPlayerData, lot);
		
		// Update the playerData
		targetPlayerData.lots.add(lot);
		plugin.players.update(targetPlayerData);
		
		// Update Lots to set the owner
		lot.owner = targetPlayerData.name;
		plugin.lots.put(lot);
		
		Util.message(player, targetPlayerData.getNameColor() + " now owns lot " + lot.getLotCode() + ".");
	}
	
	// /lot check
	public void check(Player player)
	{		
		// Make sure the player is in the capitol
		if( !RageZones.isInCapitol(player.getLocation()) )
		{
			Util.message(player, "You must be in " + plugin.config.Capitol_Name + " to use this command.");
		}
		
		Lot lot = plugin.lots.findCurrentLot(player.getLocation());
		
		if( lot != null )
		{
			Util.message(player, "You are currently in lot " + lot.getLotCode() + " (" + lot.getCategoryName() + ").");
			if( lot.owner.equals("") )
				Util.message(player, "This lot is unowned - type /lot claim to claim it.");
			else
				Util.message(player, "This lot is owned by " + plugin.players.get(lot.owner).getNameColor() + ".");
		}
		else
		{
			Util.message(player, "You are not standing inside of a lot.");
		}
	}

	// /lot claim [lot_code]
	public void claim(Player player, String lotCode) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		Lot lot;
		
		// Get the current lot, whether blank (current location) or typed
		if( lotCode.equals("") )
			lot = plugin.lots.findCurrentLot(player.getLocation());
		else
			lot = plugin.lots.get(lotCode);
		
		// lot will be null if either of the above methods failed
		if( lot == null )
		{
			if( lotCode.equals("") )
				Util.message(player, "You are not standing on a valid lot.  (consult the online map)");
			else
				Util.message(player, lotCode + " is not a valid lot code.  (consult the online map)");
			return;
		}
		// See if the lot is already claimed
		if( !lot.owner.equals("") )
		{
			if( lot.owner.equals(playerData.name) )
				Util.message(player, "You already own this lot!");
			else
				Util.message(player, "Lot " + lot.getLotCode() + " is already owned by " + plugin.players.get(lot.owner).getNameColor() + ".");
			return;
		}
		// Make sure the player does not already own a lot of the current lot's category
		for( Lot ownedLot : playerData.lots )
		{
			if( ownedLot.category == lot.category && (lot.category == LotCategory.WARRENS || lot.category == LotCategory.MARKET) )
			{
				Util.message(player, "You can only own one " + lot.getCategoryName() + " lot at a time.");
				return;
			}
			else if( (lot.category == LotCategory.COAL || lot.category == LotCategory.IRON || lot.category == LotCategory.GOLD || lot.category == LotCategory.DIAMOND) &&
					 (ownedLot.category == LotCategory.COAL || ownedLot.category == LotCategory.IRON || ownedLot.category == LotCategory.GOLD || ownedLot.category == LotCategory.DIAMOND) )
			{
				Util.message(player, "You can only own one member lot at a time.");
				return;
			}
		}
		// If the player is claiming a member lot, see if they have donated the appropriate amount
		if( lot.isMemberLot() )
		{
			int donation = plugin.database.playerQueries.getRecentDonations(playerData.id_Player);
			
			if( donation < lot.getPrice() )
			{
				Util.message(player, "To claim this lot you must be a " + lot.getCategoryName() + "-level " + plugin.config.ServerName + " member.");
				Util.message(player, "Visit http://www.rageland.net/donate for more details.");
				return;
			}
		}
		
		// All checks have succeeded - give the lot to the player
		plugin.database.lotQueries.lotClaim(playerData, lot);
		
		// Update the playerData
		playerData.lots.add(lot);
		plugin.players.update(playerData);
		
		// Update Lots to set the owner
		lot.owner = playerData.name;
		plugin.lots.put(lot);
		
		Util.message(player, "You now own lot " + lot.getLotCode() + ".");
	}
	
	// /lot disallow <player_name/all>
	public void disallow(Player player, String targetPlayerName) 
	{
		PlayerData playerData = plugin.players.get(player.getName());

		if( targetPlayerName.equalsIgnoreCase("all") )
		{
			if( playerData.lotPermissions.size() == 0 )
			{
				Util.message(player, "You have no permissions to disallow.");
				return;
			}
			
			// All checks have succeeded - update the database
			plugin.database.lotQueries.lotDisallow(playerData.id_Player, 0);
			
			// Update the playerData
			playerData.lotPermissions.clear();
			plugin.players.update(playerData);
			
			Util.message(player, "All of your lot permissions have been cleared.");
		}
		else
		{
			PlayerData targetPlayerData = plugin.players.get(targetPlayerName);
			
			// Check to see if target player exists
			if( targetPlayerData == null )
			{
				Util.message(player, "Player " + targetPlayerName + " does not exist.");
				return;
			}
			// Check to see if the target player already has permission
			if( !playerData.lotPermissions.contains(targetPlayerData.name) )
			{
				Util.message(player, targetPlayerData.getNameColor() + " does not have permission to build in your lots.");
				return;
			}
			
			// All checks have succeeded - update the database
			plugin.database.lotQueries.lotDisallow(playerData.id_Player, targetPlayerData.id_Player);
			
			// Update the playerData
			playerData.lotPermissions.remove(targetPlayerData.name);
			plugin.players.update(playerData);
			
			Util.message(player, targetPlayerData.getNameColor() + " is no longer allowed to build in your lots.");
		}
		
	}
	
	// /lot assign <lot_code> <player_name>
	public void evict(Player player, String lotCode) 
	{
		Lot lot = plugin.lots.get(lotCode);
		
		// Make sure the player has permission to perform this command
		if( !RageMod.permissionHandler.has(player, "ragemod.lot.evict") )
		{
			Util.message(player, "You do not have permission to perform that command.");
			return;
		}		
		// lot will be null if invalid
		if( lot == null )
		{	
			Util.message(player, lotCode + " is not a valid lot code.  (consult the online map)");
			return;
		}
		// Make sure the lot is already claimed
		if( lot.owner.equals("") )
		{
			Util.message(player, "Lot " + lot.getLotCode() + " is already unclaimed.");
			return;
		}
		
		// All checks have succeeded - remove the lot owner
		plugin.database.lotQueries.lotUnclaim(lot);
		
		// Update the playerData
		PlayerData targetPlayerData = plugin.players.get(lot.owner);
		targetPlayerData.lots.remove(lot);
		plugin.players.update(targetPlayerData);
		
		// Update Lots to set the owner
		lot.owner = "";
		plugin.lots.put(lot);
		
		Util.message(player, targetPlayerData.getNameColor() + " has been evicted from lot " + lot.getLotCode() + ".");
	}
	
	// /lot list
	public void list(Player player) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		
		// Make sure the player actually owns lots
		if( playerData.lots.size() == 0 )
		{
			Util.message(player, "You do not own any lots.");
			return;
		}
		
		Util.message(player, "You currently own the following lots:");
		
		for( Lot lot : playerData.lots )
		{
			Util.message(player, "   " + lot.getLotCode() + " (" + lot.getCategoryName() + ")  " + 
							   "x: " + (int)lot.region.nwCorner.getX() + "  z: " + (int)lot.region.nwCorner.getZ());
		}
		
		// List all players allowed to build in their lots, if any
		if( playerData.lotPermissions.size() > 0 )
		{
			Util.message(player, "The following players are allowed to build in your lots:");
			String nameList = "";
			for( String name : playerData.lotPermissions )
			{
				if( nameList.equals("") )
					nameList += plugin.players.get(name).getNameColor();
				else
					nameList += ", " + plugin.players.get(name).getNameColor();
			}
			Util.message(player, "   " + nameList);
		}		
	}

	// /lot unclaim [lot_code]
	public void unclaim(Player player, String lotCode) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		Lot lot;
		boolean isLotOwned = false;
		
		lotCode = lotCode.toUpperCase();
		
		// Get the current lot, whether blank (current location) or typed
		if( lotCode.equals("") )
			lot = plugin.lots.findCurrentLot(player.getLocation());
		else
			lot = plugin.lots.get(lotCode);
		
		// lot will be null if either of the above methods failed
		if( lot == null )
		{
			if( lotCode.equals("") )
				Util.message(player, "You are not standing on a valid lot.  (use /lot check and consult the online map)");
			else
				Util.message(player, lotCode + " is not a valid lot code.  (consult the online map)");
			return;
		}
		// Make sure the player owns the specified lot
		for( Lot ownedLot : playerData.lots )
		{
			if( ownedLot.id_Lot == lot.id_Lot )
				isLotOwned = true;
		}
		if( !isLotOwned )
		{
			Util.message(player, "You do not own lot " + lot.getLotCode() + ".");
			return;
		}
		
		// All checks have succeeded - reset the lot owner
		plugin.database.lotQueries.lotUnclaim(lot);
		
		// Update the playerData
		playerData.lots.remove(lot);
		plugin.players.update(playerData);
		
		// Update Lots to remove the owner
		lot.owner = "";
		plugin.lots.put(lot);
		
		Util.message(player, "You are no longer the owner of lot " + lot.getLotCode() + ".");
	}



	
	
}
