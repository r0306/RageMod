package net.rageland.ragemod;

// TODO: Make a Util class that parses player.sendMessage and highlights /commands, <required>, [optional], and (info)
// Also create a default color

// TODO: Check whether we need to clear any homes or spawns when a bed is destroyed

// TODO: Make sure location checks check the world that the player is in

// TODO: Add a /lot invite command to let players work together on buildings (also uninvite)

import java.util.HashMap;

import net.rageland.ragemod.commands.CompassCommands;
import net.rageland.ragemod.commands.DebugCommands;
import net.rageland.ragemod.commands.FactionCommands;
import net.rageland.ragemod.commands.LotCommands;
import net.rageland.ragemod.commands.Commands;
import net.rageland.ragemod.commands.NPCCommands;
import net.rageland.ragemod.commands.PermitCommands;
import net.rageland.ragemod.commands.QuestCommands;
import net.rageland.ragemod.commands.TownCommands;
import net.rageland.ragemod.data.Factions;
import net.rageland.ragemod.data.Lot;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.data.PlayerTowns;
import net.rageland.ragemod.data.Players;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.World;

/**
 * Handle events for all Player related events
 * @author TheIcarusKid
 */
public class RMPlayerListener extends PlayerListener 
{
    private final RageMod plugin;
    private QuestCommands questCommands;
    private CompassCommands compassCommands;
    private LotCommands lotCommands;
    private TownCommands townCommands;
    private FactionCommands factionCommands;
    private DebugCommands debugCommands;
    private Commands commands;
    private NPCCommands npcCommands;
    private PermitCommands permitCommands;

    public RMPlayerListener(RageMod instance) 
    {
        plugin = instance;
        questCommands = new QuestCommands(plugin);
        compassCommands = new CompassCommands(plugin);
        lotCommands = new LotCommands(plugin);
        townCommands = new TownCommands(plugin);
        factionCommands = new FactionCommands(plugin);
        debugCommands = new DebugCommands(plugin);
        commands = new Commands(plugin);
        npcCommands = new NPCCommands(plugin);
        permitCommands = new PermitCommands(plugin);
    }

    // Pull the player data from the DB and register in memory
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	Player player = event.getPlayer();    	
    	PlayerData playerData = plugin.players.playerLogin(player.getName());    	  
    	
		// Set the state info
    	playerData.currentZone = plugin.zones.getCurrentZone(player.getLocation());
    	playerData.currentTown = plugin.playerTowns.getCurrentTown(player.getLocation());
    	playerData.isInCapitol = plugin.zones.isInCapitol(player.getLocation()); 
    	
    	// Display any messages they have from actions that happened while they were offline
    	if( !playerData.logonMessageQueue.equals("") )
    	{
    		String[] split = playerData.logonMessageQueue.split("<br>");
    		
    		for( String message : split )
    		{
    			if( !message.equals("") )
    			{
    				plugin.text.parse(player, message, ChatColor.DARK_GREEN);
    			}
    		}
    		
    		// Clear the message queue
    		playerData.logonMessageQueue = "";
    		plugin.database.playerQueries.updatePlayer(playerData);
    	}
    	
    	// Check for expired or new memberships
    	if( RageMod.permissionHandler.inGroup("world", playerData.name, "Member") && !playerData.isMember && !RageMod.permissionHandler.has(player, "ragemod.ismoderator") ) 
    	{
    		// TODO: Find out a way to do this programatically
    		// Message all mods/admins to demote the member
    		for( Player onlinePlayer : plugin.getServer().getOnlinePlayers() )
    		{
    			if( RageMod.permissionHandler.has(onlinePlayer, "ragemod.ismoderator") )
    			{
    				plugin.text.parse(onlinePlayer, playerData.getCodedName() + "'s membership has expired - please /demote him/her.");
    			}
    		}
    	}
    	else if( !RageMod.permissionHandler.inGroup("world", playerData.name, "Member") && playerData.isMember && !RageMod.permissionHandler.has(player, "ragemod.ismoderator") ) 
    	{
    		// Message all mods/admins to promote the member
    		for( Player onlinePlayer : plugin.getServer().getOnlinePlayers() )
    		{
    			if( RageMod.permissionHandler.has(onlinePlayer, "ragemod.ismoderator") )
    			{
    				plugin.text.parse(onlinePlayer, playerData.getCodedName() + " has donated $" + plugin.database.playerQueries.getRecentDonations(playerData.id_Player) + " to the server!  Please /promote him/her.");
    			}
    		}
    	}
    	
