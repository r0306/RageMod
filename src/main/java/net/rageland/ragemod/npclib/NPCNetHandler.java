package net.rageland.ragemod.npclib;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet101CloseWindow;
import net.minecraft.server.Packet102WindowClick;
import net.minecraft.server.Packet106Transaction;
import net.minecraft.server.Packet10Flying;
import net.minecraft.server.Packet130UpdateSign;
import net.minecraft.server.Packet14BlockDig;
import net.minecraft.server.Packet15Place;
import net.minecraft.server.Packet16BlockItemSwitch;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet19EntityAction;
import net.minecraft.server.Packet255KickDisconnect;
import net.minecraft.server.Packet3Chat;
import net.minecraft.server.Packet7UseEntity;
import net.minecraft.server.Packet9Respawn;
import org.bukkit.craftbukkit.entity.CraftPlayer;

public class NPCNetHandler extends NetServerHandler {
	public NPCNetHandler(MinecraftServer minecraftserver,
			NetworkManager networkmanager, EntityPlayer entityplayer) {
		super(minecraftserver, networkmanager, entityplayer);
	}

	public CraftPlayer getPlayer() {
		return null;
	}

	public void a() {
	}

	public void a(Packet10Flying packet10flying) {
	}

	public void sendMessage(String s) {
	}

	public void a(double d0, double d1, double d2, float f, float f1) {
	}

	public void a(Packet14BlockDig packet14blockdig) {
	}

	public void a(Packet15Place packet15place) {
	}

	public void a(String s, Object[] aobject) {
	}

	public void a(Packet packet) {
	}

	public void a(Packet16BlockItemSwitch packet16blockitemswitch) {
	}

	public void a(Packet3Chat packet3chat) {
	}

	public void a(Packet18ArmAnimation packet18armanimation) {
	}

	public void a(Packet19EntityAction packet19entityaction) {
	}

	public void a(Packet255KickDisconnect packet255kickdisconnect) {
	}

	public void sendPacket(Packet packet) {
	}

	public void a(Packet7UseEntity packet7useentity) {
	}

	public void a(Packet9Respawn packet9respawn) {
	}

	public void a(Packet101CloseWindow packet101closewindow) {
	}

	public void a(Packet102WindowClick packet102windowclick) {
	}

	public void a(Packet106Transaction packet106transaction) {
	}

	public int b() {
		return super.b();
	}

	public void a(Packet130UpdateSign packet130updatesign) {
	}
}