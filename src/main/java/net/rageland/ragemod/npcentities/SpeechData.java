package net.rageland.ragemod.npcentities;

import java.util.ArrayList;

public class SpeechData
{
	
	private ArrayList<String> messages;
	private int messagePointer;
	private int radius;
	private int interval;
	
	public SpeechData(ArrayList<String> messages, int radius, int interval)
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
			String message = messages.get(messagePointer);
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
	
	public void addMessage(String message)
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
