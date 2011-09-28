package net.rageland.ragemod.npcentities;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.WorldServer;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCInstance;
import net.rageland.ragemod.data.NPCPhrase;
import net.rageland.ragemod.data.PlayerData;
import org.martin.bukkit.npclib.NPCNetHandler;
import org.martin.bukkit.npclib.NPCNetworkManager;
import org.martin.bukkit.npclib.NpcEntityTargetEvent;
import org.martin.bukkit.npclib.NullSocket;
import org.martin.bukkit.npclib.BServer;
import org.martin.bukkit.npclib.BWorld;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import org.martin.bukkit.npclib.NPCEntity;

/**
 * 
 * @author TheIcarusKid
 * Provides a layer between NPCEntity and the custom entity classes for Ragemod-specific code
 *
 */
public class RageEntity extends NPCEntity
{
	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;
	protected RageMod plugin;
	protected SpeechData speechData;
	protected NPCInstance instance;
	protected Location location;

	public RageEntity( NPCInstance instance )
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
		
		this.speechData = plugin.database.npcQueries.getPhrases(instance.getNPCData(), instance.getTownID());
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
		private RageEntity npcEntity;
		private Timer timer;
		private SpeechData speechData;

		public SpeechTask(RageEntity npcEntity, Timer timer, SpeechData speechData)
		{
			this.npcEntity = npcEntity;
			this.timer = timer;
			this.speechData = speechData;
		}

		public void run()
		{			
			if(RageMod.getInstance().npcManager.contains(npcEntity) && speechData.getInterval() > 0)
			{
				Player[] players = RageEntity.this.plugin.getServer().getOnlinePlayers();
				
				for (Player player : players)
				{
					if ((player.getLocation().distance(this.npcEntity.getBukkitEntity().getLocation()) >= speechData.getRadius()))
					{
						continue;
					}					
					PlayerData playerData = plugin.players.get(player.getName());
					player.sendMessage(speechData.getNextMessage(playerData));
				}

				if (RageMod.getInstance().npcManager.contains(this.npcEntity))
					this.timer.schedule(new SpeechTask(RageEntity.this, this.timer, speechData), speechData.getInterval());
			}				
		}
	}

	// 9-7-11 DC: SpeechData rewritten, will probably not need this anymore
	// Pulls speech messages from the database
//	public void addSpeechMessages() 
//	{
//		for( NPCPhrase message : plugin.database.npcQueries.getPhrases(instance.getRaceID(), instance.getTownID(), instance.getNPCid()) )
//		{
//			this.addSpeechMessage(message);
//		}
//	}



}
