package net.will_co21.application.googleAPIImageSearch;

import java.util.function.BooleanSupplier;

public class CancelStateReader implements BooleanSupplier {
	protected final ICanceled target;

	public CancelStateReader(ICanceled target)
	{
		this.target = target;
	}

	@Override
	public boolean getAsBoolean() {
		return target.isCancelled();
	}
}
