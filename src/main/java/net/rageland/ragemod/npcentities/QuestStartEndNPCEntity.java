package net.rageland.ragemod.npcentities;

import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.Util;
import net.rageland.ragemod.quest.Quest;

import org.bukkit.entity.Player;

public class QuestStartEndNPCEntity extends NPCEntity {
	private Quest quest;
	
	public QuestStartEndNPCEntity(
			MinecraftServer minecraftserver, 
			World world, String name,
			ItemInWorldManager iteminworldmanager, 
			RageMod plugin,
			Quest quest) 
	{
		super(minecraftserver, world, name, iteminworldmanager, plugin);
		this.quest = quest;		
	}
	
	public void rightClickAction(Player player) 
	{
		Util.message(player, "Quest: " + quest.getQuestName());
		Util.message(player, quest.getQuestText());
		Util.message(player, "[Left click npc to accept]");
	}

	public void leftClickAction(Player player) 
	{
		quest.questStart(player, plugin.players.get(player.getName()));
	}
}
