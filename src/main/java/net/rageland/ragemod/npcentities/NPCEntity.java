package net.rageland.ragemod.npcentities;

import java.sql.Timestamp;
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
import net.rageland.ragemod.data.NPCData;
import net.rageland.ragemod.data.NPCInstance;
import net.rageland.ragemod.data.NPCLocation;
import net.rageland.ragemod.data.NPCPhrase;
import net.rageland.ragemod.npclib.BWorld;
import net.rageland.ragemod.npclib.NPCNetHandler;
import net.rageland.ragemod.npclib.NPCNetworkManager;
import net.rageland.ragemod.npclib.NpcEntityTargetEvent;
import net.rageland.ragemod.npclib.NullSocket;
import net.rageland.ragemod.npclib.NpcEntityTargetEvent.NpcTargetReason;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class NPCEntity extends EntityPlayer
{
	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;
	protected RageMod plugin;
	protected SpeechData speechData;
	protected NPCInstance instance;
	protected Location location;

	public NPCEntity( NPCInstance instance )
	{		
		super(instance.server.getMCServer(), instance.world.getWorldServer(), 
				instance.getColorName(), new ItemInWorldManager( instance.world.getWorldServer()));
		
		NetworkManager netMgr = new NPCNetworkManager(new NullSocket(),
				"NPC Manager", new NetHandler()
				{
					public boolean c()
					{
						return true;
					}
				});
		
		this.netServerHandler = new NPCNetHandler(instance.server.getMCServer(), netMgr, this);
		this.lastTargetId = -1;
		this.lastBounceId = -1;
		this.lastBounceTick = 0L;
		this.plugin = instance.plugin;
		this.instance = instance;
		
		int radius = 20; 
		int interval = 30;
		
		this.speechData = new SpeechData(new ArrayList<NPCPhrase>(), radius, interval);
	}

	public void rightClickAction(Player player)
	{

	}

	public void leftClickAction(Player player)
	{

	}
	
	public void addSpeechMessage(NPCPhrase message)
	{
		speechData.addMessage(message);
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
		if (this.lastBounceId != entity.id
				|| (System.currentTimeMillis() - this.lastBounceTick > 1000L))
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
	
	// Sets the yaw & pitch to face the player interacting with NPC
	public void facePlayer(Player player)
	{
		float yaw = (float) ((Math.atan2(instance.getLocation().getX() - player.getLocation().getX(), 
				player.getLocation().getZ() - instance.getLocation().getZ()) * 180) / Math.PI);
		float pitch = (float) ((Math.atan(instance.getLocation().getY() - player.getLocation().getY()) * 180) / Math.PI);
		
		this.setPositionRotation(instance.getLocation().getX(), instance.getLocation().getY(), instance.getLocation().getZ(), yaw, pitch);
	}

	public class SpeechTask extends TimerTask
	{
		private NPCEntity npcEntity;
		private Timer timer;
		private SpeechData speechData;

		public SpeechTask(NPCEntity npcEntity, Timer timer, SpeechData speechData)
		{
			this.npcEntity = npcEntity;
			this.timer = timer;
			this.speechData = speechData;
		}

		public void run()
		{			
			if(RageMod.getInstance().npcManager.contains(npcEntity) && speechData.getInterval() > 0)
			{
				Player[] players = NPCEntity.this.plugin.getServer().getOnlinePlayers();
				String message = speechData.getNextMessage();
				
				for (Player player : players)
				{
					if ((player.getLocation().distance(this.npcEntity.getBukkitEntity().getLocation()) >= speechData.getRadius()))
					{
						continue;
					}					
					player.sendMessage(message);
				}

				if (RageMod.getInstance().npcManager.contains(this.npcEntity))
					this.timer.schedule(new SpeechTask(NPCEntity.this, this.timer, speechData), speechData.getInterval());
			}				
		}
	}

	// Pulls speech messages from the database
	public void addSpeechMessages() 
	{
		for( NPCPhrase message : plugin.database.npcQueries.getPhrases(instance.getRaceID(), instance.getTownID(), instance.getNPCid()) )
		{
			this.addSpeechMessage(message);
		}
	}

}