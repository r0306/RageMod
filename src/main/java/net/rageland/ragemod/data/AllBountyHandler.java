package net.rageland.ragemod.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.milkbowl.vault.Vault;
import net.rageland.ragemod.RageMod;
import net.rageland.ragemod.entity.Bounty;

@SuppressWarnings({"unused", "null"})
public class AllBountyHandler {
	private Map<String, BountyHandler> BH = new HashMap<String, BountyHandler>();
	private List<BountyHandler> BHL;
	private RageMod plugin; 
	private Vault vault;
	
	public void addPlayer(String PlayerName){
		Bounty[] temp = null; //TODO get from query
		if (temp.length==0){
			plugin.players.get(PlayerName).hasBounty=false;
			return;
		}
		for (int i=0;i<temp.length;i++){
			Bounty b = temp[i];
			this.BH.get(b.getCity()).addBounty(b);
		}
		plugin.players.get(PlayerName).hasBounty=true;
	}
	//TODO create a constructor which gets all the BountyHandlers/ creates all the BountyHandlers
	
	public void removePlayer(String PlayerName){
		BountyHandler[] temp = (BountyHandler[]) this.BHL.toArray();
		for (int i=0;i<temp.length;i++){
			temp[i].removePlayer(PlayerName);
		}
	}
	
	public BountyHandler getBH(String city){
		return this.BH.get(city);
	}
	
	public void updateallsigns(){
		for (int i=0;i<BHL.size();i++){
			if (BHL.get(i)!=null){
				BHL.get(i).upadtesigns();
			}
		}
	}
	
	public double removeallBountys(String PlayerName){
		double temp = 0;
		for (int i=0;i<BHL.size();i++){
			temp = temp + BHL.get(i).removeBounty(PlayerName).getAmount();
		}
		return temp;
	}
	

}