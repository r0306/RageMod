package net.rageland.ragemod.entity;

public class Bounty {
	private String PlayerName;
	private double Amount;
	private boolean global;
	private String city;
	private String bountyGiver;
	
	public Bounty(){
		
	}

	public String getPlayerName() {
		return PlayerName;
	}

	public void setPlayerName(String playerName) {
		PlayerName = playerName;
	}

	public double getAmount() {
		return Amount;
	}

	public void setAmount(double d) {
		Amount = d;
	}

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBountyGiver() {
		return bountyGiver;
	}

	public void setBountyGiver(String bountyGiver) {
		this.bountyGiver = bountyGiver;
	}

}