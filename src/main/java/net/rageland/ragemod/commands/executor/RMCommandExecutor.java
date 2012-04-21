package net.rageland.ragemod.commands.executor;

import java.util.logging.Logger;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.commands.BountyCommands;
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
				if (split[0] == "teach") {
					langCmds.teach(player, split[1], split[2], split[3]);
				} else if (split[0] == "addword") {
					langCmds.addword(player, split[1], split[2], split[3]);
				} else if (split[0] == "removeword") {
					
				} else if (split[0] == "translate" || split[0] == "t") {
					
				} else if (split[0] == "create") {
					
				} else if (split[0] == "wg" || split[0] == "wordgen") {
					
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "Not a valid RageMod language command!");
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
			}
		} else if (sender instanceof ConsoleCommandSender) {
			final Logger log = Bukkit.getLogger();
			log.info("[RAGE] ======= RageMod Console Command Handling Sevice ======= [RAGE]");
			log.warning("[RAGE] We here at RageModDevs do not support console command usage in RageMod as of yet. RageMod will now check for updates in case of a later version which does having been released.");
			log.info("[RAGE] ======= RageMod Console Command Handling Sevice ======= [RAGE]");
			plugin.checkForUpdates();
		}
		return false;
	}

}
