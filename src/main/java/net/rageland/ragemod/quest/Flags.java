package net.rageland.ragemod.quest;

public class Flags
{
	private boolean repeatable;
	private boolean random;
	
	/**
	 * When a quest is created it is always active.
	 * @param repeatable
	 * @param random
	 */
	public Flags(boolean repeatable, boolean random)
	{
		this.repeatable = repeatable;
		this.random = random;
	}
	
	public boolean isRepeatable()
	{
		return repeatable;
	}
	
	public boolean isRandom()
	{
		return random;
	}
}
