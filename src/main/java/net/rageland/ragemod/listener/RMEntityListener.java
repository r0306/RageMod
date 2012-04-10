package net.rageland.ragemod.listener;

// TODO: Bounty

import java.util.Random;

import net.citizensnpcs.api.event.NPCTargetEvent;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.npcentities.RageEntity;
import net.rageland.ragemod.npcentities.RageNPCManager;
import net.rageland.ragemod.quest.KillCreatureQuest;
import net.rageland.ragemod.world.PlayerTown;
import net.rageland.ragemod.world.Town;
import net.rageland.ragemod.utilities.Util;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * RageMod entity listener
 * @author TheIcarusKid
 */
@SuppressWarnings("deprecation")
public class RMEntityListener implements Listener
{
	private final RageMod plugin;
	private Random random;

    public RMEntityListener(final RageMod plugin) 
    {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    // Called when an entity damages another entity
    public void onEntityDamage(EntityDamageEvent rawEvent) 
    {
    	Entity defenderEntity = rawEvent.getEntity();
    	
    	// Makes NPC invulnerable
        if (defenderEntity instanceof HumanEntity) 
        {			
			if (RageNPCManager.isNPC(defenderEntity)) 
			{				
				rawEvent.setCancelled(true);
			}
		}
		
    	
    	if(rawEvent instanceof EntityDamageByEntityEvent)
    	{
    		EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent)rawEvent;
        	
        	Entity attackerEntity = edbeEvent.getDamager();  
        	
        	// If defender is NPC, the event is already cancelled, must check if it is a leftClickAction from player.
        	if (RageNPCManager.isNPC(defenderEntity)) 
			{				
        		if (attackerEntity instanceof Player) 
    			{						
    				RageEntity npcEntity = RageNPCManager.getNPCFromEntity(defenderEntity);
    				npcEntity.leftClickAction((Player) attackerEntity);
    			}
				return;
			}
        	
            // Handle PvP
            if( attackerEntity instanceof Player && defenderEntity instanceof Player && !plugin.config.PRE_RELEASE_MODE ) 
            {
            	Player attacker = (Player)attackerEntity;
            	Player defender = (Player)defenderEntity;
            	PlayerData attackerData = plugin.players.get(attacker.getName());
            	PlayerData defenderData = plugin.players.get(defender.getName());
            	
            	// Always prevent allies from harming each other
    			if( attackerData.id_Faction == defenderData.id_Faction )
    			{
    				edbeEvent.setCancelled(true);
        			plugin.message.parseNo(attacker, plugin.players.get(defenderData.name).getCodedName() + " is your ally!");
        			return;
    			}
            	
            	// Use the defender's position to determine behavior
            	// *** ZONE A (Neutral Zone) **
            		// No PvP in capitol
            		if( !plugin.zones.isInside(defender.getLocation()).getConfig().isPvp())
            		{
            			edbeEvent.setCancelled(true);
            			plugin.message.parseNo(attacker, "PvP is not allowed inside " + plugin.config.Capitol_CodedName + ".");
            			return;
            		}
            		else
            		{
            			if (plugin.zones.isInside(defender.getLocation()).getConfig().isFactionPvp()){
            			
            			// Only faction-faction PvP is allowed in neutral zone
            			if( defenderData.id_Faction == 0 )
            			{
            				edbeEvent.setCancelled(true);
                			plugin.message.sendNo(attacker, "You cannot attack neutral players in " + plugin.zones.getName(defender.getLocation()) + ".");
                			return;
            			}
            			else if( attackerData.id_Faction == 0 )
            			{
            				edbeEvent.setCancelled(true);
                			plugin.message.sendNo(attacker, "Neutral players cannot attack in " + plugin.zones.getName(defender.getLocation()) + ".");
                			return;
            			}
            		}
        
            	// *** ZONE B (War Zone) ***
            	else
            	{
            		PlayerTown playerTown = (PlayerTown)plugin.towns.getCurrentTown(defender.getLocation());
            		
            		// Keep referees from participating in combat
            		if(RageMod.perms.has(attacker, "ragemod.referee.blockpvp") )
            		{
            			edbeEvent.setCancelled(true);
            			plugin.message.sendNo(attacker, "Referees may not participate in combat.");
            			return;
            		}
            		// Protect referees 
            		else if( RageMod.perms.has(defender, "ragemod.referee.blockpvp") )
            		{
            			edbeEvent.setCancelled(true);
            			plugin.message.sendNo(attacker, "You cannot harm a referee.");
            			return;
            		}
            		// Handle combat inside of towns
            		else if( playerTown != null )
            		{
    	        		// Protect neutral players inside all towns
    	        		if( defenderData.id_Faction == 0 )
    	        		{
    	        			edbeEvent.setCancelled(true);
    	        			plugin.message.sendNo(attacker, "You cannot harm neutral players inside of towns.");
    	        			return;
    	        		}
    	        		// Keep neutral players from harming any players
    	        		if( attackerData.id_Faction == 0 )
    	        		{
    	        			edbeEvent.setCancelled(true);
    	        			plugin.message.sendNo(attacker, "Neutral players cannot attack inside of towns.");
    	        			return;
    	        		}
    	        		// Protect faction players inside of their own and allied towns
    	        		if( defenderData.id_Faction == playerTown.id_Faction )
    	        		{
    	        			edbeEvent.setCancelled(true);
    	        			plugin.message.parseNo(attacker, "You cannot harm " + plugin.factions.getCodedName(defenderData.id_Faction) + " inside of their own towns.");
    	        			return;
    	        		}
            		}
            	
            else if( defenderEntity instanceof Player && attackerEntity instanceof Creature)
            {
            	Player defenderPlayer = (Player) defenderEntity;
            	if( RageMod.perms.has(defenderPlayer, "ragemod.admin.smitemobs") )
            	{
            		// Automatically kill monsters who attack admins
                	Creature creature = (Creature)attackerEntity;
                	creature.setFireTicks(1);
                	creature.damage(100);
            	}
            } 
            // Defender is Creature, Attacker is Player
            else if(defenderEntity instanceof Creature && attackerEntity instanceof Player && !plugin.config.PRE_RELEASE_MODE)
            {
            	Creature defenderCreature = (Creature)defenderEntity;
            	Player attackerPlayer = (Player) attackerEntity;
            	PlayerData playerData = plugin.players.get(attackerPlayer.getName());
            	
            	// Creature dies from attack
            	if(defenderCreature.getHealth() <= edbeEvent.getDamage())
            	{
            		// Player is on a quest, quest is of KillCreatureQuest type
            		if(playerData.isOnKillQuest())
            		{
            			KillCreatureQuest kcQuest = (KillCreatureQuest) playerData.activeQuestData.getQuest();
            			CreatureType entityCreatureType = Util.getCreatureTypeFromEntity(defenderCreature);
            			
            			if(entityCreatureType == kcQuest.getCreatureToBeKilled())
            			{
            				playerData.activeQuestData.incrementObjectiveCounter();
            				playerData.activeQuestData.getQuest().statusUpdate(attackerPlayer, playerData);
            			}
            		}
            	}
            }
            	}
            		}
            }
    	}
    	
    }
    
