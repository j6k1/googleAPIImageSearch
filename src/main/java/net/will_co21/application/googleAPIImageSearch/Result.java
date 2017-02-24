package net.will_co21.application.googleAPIImageSearch;

import java.util.Optional;
import java.util.function.Consumer;

public class Result<T, E extends Exception> {
	protected Optional<T> result;
	protected Optional<E> error;

	private Result(Optional<T> result, Optional<E> error)
	{
		this.result = result;
		this.error = error;
	}

	public static <T> Result<T, Exception> of(T result)
	{
		if(result == null) throw new NullValueNotAllowedException("Null value was passed as a non-error value for the Result object.");

		return new Result<T, Exception>(Optional.of(result), Optional.empty());
	}

	public static <E extends Exception> Result<Object, E> error(E error)
	{
		if(error == null) throw new NullValueNotAllowedException("Null value was passed as a error value for the Result object.");

		return new Result<Object, E>(Optional.empty(), Optional.of(error));
	}

	public void throwIfError() throws E
	{
		if(error.isPresent()) throw error.get();
	}

	public void ifHasResult(Consumer<T> callback)
	{
		result.ifPresent(callback);
	}
}
