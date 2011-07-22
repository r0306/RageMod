package net.rageland.ragemod;

import java.util.HashMap;
import net.rageland.ragemod.npclib.NPCEntity;
import net.rageland.ragemod.npclib.NPCManager;
import net.rageland.ragemod.quest.Quest;
import net.rageland.ragemod.quest.QuestManager;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RageQuest extends JavaPlugin {
	private RQPlayerListener playerListener;
	private RQBlockListener blockListener;
	private RQServerListener serverListener;
	private RQEntityListener entityListener;
	private PluginManager pluginManager;
	public NPCManager npcManager;
	public QuestManager questManager;

	public RageQuest() {
		this.serverListener = new RQServerListener(this);
		this.playerListener = new RQPlayerListener(this);
		this.blockListener = new RQBlockListener(this);
		this.entityListener = new RQEntityListener(this);
	}

	public void onEnable() {
		this.pluginManager = getServer().getPluginManager();
		this.npcManager = new NPCManager(this);
		this.questManager = new QuestManager();

		this.pluginManager.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Event.Priority.Highest, this);
		this.pluginManager.registerEvent(Event.Type.ENTITY_TARGET, this.entityListener, Event.Priority.Normal, this);

		this.npcManager.spawnNPC("TraderNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 96.0D), "1", NPCManager.TRADERNPC);
		this.npcManager.spawnNPC("QuestStartNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 94.0D), "2", NPCManager.QUESTSTARTNPC);
		this.npcManager.spawnNPC("QuestEndNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 92.0D), "3", NPCManager.QUESTENDNPC);
		this.npcManager.spawnNPC("QuestNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D,90.0D), "4", 3);
		this.npcManager.spawnNPC("RewardNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 88.0D), "5", 5);
		NPCManager.npcs.get("5").setQuest((Quest) QuestManager.quests.get(Integer.valueOf(1)));
	}

	public void onDisable() {
		this.npcManager.despawnAll();
	}
}