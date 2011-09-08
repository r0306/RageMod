package net.rageland.ragemod.npcentities;

import java.util.ArrayList;
import java.util.HashMap;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCPhrase;

public class SpeechData
{
	private ArrayList<NPCPhrase> messages;
	private NPCPhrase initialGreeting;
	private HashMap<Integer, NPCPhrase> followups;
	
	private int messagePointer;
	private int radius = 20;
	private int interval = 30;
	private int id_Race;
	
	private RageMod plugin;
	
	public SpeechData(ArrayList<NPCPhrase> messages, NPCPhrase initialGreeting, HashMap<Integer, NPCPhrase> followups, int id_Race, RageMod plugin)
	{
		this.messages = messages;
		messagePointer = 0;
		this.initialGreeting = initialGreeting;
		this.followups = followups;
		this.id_Race = id_Race;
		this.plugin = plugin;
	}
	
	public String getNextMessage(int languageSkill) 
	{
		if(messages.size() > 0)
		{
			String message = processPhrase(messages.get(messagePointer), languageSkill);
			
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
	public String getInitialGreeting(int languageSkill)
	{
		return processPhrase(initialGreeting, languageSkill);
	}
	
	// Gets the message for a followup encounter
	public String getFollowupGreeting(int languageSkill, float affinity)
	{
		// Convert the -10 to 10 affinity float value to the -2 to 2 affinity integer code
		int affinityCode = Math.round(affinity / 4);
		if( affinityCode > 2 )
			affinityCode = 2;
		else if( affinityCode < -2 )
			affinityCode = -2;
		
		return processPhrase(followups.get(affinityCode), languageSkill);
	}
	
	// Processes the language for a phrase
	private String processPhrase(NPCPhrase phrase, int languageSkill)
	{
		if( languageSkill == 100 || id_Race == plugin.config.NPC_HUMAN_ID )
			return phrase.getMessage();
		else
			return phrase.getTranslation(languageSkill);
	}
	
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
