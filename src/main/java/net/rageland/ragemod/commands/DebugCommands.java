package net.rageland.ragemod.commands;

import java.util.ArrayList;

import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.data.Factions;
import net.rageland.ragemod.data.PlayerData;
import net.rageland.ragemod.data.Players;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class DebugCommands 
{
	
	private RageMod plugin;
	
	public DebugCommands(RageMod plugin) 
	{
		this.plugin = plugin;
	}
	
	public void onDebugCommand(Player player, PlayerData playerData, String[] split) 
	{
		if( split.length < 2 || split.length > 3 )
		{
			plugin.text.message(player, "Debug commands: <required> [optional]");
			if( true )
				plugin.text.message(player, "   /debug colors   (displays all chat colors)");
			if( true )
				plugin.text.message(player, "   /debug donation  (displays amount of donations)");
			if( true )
				plugin.text.message(player, "   /debug sanctum <level> (attempts to build sanctum floor)");
		}
		else if( split[1].equalsIgnoreCase("colors") )
		{
			this.colors(player);
		}
		else if( split[1].equalsIgnoreCase("donation") )
		{
			this.donation(player);
		}
		else if( split[1].equalsIgnoreCase("sanctum") )
		{
			if( split.length == 3 )
				this.sanctum(player, split[2]); 
			else
    			plugin.text.message(player, "Usage: /debug sanctum <level>"); 
		}
		else
			plugin.text.message(player, "Type /debug to see a list of available commands.");
	}
	
	// /debug colors
	public void colors(Player player) 
	{
		player.sendMessage(ChatColor.DARK_GRAY + "Dark Gray: Player (Tourist)");
		player.sendMessage(ChatColor.GRAY + "Gray: Player (Neutral)");
		player.sendMessage(ChatColor.WHITE + "White: Town (Neutral), Player (Neutral Member)");
		player.sendMessage(ChatColor.YELLOW + "Yellow: Player (Admin)");
		player.sendMessage(ChatColor.GOLD + "Gold: Player (Owner)");
		player.sendMessage(ChatColor.RED + "Red: Player (Red Faction)");
		player.sendMessage(ChatColor.DARK_RED + "Dark Red: Town (Red Faction), Player (Red Member)");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Light Purple: Battle messages");
		player.sendMessage(ChatColor.DARK_PURPLE + "Dark Purple: Important battle messages");
		player.sendMessage(ChatColor.BLUE + "Blue: Player (Blue Faction)");
		player.sendMessage(ChatColor.DARK_BLUE + "Dark Blue: Town Name (Blue Faction), Player (Blue Member)");
		player.sendMessage(ChatColor.AQUA + "Aqua: NPC quests/shops/etc. ");
		player.sendMessage(ChatColor.DARK_AQUA + "Dark Aqua: NPC speech");
		player.sendMessage(ChatColor.GREEN + "Green: Ragemod messages");
		player.sendMessage(ChatColor.DARK_GREEN + "Dark Green: Important messages, Player (Moderator)");
		
	}
	
	// /debug donation
	public void donation(Player player) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		int donation = plugin.database.playerQueries.getRecentDonations(playerData.id_Player);
		
		plugin.text.message(player, "The database records you with a total donation of $" + donation + " in the last month.");
	}

	public void sanctum(Player player, String levelString) 
	{
		int level;
		World world = player.getWorld();
		PlayerData playerData = plugin.players.get(player.getName());
		
		try
		{
			level = Integer.parseInt(levelString);
		}
		catch( Exception ex )
		{
			plugin.text.message(player, "Invalid level.");
			return;
		}
		
		if( level < 1 || level > 5 )
		{
			plugin.text.message(player, "Invalid level.");
			return;
		}
		
		// Pinpoint the top-left corner
		int cornerX = (int)player.getLocation().getX() - 2; // - 10;
		int cornerY = (int)player.getLocation().getY() - 1;
		int cornerZ = (int)player.getLocation().getZ() - 2; // - 10;
		Block currentBlock;
		
		ArrayList<String> layout = RageConfig.buildSanctumFloor(level);
		
	    //		c: Cobblestone
	    //		d: Dirt
	    //		p: Wood Planks
	    //		s: Stone
	    //		t: Tile (slab)
	    //		o: Obsidian
		//		O: Obsidian stack 2-high w/ glowstone on top
	    //		g: Glowstone
		//		G: Glowstone stack 2-high
	    //		w: Wool (of appropriate color)
	    //		-: Portal (inside)
	    //		|: Portal (outside)
	    //		b: Bedrock
	    //		L: Liquid (water/lava)
	    //		n: Snow
	    //		i: Iron Block
	    //		S: Special faction block (lapis/netherrack)
		for( int x = 0; x < 20; x++ )
		{
			for( int z = 0; z < 20; z++ )
			{
				currentBlock = world.getBlockAt(cornerX + x, cornerY, cornerZ + z);
				
				// Clear the air above the floor
				world.getBlockAt(cornerX + x, cornerY + 1, cornerZ + z).setType(Material.AIR);
				world.getBlockAt(cornerX + x, cornerY + 2, cornerZ + z).setType(Material.AIR);
				world.getBlockAt(cornerX + x, cornerY + 3, cornerZ + z).setType(Material.AIR);
				world.getBlockAt(cornerX + x, cornerY + 4, cornerZ + z).setType(Material.AIR);
				world.getBlockAt(cornerX + x, cornerY + 5, cornerZ + z).setType(Material.AIR);
				
				// Create bedrock below the floor, to hold liquids
				world.getBlockAt(cornerX + x, cornerY - 1, cornerZ + z).setType(Material.BEDROCK);
				world.getBlockAt(cornerX + x, cornerY - 2, cornerZ + z).setType(Material.BEDROCK);
				
				// Set the floor block(s)
				switch( layout.get(x).charAt(z) )
				{
					case 'c':
						currentBlock.setType(Material.COBBLESTONE); break;
					case 'd':
						currentBlock.setType(Material.DIRT); break;
					case 'p':
						currentBlock.setType(Material.WOOD); break;
					case 's':
						currentBlock.setType(Material.STONE); break;
					case 't':
						currentBlock.setType(Material.DOUBLE_STEP); break;
					case 'o':
						currentBlock.setType(Material.OBSIDIAN); break;
					case 'O':
						currentBlock.setType(Material.OBSIDIAN);
						world.getBlockAt(cornerX + x, cornerY + 1, cornerZ + z).setType(Material.OBSIDIAN);
						world.getBlockAt(cornerX + x, cornerY + 2, cornerZ + z).setType(Material.OBSIDIAN);
						world.getBlockAt(cornerX + x, cornerY + 3, cornerZ + z).setType(Material.OBSIDIAN);
						break;
					case 'g':
						currentBlock.setType(Material.GLOWSTONE); break;
					case 'G':
						currentBlock.setType(Material.GLOWSTONE); 
						world.getBlockAt(cornerX + x, cornerY + 1, cornerZ + z).setType(Material.GLOWSTONE);
						break;
					case 'w':
						currentBlock.setType(Material.WOOL); 
						currentBlock.setData((byte) Byte.valueOf(String.valueOf(plugin.factions.getWoolColor(playerData.id_Faction))));
						break;
					case '-':
						currentBlock.setType(Material.OBSIDIAN); 
						world.getBlockAt(cornerX + x, cornerY + 1, cornerZ + z).setType(Material.PORTAL);
						world.getBlockAt(cornerX + x, cornerY + 2, cornerZ + z).setType(Material.PORTAL);
						world.getBlockAt(cornerX + x, cornerY + 3, cornerZ + z).setType(Material.PORTAL);
						world.getBlockAt(cornerX + x, cornerY + 4, cornerZ + z).setType(Material.OBSIDIAN);
						break;
					case '|':
						currentBlock.setType(Material.OBSIDIAN); 
						world.getBlockAt(cornerX + x, cornerY + 1, cornerZ + z).setType(Material.OBSIDIAN);
						world.getBlockAt(cornerX + x, cornerY + 2, cornerZ + z).setType(Material.OBSIDIAN);
						world.getBlockAt(cornerX + x, cornerY + 3, cornerZ + z).setType(Material.OBSIDIAN);
						world.getBlockAt(cornerX + x, cornerY + 4, cornerZ + z).setType(Material.OBSIDIAN);
						break;
					case 'b':
						currentBlock.setType(Material.BEDROCK); break;
					case 'L':
						currentBlock.setType(Material.GLASS);
						world.getBlockAt(cornerX + x, cornerY - 1, cornerZ + z).setType(plugin.factions.getLiquidBlock(playerData.id_Faction));
						break;
					case 'n':
						currentBlock.setType(Material.SNOW_BLOCK); break;
					case 'i':
						currentBlock.setType(Material.IRON_BLOCK); break;
					case 'S':
						currentBlock.setType(plugin.factions.getSpecialBlock(playerData.id_Faction)); break;
						
						
						
				}
			}
		}
		

		

		
	}

}
