package net.will_co21.application.googleAPIImageSearch;

public class Pair<F,S> {
	public final F fst;
	public final S snd;

	public Pair(F fst, S snd)
	{
		this.fst = fst;
		this.snd = snd;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Pair)) return false;
		else return this.fst.equals(((Pair)obj).fst) && this.snd.equals(((Pair)obj).snd);
	}

	@Override
	public String toString()
	{
		return this.fst.toString() + ", " + this.snd.toString();
	}
}
