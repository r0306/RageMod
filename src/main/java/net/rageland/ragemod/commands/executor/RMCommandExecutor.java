package net.rageland.ragemod.commands.executor;

import java.util.logging.Logger;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.commands.BountyCommands;
import net.rageland.ragemod.commands.Commands;
import net.rageland.ragemod.commands.CompassCommands;
import net.rageland.ragemod.commands.DebugCommands;
import net.rageland.ragemod.commands.LanguageCommands;
import net.rageland.ragemod.commands.LotCommands;
import net.rageland.ragemod.commands.NPCCommands;
import net.rageland.ragemod.commands.NPCTownCommands;
import net.rageland.ragemod.commands.PermitCommands;
import net.rageland.ragemod.commands.QuestCommands;
import net.rageland.ragemod.commands.RageCommands;
import net.rageland.ragemod.commands.TownCommands;
import net.rageland.ragemod.entity.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class RMCommandExecutor implements CommandExecutor {

	private RageMod plugin;
	private RMCommandExecutor rmCmdExec;
	private CommandSender sender;
	private Player player = (Player) sender;
	private PlayerData pd = plugin.players.get(player.getName());
	private Commands cmds;
	private DebugCommands dbCmds;
	private LotCommands lCmds;
	private LanguageCommands langCmds;
	private NPCCommands npcCmds;
	private NPCTownCommands npcTCmds;
	private PermitCommands permitCmds;
	private RageCommands rCmds;
	private QuestCommands qCmds;
	private BountyCommands boCmds;
	private TownCommands tCmds;
	private CompassCommands compCmds;
	private CommandExecutor cmdExec;
	
	public RMCommandExecutor(RageMod plugin) {
		this.plugin = plugin;
		
		dbCmds = new DebugCommands(plugin);
		lCmds = new LotCommands(plugin);
		langCmds = new LanguageCommands(plugin);
		npcCmds = new NPCCommands(plugin);
		npcTCmds = new NPCTownCommands(plugin);
		permitCmds = new PermitCommands(plugin);
		rCmds = new RageCommands(plugin);
		qCmds = new QuestCommands(plugin);
		boCmds = new BountyCommands(plugin);
		tCmds = new TownCommands(plugin);
		compCmds = new CompassCommands(plugin);
		cmds = new Commands(plugin);
	}
	
	private boolean check(int num, String[] args, CommandSender sender){
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
	
	public RMCommandExecutor get() {
		if (rmCmdExec == null) {
			rmCmdExec = new RMCommandExecutor(plugin);
		}
		return rmCmdExec;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String args, String[] split) {	
		if (sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase("rmdebug")) {
				dbCmds.onRmdebugCommand(player, pd, split);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("lot")) {
				lCmds.onLotCommand(player, pd, split);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("town")) {
				tCmds.onTownCommand(player, pd, split);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("npctown")) {
				npcTCmds.onNPCTownCommand(player, pd, split);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("npc")) {
				npcCmds.onNPCCommand(player, pd, split);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("compass")) {
				compCmds.onCompassCommand(player, pd, split);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("bounty")) {
				boCmds.onBountyCommand(player, pd, split);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("language") || cmd.getName().equalsIgnoreCase("lang")) {
				if (checkmin(1, split, sender)) {
					if (split[0] == "teach") {
						 if (check(4,split,sender)) {
							 langCmds.teach(player, split[1], split[2], split[3]);
							 return true;
						 } else {
							 return false;
						 }
					} else if (split[0] == "addword") {
						if (check(4, split, sender)) {
							langCmds.addword(player, split[1], split[2], split[3]);
							return true;
						} else {
							return false;
						}
					} else if (split[0] == "removeword") {

						return true;
					} else if (split[0] == "translate" || split[0] == "t") {

						return true;
					} else if (split[0] == "create") {
						
						return true;
					} else if (split[0] == "wg" || split[0] == "wordgen") {

						return true;
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "Not a valid RageMod language command!");
						return false;
					}
				}
				return true;
			} else if (cmd.getName().equalsIgnoreCase("quest")) {
				qCmds.onQuestCommand(player, pd, split);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("permit")) {
				permitCmds.onCommand(player, pd, split);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("rage")) {
				rCmds.onCommand(player, pd, split);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("home")) {
				cmds.home(player, split[0]);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("spawn")) {
				cmds.spawn(player, split[0]);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("affinity") || cmd.getName().equalsIgnoreCase("aff")) {
				cmds.affinity(player);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("chat") || cmd.getName().equalsIgnoreCase("c")) {
				
				return true;
			} else if (cmd.getName().equalsIgnoreCase("speak") || cmd.getName().equalsIgnoreCase("s")) {
				langCmds.speak(player, split[0]);
				return true;
			} else if (cmd.getName().equalsIgnoreCase("ragemod") || cmd.getName().equalsIgnoreCase("rm")) {
				cmds.ragemod(player);
				return true;
			} else {
				if (cmdExec.onCommand(sender, cmd, args, split) == false) {
					player.sendMessage(ChatColor.DARK_RED + "That command does not exist!");
					return true;
				}
			}
		} else if (sender instanceof ConsoleCommandSender) {
			final Logger log = Bukkit.getLogger();
			log.info("[RAGE] RageMod does not support console commands as of yet.");
			plugin.checkForUpdates();
		}
		return false;
	}

}
