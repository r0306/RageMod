package net.rageland.ragemod.chat;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.world.Zone;


public class ZoneChat extends BaseChat{
	
	private Zone zone;
	
	public ZoneChat(RageMod Plugin, Zone Zone) {
		super(Plugin, Zone.getName());
		this.zone = Zone;
	}

	public Zone getZone() {
		return zone;
	}
	
	//TODO colors and prefixes
	

}
