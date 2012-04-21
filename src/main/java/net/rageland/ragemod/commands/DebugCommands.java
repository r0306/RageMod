package net.rageland.ragemod.commands;

import java.util.ArrayList;

import net.rageland.ragemod.Build;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;
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
