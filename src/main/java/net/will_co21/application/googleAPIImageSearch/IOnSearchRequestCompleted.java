package net.will_co21.application.googleAPIImageSearch;

@FunctionalInterface
public interface IOnSearchRequestCompleted {
	public void onSearchRequestCompleted(boolean cancelled);
}
