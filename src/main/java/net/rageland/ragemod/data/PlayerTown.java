package net.rageland.ragemod.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import net.rageland.ragemod.Build;
import net.rageland.ragemod.RageConfig;
import net.rageland.ragemod.RageMod;

// TODO: Make this inherit from Region2D, along with NPCTown (make another class in between)

// TODO: Figure out how to make the constructor more graceful and safer - the current buildRegion() setup is asking for null pointer errors

// TODO: Store resident list in memory, there's no reason to be always pulling that from the database

public class PlayerTown extends Town implements Comparable<PlayerTown> {
	
	// PlayerTowns table
	public int id_Faction;
	public double treasuryBalance;
	public double minimumBalance;
	public Timestamp bankruptDate;
	public String mayor;					// Name of mayor
	public boolean isDeleted = false;
	
	public ArrayList<String> residents;
	
	public Region3D sanctumFloor;  			// A 20x20 region in the center of the city
	public Region3D sanctumRoom;
	public Location travelNode;
	
	// Constructor: Blank
	public PlayerTown (RageMod plugin, int id, String name, World world)
	{		
		super(plugin, id, name, world);
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
		return otherTown.id == this.id;
	}
	
	// Creates the regions
	public void createRegions()
	{
		region = new Region2D(world, centerPoint.getX() - (townLevel.size / 2), centerPoint.getZ() + (townLevel.size / 2),
							  centerPoint.getX() + (townLevel.size / 2), centerPoint.getZ() - (townLevel.size / 2));
		sanctumFloor = new Region3D(world, centerPoint.getX() - 10, centerPoint.getY() - 1, centerPoint.getZ() + 9,
									centerPoint.getX() + 9, centerPoint.getY() - 3, centerPoint.getZ() - 10);
		sanctumRoom = new Region3D(world, centerPoint.getX() - 10, centerPoint.getY() + 4, centerPoint.getZ() + 9,
				centerPoint.getX() + 9, centerPoint.getY(), centerPoint.getZ() - 10);
		travelNode = plugin.zones.getTravelNode(centerPoint);
	}
	
	// Processes all block building for create and upgrade
	public void build()
	{
		createBorder();
		buildSanctumFloor();
		buildTravelNode();
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
		return townLevel.isCapitol;
	}

	// Checks to see if the town already has its maximum number of residents
	public boolean isFull() 
	{
		int numberOfResidents = plugin.database.townQueries.countResidents(name);
		
		return numberOfResidents >= townLevel.maxResidents;
	}
	
	// Returns all of the info for the current level
	public TownLevel getLevel()
	{
		return townLevel;
	}
	
	// Puts a border of cobblestone on the edges of the town
	private void createBorder()
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
		return "<t" + colorCode + ">" + this.name + "</t" + colorCode + ">";
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
	
	// Checks to see if a block is part of the sanctum floor
	public boolean isInsideSanctumFloor(Location location) 
	{
		return this.sanctumFloor.isInside(location);
	}
	
	// Checks to see if a block is part of the sanctum room
	public boolean isInsideSanctum(Location location) 
	{
		return this.sanctumRoom.isInside(location);
	}
	
