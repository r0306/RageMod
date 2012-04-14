package net.rageland.ragemod.database.queries;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.ZoneHandler;
import net.rageland.ragemod.database.*;
import net.rageland.ragemod.world.WarZone;
import net.rageland.ragemod.world.Zone;

public class ZoneQueries {
	
	private RageMod plugin;
	private WarZone wz;
	private Zone zone;
	private ZoneHandler zHandler;
	private RageDB rdb;
	private JDCConnection jdcC;
	private JDCConnectionDriver jdcCD;
	private JDCConnectionPool jdcCP;
	
	public ZoneQueries(RageMod plugin, ZoneHandler zHandler, Zone zone, WarZone wz, JDCConnection jdcC, JDCConnectionDriver jdcCD, JDCConnectionPool jdcCP, RageDB rdb) {
		this.plugin = plugin;
		this.wz = wz;
		this.zone = zone;
		this.zHandler = zHandler;
		this.jdcC = jdcC;
		this.jdcCD = jdcCD;
		this.jdcCP = jdcCP;
		this.rdb = rdb;
	}
	
	public void submitWarZone() {
		
	}

}
