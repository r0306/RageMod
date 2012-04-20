package net.rageland.ragemod.listener;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.commands.*;
import net.rageland.ragemod.entity.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class RMChatListener implements Listener, CommandExecutor {
    private RageMod plugin;
    private QuestCommands questCommands;
    private CompassCommands compassCommands;
    private LotCommands lotCommands;
    private TownCommands townCommands;
    private FactionCommands factionCommands;
    private DebugCommands debugCommands;
    private Commands commands;
    private NPCCommands npcCommands;
    private NPCTownCommands npcTownCommands;
    private PermitCommands permitCommands;
    private LanguageCommands langCommands;
    private BountyCommands bountyCommands;
    
    public RMChatListener(RageMod Plugin){
        questCommands = new QuestCommands(plugin);
        compassCommands = new CompassCommands(plugin);
        lotCommands = new LotCommands(plugin);
        townCommands = new TownCommands(plugin);
        factionCommands = new FactionCommands(plugin);
        debugCommands = new DebugCommands(plugin);
        commands = new Commands(plugin);
        npcCommands = new NPCCommands(plugin);
        npcTownCommands = new NPCTownCommands(plugin);
        permitCommands = new PermitCommands(plugin);
        langCommands = new LanguageCommands(plugin);
    }
	
    private boolean check(int num,String[] args,CommandSender sender){
    	if (args.length > num) {
            sender.sendMessage(ChatColor.RED + "Too many arguments!");
            return false;
         } 
         if (args.length < num) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments!");
            return false;
         }
         return true;
    }
    private boolean checkmin(int num, String[] args, CommandSender sender) {
        if (args.length < num) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments!");
            return false;
         }
         return true;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(PlayerCommandPreprocessEvent event) 
    {
    	Player player = event.getPlayer();
    	this.plugin.chat.ChatEvent(player.getName(), event.getMessage());
    	event.setCancelled(true);	
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player) {
			Player player = null;
			player = (Player) sender;
	    	PlayerData playerData = plugin.players.get(player.getName());
	    switch(cmd.getName().toLowerCase()){
	    	case "bounty": case "bty":{
	    		if(checkmin(1,args,sender))
	    		switch(args[0]){
	    		case "addsign":		this.bountyCommands.addSignIssue(player, args);
	    		case "add":			if(check(3,args,sender))this.bountyCommands.addBounty(player, args[2], args[1],playerData.townName);break;
	    		case "addcity":		if(check(4,args,sender))this.bountyCommands.addBounty(player, args[2], args[1],args[3]);break;
	    		case "addglobal":	if(check(3,args,sender))this.bountyCommands.addGlobalBounty(player, args[2], args[1]);break;
	    		case "remove":		if(check(4,args,sender) && args[3] == "all")this.bountyCommands.removeAllBountys(player, args[2]);break;
	    		case "cancel":		this.bountyCommands.resultBlockIssue(player);
	    		case "show": 		if(check(2,args,sender) && args[1] == "all"){//TODO add show for all break;
	    							}else if(check(2,args,sender)&& this.bountyCommands.knowsCity(args[1])){
	    							 //show for city break;
	    							}else{
	    								//show for city player is in break;
	    							}
	    		}
	    		return true;
	    	}
	    	case "chat": case "c":{
	    		if(checkmin(1,args,sender))
	    		switch(args[0]){
	    		case "global":		this.bountyCommands.addSignIssue(player, args);
	    		case "local":		if(check(3,args,sender))this.bountyCommands.addBounty(player, args[2], args[1],playerData.townName);break;
	    		case "whisper":		if(check(4,args,sender))this.bountyCommands.addBounty(player, args[2], args[1],args[3]);break;
	    		case "zone":		if(check(3,args,sender))this.bountyCommands.addGlobalBounty(player, args[2], args[1]);break;
	    		}
	    		return true;
	    	}
	    	case "language": case "lang":{
	    		if(checkmin(1,args,sender)){
	    		switch(args[0]){
	    		case "teach": 		if(check(4,args,sender))this.langCommands.teach(player, args[1], args[2], args[3]); break;
	    		case "addword":		if((check(4,args,sender)))this.langCommands.addword(player, args[1], args[2], args[3]);	break;	 
	    		case "removeword":  //TODO needs to be added
	    		case "translate":	//TODO translate the word/text
	    		case "t":			//TODO translate the word
	    		case "create": 		//TODO create a Language
	    		case "wg":			//TODO create the wordgen and fill it
	    		}
	    		}else this.langCommands.skill(player); 
	    		return true;
	    		
	    	}
	    	case "compass": case "com":{
	    		if(checkmin(1,args,sender))
	    		switch(args[0]){
	    		case "lot":			if(check(2,args,sender))this.compassCommands.lot(player, args[1]);break;
	    		case "town":		if(check(2,args,sender))this.compassCommands.town(player, args[1]);break;
	    		case "htown":		this.compassCommands.town(player, playerData.townName);break;
	    		case "spawn":		this.compassCommands.spawn(player);break;
	    		}
	    		return true;
	    	}
	    	case "lot":{
	    		if(checkmin(1,args,sender))
	    		switch(args[0]){
	    		case "assign":		if(check(3,args,sender))this.lotCommands.assign(player, args[1], args[2]);break;
	    		case "allow":		if(check(2,args,sender))this.lotCommands.allow(player, args[1]);break;
	    		case "check":		this.lotCommands.check(player);break;
	    		case "evict":		if(check(2,args,sender))this.lotCommands.evict(player, args[1]);break;
	    		case "unclaim":		if(check(2,args,sender))this.lotCommands.unclaim(player, args[1]);break;
	    		case "claim":		if(check(2,args,sender))this.lotCommands.claim(player, args[1]);break;
	    		}
	    		return true;
	    	}
	    	case "rmdebug": case "rmd":{
	    		if(checkmin(1,args,sender))
	    		switch(args[0]){
	    		case "colors":		this.debugCommands.colors(player);break;
	    		case "donation":	this.debugCommands.donation(player);break;
	    		case "tptown":		if(check(2,args,sender))this.debugCommands.towntp(player, args[1]);break;
	    		case "tphtown":     this.debugCommands.towntp(player, playerData.townName);break;
	    		case "langs" :		this.debugCommands.langs(player);break;
	    		case "languages" :	this.debugCommands.langs(player);break;
	    		}
	    		return true;
	    	}
	    	case "npctown": case "npct":{
	    		if(checkmin(1,args,sender))
	    		switch(args[0]){
	    		case "allow":		if(check(2,args,sender))this.npcTownCommands.allow(player,args[1]);break;
	    		case "create":		if(check(4,args,sender))this.npcTownCommands.create(player,args[1],args[2],args[3]);break;
	    		case "disallow":	if(check(2,args,sender))this.npcTownCommands.disallow(player, args[1]);break;
	    		case "list":     	if(check(2,args,sender))this.npcTownCommands.list(player, args[1]);break;
	    		case "info" :		if(check(2,args,sender))this.npcTownCommands.info(player,args[1]);break;
	    		case "newloc" :		this.npcTownCommands.newloc(player);break;
	    		case "setrace": 	//race
	    		case "resident": 	if(check(3,args,sender))this.npcTownCommands.resident(player, args[1], args[2]);break;
	    		case "setsteward":	if(check(3,args,sender))this.npcTownCommands.setsteward(player, args[1], args[2]);break;
	    		}
	    		return true;
	    	}
	    	case "permit":{
	    		if(checkmin(1,args,sender))this.permitCommands.capitol(player, player.getName());
	    		if(checkmin(2,args,sender))this.permitCommands.capitol(player, args[1]);
	    		return true;
	    	}
	    	case "affinity": case "aff":{
	    		commands.affinity(player);
	    		return true;	
	    	}
	    	case "speak": case "s":{
	    		if(checkmin(1,args,sender)){this.langCommands.speak(player,args[0]);
	    		}else{ 						this.langCommands.speak(player,args);}
	    		return true;
	    	}
	    	
	    }
		return false;
		}
		return false;
	}

}

