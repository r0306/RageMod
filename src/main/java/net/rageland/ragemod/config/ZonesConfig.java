package net.rageland.ragemod.config;


/**
 * Simple Config File for the Zones should be parsed out of a yaml later
 * Allows you to make custom Zones
 */
public class ZonesConfig {
	//Basic Settings
	private final String name;
	private final int position;
	private final int id;
	private final int width;
	//Mob Settings
	private final boolean mobs;
	private final int mob_percent;
	private final int mob_strength;
	private final boolean mobsday;
	//Player Settings
	private final boolean player_build;
	private final boolean player_city;
	private final boolean pvp;
	private final boolean faction_pvp;
	//Npc Settings
	private final boolean npcs;
	private final boolean npc_town;
	
	/**
	 * Simple Config File for the Zones should be parsed out of a yaml later
	 *
	 */
	
	public ZonesConfig(String name,int position,int id, int width, boolean mobs, int mobpercentage, int mobstrength, 
			boolean mobsspawnday,boolean playerbuild, boolean playercity, boolean pvp, boolean factionpvp, boolean npcs, boolean npctowns){
		this.id= id;
		this.name= name;
		this.position=position;
		this.width=width;
		
		this.mobs=mobs;
		this.mob_percent= mobpercentage;
		this.mob_strength= mobstrength;
		this.mobsday= mobsspawnday;
		
		this.player_build= playerbuild;
		this.player_city= playercity;
		this.faction_pvp=factionpvp;
		this.pvp= pvp;
		
		this.npcs= npcs;
		this.npc_town=npctowns;
		
		
	}
	
	/**
	 * For mobs = false
	 * @param mobs must be false
	 */
	public ZonesConfig(String name,int position,int id, int width, boolean mobs,boolean playerbuild, boolean playercity, boolean pvp, boolean factionpvp, boolean npcs, boolean npctowns){
		this.id= id;
		this.name= name;
		this.position=position;
		this.width=width;
		
		this.mobs=false;
		this.mob_percent= 0;
		this.mob_strength= 0;
		this.mobsday= false;
		
		this.player_build= playerbuild;
		this.player_city= playercity;
		this.faction_pvp=factionpvp;
		this.pvp= pvp;
		
		this.npcs= npcs;
		this.npc_town=npctowns;
			
	}
	
	public String getName() {
		return name;
	}
	public int getPosition() {
		return position;
	}
	public int getId() {
		return id;
	}
	public boolean isMobs() {
		return mobs;
	}
	public int getMobPercent() {
		return mob_percent;
	}
	public int getMobStrength() {
		return mob_strength;
	}
	public boolean isMobSpawnDay() {
		return mobsday;
	}
	public boolean isPlayerBuild() {
		return player_build;
	}
	public boolean isPlayerCity() {
		return player_city;
	}
	public boolean isPvp() {
		return pvp;
	}
	public boolean isFactionPvp() {
		return faction_pvp;
	}
	public boolean isNpcs() {
		return npcs;
	}
	public boolean isNpcTown() {
		return npc_town;
	}

	public int getWidth() {
		return width;
	}
	
	
}
