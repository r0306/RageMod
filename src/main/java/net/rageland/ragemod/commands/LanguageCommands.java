package net.rageland.ragemod.commands;

import org.bukkit.entity.Player;

import net.milkbowl.vault.permission.Permission;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.AllBountyHandler;
import net.rageland.ragemod.entity.PlayerData;

public class LanguageCommands {
	
	private RageMod plugin;
	private AllBountyHandler oab;
	private Permission perms;
	
	public boolean speak(Player Issuer,String lang){
		PlayerData data = plugin.players.get(Issuer.getName());
		if (data.isSpeaking == plugin.languages.getlangid(lang)) {
			return true;
		}
		if (data.getLanguageSkill(plugin.languages.getlangid(lang))==100){
			data.isSpeaking=plugin.languages.getlangid(lang);
		}
		return true;
		
	}
}
