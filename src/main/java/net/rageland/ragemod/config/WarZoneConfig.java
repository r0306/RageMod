package net.rageland.ragemod.config;

import java.util.List;

import net.rageland.ragemod.database.RageDB;

import org.bukkit.Material;

public class WarZoneConfig {
	
	public RageDB db;
	
	public String Zone_WZ_CENTER = "";
	public String Zone_WZ_REGION = "";
	
	public boolean contains(Material mat) {
		return false;	
	}

	private static List<Material> Blocks_To_Drop;
	
	public WarZoneConfig(){
		//TODO get Data
		Material[] mats = null; //This will be loaded out of the database
		for (int i = 0;i<mats.length;i++){
		Blocks_To_Drop.add(mats[i]);
		}
	}
}