	// Processes placing and breaking of gold blocks in the inner sanctum (treasury)
	public boolean processGoldBlock(BlockEvent rawEvent) 
	{
		// Block broken (withdrawl or theft)
		if( rawEvent instanceof BlockBreakEvent )
		{
			BlockBreakEvent event = (BlockBreakEvent)rawEvent;
			Player player = event.getPlayer();
	    	PlayerData playerData = plugin.players.get(player.getName());
	    	
	    	// Town resident
	    	if( playerData.townName.equals(this.name) )
	    	{
	    		// Make sure the player has enough blocks deposited
	    		if( playerData.treasuryBlocks < 1 )
	    		{
	    			plugin.message.send(player, "You don't have any gold blocks deposited in the treasury.");
	    			return false;
	    		}
	    		else
	    		{
	    			playerData.treasuryBlocks--;
	    			playerData.update();
	    			plugin.message.parse(player, "You now have " + playerData.treasuryBlocks + " block" + 
	    					(playerData.treasuryBlocks == 1 ? "" : "s") + " deposited into the treasury.", ChatColor.GOLD);
	    			return true;
	    		}
	    	}
		}
		else if( rawEvent instanceof BlockPlaceEvent )
		{
			BlockPlaceEvent event = (BlockPlaceEvent)rawEvent;
			Player player = event.getPlayer();
	    	PlayerData playerData = plugin.players.get(player.getName());
	    	
	    	// Town resident
	    	if( playerData.townName.equals(this.name) )
	    	{
	    		// Neutral town are not allowed to have treasuries
	    		if( this.id_Faction == 0 )
	    		{
	    			plugin.message.send(player, "Neutral towns cannot use treasury blocks.");
	    			return false;
	    		}
	    		if( this.townLevel.treasuryLevel == 0 )
	    		{
	    			plugin.message.send(player, this.townLevel.name + "s are not allowed treasury blocks - upgrade your town to create income.");
	    			return false;
	    		}
	    		
	    		// See if the placement would exceed the town's treasury level
	    		if( playerData.treasuryBlocks + 1 > this.townLevel.treasuryLevel )
	    		{
	    			plugin.message.parse(player, "Your town only allows " + this.townLevel.treasuryLevel + " treasury block" + 
	    					(this.townLevel.treasuryLevel == 1 ? "" : "s") + " per resident.");
	    			return false;
	    		}
	    		else
	    		{
	    			playerData.treasuryBlocks++;
	    			playerData.update();
	    			plugin.message.parse(player, "You now have " + playerData.treasuryBlocks + " block" + 
	    					(playerData.treasuryBlocks == 1 ? "" : "s") + " deposited into the treasury.", ChatColor.GOLD);
	    			return true;
	    		}
	    	}
		}
		// TODO Auto-generated method stub
		return false;
	}
	
	// Checks for the number of gold blocks in the sanctumRoom region
	public int countTreasuryBlocks() 
	{
		int total = 0;
		
		for( int x = (int)sanctumRoom.nwCorner.getX(); x <= (int)sanctumRoom.seCorner.getX(); x++ )
		{
			for( int z = (int)sanctumRoom.nwCorner.getZ(); z >= (int)sanctumRoom.seCorner.getZ(); z-- )
			{
				for( int y = (int)sanctumRoom.nwCorner.getY(); y >= (int)sanctumRoom.seCorner.getY(); y-- )
				{
					if( world.getBlockAt(x, y, z).getType() == Material.GOLD_BLOCK )
						total++;
				}
			}
		}
		
		return total;
	}
	
	// Removes erroneous gold blocks from the sanctum (compensates treasury)
	public void removeTreasuryBlocks(int blocksToRemove) 
	{
		// Give money to treasury (9 ingots in a block)
		this.treasuryBalance += blocksToRemove * plugin.config.PRICE_GOLD * 9;
		
		// Clear the blocks
		for( int x = (int)sanctumRoom.nwCorner.getX(); x <= (int)sanctumRoom.seCorner.getX(); x++ )
		{
			for( int z = (int)sanctumRoom.nwCorner.getZ(); z >= (int)sanctumRoom.seCorner.getZ(); z-- )
			{
				for( int y = (int)sanctumRoom.nwCorner.getY(); y >= (int)sanctumRoom.seCorner.getY(); y-- )
				{
					if( world.getBlockAt(x, y, z).getType() == Material.GOLD_BLOCK )
					{
						world.getBlockAt(x, y, z).setType(Material.AIR);
						blocksToRemove--;
						if( blocksToRemove <= 0 )
							return;
					}
				}
			}
		}	
	}
	
	// Builds the inner sanctum floor
	private void buildSanctumFloor() 
	{
		Build.sanctumFloor(plugin, this.world, (int)this.centerPoint.getX() - 10, (int)this.centerPoint.getY() - 1, (int)this.centerPoint.getZ() - 10, 
				this.townLevel.level, this.id_Faction);
	}
	
	// Creates the corresponding location in the travel zone for town
	private void buildTravelNode()
	{
		Build.travelNode(this.travelNode, this.townLevel.size, this.id_Faction);
	}






	
	
	

}
