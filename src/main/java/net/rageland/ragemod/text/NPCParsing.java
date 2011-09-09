package net.rageland.ragemod.text;

import java.util.regex.Pattern;

import net.rageland.ragemod.RageMod;

public class NPCParsing 
{
	private RageMod plugin;
	private Pattern playerPattern;
	
	public NPCParsing(RageMod plugin)
	{
		this.plugin = plugin;
		
		playerPattern = Pattern.compile("<playerName/>");
	}
	
}
