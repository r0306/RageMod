package net.rageland.ragemod.npcentities;

import java.util.ArrayList;

import net.rageland.ragemod.data.NPCPhrase;

public class SpeechData
{
	
	private ArrayList<NPCPhrase> messages;
	private int messagePointer;
	private int radius;
	private int interval;
	
	public SpeechData(ArrayList<NPCPhrase> messages, int radius, int interval)
	{
		this.messages = messages;
		messagePointer = 0;
		this.radius = radius;
		this.interval = interval;
	}
	
	public String getNextMessage() 
	{
		if(messages.size() > 0)
		{
			String message = messages.get(messagePointer).getMessage();
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
