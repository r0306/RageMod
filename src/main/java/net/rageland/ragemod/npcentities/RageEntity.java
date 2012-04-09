package net.rageland.ragemod.npcentities;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import net.citizensnpcs.lib.NPC;
import net.citizensnpcs.lib.NPCNetHandler;
import net.citizensnpcs.lib.creatures.CreatureNPC;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.NetHandler;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.WorldServer;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCInstance;
import net.rageland.ragemod.data.NPCPhrase;
import net.rageland.ragemod.data.PlayerData;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import net.minecraft.server.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * 
 * @author TheIcarusKid
 * Provides a layer between NPCEntity and the custom entity classes for Ragemod-specific code
 *
 */

@SuppressWarnings("unused")
// TODO Fix this!
public class RageEntity extends CreatureNPC
{
	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;
	protected RageMod plugin;
	protected SpeechData speechData;
	protected NPCInstance instance;
	protected Location location;
	WorldServer world;
	private NPCNetHandler netServerHandler;
	
	// TODO Fix this!
	public RageEntity(NPCInstance instance)
	{		
		super(handle); // TODO Fix this!
		
		NetworkManager netMgr = new NetworkManager(new Socket(),
				"NPC Manager", new NetHandler()
				{
					public boolean c()
					{
						return true;
					}
				});
		// TODO Fix this!
		//this.netServerHandler = new NPCNetHandler(instance.server.getMCServer(), netMgr, this);
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
	
	@Override
	public void onLeftClick(Player player) {	
		
	}
	
	public void addSpeechMessage(NPCPhrase message)
	{
		speechData.addMessage(message);
	}

	public void actAsHurt()
	{
		
		Entity entity = handle;
		this.world.tracker.a(entity, new Packet18ArmAnimation());
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

	public void setPositionRotation(double x, double y, double z, float yaw,
			float pitch) {
		// TODO Unfinished
		
	}

	org.bukkit.entity.Entity bukkitEntity;
	public org.bukkit.entity.Entity getBukkitEntity() {
		// TODO Unfinished
		return bukkitEntity;
	}

	public void setPosition(double x, double y, double z) {
		// TODO Auto-generated method stub
		
	}

	public boolean setSneak(boolean flag) {
		return flag;
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