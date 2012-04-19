package net.rageland.ragemod.entity;

import net.rageland.ragemod.text.Language;

public class Race {
	private final Language lang;
	private final String Name;
	private final int id;
	private final boolean NPC; // if false players can join a race (later)
	
	public Race(Language Lang,String Name,int id,boolean NPC){
		this.lang=Lang;
		this.Name=Name;
		this.id=id;
		this.NPC=NPC;
	}

	public Language getLang() {
		return lang;
	}

	public String getName() {
		return Name;
	}

	public int getId() {
		return id;
	}

	public boolean isNPC() {
		return NPC;
	}
}
