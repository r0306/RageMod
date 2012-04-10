package net.rageland.ragemod.config;

public class FactionConfig {
	private final String Name;
	private final int id;
	private final String Color;
	private final boolean PlayerFaction;
	private final String Capitol;
	private final String language;
	
	public FactionConfig(String name,int id, String Color, String Capitol, boolean PlayerFaction,String language){
		this.Capitol=Capitol;
		this.Color=Color;
		this.id=id;
		this.Name=name;
		this.PlayerFaction=PlayerFaction;
		this.language= language;
		}
	
	public String getName() {
		return Name;
	}
	public String getColor() {
		return Color;
	}
	public int getId() {
		return id;
	}
	public String getCapitol() {
		return Capitol;
	}
	public boolean isPlayerFaction() {
		return PlayerFaction;
	}

	public String getLanguage() {
		return language;
	}

}
