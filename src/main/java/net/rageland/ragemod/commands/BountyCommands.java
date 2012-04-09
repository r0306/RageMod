package net.rageland.ragemod.commands;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.data.OverAllBounty;
import net.rageland.ragemod.entity.Bounty;

public class BountyCommands {
	
	private RageMod plugin;
	private OverAllBounty oab;
	private Permission perms;
	private HashMap<String,String[]> BlockCommands = new HashMap<String,String[]>();

	public BountyCommands(RageMod plugin) {
		this.plugin = plugin;
		this.oab = plugin.Bounties;
	}
	
	public void addSignIssue(Player Issuer,String[] Command){
		if (perms.playerHas(Issuer, "ragemod.bounty.createsign") || perms.playerHas(Issuer, "ragemod.bounty.signcreate") || perms.playerHas(Issuer, "ragemod.*") || perms.playerHas(Issuer, "ragemod.bounty")) {
			this.BlockCommands.put(Issuer.getName(),Command);
			Issuer.sendMessage(ChatColor.GREEN + "You may no right-click a sign.");
		} else if (Issuer instanceof Player) {
			Issuer.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		} else {
			Issuer.sendMessage("Non-players can't issue bounties!");
		}	
	}
	
	public void resultBlockIssue(Player Issuer,Block block){
		if (!BlockCommands.containsKey((Issuer.getName()) == null)){
			if (BlockCommands.get((Issuer.getName()))[0]== "signcreate"){
				if (BlockCommands.get((Issuer.getName())).length > 1){
					this.createsign(block, BlockCommands.get((Issuer.getName()))[1], Issuer);
				}else{
					this.createsign(block, Issuer);
				}
			}
		}
	}
	
	public void createsign(Block block,String city,Player Issuer){ //admin one
		if (perms.playerHas(Issuer, "ragemod.bounty.createsign") || perms.playerHas(Issuer, "ragemod.*") || perms.playerHas(Issuer, "ragemod.bounty")) {
			this.oab.getBH(city).addSign(block);						
			Issuer.sendMessage(ChatColor.GREEN + "Bounty-Sign successfully added!");
		} else if (Issuer instanceof Player) {
			Issuer.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		} else {
			Issuer.sendMessage("Non-players can't issue bounties!");
		}
	}
	
	public void createsign(Block block,Player Issuer){
		if (perms.playerHas(Issuer, "ragemod.bounty.signcreate") || perms.playerHas(Issuer, "ragemod.*") || perms.playerHas(Issuer, "ragemod.bounty")) {
			this.oab.getBH(this.plugin.players.get(Issuer.getName()).townName).addSign(block);	
			Issuer.sendMessage(ChatColor.GREEN + "Bounty-Sign successfully added!");
		} else if (Issuer instanceof Player) {
			Issuer.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		} else {
			Issuer.sendMessage("Non-players can't issue bounties!");
		}
	}
	
	public void updateall(){
		this.oab.updateallsigns();
	}
	
	public void addBounty(Player Issuer,String Amount,String Target,String City){
		if (perms.playerHas(Issuer, "ragemod.bounty.add.normal") || perms.playerHas(Issuer, "ragemod.bounty") || perms.playerHas(Issuer, "ragemod.*")) {
			Bounty temp = new Bounty();
			EconomyResponse work = this.plugin.economy.bankWithdraw(Issuer.getName(),Double.parseDouble(Amount) );
			if (work.type == EconomyResponse.ResponseType.SUCCESS){		
			temp.setAmount(Double.parseDouble(Amount));
			temp.setPlayerName(Target);
			temp.setBountyGiver(Issuer.getName());
			temp.setCity(City);
			this.oab.getBH(City).addBounty(temp);
			Issuer.sendMessage(ChatColor.GREEN + "Bounty added!");
			}else{
				Issuer.sendMessage("You don't have enough money. You need to hunt him by yourself :(");	
			}
		} else if (Issuer instanceof Player ) {
			Issuer.sendMessage("You don't have permission to do that!");
		} else {
			Issuer.sendMessage("Non-players can't issue bounties!");
		}
	}
	
	public void addGlobalBounty(Player Issuer,String Amount,String Target){
		if (perms.playerHas(Issuer, "ragemod.bounty.add.global") || perms.playerHas(Issuer,  "ragemod.bounty") || perms.playerHas(Issuer, "ragemod.*")) {
			EconomyResponse work = this.plugin.economy.bankWithdraw(Issuer.getName(),Double.parseDouble(Amount) );
			if (work.type == EconomyResponse.ResponseType.SUCCESS){	
			Bounty temp = new Bounty();
			temp.setAmount(Integer.parseInt(Amount));
			temp.setPlayerName(Target);
			temp.setBountyGiver(Issuer.getName());
			temp.setCity("global");
			this.oab.getBH("global").addBounty(temp);
			Issuer.sendMessage(ChatColor.GREEN + "Bounty added!");
			}else{
				Issuer.sendMessage("You don't have enough money. You need to hunt him by yourself :(");	
			}
		} else if (Issuer instanceof Player) {
			Issuer.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		} else {
			Issuer.sendMessage("Non-players can't issue bounties!");
		}
	}
	
	public void removeAllBountys(Player Issuer, String Target){
		if (perms.playerHas(Issuer, "ragemod.bounty.removeall") || perms.playerHas(Issuer, "ragemod.*") || perms.playerHas(Issuer,  "ragemod.bounty")) {
			this.oab.removeallBountys(Target);
			Issuer.sendMessage(ChatColor.RED + "All Bountys removed!");
		} else if (Issuer instanceof Player) {
			Issuer.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		} else {
			Issuer.sendMessage("Non-players can't issue bounties!");
		}
	}
	
	

}
