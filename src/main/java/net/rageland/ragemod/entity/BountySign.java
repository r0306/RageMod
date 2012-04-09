package net.rageland.ragemod.entity;

import net.rageland.ragemod.data.BountyHandler;

import org.bukkit.block.Block;

public class BountySign {
	public Block block;
	public Bounty bounty;
	public int num;
	public BountyHandler owner;
	
	public BountySign(Block block,Bounty bounty, int Number,BountyHandler BH){
		this.block=block;
		this.num=Number;
		this.bounty=bounty;
		this.owner=BH;
	}
	

}
