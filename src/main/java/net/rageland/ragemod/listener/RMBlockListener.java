package net.rageland.ragemod.listener;

import java.util.HashMap;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.npc.NPCTown;
import net.rageland.ragemod.world.Location2D;
import net.rageland.ragemod.world.Lot;
import net.rageland.ragemod.world.PlayerTown;
import net.rageland.ragemod.world.Region2D;
import net.rageland.ragemod.world.Town;
import net.rageland.ragemod.world.WarZone;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * RageMod block listener
 * @author TheIcarusKid
 */
public class RMBlockListener implements Listener 
{
    private final RageMod plugin;
    private WarZone wz;
    private Location2D loc;
	private Region2D r2d;
    
    public RMBlockListener(final RageMod plugin) 
    {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDropItem(BlockDispenseEvent e) {
    	if (wz.isInside(loc, r2d)) {
    		if (e.getBlock().equals("Dispenser")) {
    			if (e.isCancelled()) {
    				e.setCancelled(false);
    				return;
    			} else {
    				return;
    			}
    		} else {
    			e.setCancelled(true);
    		}
    	}
    }

    // Prevent block breaking without permission
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) 
    {
    	Player player = event.getPlayer();
    	PlayerData playerData = plugin.players.get(player.getName());
    	Block block = event.getBlock();
    	
    	if (event.isCancelled()) 
        {
            return;
        }

    	// Perform generic edit permission handling
    	if( !canEditBlock(event, player) )
    	{
    		event.setCancelled(true);
    		return;
    	}
    	
    	// *** DISABLE ALL FURTHER CODE FOR LOT RELEASE ***
    	if( plugin.config.PRE_RELEASE_MODE )
    		return;
    	
    	if (wz.isInside(loc, r2d)) {
    		event.setCancelled(true);
    		event.getBlock().breakNaturally();
    		event.getBlock().getDrops().clear();
    	}
    	
    	
    	// Bed breaking - clear spawn and home
    	if( block.getType() == Material.BED_BLOCK )
    	{
				for( Lot lot : playerData.lots )
				{
					if( lot.canSetHome() && lot.isInside(block.getLocation()) )
					{
						playerData.clearHome();
		    			plugin.message.send(player, "You no longer have a home point.");
		    			// Update both memory and database
		    			plugin.database.playerQueries.updatePlayer(playerData);
					}
				}
			}
			// /spawn: for beds in player towns
			else {
				PlayerTown playerTown = (PlayerTown)plugin.towns.getCurrentTown(block.getLocation());

	    		if( playerTown != null )
	    		{
	    			if( playerData.getSpawn() != null && playerData.getSpawn().distance(block.getLocation()) < plugin.config.Town_DISTANCE_BETWEEN_BEDS )
	    			{
		    			plugin.message.send(player, "You no longer have a spawn point.");
	    				playerData.clearSpawn();
		    			playerData.update();
	    			}
	    			else
	    			{
	    				plugin.message.sendNo(player, "You can only move your own bed.");
	    				event.setCancelled(true);
	    			}
	    		}
			}
    	}
    
    
    // Prevent block placing without permission
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) 
    {
    	Player player = event.getPlayer();
    	PlayerData playerData = plugin.players.get(player.getName());
    	Block block = event.getBlock();
    	
    	if (event.isCancelled()) 
        {
            return;
        }
    	
    	// Perform generic edit permission handling
    	if( !canEditBlock(event, player) )
    	{
    		event.setCancelled(true);
    		return;
    	}
    	
    	// *** DISABLE ALL FURTHER CODE FOR LOT RELEASE ***
    	if( plugin.config.PRE_RELEASE_MODE )
    		return;
    	
    	if (wz.isInside(loc, r2d)) {
    		
    	}
    	
    	// *** ZONE A (Neutral Zone) ***
    		// Bed placement - set spawn and home
        	if( block.getType() == Material.BED_BLOCK )
        	{
        		// /home: bed inside capitol lot
    			if( playerData.isMember )
    			{
    				for( Lot lot : playerData.lots )
    				{
    					if( lot.canSetHome() && lot.isInside(block.getLocation()) )
    					{
    						if( playerData.getHome() != null )
    						{
    							plugin.message.sendNo(player, "You already have a bed inside your lot (use /home clear to fix errors).");
    							event.setCancelled(true);
    							return;
    						}
    						
    						Location loc = block.getLocation();
    						playerData.setHome(new Location(loc.getWorld(), loc.getX(), loc.getY() + 2, loc.getZ()));
    		    			plugin.message.send(player, "Your home location has now been set.");
    		    			// Update both memory and database
    		    			playerData.update();
    					}
    				}
    			}
        	}	
    	
    	// *** ZONE B (War Zone) ***
    	else 
    	{
    		// /spawn: for beds in player towns
			if( block.getType() == Material.BED_BLOCK )
			{
				PlayerTown playerTown = (PlayerTown)plugin.towns.getCurrentTown(block.getLocation());

	    		if( playerTown != null && playerTown.getName().equals(playerData.townName) )
	    		{
	    			if( playerData.getSpawn() != null )
					{
						plugin.message.sendNo(player, "You already have a bed inside your town (use /spawn clear to fix errors).");
						event.setCancelled(true);
						return;
					}
	    			
	    			// Make sure the location is not too close to another player's spawn
	    			HashMap<String, Location> spawns = plugin.database.playerQueries.getSpawnLocations(playerTown.getID());
	    			for( String resident : spawns.keySet() )
	    			{
	    				if( block.getLocation().distance(spawns.get(resident)) < plugin.config.Town_DISTANCE_BETWEEN_BEDS && !resident.equals(playerData.name) )
	    				{
	    					plugin.message.parseNo(player, "This bed is too close to " + plugin.players.get(resident).getCodedName() + "'s bed - spawn not set.");
	    					event.setCancelled(true);
	    					return;
	    				}
	    			}
	    			
	    			Location loc = block.getLocation();
	    			playerData.setSpawn(new Location(loc.getWorld(), loc.getX(), loc.getY() + 2, loc.getZ()));
	    			plugin.message.send(player, "Your spawn location has now been set.");
	    			// Update both memory and database
	    			plugin.database.playerQueries.updatePlayer(playerData);
	    		}
			}
    	}
        	
    	
    }
    
    // Generic permission edit handler that handles multiple types of block editing
    private boolean canEditBlock(BlockEvent event, Player player)
    {
    	PlayerData playerData = plugin.players.get(player.getName());
    	Block block = event.getBlock();
    	Location location = block.getLocation();
    	
    	// Check for NPCTowns (zone code handled inside)
    	Town town = plugin.towns.getCurrentTown(location);
    	if( town instanceof NPCTown )
    	{
    		NPCTown npcTown = (NPCTown)town;
    		if(!RageMod.perms.has(player, "ragemod.build.npctown") && !playerData.npcTownName.equalsIgnoreCase(town.getName()) 
    				&& !npcTown.hasPermission(playerData.name) )
    		{
    			plugin.message.sendNo(player, "You don't have permission to build here.");
    			return false;
    		}
    		
    	} // *** ZONE A (Neutral Zone) ***
    	else if( plugin.zones.isInside(location).getConfig().isPlayerBuild() )
    	{
    		if( !playerData.isInsideOwnLot(location) && !RageMod.perms.has(player, "ragemod.build.anylot") )
        		{
        			Lot lot = plugin.lots.findCurrentLot(location);
        			
        			if( lot == null )
        			{
        				if( RageMod.perms.has(player, "ragemod.build.capitol") || playerData.permits.capitol == true )
        					return true;
        				
        				plugin.message.sendNo(player, "You don't have permission to edit city infrastructure.");
        			}
        			else
        			{
        				if( lot.owner.equals("") )
        					plugin.message.sendNo(player, "You cannot edit unclaimed lots.");
        				else
        				{
        					// Check to see if the player has permission to build in this lot
        					PlayerData ownerData = plugin.players.get(lot.owner);
        					if( !ownerData.lotPermissions.contains(playerData.name) )
        						plugin.message.parseNo(player, "This lot is owned by " + plugin.players.get(lot.owner).getCodedName() + ".");
        					else
        						return true;
        				}
        			}
        			
        			return false;
        		}
    		if( town != null && town instanceof PlayerTown )
    		{
    			PlayerTown playerTown = (PlayerTown)town;
    					
    			// Players can only build inside their own towns
    			if( !playerTown.getName().equals(playerData.townName) && !RageMod.perms.has(player, "ragemod.build.anytown") ) 
    			{		
    				plugin.message.sendNo(player, "You can only build inside of your own town.");
    				return false;
    			}
    			else if( playerTown.isInsideSanctumFloor(location) )
    			{
    				plugin.message.sendNo(player, "You cannot modify the inner sanctum floor.");
    				return false;
    			}
    			else if( playerTown.isInsideSanctum(location) )
    			{
    				if( block.getType() != Material.TORCH && block.getType() != Material.GOLD_BLOCK ) 
    				{
    					plugin.message.sendNo(player, "Only torches and gold blocks can be placed inside the inner sanctum.");
        				return false;
    				}
    				else if( block.getType() == Material.GOLD_BLOCK )
    				{
    					return playerTown.processGoldBlock(event);
    				}	
    			}
    		}
    	}
    	// *** TRAVEL ZONE ***
    	else if( plugin.zones.isInTravelZone(location) && !RageMod.perms.has(player, "ragemod.build.travelzone") )
    	{
    		plugin.message.sendNo(player, "You cannot build inside the Travel Zone.");
    		return false;
    	} else if (wz.isInside(loc,  r2d))
    	{
    		return true;
    	}
    	
    	return true;
    }
    
    

}
