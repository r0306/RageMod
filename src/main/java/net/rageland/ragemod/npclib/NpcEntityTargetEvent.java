package net.rageland.ragemod.npclib;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class NpcEntityTargetEvent extends EntityTargetEvent {
	private static final long serialVersionUID = -8103432985035183865L;
	private NpcTargetReason reason;

	public NpcEntityTargetEvent(Entity entity, Entity target,
			NpcTargetReason reason) {
		super(entity, target, EntityTargetEvent.TargetReason.CUSTOM);
		this.reason = reason;
	}

	public NpcTargetReason getNpcReason() {
		return this.reason;
	}

	public static enum NpcTargetReason {
		CLOSEST_PLAYER, NPC_RIGHTCLICKED, NPC_BOUNCED;
	}
}