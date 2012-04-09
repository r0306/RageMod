package net.rageland.ragemod.data;

import java.util.ArrayList;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import net.rageland.ragemod.entity.Bounty;
import net.rageland.ragemod.entity.BountySign;
import net.rageland.ragemod.utilities.Simple_Quicksort;

public class BountyHandler {
	private final ArrayList<Bounty> bountys = new ArrayList<Bounty>();
	private final ArrayList<BountySign> signs = new ArrayList<BountySign>();
	private String City; // if it is the global Handler insert "global" as name.
	private int currentpos;
	
	
	public boolean isGlobal(){
		if (City=="global"){
			return true;
		}
		return false;
	}
	
	public String getCity() {
		return City;
	}
	
	public Bounty lookupPlayer(String PlayerName){
		for (int i = 0; i<=this.bountys.size();i++){
			if (this.bountys.get(i).getPlayerName() == PlayerName){
				return this.bountys.get(i);
			}
		}
		return null;
		
	}
	
	private void sort(){
		Bounty[] temp = (Bounty[]) this.bountys.toArray();
		this.bountys.clear();
		int[] work = new int[temp.length];
		for (int i=0; i <= temp.length;i++){
			work[i]= Double.valueOf(temp[i].getAmount()).intValue();
		}
		Simple_Quicksort work2 = new Simple_Quicksort();
		work = work2.sort(work);
		work2 = null;
		for (int i=0; i <= temp.length;i++){
			for (int x=0; x <= temp.length;x++){
				if(temp[i].getAmount() == work[x] && this.bountys.get(x) == null){
					this.bountys.add(i, temp[x]);
					break;
				}
			}
		}
		
	}
	
	public boolean hasPlayer(String PlayerName){
		for (int i = 0; i<=this.bountys.size();i++){
			if (this.bountys.get(i).getPlayerName() == PlayerName){
				return true;
			}
		}
		return false;
	}
	
	public boolean addBounty(Bounty bounty){
		if (this.hasPlayer(bounty.getPlayerName())){
			return increaseBounty(bounty);
		}
		this.bountys.add(bounty);
		this.upadtesigns();
		return true;
		
	}
	
	public boolean increaseBounty(Bounty bounty){
		if (!this.hasPlayer(bounty.getPlayerName())){
			return addBounty(bounty);
		}
		Bounty temp = this.lookupPlayer(bounty.getPlayerName());
		temp.setAmount(temp.getAmount()+bounty.getAmount());
		return true;
	}
	
	public boolean removePlayer(String PlayerName){
		Bounty temp = this.lookupPlayer(PlayerName);
		this.bountys.remove(temp);
		this.upadtesigns();
		return true;
	}
	
	public Bounty removeBounty(String PlayerName){
		Bounty temp = this.lookupPlayer(PlayerName);
		this.bountys.remove(temp);
		return temp;
	}	
	
	public void addSign(Block block){
		this.currentpos++;
		BountySign temp = new BountySign(block,this.bountys.get(currentpos),currentpos,this);
		Sign temp2 = (Sign) block;
		temp2.setLine(0, "[Bounty]");
		temp2.setLine(1, this.bountys.get(currentpos).getPlayerName());
		temp2.setLine(2, String.valueOf(this.bountys.get(currentpos).getAmount()));
		temp2.setLine(3, "- "+this.bountys.get(currentpos).getBountyGiver());
		this.signs.add(temp);
	}
	
	public void upadtesigns(){
		this.currentpos=0;
		this.sort();
		for (int i=0; i <= signs.size();i++){
			if (this.bountys.get(i) != null)
			this.signs.get(i).bounty=this.bountys.get(i);
			Sign temp =((Sign)this.signs.get(i).block);
			temp.setLine(0, "[Bounty]");
			temp.setLine(1, this.bountys.get(i).getPlayerName());
			temp.setLine(2, String.valueOf(this.bountys.get(i).getAmount()));
			temp.setLine(3, "- "+this.bountys.get(i).getBountyGiver());
		}
	}
	
}