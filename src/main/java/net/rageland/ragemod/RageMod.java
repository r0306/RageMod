package net.rageland.ragemod;

import java.io.File;
import java.util.HashMap;
import java.util.List;
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
import net.rageland.ragemod.data.ChatHandler;
import net.rageland.ragemod.data.FactionHandler;
import net.rageland.ragemod.data.LanguageHandler;
import net.rageland.ragemod.data.LotHandler;
import net.rageland.ragemod.data.PlayerHandler;
import net.rageland.ragemod.data.RaceHandler;
import net.rageland.ragemod.data.TownHandler;
import net.rageland.ragemod.data.TaskHandler;
import net.rageland.ragemod.data.ZoneHandler;
import net.rageland.ragemod.database.RageDB;
import net.rageland.ragemod.listener.RMBlockListener;
import net.rageland.ragemod.listener.RMChatListener;
import net.rageland.ragemod.listener.RMEntityListener;
import net.rageland.ragemod.listener.RMLoginListener;
import net.rageland.ragemod.listener.RMPlayerListener;
import net.rageland.ragemod.listener.RMServerListener;
import net.rageland.ragemod.npcentities.RageNPCManager;
import net.rageland.ragemod.npcentities.SL;
import net.rageland.ragemod.npcentities.WL;
import net.rageland.ragemod.quest.QuestManager;
import net.rageland.ragemod.text.Message;
import net.rageland.ragemod.commands.executor.RMCommandExecutor;
import net.rageland.ragemod.config.*;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

/**
 * RageMod for Bukkit
 *
 * @author TheIcarusKid
 * @author PandazNWafflez
 * @author Perdemot
 */

public class RageMod extends JavaPlugin {
	
    private RMPlayerListener playerListener;
    private RMBlockListener blockListener;
    private RMServerListener serverListener;
    private RMEntityListener entityListener;
    private RMChatListener chatListener;
	private RMLoginListener loginListener;
    private RageMod rm;
    private RMCommandExecutor exec = new RMCommandExecutor(this);
    private static RageMod plugin;
    public SL sl;
    public WL wl;
    
    private PluginDescriptionFile pdf = rm.getDescription();
    private Logger log = Bukkit.getLogger();
    
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private Server server; 
    private PluginManager pluginManager;
    public static Permission perms;
    public static Economy econ;
    public Economy economy;
    public Message text;
	public String noPerms = ChatColor.DARK_RED + "You don't have permission to do that!";
    
    // Update stuff
    private double currentVersion;
    private double newVersion;
    
    // Config stuff
    public static String mainDirectory = "plugins/RageMod";
    public File file = new File(mainDirectory + File.separator + "ragemod.yml");
    
    // Global data
    public LotHandler lots;
    public PlayerHandler players;
    public TownHandler towns;
    public TaskHandler tasks;
    public RaceHandler races;
    public FactionHandler factions;
    public LanguageHandler languages;
    public AllBountyHandler Bounties;
    public ZoneHandler zones;
    public ChatHandler chat;
    
    // Semi-static data and methods
    public RageDB database;
    public Message message;
    public RageNPCManager npcManager;
    public QuestManager questManager;
    
    // Config stuff
    public ZonesConfig zc;
    public RageConfig config; 
    public WarZoneConfig wzConfig;
    public FactionConfig fConfig;
    public Configuration configuration;

    
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
        checkDependencies();   
    	load();  
    	checkForUpdates();
        initializeVariables();
    	initializeCommands();
        registerEvents();        
        setupPermissions();        
        startScheduledTasks();        
        runDebugTests();
        loadDatabaseData();
        
