package net.rageland.ragemod.npclib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;
import net.rageland.ragemod.quest.Quest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class NPCEntity extends EntityPlayer {
	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;
	private Timer timer = new Timer();
	private JavaPlugin plugin;
	private int npcType;
	private Quest quest;
	public double speechDistance = 40.0D;
	private ArrayList<String> speechMessages;
	int speechCounter;
	int speechInterval;

	public NPCEntity(MinecraftServer minecraftserver, World world, String s,
			ItemInWorldManager iteminworldmanager, JavaPlugin plugin,
			int npcType) {
		
		super(minecraftserver, world, s, iteminworldmanager);
		NetworkManager netMgr = new NPCNetworkManager(new NullSocket(),
				"NPC Manager", new NetHandler() {
					public boolean c() {
						return true;
					}
				});
		this.netServerHandler = new NPCNetHandler(minecraftserver, netMgr, this);
		this.lastTargetId = -1;
		this.lastBounceId = -1;
		this.lastBounceTick = 0L;
		this.plugin = plugin;
		this.npcType = npcType;

		initializeNpc();
	}

	private void initializeNpc() {
		switch (this.npcType) {
		case NPCManager.QUESTSTARTNPC:
			break;
		case NPCManager.QUESTENDNPC:
			break;
		case NPCManager.QUESTNPC:
			break;
		case NPCManager.TRADERNPC:
			initializeTrader();
			break;
		case NPCManager.REWARDNPC:
			break;
		}
	}

	public void rightClickAction(Player player) {
		switch (this.npcType) {
		case NPCManager.QUESTSTARTNPC:
			player.sendMessage("Right clicked a Quest Start NPC");

			break;
		case NPCManager.QUESTENDNPC:
			player.sendMessage("Right clicked a Quest End NPC");

			break;
		case NPCManager.QUESTNPC:
			player.sendMessage("Right clicked a Quest NPC");

			break;
		case NPCManager.TRADERNPC:
			player.sendMessage("Right clicked a Trader NPC");
			setLocation(this.locX, this.locY, this.locZ, -180.0F, -20.0F);
			break;
		case NPCManager.REWARDNPC:
			if (this.quest == null)
				break;
			this.quest.presentQuest(player);

			break;
		}
	}

	public void leftClickAction(Player player) {
		switch (this.npcType) {
		case NPCManager.QUESTSTARTNPC:
			player.sendMessage("Left clicked a Quest Start NPC");
			
			break;
		case NPCManager.QUESTENDNPC:
			player.sendMessage("Left clicked a Quest End NPC");
			
			break;
		case NPCManager.QUESTNPC:
			player.sendMessage("Left clicked a Quest NPC");
			
			break;
		case NPCManager.TRADERNPC:
			player.sendMessage("Left clicked a Trader NPC");
			
			break;
		case NPCManager.REWARDNPC:
			if (this.quest != null)
				this.quest.questStart(player);
			
			break;
		}
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
	}

	private void initializeTrader() {
		this.speechMessages = new ArrayList();
		this.speechMessages.add("Testmessage #1 from npc.");
		this.speechMessages.add("Testmessage #2 from npc.");
		this.speechCounter = 0;
		this.speechInterval = 15000;
	}

	public void actAsHurt() {
		((WorldServer) this.world).tracker.a(this, new Packet18ArmAnimation(
				this, 2));
	}

	public boolean a(EntityHuman entity) {
		EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(),
				entity.getBukkitEntity(),
				NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
		CraftServer server = ((WorldServer) this.world).getServer();
		server.getPluginManager().callEvent(event);

		return super.a(entity);
	}

	public void b(EntityHuman entity) {
		if ((this.lastTargetId == -1) || (this.lastTargetId != entity.id)) {
			EntityTargetEvent event = new NpcEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
			CraftServer server = ((WorldServer) this.world).getServer();
			server.getPluginManager().callEvent(event);
		}
		this.lastTargetId = entity.id;

		super.b(entity);
	}

	public void c(net.minecraft.server.Entity entity) {
		if ((this.lastBounceId != entity.id)
				|| (System.currentTimeMillis() - this.lastBounceTick > 1000L)) {
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

	public PlayerInventory getInventory() {
		return ((HumanEntity) getBukkitEntity()).getInventory();
	}

	public void setItemInHand(Material m) {
		((HumanEntity) getBukkitEntity()).setItemInHand(new ItemStack(m, 1));
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public class LookAlive implements Runnable {
		private NPCEntity npcEntity;
		private Random rand;
		private int interval;

		public LookAlive(NPCEntity npcEntity, int interval) {
			this.npcEntity = npcEntity;
			this.interval = interval;
			this.rand = new Random();
		}

		public void run() {
			if (NPCManager.npcs.containsValue(this.npcEntity)) {
				float yaw = this.rand.nextFloat() * -360.0F;
				float pitch = this.rand.nextFloat() * 50.0F;
				this.npcEntity.setLocation(this.npcEntity.locX,
						this.npcEntity.locY, this.npcEntity.locZ, yaw, pitch);
			}
		}
	}

	public class SpeechTask extends TimerTask {
		private NPCEntity npcEntity;
		private Timer timer;
		private int speechInterval;

		public SpeechTask(NPCEntity npcEntity, Timer timer, int speechInterval) {
			this.npcEntity = npcEntity;
			this.timer = timer;
			this.speechInterval = speechInterval;
		}

		public void run() {
			Player[] players = NPCEntity.this.plugin.getServer()
					.getOnlinePlayers();

			for (Player player : players) {
				if ((this.npcEntity == null)
						|| (player.getLocation().distance(
								this.npcEntity.getBukkitEntity().getLocation()) >= NPCEntity.this.speechDistance))
					continue;
				player.sendMessage((String) NPCEntity.this.speechMessages
						.get(NPCEntity.this.speechCounter));
				NPCEntity.this.speechCounter += 1;

				if (NPCEntity.this.speechCounter == NPCEntity.this.speechMessages
						.size()) {
					NPCEntity.this.speechCounter = 0;
				}

			}

		}
	}
}