package net.rageland.ragemod.data;

import java.util.HashMap;

import net.rageland.ragemod.RageMod;

import org.bukkit.DyeColor;
import org.bukkit.Material;

public class FactionHandler 
{	
	private HashMap<Integer, String> factions;
	
	public FactionHandler(RageMod plugin)
	{
		factions = new HashMap<Integer, String>();
		loadFactions();
	}
    
    
    // TODO: Eventually pull all of this stuff from the DB or config - since we only have 2 factions, keep this hardcoded for now
    public void loadFactions()
    {
    	factions.put(0, "Neutral");
    	factions.put(1, "Red");
    	factions.put(2, "Blue");
    }
    
    // Returns the faction name from ID
    public String getName(int id)
    {
    	return factions.get(id);
    }
    
	// Returns the faction name with special tags to be interpreted by the messaging methods
	public String getCodedName(int id) 
	{
		char colorCode = getColorCode(id);
		return "<t" + colorCode + ">" + factions.get(id) + "</t" + colorCode + ">";
	}
    
    // Returns the faction ID from name
    public int getID(String name)
    {
    	for( int id : factions.keySet() )
    	{
    		if( factions.get(id).equalsIgnoreCase(name) )
    			return id;
    	}
    	
    	return 0;
    }
    
    // Returns the correct data value for the faction's wool color
    public static int getWoolColor(int id)
    {
    	switch(id)
    	{
    		case 1:		// red
    			return 14;
    		case 2:		// blue
    			return 11;
    		default:	// neutral
    			return 0;
    	}
    }

	public Material getLiquidBlock(int id) 
	{
		switch(id)
    	{
    		case 1:		// red
    			return Material.LAVA;
    		case 2:		// blue
    			return Material.WATER;
    		default:	// neutral
    			return Material.ICE;
    	}
	}
	
	public Material getSpecialBlock(int id) 
	{
		switch(id)
    	{
    		case 1:		// red
    			return Material.NETHERRACK;
    		case 2:		// blue
    			return Material.LAPIS_BLOCK;
    		default:	// neutral
    			return Material.SNOW_BLOCK;
    	}
	}

	// Returns codes for faction colors
	public char getColorCode(int id) 
	{
		switch(id)
		{
			case 1:		// red
				return 'r';
			case 2:		// blue
				return 'b';
			default:	// neutral
				return 'n';
		}
	}
	
	// Returns the corresponding dye colors
	public DyeColor getDyeColor(int id, boolean isPrimary)
	{
		if( isPrimary )			// Primary colors
			switch(id)
			{
				case 1:		// red
					return DyeColor.RED;
				case 2:		// blue
					return DyeColor.BLUE;
				default:
					return DyeColor.WHITE;
			}
		else					// Secondary colors
			switch(id)
			{
				case 1:		// red
					return DyeColor.PINK;
				case 2:		// blue
					return DyeColor.LIGHT_BLUE;
				default:
					return DyeColor.GRAY;
			}
	}
	
	

}
