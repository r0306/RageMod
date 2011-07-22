package net.rageland.ragemod;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

import net.rageland.ragemod.data.Factions;
import net.rageland.ragemod.data.Lots;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.PlayerTowns;
import net.rageland.ragemod.data.Players;
import net.rageland.ragemod.data.Tasks;
import net.rageland.ragemod.npclib.NPCManager;
import net.rageland.ragemod.quest.Quest;
import net.rageland.ragemod.quest.QuestManager;

import com.iConomy.*;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
//import com.nijiko.permissions.PermissionHandler;
//import com.nijikokun.bukkit.Permissions.Permissions;
//import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

// TODO: Update all method names to lowercase to match Java convention - research first :(

import org.bukkit.plugin.Plugin;

/**
 * RageMod for Bukkit
 *
 * @author TheIcarusKid
 */
public class RageMod extends JavaPlugin {
    private final RMPlayerListener playerListener;
    private final RMBlockListener blockListener;
    private final RMServerListener serverListener;
    private final RMEntityListener entityListener;
    
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private Server server; 
    private PluginManager pluginManager;
    public iConomy iConomy;
    public static PermissionHandler permissionHandler;

    public static String mainDirectory = "plugins/RageMod";
    public File file = new File(mainDirectory + File.separator + "config.yml");
    private Timer rageTimer = new Timer(true);
    
    // Static utility classes
    public static RageConfig config = null;
    public static RageDB database = null;  
    public static RageZones zones = null;
    
    
    public RageMod() 
    {
    	serverListener = new RMServerListener(this);
    	playerListener = new RMPlayerListener(this);
    	blockListener = new RMBlockListener(this);  
    	entityListener = new RMEntityListener(this);
    	iConomy = null; 
    }
    
    
    public void onEnable() 
    {    		           
    	server = this.getServer();
        pluginManager = server.getPluginManager();
        
        pluginManager.registerEvent(Event.Type.PLUGIN_ENABLE, this.serverListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLUGIN_DISABLE, this.serverListener, Event.Priority.Normal, this);
        
        pluginManager.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        
        pluginManager.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        
        pluginManager.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Highest, this);
        pluginManager.registerEvent(Event.Type.ENTITY_INTERACT, entityListener, Priority.High, this);
        pluginManager.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Priority.Normal, this);        
		pluginManager.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Normal, this);
        
        setupPermissions();
        System.out.println( "RageMod is enabled!" );
        
        // Initialize the static classes - make sure to initialize Config first as ther other constuctors rely on it
        config = new RageConfig();
        database = new RageDB(this);
        zones = new RageZones(this);
        
        // Load the HashMaps for DB data
        PlayerTowns.getInstance().loadPlayerTowns();
        Players.getInstance();	// Player data is not loaded until players log on
        Lots.getInstance().loadLots();
        Factions.getInstance().loadFactions();
        Tasks.getInstance().loadTaskTimes();
        
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new RageTimer(this), 20, 20);
        
        
        // Run basic debug tests
        runTests();
        
        
        
        
        
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
    
    private void runTests()
    {
    	System.out.println("Number of lots:" + Lots.getAll().size());    	
    }
}

