package net.rageland.ragemod;

import java.util.ArrayList;

import net.rageland.ragemod.data.FactionHandler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Build 
{
	public static void sanctumFloor(RageMod plugin, World world, int cornerX, int cornerY, int cornerZ, int level, int id_Faction)
	{
		Block currentBlock;
		
		// Store a list of blocks to light with fire to activate portals (this needs to be done last)
		ArrayList<Block> portalBlocks = new ArrayList<Block>();
		
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
				if( level == 1 )
				{
					world.getBlockAt(cornerX + x, cornerY + 1, cornerZ + z).setType(Material.AIR);
					world.getBlockAt(cornerX + x, cornerY + 2, cornerZ + z).setType(Material.AIR);
					world.getBlockAt(cornerX + x, cornerY + 3, cornerZ + z).setType(Material.AIR);
					world.getBlockAt(cornerX + x, cornerY + 4, cornerZ + z).setType(Material.AIR);
					world.getBlockAt(cornerX + x, cornerY + 5, cornerZ + z).setType(Material.AIR);
				}
				
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
						currentBlock.setData((byte) Byte.valueOf(String.valueOf(FactionHandler.getWoolColor(id_Faction))));
						break;
					case '-':
						currentBlock.setType(Material.OBSIDIAN); 
						portalBlocks.add(world.getBlockAt(cornerX + x, cornerY + 1, cornerZ + z));
						world.getBlockAt(cornerX + x, cornerY + 1, cornerZ + z).setType(Material.AIR);	// Any existing portal blocks must be removed
						world.getBlockAt(cornerX + x, cornerY + 2, cornerZ + z).setType(Material.AIR);
						world.getBlockAt(cornerX + x, cornerY + 3, cornerZ + z).setType(Material.AIR);		
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
						world.getBlockAt(cornerX + x, cornerY - 1, cornerZ + z).setType(plugin.factions.getLiquidBlock(id_Faction));
						break;
					case 'n':
						currentBlock.setType(Material.SNOW_BLOCK); break;
					case 'i':
						currentBlock.setType(Material.IRON_BLOCK); break;
					case 'S':
						currentBlock.setType(plugin.factions.getSpecialBlock(id_Faction)); break;
						
				}
			}
		}
		
		// Light the portal blocks, now that all the obsidian is in place
		for( Block portalBlock : portalBlocks )
		{
			portalBlock.setType(Material.FIRE);
			break;		// only one block is needed, trying to light more of them causes the portal to break
		}
	}

	// Checks for any nearby bed blocks and deletes them
	public static void clearNearbyBeds(Location location) 
	{
		Block currentBlock;
		int locX = (int)(location.getX());
		int locY = (int)(location.getY());
		int locZ = (int)(location.getZ());
		
		
		for( int x = locX - 2; x < locX + 2; x++ )
		{
			for( int y = locY - 2; y < locY + 2; y++ )
			{
				for( int z = locZ - 2; z < locZ + 2; z++ )
				{
					currentBlock = location.getWorld().getBlockAt(x, y, z);
					if( currentBlock.getType() == Material.BED_BLOCK )
						currentBlock.setType(Material.AIR);	
				}
			}
		}	
	}
	
	// Builds the corresponding location in the travel zone
	public static void travelNode(Location location, int size, int id_Faction) 
	{
		Block currentBlock;
		int locX = (int)(location.getX());
		int locY = (int)(location.getY());
		int locZ = (int)(location.getZ());
		int offset = (int)(size / 16);			// 1/8 scale for nether, divided by 2 for distance from center
		
		
		for( int x = locX - offset; x < locX + offset; x++ )
		{
			for( int z = locZ - offset; z < locZ + offset; z++ )
			{
				currentBlock = location.getWorld().getBlockAt(x, locY, z);
				currentBlock.setType(Material.WOOL); 
				currentBlock.setData((byte) Byte.valueOf(String.valueOf(FactionHandler.getWoolColor(id_Faction))));	
			}
		}	
	}

}
