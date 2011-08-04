package net.rageland.ragemod.quest;

public class Flags
{
	private boolean repeatable;
	private boolean active;
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
		this.active = true;
	}
	
	public boolean isRepeatable()
	{
		return repeatable;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public boolean isRandom()
	{
		return random;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}
	
}
