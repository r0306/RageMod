package net.rageland.ragemod.listener;

import net.milkbowl.vault.economy.EconomyResponse;
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
import net.rageland.ragemod.commands.LanguageCommands;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.utilities.Util;
import net.rageland.ragemod.world.PlayerTown;
import net.rageland.ragemod.world.Town;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Handle events for all Player related events
 * 
 * @author Perdemot
 * @author TheIcarusKid
 */

@SuppressWarnings("unused")
public class RMPlayerListener implements Listener 
{
	private RageMod plugin;
	
	public RMPlayerListener(RageMod plugin){
		this.plugin=plugin;
	}
    
    // Player movement
    @EventHandler(priority = EventPriority.HIGHEST)
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
        		plugin.chat.removePlayer(playerData);
        		playerData.currentZone = plugin.zones.isInside(player.getLocation());
        		plugin.chat.addPlayer(playerData);
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
    @EventHandler(priority = EventPriority.HIGHEST)
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
    @EventHandler(priority = EventPriority.HIGHEST)
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
    @EventHandler(priority = EventPriority.HIGHEST)
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
	    		}else if( playerData.currentTown != null && playerData.currentTown instanceof PlayerTown ) 
	    		{
	    			PlayerTown currentTown = (PlayerTown)playerData.currentTown;
	    			event.setTo(currentTown.travelNode);
	    		}
	    		else
	    			event.setTo(plugin.zones.TZ_Center);
	    	} else if( world.getName().equalsIgnoreCase("world_nether") )
	    {
	    	// Temp: Make all portals go back to spawn
	    	event.setTo(plugin.zones.world.getSpawnLocation());
	    } 
    }
    
	public void onPlayerDeath(PlayerDeathEvent event){
	    if (!(event.getEntity() instanceof Player)) {
	        return;
	      }

	      Player victim = (Player)event.getEntity();

	      EntityDamageEvent e = event.getEntity().getLastDamageCause();
	      if (!(e instanceof EntityDamageByEntityEvent)) {
	        return;
	      }
	      EntityDamageByEntityEvent nEvent = (EntityDamageByEntityEvent)e;

	      if (!(nEvent.getDamager() instanceof Player)) {
	        return;
	      }
	      Player killer = (Player)nEvent.getDamager();
	      	double temp= plugin.Bounties.removeallBountys(victim.getName());
			EconomyResponse work = plugin.economy.bankDeposit(killer.getName(), temp);
			if (work.type == EconomyResponse.ResponseType.SUCCESS){	
				killer.sendMessage(ChatColor.GREEN + "You have been awarded " + temp + " in bounty for your kill!");
				victim.sendMessage(ChatColor.RED + killer.getDisplayName()+" has taken "+ temp+" for your head.");
			}

	}
}

