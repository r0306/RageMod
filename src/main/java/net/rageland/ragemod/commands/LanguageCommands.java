package net.rageland.ragemod.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.text.Message;

public class LanguageCommands {
	
	private RageMod plugin;
	
	public LanguageCommands(RageMod plugin)
	{
		this.plugin=plugin;
	}
	public void skill(Player player)
	{
		PlayerData playerData = plugin.players.get(player.getName());
		String skillText;
		
		plugin.message.send(player, ChatColor.GOLD + "Language skill:");
		
		for(int i = 1; i <= 4; i++ )
		{
			if( playerData.getLanguageSkill(i) == 100 )
				skillText = ChatColor.WHITE + "100/100";
			else
				skillText = ChatColor.GRAY + Integer.toString(playerData.getLanguageSkill(i)) + "/100";
			plugin.message.send(player, Message.LANGUAGE_NAME_COLOR + "   " + plugin.config.NPC_LANGUAGE_NAMES.get(i) + ": " + skillText);
		}
	}
	
	public boolean speak(Player Issuer,String lang){
		PlayerData data = plugin.players.get(Issuer.getName());
		if (data.isSpeaking == plugin.languages.getlangid(lang)) {
			Issuer.sendMessage("You are speaking that");
			return true;
		}
		if (data.getLanguageSkill(plugin.languages.getlangid(lang))==100){
			data.isSpeaking=plugin.languages.getlangid(lang);
			Issuer.sendMessage("You are speaking now "+lang);
			return true;
		}
		Issuer.sendMessage("You can't speak that. Go to school!");
		return false;
	}
	
	public boolean speak(Player Issuer,String[] lang){
		PlayerData data = plugin.players.get(Issuer.getName());
		if (data.isSpeaking == plugin.languages.getlangid(lang[1])) {
			Issuer.sendMessage("You are speaking that");
			String work= "";
			for (int i=2;i<lang.length;i++){
				work=work+lang[i];
			}
			Issuer.chat(work);
			return true;
		}
		if (data.getLanguageSkill(plugin.languages.getlangid(lang[1]))==100){
			data.isSpeaking=plugin.languages.getlangid(lang[1]);
			String work= "";
			for (int i=2;i<lang.length;i++){
				work=work+lang[i];
			}
			Issuer.sendMessage("You say this: "+work+"in "+lang);
			Issuer.chat(work);
			return true;
		}
		Issuer.sendMessage("You can't speak that. Go to school!");
		String work= "";
		for (int i=2;i<lang.length;i++){
			work=work+lang[i];
		}
		Issuer.chat(work);
		return false;
	}
	
	public void teach(Player Issuer,String Target, String Language, String amount){
		PlayerData pd = this.plugin.players.get(Target);
		try {
			int i =Integer.valueOf(Language);
			int x =Integer.valueOf(amount);
			pd.setLanguageSkill(i,x);
		}catch(Exception e){
			try{
			int x =Integer.valueOf(amount);
			pd.setLanguageSkill(this.plugin.languages.getlangid(Language),x);
			}catch(Exception ex){
				//mesage u suck
			}
		}
		
	}
	public void addword(Player player, String Language, String word,
			String Meaning) {
		try {
			int i =Integer.valueOf(Language);
			this.plugin.languages.getLanguage(i).addWord(word, Meaning);
		}catch(Exception e){
			int i = this.plugin.languages.getlangid(Language);
			if (i != 0){
				this.plugin.languages.getLanguage(i).addWord(word, Meaning);
			}else{
				//buuug
			}
		}
	}
}
