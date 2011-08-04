package net.rageland.ragemod;

import net.rageland.ragemod.data.Tasks;

public class RageTimer implements Runnable
{
	private final RageMod plugin;
	int secondHand = 0;
	
	public RageTimer(final RageMod plugin) 
	{
        this.plugin = plugin;
    }
	
	// Process timer tasks
	public void run()
	{
		
		// Process per-minute tasks (separated out for efficiency)
		//if( secondHand == 0 )
		//{
			// Process town upkeep/taxes
			if( plugin.tasks.getSeconds("TOWN_UPKEEP") > plugin.config.Task_TOWN_UPKEEP )
			{
				plugin.tasks.processTownUpkeeps();
				plugin.tasks.setComplete("TOWN_UPKEEP");
			}
			// Fill sandlot
			if( plugin.tasks.getSeconds("FILL_SANDLOT") > plugin.config.Task_FILL_SANDLOT )
			{
				System.out.println("It has been " + plugin.tasks.getSeconds("FILL_SANDLOT") + " seconds since the last sandlot fill.");
				plugin.tasks.processFillSandlot(plugin);
				plugin.tasks.setComplete("FILL_SANDLOT");
			}
			
				
			
		//}
		
		
		
		
		// Increment/reset the second hand
		secondHand++;
		if( secondHand == 60 )
			secondHand = 0;
	}	
}