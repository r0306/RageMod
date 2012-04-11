package net.rageland.ragemod.world;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.ZoneHandler;

public class SandLot {
	
	// Fill a specified area with sand for a public mine	  	
	  public void processFillSandlot(RageMod plugin) 		  	
	  {	  	
	    World world = plugin.getServer().getWorld("world");		  	
	    Random random = new Random();	  	
	    		  	
	    System.out.println("Refilling sand lot...");	  	
		  	
	    // Look for players who are currently in the area and evacuate them		  	
	    for( Player player : plugin.getServer().getOnlinePlayers() )		  	
	    {		  	
	      if( plugin.zones.isInSandlot(player.getLocation()) )		  	
	      {		  	
	        plugin.message.parse(player, "Automatically refilling sand lot - get out of the way!");		  	
	        player.teleport(world.getSpawnLocation());	 				  	
	      }		  	
	    }	  			  	
	    Block currentBlock;		  	
	    
		  	
	    for( int x = (int)ZoneHandler.Capitol_SandLot.nwCorner.getX(); x <= (int)ZoneHandler.Capitol_SandLot.seCorner.getX(); x++ )		  	
	    {		  	
	      for( int y = (int)ZoneHandler.Capitol_SandLot.nwCorner.getY(); y <= (int)ZoneHandler.Capitol_SandLot.seCorner.getY(); y++ )		  	
	      {		  	
	        for( int z = (int)ZoneHandler.Capitol_SandLot.nwCorner.getZ(); z >= (int)ZoneHandler.Capitol_SandLot.seCorner.getZ(); z-- )		  	
	        {		  	
	          currentBlock = world.getBlockAt(x, y, z);		  	
	          if( random.nextInt( plugin.config.Capitol_SANDLOT_GOLD_ODDS ) == 0 )		  	
	            currentBlock.setType(Material.GOLD_ORE);		  	
	          else if( random.nextInt( plugin.config.Capitol_SANDLOT_DIAMOND_ODDS ) == 0 ) 		  	
	            currentBlock.setType(Material.DIAMOND_ORE);		  	
	          else		  	
	            currentBlock.setType(Material.SAND);		  	
	        }		  	
	      }		  	
	    }		  	
	  }
}
