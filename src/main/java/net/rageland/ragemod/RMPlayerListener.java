package net.rageland.ragemod;

// TODO: Make a Util class that parses player.sendMessage and highlights /commands, <required>, [optional], and (info)
// Also create a default color

// TODO: Check whether we need to clear any homes or spawns when a bed is destroyed

// TODO: Make sure location checks check the world that the player is in

// TODO: Add a /lot invite command to let players work together on buildings (also uninvite)

import java.util.HashMap;

import net.rageland.ragemod.RageZones.Zone;
import net.rageland.ragemod.commands.CompassCommands;
import net.rageland.ragemod.commands.DebugCommands;
import net.rageland.ragemod.commands.FactionCommands;
import net.rageland.ragemod.commands.LotCommands;
import net.rageland.ragemod.commands.Commands;
import net.rageland.ragemod.commands.QuestCommands;
import net.rageland.ragemod.commands.TownCommands;
import net.rageland.ragemod.data.Factions;
import net.rageland.ragemod.data.Lot;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTown;
import net.rageland.ragemod.data.PlayerTowns;
import net.rageland.ragemod.data.Players;

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

    public RMPlayerListener(RageMod instance) 
    {
        plugin = instance;
        questCommands = new QuestCommands();
        compassCommands = new CompassCommands();
        lotCommands = new LotCommands();
        townCommands = new TownCommands();
        factionCommands = new FactionCommands();
        debugCommands = new DebugCommands();
    }

    // Pull the player data from the DB and register in memory
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	Player player = event.getPlayer();    	
    	PlayerData playerData = Players.playerLogin(player.getName());    	  
    	
    	// Set the state info
    	playerData.currentZone = RageZones.getCurrentZone(player.getLocation());
    	playerData.currentTown = PlayerTowns.getCurrentTown(player.getLocation());
    	playerData.isInCapitol = RageZones.isInCapitol(player.getLocation());
    	Players.update(playerData);
    }
    
    // Process commands
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) 
    {
    	Player player = event.getPlayer();
    	PlayerData playerData = Players.get(player.getName());
    	
    	String[] split = event.getMessage().split(" ");
    	
    	// ********* BASIC COMMANDS *********
    	if( split[0].equalsIgnoreCase("/spawn") )
    	{
    		if( split.length == 1 )
    			Commands.spawn(player, playerData.name);
    		else if( split.length == 2 )
    			Commands.spawn(player, split[1]);
    		else
    			Util.message(player, "Usage: /spawn [player_name]");
    	}
    	else if( split[0].equalsIgnoreCase("/home") )
    	{
    		if( split.length == 1 )
    			Commands.home(player, playerData.name);
    		else if( split.length == 2 )
    			Commands.home(player, split[1]);
    		else
    			Util.message(player, "Usage: /home [player_name]");
    	}
    	else if(split[0].equalsIgnoreCase("/zone"))
    	{
    		Commands.zone(player);
    	}
    	
    	// ********* COMPASS COMMANDS *********
    	else if( split[0].equalsIgnoreCase("/compass") )
    	{
    		compassCommands.onCompassCommand(player, playerData, split);
    	}
    	
    	// ********* LOT COMMANDS *********
    	else if( split[0].equalsIgnoreCase("/lot") )
    	{
    		lotCommands.onLotCommand(player, playerData, split);
    	}
    	
    	// ********* TOWN COMMANDS *********
    	else if( split[0].equalsIgnoreCase("/town") )
    	{
    		townCommands.onTownCommand(player, playerData, split);
    	}
    	
    	// ********* FACTION COMMANDS **********
    	else if(split[0].equalsIgnoreCase("/faction") )
    	{
    		factionCommands.onFactionCommand(player, playerData, split);
    	}
    	// ********* QUEST COMMANDS **********
    	else if(split[0].equalsIgnoreCase("/quest")) 
    	{
    		questCommands.onQuestCommand(player, playerData, split);
    	}
    	
    	// ********* DEBUG COMMANDS **********
    	else if(split[0].equalsIgnoreCase("/debug") && RageMod.permissionHandler.has(player, "ragemod.debug") )
    	{
    		debugCommands.onDebugCommand(player, playerData, split);    		
    	}
    	event.setCancelled(true);
    }
    
    // Player movement
    public void onPlayerMove(PlayerMoveEvent event) 
    {
        Player player = event.getPlayer();
        PlayerData playerData = Players.get(player.getName());
        World world = player.getWorld();

        if( event.getFrom().getBlockX() != event.getTo().getBlockX() ||
        	event.getFrom().getBlockZ() != event.getTo().getBlockZ() )
        {
        	// Check to see if the player has changed zones
        	if( playerData.currentZone != RageZones.getCurrentZone(player.getLocation()))
        	{
        		playerData.currentZone = RageZones.getCurrentZone(player.getLocation());
        		Util.message(player, "Your current zone is now " + RageZones.getName(playerData.currentZone));
        		Players.update(playerData);
        	}
        	
        	// *** ZONE A (Neutral Zone) ***
        	if( playerData.currentZone == Zone.A )
        	{
        		// Check to see if the player has entered or left the capitol
        		if( playerData.isInCapitol )
        		{
        			if( !RageZones.isInCapitol(player.getLocation()) )
        			{
        				playerData.isInCapitol = false;
        				Players.update(playerData);
        				
        				// TODO: Is this necessary?  It might end up doing more harm than good...
        				
        				if( playerData.enterLeaveMessageTime == null || Util.secondsSince(playerData.enterLeaveMessageTime) > 10 )
        				{
        					Util.message(player, "Now leaving the capitol of " + RageConfig.Capitol_Name);
        					playerData.enterLeaveMessageTime = Util.now();
        				}
        			}
        		}
        		else
        		{
        			if( RageZones.isInCapitol(player.getLocation()) )
        			{
        				playerData.isInCapitol = true;
        				Players.update(playerData);
        				if( playerData.enterLeaveMessageTime == null || Util.secondsSince(playerData.enterLeaveMessageTime) > 10 )
        				{
        					Util.message(player, "Now entering the capitol of " + RageConfig.Capitol_Name);
        					playerData.enterLeaveMessageTime = Util.now();
        				}
        			}
        		}
        	}
        	// *** ZONE B (War Zone) ***
        	else if( playerData.currentZone == Zone.B )
        	{
	        	// See if the player has entered or left a PlayerTown
	        	if( playerData.currentTown == null )
	        	{
	        		PlayerTown currentTown = PlayerTowns.getCurrentTown(player.getLocation());
	        		if( currentTown != null )
	        		{
	        			Util.message(player, "Now entering the " + currentTown.townLevel.name.toLowerCase() + " of " + currentTown.townName);
	        			playerData.currentTown = currentTown;
	        			Players.update(playerData);
	        		}
	        	}
	        	else
	        	{
	        		PlayerTown currentTown = PlayerTowns.getCurrentTown(player.getLocation());
	        		if( currentTown == null )
	        		{
	        			Util.message(player, "Now leaving the " + playerData.currentTown.townLevel.name.toLowerCase() + " of " + playerData.currentTown.townName);
	        			playerData.currentTown = null;
	        			Players.update(playerData);
	        		}
	        	}
        	}
        }
    }
    
    // Player interacts with objects (right-clicking, etc.)
    public void onPlayerInteract(PlayerInteractEvent event) 
    {
    	Player player = event.getPlayer();
    	PlayerData playerData = Players.get(player.getName());
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
    	PlayerData playerData = Players.get(player.getName());
    	
    	if( playerData.spawn_IsSet )
    	{
    		event.setRespawnLocation(playerData.getSpawnLocation());
    	}
    	
    	
    }
    
    
    
}

