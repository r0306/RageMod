package net.rageland.ragemod.quest;

public class Flags
{
	private boolean isNonExclusive;
	private boolean isReserved;
	private boolean isRepeatable;
	
	/**
	 * When a quest is created it is always active.
	 * @param repeatable
	 * @param random
	 */
	public Flags(boolean isNonExclusive, boolean isReserved)
	{
		this.isNonExclusive = isNonExclusive;
		this.isReserved = isReserved;
	}
	
	public boolean isNonExclusive()
	{
		return isNonExclusive;
	}
	
	public boolean isReserved()
	{
		return isReserved;
	}

	public boolean isRepeatable() {
		// TODO Auto-generated method stub
		return isRepeatable;
	}
}
