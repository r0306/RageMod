package net.rageland.ragemod.chat;

import java.util.ArrayList;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.PlayerHandler;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.text.Language;

import org.bukkit.entity.Player;

public class BaseChat {
	protected ArrayList<String> Users;
	protected PlayerHandler ph;
	protected RageMod plugin;
	protected String name;
	
	public BaseChat(RageMod Plugin, String Name){
		this.Users= new ArrayList<String>();
		this.ph= Plugin.players;
		this.name = Name;
		this.plugin= Plugin;
		
	}
	
	public boolean ChatEvent(String Player, String Message){
		PlayerData Issuer = this.ph.get(Player);
		if (Issuer.isSpeaking != 0){
			Language lang = plugin.languages.getLanguage(Issuer.isSpeaking);
			int la = Issuer.isSpeaking;
			for (int i=0; i<Users.size();i++){
				PlayerData work = this.ph.get(this.Users.get(i));
				String msg = lang.translateLaEn(Message, work.getLanguageSkill(la));
				work.getPlayer().sendMessage(Player+" : "+msg);
				//TODO colors and stuff
			}
			
		}else{
			for (int i=0; i<Users.size();i++){
				this.ph.get(this.Users.get(i)).getPlayer().sendMessage(Message);
				//TODO colors and stuff
			}
		}
		
		return true;
		
	}
	
	public String[] getUsers(){
		return (String[]) Users.toArray();
	}
	
	public void addUser(String Player){
		this.Users.add(Player);
	}
	
	public void addUser(Player player){
		this.Users.add(player.getName());
	}
	
	public void removeUser(Player player){
		this.Users.remove(player.getName());
	}

	public void removeUser(String player){
		this.Users.remove(player);
	}

	public String getName() {
		return name;
	}
}
