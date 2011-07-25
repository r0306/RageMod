package net.rageland.ragemod.npcentities;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.npclib.NPCNetHandler;
import net.rageland.ragemod.npclib.NPCNetworkManager;
import net.rageland.ragemod.npclib.NpcEntityTargetEvent;
import net.rageland.ragemod.npclib.NullSocket;
import net.rageland.ragemod.npclib.NpcEntityTargetEvent.NpcTargetReason;

import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class NPCEntity extends EntityPlayer {
	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;
	protected RageMod plugin;

	public NPCEntity(MinecraftServer minecraftserver, World world, String name,
			ItemInWorldManager iteminworldmanager, RageMod plugin) 
	{
		
		super(minecraftserver, world, name, iteminworldmanager);
		NetworkManager netMgr = new NPCNetworkManager(new NullSocket(),
				"NPC Manager", new NetHandler() 
				{
					public boolean c() 
					{
						return true;
					}
				});
		this.netServerHandler = new NPCNetHandler(minecraftserver, netMgr, this);
		this.lastTargetId = -1;
		this.lastBounceId = -1;
		this.lastBounceTick = 0L;
		this.plugin = plugin;
	}

	public void rightClickAction(Player player) 
	{
		
	}

	public void leftClickAction(Player player) 
	{
		
	}

	public void actAsHurt() 
	{
		((WorldServer) this.world).tracker.a(this, new Packet18ArmAnimation(
				this, 2));
	}

	public boolean a(EntityHuman entity) 
	{
		EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(),
				entity.getBukkitEntity(),
				NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
		CraftServer server = ((WorldServer) this.world).getServer();
		server.getPluginManager().callEvent(event);

		return super.a(entity);
	}

	public void b(EntityHuman entity) 
	{
		if ((this.lastTargetId == -1) || (this.lastTargetId != entity.id)) 
		{
			EntityTargetEvent event = new NpcEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
			CraftServer server = ((WorldServer) this.world).getServer();
			server.getPluginManager().callEvent(event);
		}
		this.lastTargetId = entity.id;

		super.b(entity);
	}

	public void c(net.minecraft.server.Entity entity) 
	{
		if (this.lastBounceId != entity.id || (System.currentTimeMillis() - this.lastBounceTick > 1000L)) 
		{
			EntityTargetEvent event = new NpcEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
			CraftServer server = ((WorldServer) this.world).getServer();
			server.getPluginManager().callEvent(event);

			this.lastBounceTick = System.currentTimeMillis();
		}

		this.lastBounceId = entity.id;

		super.c(entity);
	}

	public PlayerInventory getInventory() 
	{
		return ((HumanEntity) getBukkitEntity()).getInventory();
	}

	public void setItemInHand(Material m) 
	{
		((HumanEntity) getBukkitEntity()).setItemInHand(new ItemStack(m, 1));
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getName() 
	{
		return this.name;
	}

	
}