package net.rageland.ragemod.commands;


import org.bukkit.entity.Player;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.PlayerData;

public class RageCommands {
	
	private RageMod plugin;
	
	public RageCommands(RageMod plugin)
	{
		this.plugin = plugin;
	}
	
	public void onCommand(Player player, PlayerData playerData, String[] split) 
	{
		if( split.length < 2 )
		{
			plugin.message.parse(player, "Rage commands: <required> [optional]");
			if( playerData.isSteward )
				plugin.message.parse(player, "   /rage addphrase <phrase> (adds a new NPC phrase)");
		}
		else if( split[1].equalsIgnoreCase("addphrase") )
		{
			if( split.length > 2 )
				this.addphrase(player, split); 
			else
    			plugin.message.parse(player, "Usage: /rage addphrase <phrase>"); 
		}
		
		else
			plugin.message.parse(player, "Type /npctown to see a list of available commands.");
		
	}
	
	// Creates a new NPC location in the town
	private void addphrase(Player player, String[] split) 
	{
		PlayerData playerData = plugin.players.get(player.getName());
		String message = new String();
		
		try
		{
			// Pull the phrase out of the string
			for( int i = 2; i < split.length; i++ )
				message += split[i] + " ";
			
			if( plugin.database.npcQueries.submitPhrase(null, playerData, message) )
				plugin.message.send(player, "Phrase submitted for approval.");
			else
				plugin.message.send(player, "Error submitting phrase.");
			
		}
		catch( Exception ex )
		{
			plugin.message.sendNo(player, "Error: " + ex.getMessage());
		}	
	}



}
