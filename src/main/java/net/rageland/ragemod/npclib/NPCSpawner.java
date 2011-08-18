package net.rageland.ragemod.npclib;

import java.util.logging.Level;

import net.minecraft.server.ItemInWorldManager;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.npcentities.QuestEndNPCEntity;
import net.rageland.ragemod.npcentities.QuestStartEndNPCEntity;
import net.rageland.ragemod.npcentities.QuestStartNPCEntity;
import net.rageland.ragemod.npcentities.RewardQuestNPCEntity;
import net.rageland.ragemod.npcentities.SpeechNPC;
import net.rageland.ragemod.quest.Quest;
import net.rageland.ragemod.quest.QuestImplementation;

import org.bukkit.Location;

public class NPCSpawner
{
	
	private BServer server;
	private RageMod plugin;
	
	public NPCSpawner()
	{
		plugin = RageMod.getInstance();
		this.server = BServer.getInstance(RageMod.getInstance());		
	}
	
	public void questStartNPC(String name, int npcId, Location l, QuestImplementation quest)
	{		
		if (isNpcIdUsed(npcId)) 
		{
			this.server.getLogger().log(Level.WARNING, "NPC with that id already exists");
			return;
		}
		if (name.length() > 16) 
		{
			String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		BWorld bworld = new BWorld(l.getWorld());
		QuestStartNPCEntity npcEntity = new QuestStartNPCEntity(
				this.server.getMCServer(), bworld.getWorldServer(), name, new ItemInWorldManager( bworld.getWorldServer()), quest, plugin);
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		bworld.getWorldServer().addEntity(npcEntity);
		plugin.npcManager.getNpcs().put(npcId, npcEntity);
	}
	
	public void rewardNPC(String name, int npcId, Location l, QuestImplementation quest)
	{		
		if (isNpcIdUsed(npcId)) 
		{
			this.server.getLogger().log(Level.WARNING, "NPC with that id already exists");
			return;
		}
		if (name.length() > 16) 
		{
			String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		BWorld bworld = new BWorld(l.getWorld());
		RewardQuestNPCEntity npcEntity = new RewardQuestNPCEntity(
				this.server.getMCServer(), bworld.getWorldServer(), name, new ItemInWorldManager( bworld.getWorldServer()), quest, plugin);
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		bworld.getWorldServer().addEntity(npcEntity);
		plugin.npcManager.getNpcs().put(npcId, npcEntity);
	}
	
	public void questEndNPC(String name, int npcId, Location l, QuestImplementation quest)
	{		
		if (isNpcIdUsed(npcId)) 
		{
			this.server.getLogger().log(Level.WARNING, "NPC with that id already exists");
			return;
		}
		if (name.length() > 16) 
		{
			String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		BWorld bworld = new BWorld(l.getWorld());
		QuestEndNPCEntity npcEntity = new QuestEndNPCEntity(
				this.server.getMCServer(), bworld.getWorldServer(), name, new ItemInWorldManager( bworld.getWorldServer()), quest, plugin);
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		bworld.getWorldServer().addEntity(npcEntity);
		plugin.npcManager.getNpcs().put(npcId, npcEntity);
	}
	
	public void questStartEndNPC(String name, int npcId, Location l, QuestImplementation quest)
	{		
		if (isNpcIdUsed(npcId)) 
		{
			this.server.getLogger().log(Level.WARNING, "NPC with that id already exists");
			return;
		}
		if (name.length() > 16) 
		{
			String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		BWorld bworld = new BWorld(l.getWorld());
		QuestStartEndNPCEntity npcEntity = new QuestStartEndNPCEntity(
				this.server.getMCServer(), bworld.getWorldServer(), name, 
				new ItemInWorldManager( bworld.getWorldServer()), quest, plugin);
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		bworld.getWorldServer().addEntity(npcEntity);
		plugin.npcManager.getNpcs().put(npcId, npcEntity);
	}
	
	public void speechNPC(String name, int npcId, Location l)
	{		
		if (isNpcIdUsed(npcId)) 
		{
			this.server.getLogger().log(Level.WARNING, "NPC with that id already exists");
			return;
		}
		if (name.length() > 16) 
		{
			String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		BWorld bworld = new BWorld(l.getWorld());
		SpeechNPC npcEntity = new SpeechNPC(
				this.server.getMCServer(), bworld.getWorldServer(), name, 
				new ItemInWorldManager( bworld.getWorldServer()), plugin, l);
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		bworld.getWorldServer().addEntity(npcEntity);
		plugin.npcManager.getNpcs().put(npcId, npcEntity);
	}
	
	private boolean isNpcIdUsed(int npcId)
	{
		if(plugin.npcManager.getNpcs().containsKey(npcId))
		{
			return true;
		}
		else
			return false;
	}

}
