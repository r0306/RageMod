package net.rageland.ragemod.npcentities;

import java.util.ArrayList;
import java.util.HashMap;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.npc.NPCData;
import net.rageland.ragemod.entity.PlayerData;
import net.rageland.ragemod.npc.NPCPhrase;
import net.rageland.ragemod.utilities.Util;

@SuppressWarnings("unused")
public class SpeechData
{
	private ArrayList<NPCPhrase> messages;
	private NPCPhrase initialGreeting;
	private HashMap<Integer, NPCPhrase> followups;
	private NPCData npcData;
	
	private int messagePointer;
	private int radius = 20;
	private int interval = 30;
	
	private RageMod plugin;
	
	public SpeechData(ArrayList<NPCPhrase> messages, NPCPhrase initialGreeting, HashMap<Integer, NPCPhrase> followups, NPCData npcData, RageMod plugin)
	{
		this.messages = messages;
		messagePointer = 0;
		this.initialGreeting = initialGreeting;
		this.followups = followups;
		this.npcData = npcData;
		this.plugin = plugin;
	}
	
	public String getNextMessage(PlayerData playerData) 
	{
		if(messages.size() > 0)
		{
			String message = messages.get(messagePointer).getMessage(playerData);
			
			messagePointer++;
			if(messagePointer == messages.size())
				messagePointer = 0;
			
			return message;
		}
		else
		{
			return "I am error.";
		}
	}
	
	// Gets the message for a first-time meeting
	public String getInitialGreeting(PlayerData playerData)
	{
		return initialGreeting.getMessage(playerData);
	}
	
	// Gets the message for a followup encounter
	public String getFollowupGreeting(PlayerData playerData)
	{
		// Convert the -10 to 10 affinity float value to the -2 to 2 affinity integer code
		int affinityCode = Util.getAffinityCode(playerData.getAffinity(npcData.id_NPC));
		
		return followups.get(affinityCode).getMessage(playerData);
	}
	
	// Processes the language for a phrase
//	private String processPhrase(NPCPhrase phrase, PlayerData playerData)
//	{
//		if( languageSkill == 100 || npcData.id_NPCRace == plugin.config.NPC_HUMAN_ID )
//			return phrase.getMessage(playerData);
//		else
//			return phrase.getTranslation(languageSkill, playerName);
//	}
	
	public int getRadius()
	{
		return radius;
	}
	
	public void setRadius(int radius)
	{
		this.radius = radius;
	}
	
	public void addMessage(NPCPhrase message)
	{
		messages.add(message);
	}
	
	public int getInterval()
	{
		return interval;
	}
	
	public void setInterval(int interval)
	{
		this.interval = interval;
	}

}
