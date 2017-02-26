package net.will_co21.application.googleAPIImageSearch;

public class GoogleCustomSearchAPIDailyLimitExceededException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = -982542136263344387L;

	public GoogleCustomSearchAPIDailyLimitExceededException(String message)
	{
		super(message);
	}
}
