package net.rageland.ragemod.chat;

import java.util.ArrayList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.text.Language;

public class LocalChat extends BaseChat{

	public LocalChat(RageMod Plugin) { //LocalChats are being new "created" every time you do ChatEvent 
		super(Plugin, "Local");	
	}
	
	public boolean ChatEvent(String Player, String Message){
		PlayerData Issuer = this.ph.get(Player);
		if (Issuer.isSpeaking != 0){
			Language lang = plugin.languages.getLanguage(Issuer.isSpeaking);
			int la = Issuer.isSpeaking;
			this.init(Issuer);
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
	
	private void init(PlayerData player){
		int radius = 10;
		int whisper = 5; //TODO make this in a config
		this.Users= new ArrayList<String>();
		if (player.chat == 0){
			radius = whisper;
		}
		Entity[] work = (Entity[]) player.getPlayer().getNearbyEntities(radius, radius, radius).toArray();
		for (int i=0;i<work.length;i++){
			if (work[i] instanceof Player){
				this.Users.add(((Player)work[i]).getName());
			}
		
		}
	}

}
