package net.rageland.ragemod.data;

import java.sql.Timestamp;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import net.rageland.ragemod.RageMod;

public class NPCInstance 
{
	public int id_NPCInstance;
	public Timestamp despawnTime;
	
	private NPCData npcData;	
	private NPCLocation npcLocation;
	private RageMod plugin;
	
	public NPCInstance(RageMod plugin)
	{
		this.plugin = plugin;
	}
	
	public void activate(int id_NPC, int id_NPCLocation)
	{
		npcData = plugin.npcManager.activateNPC(id_NPC);
		npcLocation = plugin.npcManager.activateLocation(id_NPCLocation);
	}

	// Returns the NPC name
	public String getName() 
	{
		return npcData.name;
	}
	
	// Returns the NPC name with the appropriate color
	public String getColorName()
	{
		// Quest NPCs will use AQUA, speech will use DARK_AQUA
		return ChatColor.DARK_AQUA + npcData.name;
	}
	

	// Returns the NPC id
	public int getNPCid() 
	{
		return npcData.id_NPC;
	}

	// Return the NPCLocation object
	public NPCLocation getLocation() 
	{
		return npcLocation;
	}
}