    	// Update playerD

    	
    }
    
    // Register the player as logged off
    public void onPlayerQuit(PlayerQuitEvent event)
    {
    	Player player = event.getPlayer();    	
    	PlayerData playerData = plugin.players.get(player.getName());    	  
    	
    	plugin.database.playerQueries.playerLogoff(playerData.id_Player);
		
    }
    
    
    
    
    // Process commands
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) 
    {
    	Player player = event.getPlayer();
    	PlayerData playerData = plugin.players.get(player.getName());
    	
    	String[] split = event.getMessage().split(" ");
    	
    	// *** Reduce the command tree for the lot-only release ***
    	
    	// ********* BASIC COMMANDS *********
    	if(split[0].equalsIgnoreCase("/zone"))
    	{
    		commands.zone(player);
    		event.setCancelled(true);
    	}
    	// ********* COMPASS COMMANDS *********
    	else if( split[0].equalsIgnoreCase("/compass") )
    	{
    		compassCommands.onCompassCommand(player, playerData, split);
    		event.setCancelled(true);
    	}
    	// ********* LOT COMMANDS *********
    	else if( split[0].equalsIgnoreCase("/lot") )
    	{
    		lotCommands.onLotCommand(player, playerData, split);
    		event.setCancelled(true);
    	}
    	// ********* DEBUG COMMANDS **********
    	else if( split[0].equalsIgnoreCase("/debug") ) 
    	{
    		if( RageMod.permissionHandler.has(player, "ragemod.debug") )
    			debugCommands.onDebugCommand(player, playerData, split);  
    		else
    			plugin.text.sendNo(player, "You do not have permission to perform that command.");
    		event.setCancelled(true);
    	}
    	// ********* PERMIT COMMANDS *********
    	if( split[0].equalsIgnoreCase("/permit") )
    	{
    		permitCommands.onCommand(player, playerData, split);
    		event.setCancelled(true);
    	}
    	
    	if( !plugin.config.DISABLE_NON_LOT_CODE )
    	{
        	// ********* BASIC COMMANDS *********
        	if( split[0].equalsIgnoreCase("/spawn") )
        	{
        		if( split.length == 1 )
        			commands.spawn(player, playerData.name);
        		else if( split.length == 2 )
        			commands.spawn(player, split[1]);
        		else
        			plugin.text.parse(player, "Usage: /spawn [player_name]");
        		event.setCancelled(true);
        	}
        	else if( split[0].equalsIgnoreCase("/home") )
        	{
        		if( split.length == 1 )
        			commands.home(player, playerData.name);
        		else if( split.length == 2 )
        			commands.home(player, split[1]);
        		else
        			plugin.text.parse(player, "Usage: /home [player_name]");
        		event.setCancelled(true);
        	}
        	// ********* TOWN COMMANDS *********
        	else if( split[0].equalsIgnoreCase("/town") )
        	{
        		townCommands.onTownCommand(player, playerData, split);
        		event.setCancelled(true);
        	}
        	// ********* FACTION COMMANDS **********
        	else if(split[0].equalsIgnoreCase("/faction") )
        	{
        		factionCommands.onFactionCommand(player, playerData, split);
        		event.setCancelled(true);
        	}
        	// ********* QUEST COMMANDS **********
        	else if(split[0].equalsIgnoreCase("/quest")) 
        	{
        		questCommands.onQuestCommand(player, playerData, split);
        		event.setCancelled(true);
        	}
        	// ********* NPC COMMANDS ************
        	else if(split[0].equalsIgnoreCase("/npc"))
        	{
        		if(RageMod.permissionHandler.has(player, "ragemod.npc"))
    			{
        			npcCommands.onNPCCommand(player, playerData, split);
    			}
        		event.setCancelled(true);
        	}
    	}
    		
    }
    
    // Player movement
    public void onPlayerMove(PlayerMoveEvent event) 
    {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.players.get(player.getName());
        World world = player.getWorld();

        if( event.getFrom().getBlockX() != event.getTo().getBlockX() ||
        	event.getFrom().getBlockZ() != event.getTo().getBlockZ() )
        {
        	// Check to see if the player has changed zones
        	if( playerData.currentZone != plugin.zones.getCurrentZone(player.getLocation()))
        	{
        		playerData.currentZone = plugin.zones.getCurrentZone(player.getLocation());
        		plugin.text.parse(player, "Your current zone is now " + plugin.zones.getName(playerData.currentZone));        		
        	}
        	
        	// *** ZONE A (Neutral Zone) ***
        	if( playerData.currentZone == RageZones.Zone.A )
        	{
        		// Check to see if the player has entered or left the capitol
        		if( playerData.isInCapitol )
        		{
        			if( !plugin.zones.isInCapitol(player.getLocation()) )
        			{
        				playerData.isInCapitol = false;
        				
        				
        				// TODO: Is this necessary?  It might end up doing more harm than good...
        				
        				if( playerData.enterLeaveMessageTime == null || Util.secondsSince(playerData.enterLeaveMessageTime) > 10 )
        				{
        					plugin.text.parse(player, "Now leaving the capitol of " + plugin.config.Capitol_CodedName);
        					playerData.enterLeaveMessageTime = Util.now();
        				}
        			}
        		}
        		else
        		{
        			if( plugin.zones.isInCapitol(player.getLocation()) )
        			{
        				playerData.isInCapitol = true;
        				
        				if( playerData.enterLeaveMessageTime == null || Util.secondsSince(playerData.enterLeaveMessageTime) > 10 )
        				{
        					plugin.text.parse(player, "Now entering the capitol of " + plugin.config.Capitol_CodedName);
        					playerData.enterLeaveMessageTime = Util.now();
        				}
        			}
        		}
        	}
        	// *** ZONE B (War Zone) ***
        	else if( playerData.currentZone == RageZones.Zone.B )
        	{
	        	// See if the player has entered or left a PlayerTown
	        	if( playerData.currentTown == null )
	        	{
	        		PlayerTown currentTown = plugin.playerTowns.getCurrentTown(player.getLocation());
	        		if( currentTown != null )
	        		{
	        			plugin.text.parse(player, "Now entering the " + currentTown.townLevel.name.toLowerCase() + " of " + currentTown.getCodedName());
	        			playerData.currentTown = currentTown;
	        			
	        		}
	        	}
	        	else
	        	{
	        		PlayerTown currentTown = plugin.playerTowns.getCurrentTown(player.getLocation());
	        		if( currentTown == null )
	        		{
	        			plugin.text.parse(player, "Now leaving the " + playerData.currentTown.townLevel.name.toLowerCase() + " of " + playerData.currentTown.getCodedName());
	        			playerData.currentTown = null;
	        			
	        		}
	        	}
        	}
        }
    }
    
    // Player interacts with objects (right-clicking, etc.)
    public void onPlayerInteract(PlayerInteractEvent event) 
    {
    	Player player = event.getPlayer();
    	PlayerData playerData = plugin.players.get(player.getName());
    	Action action = event.getAction();
    	Block block = event.getClickedBlock();

    	if(action == Action.RIGHT_CLICK_BLOCK)
    	{
    		
    	}	
    }
    
    // Player respawn
    public void onPlayerRespawn(PlayerRespawnEvent event) 
    {
    	Player player = event.getPlayer();
    	PlayerData playerData = plugin.players.get(player.getName());
    	
    	if( playerData.getSpawn() != null && !plugin.config.DISABLE_NON_LOT_CODE )
    	{
    		event.setRespawnLocation(playerData.getSpawn());
    	}
    }
    
    // Player portal usage
    public void onPlayerPortal(PlayerPortalEvent event)
    {
	    Player player = event.getPlayer();
	    PlayerData playerData = plugin.players.get(player.getName());
	    World world = player.getWorld();
	    
	    if( plugin.config.DISABLE_NON_LOT_CODE )
	    	return;
	    
	    // Portals in normal world
	    if( world.getName().equalsIgnoreCase("world") )
	    {
	    	// *** ZONE A (Neutral Zone) ***
	    	if( playerData.currentZone == RageZones.Zone.A )
	    	{
	    		// All portals from the capitol will go to the center of the Travel Zone
	    		if( plugin.zones.isInCapitol(player.getLocation()) )
	    			event.setTo(plugin.zones.TZ_Center);
	    	}
	    	else if( playerData.currentZone == RageZones.Zone.B )
	    	{
	    		if( playerData.currentTown != null )
	    			event.setTo(playerData.currentTown.travelNode);
	    		else
	    			event.setTo(plugin.zones.TZ_Center);
	    	}
	    }
	    else if( world.getName().equalsIgnoreCase("world_nether") )
	    {
	    	// Temp: Make all portals go back to capitol
	    	event.setTo(plugin.zones.Capitol_Portal);
	    }
	    
    }
    
    
    
}

