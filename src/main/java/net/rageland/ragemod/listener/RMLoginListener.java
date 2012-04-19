package net.rageland.ragemod.listener;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RMLoginListener implements Listener {
	private RageMod plugin;
	
	public RMLoginListener(RageMod plugin){
		this.plugin=plugin;
	}
		
	
	 @EventHandler(priority = EventPriority.HIGHEST)
	    public void onPlayerJoin(PlayerJoinEvent event)
	    {
	    	Player player = event.getPlayer();    	
	    	PlayerData playerData = plugin.players.playerLogin(player.getName());    
	    	playerData.attachPlayer(player);
	    	plugin.chat.addPlayer(playerData);
	    	
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
	    		// Automagical demotion.
	    		for( Player onlinePlayer : plugin.getServer().getOnlinePlayers() )
	    		{
	    			RageMod.perms.playerAddGroup(onlinePlayer, "Citizen");
	    			RageMod.perms.playerRemoveGroup(onlinePlayer, "Member");
	    		}
	    	}
	    	else if( !RageMod.perms.playerInGroup("world", playerData.name, "Member") && playerData.isMember && !RageMod.perms.has(player, "ragemod.ismoderator") ) 
	    	{
	    		// Automagical promotion.
	    		for( Player onlinePlayer : plugin.getServer().getOnlinePlayers() )
	    		{
	    			RageMod.perms.playerAddGroup(onlinePlayer, "Member");
	    			RageMod.perms.playerRemoveGroup(onlinePlayer, "Citizen");
	    		}
	    	}
	    }
	    @EventHandler(priority = EventPriority.HIGHEST)
	    public void onPlayerQuit(PlayerQuitEvent event)
	    {
	    	Player player = event.getPlayer();    	
	    	PlayerData playerData = plugin.players.get(player.getName());    	  
	    	
	    	// Make one final update to get values not important enough for instant update
	    	playerData.update();
	    	
	    	plugin.database.playerQueries.playerLogoff(playerData.id_Player);
	    	plugin.database.playerQueries.recordInstances(playerData);
	    	plugin.database.playerQueries.recordAffinity(playerData);
	    	plugin.chat.removePlayer(playerData);
	    }

}
