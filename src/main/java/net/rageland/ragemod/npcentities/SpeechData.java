package net.rageland.ragemod.npcentities;

import java.util.ArrayList;

import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.NPCPhrase;

public class SpeechData
{
	private ArrayList<NPCPhrase> messages;
	private int messagePointer;
	private int radius;
	private int interval;
	private int id_Race;
	private RageMod plugin;
	
	public SpeechData(ArrayList<NPCPhrase> messages, int radius, int interval, int id_Race, RageMod plugin)
	{
		this.messages = messages;
		messagePointer = 0;
		this.radius = radius;
		this.interval = interval;
		this.id_Race = id_Race;
		this.plugin = plugin;
	}
	
	public String getNextMessage(int languageSkill) 
	{
		if(messages.size() > 0)
		{
			String message;
			
			if( languageSkill == 100 || id_Race == plugin.config.NPC_HUMAN_ID )
				message = messages.get(messagePointer).getMessage();
			else
				message = messages.get(messagePointer).getTranslation(languageSkill);
			
			messagePointer++;
			if(messagePointer == messages.size())
				messagePointer = 0;
			
			return message;
		}
		else
		{
			return "I have nothing to say to you.";
		}
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
