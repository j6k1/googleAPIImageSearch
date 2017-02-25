package net.will_co21.application.googleAPIImageSearch;

import static org.junit.Assert.*;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class JSStringDecoderTest {
	@Test
	public void testParseJSStringStartNotDoubleQuotes() throws Exception {
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("a\"aaaabbbb\"", "a\"aaaabbbb\"".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("the position of input is illegal."));
		}
	}

	@Test
	public void testParseJSStringEndsWithFirstDoubleQuote() throws Exception {
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"", "\"".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringEndsWithBackslash() throws Exception {
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aa\\", "\"aa\\".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringInvalidHexEscapeFormat() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\x0m\\x0aあああいいい\"", "\"aaaああああ\\x0m\\x0aあああいいい\"".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"m\" was found."));
		}
	}

	@Test
	public void testParseJSStringSurrogateSecondHexEscapeIsNotTerminated() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\xE0\\xA0", "\"aaaああああ\\xE0\\xA0".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringFirstSurrogateHexEscapeLowerBound() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\x00aあああいいい\"", "\"aaaああああ\\x00aあああいいい\"".toCharArray(), 0);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\u0000aあああいいい"), 20)));
	}

	@Test
	public void testParseJSStringFirstSurrogateHexEscapeUpperBound() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\xFF\\nあああいいい\"", "\"aaaああああ\\xFF\\nあああいいい\"".toCharArray(), 0);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\u00FF\nあああいいい"), 21)));
	}

	@Test
	public void testParseJSStringUnicodeNumberIsNotTerminated() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\u{10000}", "\"aaaああああ\\u{10000}".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringInvalidUnicodeNumberFormat() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\u{100m}\\u{1000}aあああいいい\"", "\"aaaああああ\\u{100m}\\u{1000}aあああいいい\"".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"m\" was found."));
		}
	}

	@Test
	public void testParseJSStringSurrogateSecondUnicodeNumberIsNotTerminated() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\uE000\\uD800\\u{2000}", "\"aaaああああ\\uE000\\uD800\\u{2000}".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringFirstSurrogateUnicodeNumberLowerBound() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\u{0}aあああいいい\"", "\"aaaああああ\\u{0}aあああいいい\"".toCharArray(), 0);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\u0000aあああいいい"), 21)));
	}

	@Test
	public void testParseJSStringFirstSurrogateUnicodeNumberUpperBound() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\u{10FFFF}\\nあああいいい\"", "\"aaaああああ\\u{10FFFF}\\nあああいいい\"".toCharArray(), 0);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\uDBFF\uDFFF\nあああいいい"), 27)));
	}

	@Test
	public void testParseJSStringUnicodeEscapeIsNotTerminated() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\u679", "\"aaaああああ\\u679".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringInvalidUnicodeEscapeFormat() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\u0E0m\\uE000aあああいいい\"", "\"aaaああああ\\u0E0m\\uE000aあああいいい\"".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"m\" was found."));
		}
	}

	@Test
	public void testParseJSStringSurrogateSecondUnicodeEscapeIsNotTerminated() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\uE000\\uD800\\uDC0", "\"aaaああああ\\uE000\\uD800\\uDC0".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringFirstSurrogateUnicodeEscapeLowerBound() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\uE000\\uD800aあああいいい\"", "\"aaaああああ\\uE000\\uD800aあああいいい\"".toCharArray(), 0);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\uE000\uD800aあああいいい"), 28)));
	}

	@Test
	public void testParseJSStringFirstSurrogateUnicodeEscapeUpperBound() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\uE000\\uDBFF\\nあああいいい\"", "\"aaaああああ\\uE000\\uDBFF\\nあああいいい\"".toCharArray(), 0);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\uE000\uDBFF\nあああいいい"), 29)));
	}

	@Test
	public void testParseJSStringIncorrectStartOfSecondUnicodeEscape() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\uE000\\uD800\\aDC00いいい\"", "\"aaaああああ\\uE000\\uD800\\aDC00いいい\"".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"a\" was found."));
		}
	}

	@Test
	public void testParseJSStringInvalidSurrogateSecondUnicodeEscapeFormat() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\uD800\\uDC0m\\uE000aあああいいい\"", "\"aaaああああ\\uD800\\uDC0m\\uE000aあああいいい\"".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"m\" was found."));
		}
	}

	@Test
	public void testParseJSStringInvalidEscapeCharacter() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaああああ\\uD800\\gあああいいい\"", "\"aaaああああ\\uD800\\gあああいいい\"".toCharArray(), 0);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"g\" was found."));
		}
	}

	@Test
	public void testParseJSStringAllEscapeCharacter() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaいいいい\\u3042\\\"\\\\\\b\\f\\n\\r\\t\\v\\0aaa\"", "\"aaaいいいい\\u3042\\\"\\\\\\b\\f\\n\\r\\t\\v\\0aaa\"".toCharArray(), 0);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaいいいいあ\"\\\b\f\n\r\t\u000B\0aaa"), 36)));
	}

	@Test
	public void testParseJSStringNotSurrogateUnicodeEscape() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaいいいい\\uE000\\uD7FF\\uDC00あああ\"", "\"aaaいいいい\\uE000\\uD7FF\\uDC00あああ\"".toCharArray(), 0);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaいいいい\uE000\uD7FF\uDC00あああ"), 30)));
	}

	@Test
	public void testParseJSStringValidSurrogateUnicodeEscape() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"aaaいいいい\\uD800\\uDC00\\uD800\\uDFFF\\uDBFF\\uDC00\\uDBFF\\uDFFFえええ\"", "\"aaaいいいい\\uD800\\uDC00\\uD800\\uDFFF\\uDBFF\\uDC00\\uDBFF\\uDFFFえええ\"".toCharArray(), 0);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaいいいい\uD800\uDC00\uD800\uDFFF\uDBFF\uDC00\uDBFF\uDFFFえええ"), 60)));
	}


	@Test
	public void testParseJSStringInValidSurrogateUnicodeEscape() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("\"\\uD800\\uDBFF\\uD800\\uE000\\uDBFF\\uDBFF\\uDBFF\\uE000えええ\"", "\"\\uD800\\uDBFF\\uD800\\uE000\\uDBFF\\uDBFF\\uDBFF\\uE000えええ\"".toCharArray(), 0);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("\uD800\uDBFF\uD800\uE000\uDBFF\uDBFF\uDBFF\uE000えええ"), 53)));
	}


	@Test
	public void testParseJSStringStartNotDoubleQuotesUseOffset() throws Exception {
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaaa\"aaaabbbb\"", "aaaaaaaaaaa\"aaaabbbb\"".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("the position of input is illegal."));
		}
	}

	@Test
	public void testParseJSStringEndsWithFirstDoubleQuoteUseOffset() throws Exception {
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"", "aaaaaaaaaa\"".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringEndsWithBackslashUseOffset() throws Exception {
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aa\\", "aaaaaaaaaa\"aa\\".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}


	@Test
	public void testParseJSStringInvalidHexEscapeFormatUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\x0m\\x0aあああいいい\"", "aaaaaaaaaa\"aaaああああ\\x0m\\x0aあああいいい\"".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"m\" was found."));
		}
	}

	@Test
	public void testParseJSStringSurrogateSecondHexEscapeIsNotTerminatedUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\xE0\\xA0", "aaaaaaaaaa\"aaaああああ\\xE0\\xA0".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringFirstSurrogateHexEscapeLowerBoundUseOffset() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\x00aあああいいい\"", "aaaaaaaaaa\"aaaああああ\\x00aあああいいい\"".toCharArray(), 10);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\u0000aあああいいい"), 30)));
	}

	@Test
	public void testParseJSStringFirstSurrogateHexEscapeUpperBoundUseOffset() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\xFF\\nあああいいい\"", "aaaaaaaaaa\"aaaああああ\\xFF\\nあああいいい\"".toCharArray(), 10);
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\u00FF\nあああいいい"), 31)));
	}

	@Test
	public void testParseJSStringUnicodeNumberIsNotTerminatedUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\u{10000}", "aaaaaaaaaa\"aaaああああ\\u{10000}".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringInvalidUnicodeNumberFormatUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\u{100m}\\u{1000}aあああいいい\"", "aaaaaaaaaa\"aaaああああ\\u{100m}\\u{1000}aあああいいい\"".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"m\" was found."));
		}
	}

	@Test
	public void testParseJSStringSurrogateSecondUnicodeNumberIsNotTerminatedUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\uE000\\uD800\\u{2000}", "aaaaaaaaaa\"aaaああああ\\uE000\\uD800\\u{2000}".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringFirstSurrogateUnicodeNumberLowerBoundUseOffset() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\u{0}aあああいいい\"", "aaaaaaaaaa\"aaaああああ\\u{0}aあああいいい\"".toCharArray(), 10);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\u0000aあああいいい"), 31)));
	}

	@Test
	public void testParseJSStringFirstSurrogateUnicodeNumberUpperBoundUseOffset() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\u{10FFFF}\\nあああいいい\"", "aaaaaaaaaa\"aaaああああ\\u{10FFFF}\\nあああいいい\"".toCharArray(), 10);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\uDBFF\uDFFF\nあああいいい"), 37)));
	}

	@Test
	public void testParseJSStringUnicodeEscapeIsNotTerminatedUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\u679", "aaaaaaaaaa\"aaaああああ\\u679".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringInvalidUnicodeEscapeFormatUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\u0E0m\\uE000aあああいいい\"", "aaaaaaaaaa\"aaaああああ\\u0E0m\\uE000aあああいいい\"".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"m\" was found."));
		}
	}

	@Test
	public void testParseJSStringSurrogateSecondUnicodeEscapeIsNotTerminatedUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\uE000\\uD800\\uDC0", "aaaaaaaaaa\"aaaああああ\\uE000\\uD800\\uDC0".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("The format of this js string is not an js string format."));
		}
	}

	@Test
	public void testParseJSStringFirstSurrogateUnicodeEscapeLowerBoundUseOffset() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\uE000\\uD800aあああいいい\"", "aaaaaaaaaa\"aaaああああ\\uE000\\uD800aあああいいい\"".toCharArray(), 10);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\uE000\uD800aあああいいい"), 38)));
	}

	@Test
	public void testParseJSStringFirstSurrogateUnicodeEscapeUpperBoundUseOffset() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\uE000\\uDBFF\\nあああいいい\"", "aaaaaaaaaa\"aaaああああ\\uE000\\uDBFF\\nあああいいい\"".toCharArray(), 10);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaああああ\uE000\uDBFF\nあああいいい"), 39)));
	}

	@Test
	public void testParseJSStringIncorrectStartOfSecondUnicodeEscapeUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\uE000\\uD800\\aDC00いいい\"", "aaaaaaaaaa\"aaaああああ\\uE000\\uD800\\aDC00いいい\"".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"a\" was found."));
		}
	}

	@Test
	public void testParseJSStringInvalidSurrogateSecondUnicodeEscapeFormatUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\uD800\\uDC0m\\uE000aあああいいい\"", "aaaaaaaaaa\"aaaああああ\\uD800\\uDC0m\\uE000aあああいいい\"".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"m\" was found."));
		}
	}

	@Test
	public void testParseJSStringInvalidEscapeCharacterUseOffset() throws Exception
	{
		try {
			Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaああああ\\uD800\\gあああいいい\"", "aaaaaaaaaa\"aaaああああ\\uD800\\gあああいいい\"".toCharArray(), 10);
			result.fst.throwIfError();
			fail();
		} catch (JSStringFormatErrorException e) {
			assertThat(e.getMessage(), is("unexpected character \"g\" was found."));
		}
	}

	@Test
	public void testParseJSStringAllEscapeCharacterUseOffset() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaいいいい\\u3042\\\"\\\\\\b\\f\\n\\r\\t\\v\\0aaa\"", "aaaaaaaaaa\"aaaいいいい\\u3042\\\"\\\\\\b\\f\\n\\r\\t\\v\\0aaa\"".toCharArray(), 10);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaいいいいあ\"\\\b\f\n\r\t\u000B\0aaa"), 46)));
	}

	@Test
	public void testParseJSStringNotSurrogateUnicodeEscapeUseOffset() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaいいいい\\uE000\\uD7FF\\uDC00あああ\"", "aaaaaaaaaa\"aaaいいいい\\uE000\\uD7FF\\uDC00あああ\"".toCharArray(), 10);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaいいいい\uE000\uD7FF\uDC00あああ"), 40)));
	}

	@Test
	public void testParseJSStringValidSurrogateUnicodeEscapeUseOffset() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"aaaいいいい\\uD800\\uDC00\\uD800\\uDFFF\\uDBFF\\uDC00\\uDBFF\\uDFFFえええ\"", "aaaaaaaaaa\"aaaいいいい\\uD800\\uDC00\\uD800\\uDFFF\\uDBFF\\uDC00\\uDBFF\\uDFFFえええ\"".toCharArray(), 10);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("aaaいいいい\uD800\uDC00\uD800\uDFFF\uDBFF\uDC00\uDBFF\uDFFFえええ"), 70)));
	}

	@Test
	public void testParseJSStringInValidSurrogateUnicodeEscapeUseOffset() throws Exception
	{
		Pair<Result<String, Exception>, Integer> result = JSStringDecoder.decode("aaaaaaaaaa\"\\uD800\\uDBFF\\uD800\\uE000\\uDBFF\\uDBFF\\uDBFF\\uE000えええ\"", "aaaaaaaaaa\"\\uD800\\uDBFF\\uD800\\uE000\\uDBFF\\uDBFF\\uDBFF\\uE000えええ\"".toCharArray(), 10);
		result.fst.throwIfError();
		assertThat(result, is(new Pair<Result<String, Exception>, Integer>(Result.of("\uD800\uDBFF\uD800\uE000\uDBFF\uDBFF\uDBFF\uE000えええ"), 63)));
	}
}
