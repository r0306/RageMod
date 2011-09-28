package net.rageland.ragemod;

import java.io.File;
import java.util.HashMap;

import net.rageland.ragemod.data.Factions;
import net.rageland.ragemod.data.Lots;
import net.rageland.ragemod.data.Players;
import net.rageland.ragemod.data.Tasks;
import net.rageland.ragemod.data.Towns;
import net.rageland.ragemod.database.RageDB;
import net.rageland.ragemod.npcentities.RageNPCManager;
import net.rageland.ragemod.quest.QuestManager;
import net.rageland.ragemod.text.Languages;
import net.rageland.ragemod.text.Message;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.iConomy.iConomy;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * RageMod for Bukkit
 *
 * @author TheIcarusKid
 */
public class RageMod extends JavaPlugin {
	
    private RMPlayerListener playerListener;
    private RMBlockListener blockListener;
    private RMServerListener serverListener;
    private RMEntityListener entityListener;
    private static RageMod plugin;
    
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private Server server; 
    private PluginManager pluginManager;
    public iConomy iConomy;
    public static PermissionHandler permissionHandler;

    public static String mainDirectory = "plugins/RageMod";
    public File file = new File(mainDirectory + File.separator + "config.yml");
    
    // Global data
    public Lots lots;
    public Players players;
    public Towns towns;
    public Tasks tasks;
    public Factions factions;
    public Languages languages;
    
    // Semi-static data and methods
    public RageConfig config;
    public RageDB database;
    public RageZones zones;
    public Message message;
    public RageNPCManager npcManager;
    public QuestManager questManager;
    
    
    
    public RageMod() 
    {
    	
    }
    
    public static RageMod getInstance() 
    {
    	if(plugin == null)
    		plugin = new RageMod(); 
    	
    	return plugin;
    }
    
    
    public void onEnable() 
    {           
    	plugin = this;
        initializeVariables();
        registerEvents();        
        setupPermissions();   
        loadDatabaseData();        
        startScheduledTasks();        
        runDebugTests();      
        
        System.out.println( "RageMod is enabled!!!" );
    }
    
    public void creatingNPCTemp() { /*    
    	this.npcManager.spawnNPC("TraderNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 96.0D), "1", NPCManager.TRADERNPC);
		this.npcManager.spawnNPC("QuestStartNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 94.0D), "2", NPCManager.QUESTSTARTNPC);
		this.npcManager.spawnNPC("QuestEndNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 92.0D), "3", NPCManager.QUESTENDNPC);
		this.npcManager.spawnNPC("QuestNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D,90.0D), "4", 3);
		this.npcManager.spawnNPC("RewardNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 88.0D), "5", 5);
		NPCManager.npcs.get("5").setQuest((Quest) QuestManager.quests.get(Integer.valueOf(1)));
		*/
    }
    
    public void onDisable() {    
    	// this.npcManager.despawnAll();
        System.out.println("Goodbye world!");
    }
        
    public Configuration load(){
        try {
            Configuration config = new Configuration(file);
            config.load();
            return config;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }
    
    

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    
    private void setupPermissions() {
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (this.permissionHandler == null) {
            if (permissionsPlugin != null) {
                this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
            } else {
                
            }
        }
    }
    
    private void runDebugTests()
    {
    	System.out.println("Number of lots:" + lots.getAll().size());    	
    }
    
    private void registerEvents()
    {
    	pluginManager.registerEvent(Event.Type.PLUGIN_ENABLE, this.serverListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLUGIN_DISABLE, this.serverListener, Event.Priority.Normal, this);
        
        pluginManager.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Priority.Normal, this);
        
        pluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        
        pluginManager.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
        pluginManager.registerEvent(Event.Type.ENTITY_INTERACT, entityListener, Priority.High, this);
        pluginManager.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this);        
		pluginManager.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Normal, this);
    }
    
    private void initializeVariables()
    {
    	serverListener = new RMServerListener(this);
    	playerListener = new RMPlayerListener(this);
    	blockListener = new RMBlockListener(this);  
    	entityListener = new RMEntityListener(this);
    	iConomy = null; 
    	config = new RageConfig(this);
        database = new RageDB(this, config);
        
        lots = new Lots(this);
        players = new Players(this);
        towns = new Towns(this);
        tasks = new Tasks(this);
        factions = new Factions();
        languages = new Languages(this);
        
    	server = this.getServer();
    	zones = new RageZones(this, config);
        pluginManager = server.getPluginManager();
        npcManager = new RageNPCManager(this);
        questManager = new QuestManager();
        message = new Message(this);
    }
    
    private void loadDatabaseData()
    {
    	towns.loadTowns();
        lots.loadLots();
        tasks.loadTaskTimes();
        languages.loadDictionaries();
        npcManager.associateLocations();
        npcManager.spawnAllInstances();
    }
    
    private void startScheduledTasks()
    {
    	this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new RageTimer(this), 20, 20);
    }
    
}

