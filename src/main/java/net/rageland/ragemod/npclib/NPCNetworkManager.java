package net.rageland.ragemod.npclib;

import java.lang.reflect.Field;
import java.net.Socket;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;

public class NPCNetworkManager extends NetworkManager {
	public NPCNetworkManager(Socket socket, String s, NetHandler nethandler) {
		super(socket, s, nethandler);
		try {
			Field f = NetworkManager.class.getDeclaredField("l");
			f.setAccessible(true);
			f.set(this, Boolean.valueOf(false));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void a(NetHandler nethandler) {
	}

	public void queue(Packet packet) {
	}

	public void a(String s, Object[] aobject) {
	}

	public void a() {
	}
}