package net.rageland.ragemod.commands;

import java.util.ArrayList;

import net.rageland.ragemod.Build;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.text.Message;
import net.rageland.ragemod.utilities.Util;
import net.rageland.ragemod.world.Town;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class DebugCommands 
{
	
	private RageMod plugin;
	int level;
	
	public DebugCommands(RageMod plugin) 
	{
		this.plugin = plugin;
	}
	
	public void onRmdebugCommand(Player player, PlayerData playerData, String[] split) {
		
		if (split.length < 2) {
			if( split.length < 2 ) {
				plugin.message.parse(player, "Debug commands: <required> [optional]");	  	
				if( true )
					plugin.message.parse(player, "   /rmdebug colors   (displays all chat colors)");
				if( true )
					plugin.message.parse(player, "   /rmdebug donation  (displays amount of donations)");
				if( false )
					plugin.message.parse(player, "   /rmdebug sanctum <level> (attempts to build sanctum floor)");
				if( true )
					plugin.message.parse(player, "   /rmdebug setlang <1-4> <level> (sets your language skill)");	  	
				if( true )
					plugin.message.parse(player, "   /rmdebug tptown <town_name> (teleports to town)");	
				if( true )
					plugin.message.parse(player, "   /rmdebug translate [text] (translates the entered text)");
				if( true )
					plugin.message.parse(player, "   /rmdebug transcast <#> <text> (translates/broadcasts text)");  	
			} else if( split[1].equalsIgnoreCase("colors") ) {
				this.colors(player);
			} else if( split[1].equalsIgnoreCase("donation") ) {
				this.donation(player);
			} else if( split[1].equalsIgnoreCase("sanctum") ) {
				if( split.length == 3 )
					this.sanctum(player, split[2]); 
				else
					plugin.message.parse(player, "Usage: /debug sanctum <level>"); 
			} else if( split[1].equalsIgnoreCase("setlang") ) {
				      if( split.length == 4 )
				        this.setlang(player, split[2], split[3]); 		  	
				      else
				      {
				    	  plugin.message.parse(player, "Usage: /rmdebug setlang <1-4> <level>"); 
				    	  plugin.message.send(player, "   Languages: 1-Creeptongue, 2-Gronuk, 3-Benali, 4-Avialese");
				      }  	
			} else if( split[1].equalsIgnoreCase("tptown") ) {
				if( split.length == 3 )
					this.towntp(player, split[2]); 
				else
					plugin.message.parse(player, "Usage: /rmdebug tptown <town_name>"); 
			} else
				plugin.message.parse(player, "Type /rmdebug to see a list of available commands.");
		}
	}
	
	@SuppressWarnings("static-access")
	public void setlang(Player player, String langString, String levelString) {
		PlayerData playerData = plugin.players.get(player.getName());	
		int id_Language;
		int level;
		
		try {
			id_Language = Integer.parseInt(langString);
			if (id_Language < 1 || id_Language > 4)
				throw new Exception();
		} catch (Exception ex) {
			plugin.message.sendNo(player, "Invalid language, languages range from ID 1 - 4");
			return;
		}
		
		try {  	
			level = Integer.parseInt(levelString);  	
		    
			if( level < 0 || level > 100 )  	
		        throw new Exception();
		} catch( Exception ex ) {
			plugin.message.sendNo(player, "Invalid skill level (0-100).");
		    return;
		}
		
		playerData.setLanguageSkill(id_Language, level);
	    playerData.update();  	
	    
	    plugin.message.send(player, "Your " + plugin.message.LANGUAGE_NAME_COLOR + plugin.config.NPC_LANGUAGE_NAMES.get(id_Language) + 
		plugin.message.DEFAULT_COLOR + " skill is now " + ChatColor.WHITE + level + ".");
	    
	}

	// /debug colors
	public void colors(Player player) 
	{
		player.sendMessage(ChatColor.DARK_GRAY + "Dark Gray: Player (Tourist)");
		player.sendMessage(ChatColor.GRAY + "Gray: Player (Neutral)");
		player.sendMessage(ChatColor.WHITE + "White: Town (Neutral), Player (Merchant)");
		player.sendMessage(ChatColor.YELLOW + "Yellow: Player (Admin)");
		player.sendMessage(ChatColor.GOLD + "Gold: Treasury messages, Player (Owner)");
		player.sendMessage(ChatColor.RED + "Red: Town (Red), Player (Red)");
		player.sendMessage(ChatColor.DARK_RED + "Dark Red: Negative messages");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Light Purple: Battle messages");
		player.sendMessage(ChatColor.DARK_PURPLE + "Dark Purple: Important battle messages");
		player.sendMessage(ChatColor.BLUE + "Blue: Player (Blue)");
		player.sendMessage(ChatColor.DARK_BLUE + "Dark Blue: Unused (illegible)  :(");
		player.sendMessage(ChatColor.AQUA + "Aqua: NPC names, NPC towns");
		player.sendMessage(ChatColor.DARK_AQUA + "Dark Aqua: NPC speech, Quest info");
		player.sendMessage(ChatColor.GREEN + "Green: Ragemod messages");
		player.sendMessage(ChatColor.DARK_GREEN + "Dark Green: Important messages, Broadcasts, Player (Moderator)");
		
	}
	
	// /debug donation
	public void donation(Player player) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		int donation = plugin.database.playerQueries.getRecentDonations(playerData.id_Player);
		
		plugin.message.send(player, "The database records you with a total donation of $" + donation + " in the last month.");
	}

	// /debug sanctum <level>
	public void sanctum(Player player, String levelString) 
	{
		if (RageMod.perms.has(player, "ragemod.admin.debug")) {
		//plugin.message.sendNo(player, "This command has been disabled.");
		//return;

		World world = player.getWorld();
		PlayerData playerData = plugin.players.get(player.getName());
		
		try
		{
			level = Integer.parseInt(levelString);
		}
		catch( Exception ex )
		{
			plugin.text.messageNo(player, "Invalid level.");
			return;
		}
		
		if( level < 1 || level > 5 )
		{
			plugin.text.messageNo(player, "Invalid level.");
			return;
		}
		
		// Pinpoint the top-left corner
		int cornerX = (int)player.getLocation().getX() - 2; 
		int cornerY = (int)player.getLocation().getY() - 1;
		int cornerZ = (int)player.getLocation().getZ() - 10; 
		
		Build.sanctumFloor(plugin, world, cornerX, cornerY, cornerZ, level, playerData.id_Faction);	
		
		} else {
			plugin.message.parse(player, plugin.noPerms);
		}
	}
	
	
	// Teleports to selected town
	public void towntp(Player player, String townName)
	{
		
		if (RageMod.perms.has(player, "ragemod.admin.debug")) {
		
		Town town = plugin.towns.get(townName);
		
		// Check to see if specified town exists
		if( town == null )
		{
			plugin.message.sendNo(player, "The town '" + townName + "' does not exist.");
			return;
		}
		
		// Teleport to the town
		plugin.message.send(player, "Teleporting...");
		player.teleport(Util.findTeleportLocation(town.centerPoint));
		
		} else {
			plugin.message.parse(player, plugin.noPerms);
		}
	}
	
	public void langs(Player player){
		player.sendMessage(this.plugin.languages.names());
	}
}
