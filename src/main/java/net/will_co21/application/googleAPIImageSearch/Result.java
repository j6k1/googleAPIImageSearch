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

	public static <T, E extends Exception> Result<T, E> error(E error, Class<T> klass)
	{
		if(error == null) throw new NullValueNotAllowedException("Null value was passed as a error value for the Result object.");

		return new Result<T, E>(Optional.empty(), Optional.of(error));
	}

	public void throwIfError() throws E
	{
		if(error.isPresent()) throw error.get();
	}

	public void ifHasResult(Consumer<T> callback)
	{
		result.ifPresent(callback);
	}

	public boolean hasResult()
	{
		return result.isPresent();
	}

	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Result)) return false;
		else if(this.result.isPresent() && ((Result<?, ?>)o).result.isPresent())
		{
			return this.result.get().equals(((Result<?, ?>)o).result.get());
		}
		else if(((Result<?, ?>)o).error.isPresent())
		{
			return this.error.get().equals(((Result<?, ?>)o).error.get());
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		if(result.isPresent()) return "result: " + result.get().toString();
		else return "error: " + error.get().getMessage();
	}
}