    // Called when creatures spawn
    public void onCreatureSpawn(CreatureSpawnEvent event) 
    {
        // Don't process code if event was cancelled by another plugin
    	if (event.isCancelled()) 
            return;
    	
    	// Don't let monsters spawn inside player towns or the capitol
    	if( (event.getCreatureType() == CreatureType.CREEPER || event.getCreatureType() == CreatureType.SKELETON ||
    			event.getCreatureType() == CreatureType.ZOMBIE || event.getCreatureType() == CreatureType.SPIDER ||
    			event.getCreatureType() == CreatureType.SQUID || event.getCreatureType() == CreatureType.ENDERMAN) && 
    			(plugin.zones.isInside((event.getLocation())).isInsideCapitol(event.getLocation()) || plugin.towns.getCurrentTown(event.getLocation()) != null) )
    		event.setCancelled(true);
    	
    	// Don't let monsters spawn inside the travel zone
    	else if( plugin.zones.isInTravelZone(event.getLocation()) )
    		event.setCancelled(true);
    	
    	// Change colors of sheep spawned inside faction towns
    	else if( event.getCreatureType() == CreatureType.SHEEP )
    	{
    		Town town = plugin.towns.getCurrentTown(event.getLocation());
    		if( town != null && town instanceof PlayerTown )
    		{
    			PlayerTown playerTown = (PlayerTown)town;
    			if( playerTown.id_Faction != 0 && random.nextInt(100) < plugin.config.Town_COLORED_SHEEP_CHANCE )
    			{
    				Sheep s = (Sheep) event.getEntity();
    				s.setColor(plugin.factions.getDyeColor(playerTown.id_Faction, random.nextBoolean()));
    			}
    		}
    	}
    }
    
    /**
     *  Called on entityTarget. Used for detecting right clicks on the NPC
     */
    public void onEntityTarget(EntityTargetEvent event) 
    {    	
		if ((event instanceof NPCTargetEvent)) 
		{			
			NPCTargetEvent netEvent = (NPCTargetEvent) event;
			if (((netEvent.getTarget() instanceof Player))) 
			{				
				RageEntity npcEntity = RageNPCManager.getNPCFromEntity(netEvent.getEntity());
				npcEntity.rightClickAction((Player) event.getTarget());
			}
		}		
	}    
}