        System.out.println("[RAGE] RageMod v" + pdf.getVersion() + " is enabled!");
    }
    
    private List<String> speakAliases;
    private List<String> chatAliases;
    private List<String> bountyAliases;
    private List<String> npcTownAliases;
    private List<String> permitAliases;
    
    public void initializeCommands() {
    	
    	speakAliases.add("s");
    	chatAliases.add("c");
    	bountyAliases.add("bty");
    	bountyAliases.add("bnty");
    	npcTownAliases.add("npct");
    	permitAliases.add("permits");
    	permitAliases.add("perm");
    	
    	getCommand("speak").setAliases(speakAliases);
    	getCommand("chat").setAliases(chatAliases);
    	getCommand("bounty").setAliases(bountyAliases);
    	getCommand("npctown").setAliases(npcTownAliases);
    	getCommand("permit").setAliases(permitAliases);
    	
    	getCommand("home").setExecutor(exec);
    	getCommand("spawn").setExecutor(exec);
    	getCommand("affinity").setExecutor(exec);
    	getCommand("npctown").setExecutor(exec);
    	getCommand("npc").setExecutor(exec);
    	getCommand("quest").setExecutor(exec);
    	getCommand("lot").setExecutor(exec);
    	getCommand("bounty").setExecutor(exec);
    	getCommand("town").setExecutor(exec);
    	getCommand("rage").setExecutor(exec);
    	getCommand("permit").setExecutor(exec);
    	getCommand("language").setExecutor(exec);
    	getCommand("speak").setExecutor(exec);
    	getCommand("chat").setExecutor(exec);
    	getCommand("rmdebug").setExecutor(exec);
    }
    
    public void checkDependencies() {
    	
    	PluginManager pm = getServer().getPluginManager();
    	Plugin v = pm.getPlugin("Vault");
    	Plugin s = pm.getPlugin("Spout");
    	Plugin c = pm.getPlugin("Citizens");
    	
    	if (v != null) {
    		log.info("[RAGE] Found Vault!");
    		if (s != null) {
    			log.info("[RAGE] Found Spout!");
    			if (c != null) {
    				log.info("[RAGE] Found Citizens");
    			} else {
    				log.severe("[RAGE] Citizens not found! Disabling");
    				pm.disablePlugin(this);
    			}
    		} else {
    			log.severe("[RAGE] Spout was not found! Disabling!");
    			pm.disablePlugin(this);
    		}
    	} else {
    		log.severe("[RAGE] Vault not found! Disabling!");
    		pm.disablePlugin(this);
    	}
    }
    
    public void creatingNPCTemp() { /*
    	this.npcManager.spawnNPC("TraderNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 96.0D), "1", RageNPCManager.TRADERNPC);
		this.npcManager.spawnNPC("QuestStartNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 94.0D), "2", RageNPCManager.QUESTSTARTNPC);
		this.npcManager.spawnNPC("QuestEndNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 92.0D), "3", RageNPCManager.QUESTENDNPC);
		this.npcManager.spawnNPC("QuestNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D,90.0D), "4", 3);
		this.npcManager.spawnNPC("RewardNPC", new Location(getServer().getWorld("world"), -10.0D, 64.0D, 88.0D), "5", 5);
		RageNPCManager.npcs.get("5").setQuest((Quest) QuestManager.squests.get(Integer.valueOf(1)));
		*/
    }
    
    public void onDisable() {    
    	//npcManager.despawnAll(true);
        System.out.println("[RAGE] RageMod disabling!");
    }
        
    public Configuration load() {
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
            // Ignore exceptions 	
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
        pluginManager.registerEvents(chatListener, this);
        pluginManager.registerEvents(loginListener, this);
    }
    
    private void initializeVariables()
    {
    	serverListener = new RMServerListener(this);
    	playerListener = new RMPlayerListener(this);
    	blockListener = new RMBlockListener(this);  
    	entityListener = new RMEntityListener(this);
    	chatListener = new RMChatListener(this);
    	loginListener = new RMLoginListener(this);
    	config = new RageConfig(this);
        database = new RageDB(this, config);
        
        lots = new LotHandler(this);
        players = new PlayerHandler(this);
        towns = new TownHandler(this);
        tasks = new TaskHandler(this);
        factions = new FactionHandler(this);
        languages = new LanguageHandler(this);
        
    	server = getServer();
    	zones = new ZoneHandler(this);
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

