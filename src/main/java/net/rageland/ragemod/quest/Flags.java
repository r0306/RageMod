package net.rageland.ragemod.quest;

public class Flags
{
	private boolean isNonExclusive;
	private boolean isReserved;
	private boolean isRepeatable;
	
	/**
	 * When a quest is created it is always active.
	 * @param isRepeatable 
	 * @param repeatable
	 * @param random
	 */
	public Flags(boolean isNonExclusive, boolean isReserved, boolean isRepeatable)
	{
		this.isNonExclusive = isNonExclusive;
		this.isReserved = isReserved;
		this.isRepeatable = isRepeatable;
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
		return isRepeatable;
	}
}
