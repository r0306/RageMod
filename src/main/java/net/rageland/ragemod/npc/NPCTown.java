package net.rageland.ragemod.npc;

// TODO: Redo this and PlayerTown... the amount of public is making things messy...

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.World;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.utilities.Util;
import net.rageland.ragemod.world.Region2D;
import net.rageland.ragemod.world.Town;

@SuppressWarnings("unused")
public class NPCTown extends Town
{
	public boolean isProtected;
	public int id_NPCRace;
	public String steward = "";
	public ArrayList<String> buildPermissions;
	private HashSet<NPCData> residents;
	
	public NPCTown(RageMod plugin, int id, String name, World world, String steward)
	{
		super(plugin, id, name, world);
		this.steward = steward;
		buildPermissions = new ArrayList<String>();
	}
	
	// Creates the regions
	public void createRegion(double x1, double z1, double x2, double z2)
	{
		region = new Region2D(world, x1, z1, x2, z2);
		this.centerPoint = new Location(this.world, Util.between(x1, x2), 65, Util.between(z1, z2));
	}
	
	public void createRegion(String coords)
	{
		region = new Region2D(world, coords);
		this.centerPoint = new Location(this.world, Util.between(region.nwCorner.getX(), region.seCorner.getX()), 65, 
				Util.between(region.nwCorner.getZ(), region.seCorner.getZ()));
	}
	
	// Returns the town name with special tags to be interpreted by the messaging methods
	public String getCodedName() 
	{
		return "<tp>" + this.name + "</tp>";
	}

	// Updates the town in the database
	public void update() 
	{
		plugin.database.npcTownQueries.update(this);
	}
	
	
	// Returns whether or not the player has permission to build in the town
	public boolean hasPermission(String playerName)
	{
		for( String builder : buildPermissions )
			if( builder.equalsIgnoreCase(playerName) )
				return true;
		
		return false;
	}
}
