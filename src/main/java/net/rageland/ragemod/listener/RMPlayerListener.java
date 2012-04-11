package net.rageland.ragemod.listener;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.commands.Commands;
import net.rageland.ragemod.commands.CompassCommands;
import net.rageland.ragemod.commands.DebugCommands;
import net.rageland.ragemod.commands.FactionCommands;
import net.rageland.ragemod.commands.LanguageCommands;
import net.rageland.ragemod.commands.LotCommands;
import net.rageland.ragemod.commands.NPCCommands;
import net.rageland.ragemod.commands.NPCTownCommands;
import net.rageland.ragemod.commands.PermitCommands;
import net.rageland.ragemod.commands.QuestCommands;
import net.rageland.ragemod.commands.RageCommands;
import net.rageland.ragemod.commands.TownCommands;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.utilities.Util;
import net.rageland.ragemod.world.PlayerTown;
import net.rageland.ragemod.world.Town;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Handle events for all Player related events
 * @author TheIcarusKid
 */

@SuppressWarnings("unused")
public class RMPlayerListener implements Listener 
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
    private NPCTownCommands npcTownCommands;
    private PermitCommands permitCommands;
    private RageCommands rageCommands;
    private LanguageCommands langcommands;

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
        npcTownCommands = new NPCTownCommands(plugin);
        permitCommands = new PermitCommands(plugin);
        rageCommands = new RageCommands(plugin);
    }

    // Pull the player data from the DB and register in memory
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	Player player = event.getPlayer();    	
    	PlayerData playerData = plugin.players.playerLogin(player.getName());    
    	playerData.attachPlayer(player);
    	
		// Set the state info
    	playerData.currentZone = plugin.zones.isInside(player.getLocation());
    	playerData.currentTown = plugin.towns.getCurrentTown(player.getLocation());
    	playerData.isInCapitol = plugin.zones.isInside(player.getLocation()).isInsideCapitol(player.getLocation()); 
    	
    	// Display any messages they have from actions that happened while they were offline
    	if( !playerData.logonMessageQueue.equals("") )
    	{
    		String[] split = playerData.logonMessageQueue.split("<br>");
    		
    		for( String message : split )
    		{
    			if( !message.equals("") )
    			{
    				plugin.message.parse(player, message, ChatColor.DARK_GREEN);
    			}
    		}
    		
    		// Clear the message queue
    		playerData.logonMessageQueue = "";
    		plugin.database.playerQueries.updatePlayer(playerData);
    	}
    	
    	// Check for expired or new memberships
    	if( RageMod.perms.playerInGroup("world", playerData.name, "Member") && !playerData.isMember && !RageMod.perms.has(player, "ragemod.ismoderator") ) 
    	{
    		// TODO: Find out a way to do this programatically
    		// Message all mods/admins to demote the member
    		for( Player onlinePlayer : plugin.getServer().getOnlinePlayers() )
    		{
    			if( RageMod.perms.has(onlinePlayer, "ragemod.ismoderator") )
    			{
    				plugin.message.parse(onlinePlayer, playerData.getCodedName() + "'s membership has expired - please /demote him/her.");
    			}
    		}
    	}
    	else if( !RageMod.perms.playerInGroup("world", playerData.name, "Member") && playerData.isMember && !RageMod.perms.has(player, "ragemod.ismoderator") ) 
    	{
    		// Message all mods/admins to promote the member
    		for( Player onlinePlayer : plugin.getServer().getOnlinePlayers() )
    		{
    			if( RageMod.perms.has(onlinePlayer, "ragemod.ismoderator") )
    			{
    				plugin.message.parse(onlinePlayer, playerData.getCodedName() + " has donated $" + plugin.database.playerQueries.getRecentDonations(playerData.id_Player) + " to the server!  Please /promote him/her.");
    			}
    		}
    	}
    }
    
    // Register the player as logged off
    public void onPlayerQuit(PlayerQuitEvent event)
    {
    	Player player = event.getPlayer();    	
    	PlayerData playerData = plugin.players.get(player.getName());    	  
    	
    	// Make one final update to get values not important enough for instant update
    	playerData.update();
    	
    	plugin.database.playerQueries.playerLogoff(playerData.id_Player);
    	plugin.database.playerQueries.recordInstances(playerData);
    	plugin.database.playerQueries.recordAffinity(playerData);
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
    	else if( split[0].equalsIgnoreCase("/language") || split[0].equalsIgnoreCase("/lang") )
    	{

    		event.setCancelled(true);
    	}
    	else if( split[0].equalsIgnoreCase("/speak") || split[0].equalsIgnoreCase("/sp") )
    	{
    		if (split.length == 2){
    		langcommands.speak(player,split[1]);
    		}else{
    			langcommands.speak(player,split);
    		}
    		event.setCancelled(true);
    	}
    	else if( split[0].equalsIgnoreCase("/affinity") || split[0].equalsIgnoreCase("/aff") )
    	{
    		commands.affinity(player);
    		event.setCancelled(true);
    	}
    	else if( split[0].equalsIgnoreCase("/rage") )
    	{
    		rageCommands.onCommand(player, playerData, split);
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
    		if( RageMod.perms.has(player, "ragemod.debug") )
    			debugCommands.onRmdebugCommand(player, playerData, split);  
    		else
    			plugin.message.sendNo(player, "You do not have permission to perform that command.");
    		event.setCancelled(true);
    	}
    	// ********* PERMIT COMMANDS *********
    	else if( split[0].equalsIgnoreCase("/permit") )
    	{
    		permitCommands.onCommand(player, playerData, split);
    		event.setCancelled(true);
    	}
    	// ********* NPC COMMANDS ************
    	else if(split[0].equalsIgnoreCase("/npc"))
    	{
    		if(RageMod.perms.has(player, "ragemod.npc"))
			{
    			npcCommands.onNPCCommand(player, playerData, split);
			}
    		event.setCancelled(true);
    	}
    	// ********* NPCTOWN COMMANDS *********
    	else if( split[0].equalsIgnoreCase("/npctown") )
    	{
    		npcTownCommands.onNPCTownCommand(player, playerData, split);
    		event.setCancelled(true);
    	}
    	
    	
    	if( !plugin.config.PRE_RELEASE_MODE )
    	{
        	// ********* BASIC COMMANDS *********
        	if( split[0].equalsIgnoreCase("/spawn") )
        	{
        		if( split.length == 1 )
        			commands.spawn(player, playerData.name);
        		else if( split.length == 2 )
        			commands.spawn(player, split[1]);
        		else
        			plugin.message.parse(player, "Usage: /spawn [player_name]");
        		event.setCancelled(true);
        	}
        	else if( split[0].equalsIgnoreCase("/home") )
        	{
        		if( split.length == 1 )
        			commands.home(player, playerData.name);
        		else if( split.length == 2 )
        			commands.home(player, split[1]);
        		else
        			plugin.message.parse(player, "Usage: /home [player_name]");
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
        	if( playerData.currentZone != plugin.zones.isInside(player.getLocation()))
        	{
        		playerData.currentZone = plugin.zones.isInside(player.getLocation());
        		plugin.message.parse(player, "Your current zone is now " + playerData.currentZone.getConfig().getName());        		
        	}
        		
        	if( playerData.isInCapitol )
        		{
        			if( !playerData.currentZone.isInsideCapitol(player.getLocation()) )
        			{
        				playerData.isInCapitol = false;
        				
        				
        				// TODO: Is this necessary?  It might end up doing more harm than good...
        				
        				if( playerData.enterLeaveMessageTime == null || Util.secondsSince(playerData.enterLeaveMessageTime) > 10 )
        				{
        					plugin.message.parse(player, "Now leaving the capitol of " + plugin.config.Capitol_CodedName);
        					playerData.enterLeaveMessageTime = Util.now();
        				}
        			}
        		}
        		else
        		{
        			if( playerData.currentZone.isInsideCapitol(player.getLocation()) )
        			{
        				playerData.isInCapitol = true;
        				
        				if( playerData.enterLeaveMessageTime == null || Util.secondsSince(playerData.enterLeaveMessageTime) > 10 )
        				{
        					plugin.message.parse(player, "Now entering the capitol of " + plugin.config.Capitol_CodedName);
        					playerData.enterLeaveMessageTime = Util.now();
        				}
        			}
        			else
        			{
        				// See if the player has entered or left a Town
        	        	if( playerData.currentTown == null )
        	        	{
        	        		Town currentTown = plugin.towns.getCurrentTown(player.getLocation());
        	        		if( currentTown != null )
        	        		{
        	        			plugin.message.parse(player, "Now entering the " + currentTown.townLevel.name.toLowerCase() + " of " + currentTown.getCodedName());
        	        			playerData.currentTown = currentTown;
        	        			
        	        		}
        	        	}
        	        	else
        	        	{
        	        		Town currentTown = plugin.towns.getCurrentTown(player.getLocation());
        	        		if( currentTown == null )
        	        		{
        	        			plugin.message.parse(player, "Now leaving the " + playerData.currentTown.townLevel.name.toLowerCase() + " of " + playerData.currentTown.getCodedName());
        	        			playerData.currentTown = null;
        	        			
        	        		}
        	        	}
        			}
        		}
        	}
        		        	// See if the player has entered or left a Town
	        	if( playerData.currentTown == null )
	        	{
	        		Town currentTown = plugin.towns.getCurrentTown(player.getLocation());
	        		if( currentTown != null )
	        		{
	        			plugin.message.parse(player, "Now entering the " + currentTown.townLevel.name.toLowerCase() + " of " + currentTown.getCodedName());
	        			playerData.currentTown = currentTown;
	        			
	        		}
	        	}
	        	else
	        	{
	        		Town currentTown = plugin.towns.getCurrentTown(player.getLocation());
	        		if( currentTown == null )
	        		{
	        			plugin.message.parse(player, "Now leaving the " + playerData.currentTown.townLevel.name.toLowerCase() + " of " + playerData.currentTown.getCodedName());
	        			playerData.currentTown = null;
	        			
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
    	
    	if( playerData.getSpawn() != null && !plugin.config.PRE_RELEASE_MODE )
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
	    
	    if( plugin.config.PRE_RELEASE_MODE )
	    	return;
	    
	    // Portals in normal world
	    if( world.getName().equalsIgnoreCase("world") )
	    {
	    		// All portals from the capitol will go to the center of the Travel Zone
	    		if( plugin.zones.isInside(player.getLocation()).isInsideCapitol(player.getLocation())){
	    			event.setTo(plugin.zones.TZ_Center);
	    		}else{
	    			if( playerData.currentTown != null && playerData.currentTown instanceof PlayerTown )
	    		{
	    			PlayerTown currentTown = (PlayerTown)playerData.currentTown;
	    			event.setTo(currentTown.travelNode);
	    		}
	    		else
	    			event.setTo(plugin.zones.TZ_Center);
	    	}
	    }
	    else{ if( world.getName().equalsIgnoreCase("world_nether") )
	    {
	    	// Temp: Make all portals go back to spawn
	    	event.setTo(plugin.zones.world.getSpawnLocation());
	    }
	    
    }
    
    
    
    }
    }

