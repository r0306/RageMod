package net.rageland.ragemod.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;

// TODO: Make this inherit from Region2D, along with NPCTown (make another class in between)

// TODO: Figure out how to make the constructor more graceful and safer - the current buildRegion() setup is asking for null pointer errors

// TODO: Store resident list in memory, there's no reason to be always pulling that from the database

public class PlayerTown implements Comparable<PlayerTown> {
	
	// PlayerTowns table
	public int id_PlayerTown;
	public String townName;
	public Location centerPoint;
	public int id_Faction;
	public double treasuryBalance;
	public double minimumBalance;
	public Timestamp bankruptDate;
	public String mayor;					// Name of mayor
	public boolean isDeleted = false;
	
	public ArrayList<String> residents;
		
	public TownLevel townLevel;				// Corresponds to the HashMap TownLevels in Config
	
	public Region2D region;
	public Region3D sanctumFloor;  			// A 20x20 region in the center of the city
	public Region3D sanctumRoom;
	public World world;
	
	private RageMod plugin;
	
	
	// Constructor: All data
//	public PlayerTown (int _id_PlayerTown, String _townName, int _xCoord, int _zCoord, String _faction, 
//			float _treasuryBalance, Date _bankruptDate, String _townLevel, float _upkeepCost, int _size, int _maxNPCs)
//	{
//		 ID_PlayerTown = _id_PlayerTown;
//		 TownName = _townName;
//		 XCoord = _xCoord;
//		 ZCoord = _zCoord;
//		 Faction = _faction;
//		 TreasuryBalance = _treasuryBalance;
//		 BankruptDate = _bankruptDate;
//			
//		 // TownLevels table
//		 TownLevel = _townLevel;
//		 UpkeepCost = _upkeepCost;
//		 Size = _size;
//		 MaxNPCs = _maxNPCs;
//	}
	
	// Constructor: Blank
	public PlayerTown (RageMod plugin)
	{		
		this.plugin = plugin;
		residents = new ArrayList<String>();
	}
	
	// Implementing Comparable for sorting purposes
	public int compareTo(PlayerTown otherTown)
	{
		return otherTown.townLevel.level - this.townLevel.level;
	}
	
	// Comparison
	public boolean equals(PlayerTown otherTown)
	{
		return otherTown.id_PlayerTown == this.id_PlayerTown;
	}
	
	// Creates the region
	public void buildRegion()
	{
		region = new Region2D(centerPoint.getX() - (townLevel.size / 2), centerPoint.getZ() + (townLevel.size / 2),
							  centerPoint.getX() + (townLevel.size / 2), centerPoint.getZ() - (townLevel.size / 2));
	}
	
	// Checks to see whether the town is already at maximum level; used by /townupgrade
	public boolean isAtMaxLevel()
	{
		if( id_Faction == 0 )
			return townLevel.level >= plugin.config.Town_MAX_LEVEL_NEUTRAL;
		else
			return townLevel.level >= plugin.config.Town_MAX_LEVEL_FACTION;
	}
	
	public boolean isCapitol()
	{
		return plugin.config.townLevels.get(townLevel).isCapitol;
	}

	// Checks to see if the town already has its maximum number of residents
	public boolean isFull() 
	{
		int numberOfResidents = plugin.database.townQueries.countResidents(townName);
		
		return numberOfResidents >= townLevel.maxResidents;
	}
	
	// Returns all of the info for the current level
	public TownLevel getLevel()
	{
		return townLevel;
	}
	
	// Returns whether or not the specified location is inside the region
	public boolean isInside(Location location)
	{
		return region.isInside(location);
	}
	
	// Returns a Location at the center of the town
	public Location getCenter()
	{
		return new Location(world, centerPoint.getX(), 65, centerPoint.getZ());
	}
	
	// Puts a border of cobblestone on the edges of the town
	public void createBorder()
	{
		int x, z;
		
		for (x = (int)region.nwCorner.getX() + 1; x <= (int)region.seCorner.getX() - 1; x++) 
		{
            // North Wall
			z = (int)region.nwCorner.getZ();
			placeOverlay(x, z);
			
			// South Wall
			z = (int)region.seCorner.getZ();
			placeOverlay(x, z);
        }
		
		for (z = (int)region.nwCorner.getZ(); z >= (int)region.seCorner.getZ(); z--) 
        {
			// West Wall
			x = (int)region.nwCorner.getX();
			placeOverlay(x, z);
			
			// East Wall
			x = (int)region.seCorner.getX();
			placeOverlay(x, z);
        }
	}
	
	// Part of createBorder()
	private void placeOverlay(int x, int z)
	{
		for (int y = 127; y >= 1; y--) 
        {
            int upperType = world.getBlockTypeIdAt(x, y, z);
            int lowerType = world.getBlockTypeIdAt(x, y-1, z);
            
            if( upperType == 0 && lowerType != 0 )
            {
            	// Replace grass with cobble
            	if( Material.getMaterial(lowerType) == Material.LONG_GRASS )
            	{
            		world.getBlockAt(x, y-1, z).setType(Material.COBBLESTONE);
            	}
            	// Don't place blocks on top of trees or torches
            	else if( Material.getMaterial(lowerType) != Material.LEAVES 
            		&& Material.getMaterial(lowerType) != Material.TORCH)
            	{
            		world.getBlockAt(x, y, z).setType(Material.COBBLESTONE);
            	}
            	return;
            }
        }
	}

	// Returns the town name with special tags to be interpreted by the messaging methods
	public String getCodedName() 
	{
		char colorCode = plugin.factions.getColorCode(this.id_Faction);
		return "<t" + colorCode + ">" + this.townName + "</t" + colorCode + ">";
	}
	
	// Updates the town info in the database
	public void update()
	{
		plugin.database.townQueries.update(this);
	}

	// Removes a resident from the town
	public void removeResident(String playerName) 
	{
		plugin.database.townQueries.townLeave(playerName);
		this.residents.remove(playerName);
	}
	
	
	
	
	
	
	
	// Builds the inner sanctum floor
	public void buildSanctumFloor() 
	{
		
		// Pinpoint the top-right corner
		int cornerX = (int)this.centerPoint.getX() - 10; // - 10;
		int cornerY = (int)this.centerPoint.getY() - 1;
		int cornerZ = (int)this.centerPoint.getZ() - 10; // - 10;
		Block currentBlock;
		
		ArrayList<String> layout = RageConfig.buildSanctumFloor(this.townLevel.level);
		
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
						currentBlock.setData((byte) Byte.valueOf(String.valueOf(plugin.factions.getWoolColor(this.id_Faction))));
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
						world.getBlockAt(cornerX + x, cornerY - 1, cornerZ + z).setType(plugin.factions.getLiquidBlock(this.id_Faction));
						break;
					case 'n':
						currentBlock.setType(Material.SNOW_BLOCK); break;
					case 'i':
						currentBlock.setType(Material.IRON_BLOCK); break;
					case 'S':
						currentBlock.setType(plugin.factions.getSpecialBlock(this.id_Faction)); break;
						
						
						
				}
			}
		}
		

		

		
	}
	
	
	

}
