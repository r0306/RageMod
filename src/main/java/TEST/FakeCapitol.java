package TEST;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.config.WarZoneConfig;
import net.rageland.ragemod.config.ZonesConfig;
import net.rageland.ragemod.world.Zone;

public class FakeCapitol extends Zone {

	public FakeCapitol(RageMod Plugin) {
		super(Plugin, new ZonesConfig("Capitol", 0, 0, 100, true, false, false, false, false, false, false, false, false),new WarZoneConfig());
		// TODO Auto-generated constructor stub
	}

}
