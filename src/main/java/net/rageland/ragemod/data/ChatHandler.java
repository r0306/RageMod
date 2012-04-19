package net.rageland.ragemod.data;

import java.util.HashMap;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.chat.BaseChat;
import net.rageland.ragemod.chat.ZoneChat;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.world.Zone;

public class ChatHandler {
	private BaseChat global;
	private HashMap<Zone,ZoneChat> zones = new HashMap<Zone,ZoneChat>();
	private BaseChat[] local;
	private int locpos;
	private RageMod plugin; 
	
	public ChatHandler(RageMod plugin){
		this.plugin= plugin;
		this.global= new BaseChat(plugin,"global");
		this.local = new BaseChat[3];
		this.locpos= 0;
		for (int i=0; i< local.length; i++){
			this.local[i]= new BaseChat(plugin,"local"+i);
		}
		Zone[] temp = plugin.zones.getZones();
		for (int i=0; i< temp.length; i++){
			this.zones.put(temp[i], new ZoneChat(plugin,temp[i]));
		}
	}

	public void ChatEvent(String Player, String Message){
		switch(plugin.players.get(Player).chat){
		case 0: {
			this.local[locpos].ChatEvent(Player, Message);
			if (locpos < this.local.length){
				locpos++;
			}else{
				locpos=0;
			}
			}
		case 1: {
			this.local[locpos].ChatEvent(Player, Message);
			if (locpos < this.local.length){
				locpos++;
			}else{
				locpos=0;
			}
			}
		case 2:{
			this.zones.get(plugin.players.get(Player).currentZone).ChatEvent(Player, Message);		
		}
		case 3:{
			this.global.ChatEvent(Player, Message);
		}
		}
	}

	public void removePlayer(PlayerData pd) {
		this.global.removeUser(pd.name);
		this.zones.get(pd.currentZone).removeUser(pd.name);	
	}
	
	public void addPlayer(PlayerData pd){
		this.global.addUser(pd.name);
		this.zones.get(pd.currentZone).addUser(pd.name);	
	}
}
