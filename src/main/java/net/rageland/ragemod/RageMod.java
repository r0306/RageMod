package net.rageland.ragemod;

import java.io.File;
import java.util.HashMap;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;	  	
import org.w3c.dom.Element;	  	
import org.w3c.dom.Node;	  	
import org.w3c.dom.NodeList;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.rageland.ragemod.data.AllBountyHandler;
import net.rageland.ragemod.data.FactionHandler;
import net.rageland.ragemod.data.LanguageHandler;
import net.rageland.ragemod.data.LotHandler;
import net.rageland.ragemod.data.PlayerHandler;
import net.rageland.ragemod.data.TownHandler;
import net.rageland.ragemod.data.TaskHandler;
import net.rageland.ragemod.data.ZoneHandler;
import net.rageland.ragemod.database.RageDB;
import net.rageland.ragemod.listener.RMBlockListener;
import net.rageland.ragemod.listener.RMEntityListener;
import net.rageland.ragemod.listener.RMPlayerListener;
import net.rageland.ragemod.listener.RMServerListener;
import net.rageland.ragemod.npcentities.RageNPCManager;
import net.rageland.ragemod.npcentities.SL;
import net.rageland.ragemod.npcentities.WL;
import net.rageland.ragemod.quest.QuestManager;
import net.rageland.ragemod.text.Message;
import net.rageland.ragemod.data.LanguageHandler;
import net.rageland.ragemod.config.*;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

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
    private RageMod rm;
    private static RageMod plugin;
    public SL sl;
    public WL wl;
    private LanguageHandler langHandler;
    
    private PluginDescriptionFile pdf = rm.getDescription();
    private Logger log = Bukkit.getLogger();
    
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private Server server; 
    private PluginManager pluginManager;
    public static Permission perms;
    public static Economy econ;
    public Economy economy;
    public Message text;
    
    // Update stuff
    private double currentVersion;
    private double newVersion;
    
    public static String mainDirectory = "plugins/RageMod";
    public File file = new File(mainDirectory + File.separator + "config.yml");
    
    // Global data
    public LotHandler lots;
    public PlayerHandler players;
    public TownHandler towns;
    public TaskHandler tasks;
    public FactionHandler factions;
    public LanguageHandler languages;
    public AllBountyHandler Bounties;
    public ZoneHandler zones;
    
    // Semi-static data and methods
    public RageConfig config;
    public RageDB database;
    public Message message;
    public RageNPCManager npcManager;
    public QuestManager questManager;
    //for now here
    public ZonesConfig zc; 
    
    
    
    public RageMod() 
    {
    	
    }
    
    public static RageMod getInstance() {
    	if(plugin == null)
    		plugin = new RageMod(); 
    	
    	return plugin;
    }
    
    
    public void onEnable() 
    {           
    	plugin = this;
    	checkForUpdates();
    	load();
        initializeVariables();
        registerEvents();        
        setupPermissions();   
        loadDatabaseData();        
        startScheduledTasks();        
        runDebugTests();  
        
        System.out.println("[RAGE] RageMod v" + pdf.getVersion() + " is enabled!");
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
        System.out.println("[RAGE] Goodbye world!");
    }
        
    public Configuration load(){
        try {
            Configuration config = new Configuration(plugin, "config.yml");
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
    
    public void checkForUpdates() {
   	try {
            newVersion = updateCheck(currentVersion);
            
            if (newVersion > currentVersion) {
                log.warning("[RageMod] RageMod " + newVersion + " is released! You are running RageMod " + currentVersion);  	
                log.warning("[RageMod] Update RageMod at: http://dev.bukkit.org/server-mods/ragemod/files");
            }	  	
        } catch (Exception e) {
	  	
            // Ignore exceptions like a bawws!
	  	
        }
	  	
  }	  	
	  	
    private double updateCheck(double currentVersion) throws Exception {
	  	
    	String pluginUrlString = "http://dev.bukkit.org/server-mods/ragemod/files.rss";	  	
        try {	  	
              URL url = new URL(pluginUrlString);	
              Document docu = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());	  	
              docu.getDocumentElement().normalize();	  	
              NodeList nodes = docu.getElementsByTagName("item");	  	
              Node node = nodes.item(0);
              
              if (node.getNodeType() == 1) {	  	
                  Element firstEle = (Element)node;	  	
                  NodeList firstElementTagName = firstEle.getElementsByTagName("title");	  	
                  Element ele = (Element) firstElementTagName.item(0);	  	
                  NodeList firstNodes = ele.getChildNodes();	  	
                  return Double.valueOf(firstNodes.item(0).getNodeValue().replace("RageMod", "").replaceFirst(".", "").trim());	  	
       		}	  	
        } catch (Exception localException) {
        	// Ignore exceptions
        }	  	
    	return currentVersion;	  	
    }   

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    
    private boolean setupPermissions() {
    	RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    private void runDebugTests()
    {
    	System.out.println("Number of lots:" + lots.getAll().size());    	
    }
    
    private void registerEvents()
    {
        pluginManager.registerEvents(serverListener, this);        
        pluginManager.registerEvents(playerListener, this);        
        pluginManager.registerEvents(blockListener, this);      
        pluginManager.registerEvents(entityListener, this);
    }
    
    private void initializeVariables()
    {
    	serverListener = new RMServerListener(this);
    	playerListener = new RMPlayerListener(this);
    	blockListener = new RMBlockListener(this);  
    	entityListener = new RMEntityListener(this);
    	config = new RageConfig(this);
        database = new RageDB(this, config);
        
        lots = new LotHandler(this);
        players = new PlayerHandler(this);
        towns = new TownHandler(this);
        tasks = new TaskHandler(this);
        factions = new FactionHandler(this);
        languages = new LanguageHandler(this);
        
    	server = this.getServer();
    	zones = new ZoneHandler(this);
        pluginManager = server.getPluginManager();
        npcManager = new RageNPCManager(this);
        questManager = new QuestManager();
        message = new Message(this);
        langHandler = new LanguageHandler(this);
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

