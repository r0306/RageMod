package net.rageland.ragemod;

import net.rageland.ragemod.npclib.NPCEntity;
import net.rageland.ragemod.npclib.NPCManager;
import net.rageland.ragemod.npclib.NpcEntityTargetEvent;
import net.rageland.ragemod.npclib.NpcEntityTargetEvent.NpcTargetReason;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

public class RQEntityListener extends EntityListener {
	private final RageQuest plugin;

	public RQEntityListener(RageQuest plugin) {
		this.plugin = plugin;
	}

	public void onEntityDamage(EntityDamageEvent event) {
		
		if ((event.getEntity() instanceof HumanEntity)) {
			
			if (NPCManager.isNPC(event.getEntity())) {
				
				if ((event instanceof EntityDamageByEntityEvent)) {
					
					EntityDamageByEntityEvent edbeEvent = (EntityDamageByEntityEvent) event;
					if ((edbeEvent.getDamager() instanceof Player)) {
						
						NPCEntity npcEntity = NPCManager
								.getNPCFromEntity(edbeEvent.getEntity());
						npcEntity.leftClickAction((Player) edbeEvent
								.getDamager());
					}
				}
				event.setCancelled(true);
			}
		}
	}

	public void onEntityTarget(EntityTargetEvent event) {
		
		if ((event instanceof NpcEntityTargetEvent)) {
			
			NpcEntityTargetEvent netEvent = (NpcEntityTargetEvent) event;
			if (((netEvent.getTarget() instanceof Player))
					&& (netEvent.getNpcReason() == NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED)) {
				
				NPCEntity npcEntity = NPCManager.getNPCFromEntity(netEvent
						.getEntity());

				npcEntity.rightClickAction((Player) event.getTarget());
			}
		}
	}
}