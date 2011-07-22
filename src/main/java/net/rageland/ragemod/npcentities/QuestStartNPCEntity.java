package net.rageland.ragemod.npcentities;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import net.rageland.ragemod.quest.Quest;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestStartNPCEntity extends NPCEntity {
	private Quest quest;
	
	public QuestStartNPCEntity(
			MinecraftServer minecraftserver, 
			World world, String name,
			ItemInWorldManager iteminworldmanager, 
			JavaPlugin plugin,
			Quest quest) 
	{
		super(minecraftserver, world, name, iteminworldmanager, plugin);
		this.quest = quest;		
	}
	
	public void rightClickAction(Player player) 
	{
		
	}

	public void leftClickAction(Player player) 
	{
		
	}

}
