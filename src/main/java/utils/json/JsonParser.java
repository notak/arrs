/*
 * Copyright 2011 The nanojson Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package utils.json;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.Arrays.copyOf;
import static utils.json.JsonTokener.Token.Type.TOK_ARRAY_END;
import static utils.json.JsonTokener.Token.Type.TOK_COLON;
import static utils.json.JsonTokener.Token.Type.TOK_COMMA;
import static utils.json.JsonTokener.Token.Type.TOK_DOUBLE;
import static utils.json.JsonTokener.Token.Type.TOK_EOF;
import static utils.json.JsonTokener.Token.Type.TOK_OBJECT_END;
import static utils.json.JsonTokener.Token.Type.TOK_STRING;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URL;

import utils.json.JsonTokener.Token;
import utils.json.JsonTokener.Token.Type;

/**
 * Simple JSON parser.
 * 
 * <pre>
 * Object json = {@link JsonParser}.any().from("{\"a\":[true,false], \"b\":1}");
 * Number json = ({@link Number}){@link JsonParser}.any().from("123.456e7");
 * JsonObject json = {@link JsonParser}.object().from("{\"a\":[true,false], \"b\":1}");
 * JsonArray json = {@link JsonParser}.array().from("[1, {\"a\":[true,false], \"b\":1}]");
 * </pre>
 */
public final class JsonParser {
	/** Thrown when the {@link JsonParser} encounters malformed JSON. */
	public static class JsonParserException extends Exception {
		private static final long serialVersionUID = 1L;
		public final int linePos;
		public final int charPos;
		public final int charOffset;

		JsonParserException(Exception e, String message, int linePos, int charPos, int charOffset) {
			super(message, e);
			this.linePos = linePos;
			this.charPos = charPos;
			this.charOffset = charOffset;
		}
	}

	/** Returns a type-safe parser context for a {@link JsonObject}, 
	 * {@link JsonArray} or "any" type from which you can parse a {@link String} 
	 * or a {@link Reader}.
	 *
	 * @param <T> The parsed type. */
	public static final class JsonParserContext<T> {
		private final Class<T> clazz;
		private boolean lazyNumbers;

		JsonParserContext(Class<T> clazz) {
			this.clazz = clazz;
		}

		/** Parses numbers lazily, allowing us to defer some of the cost of
		 * number construction until later. */
		public JsonParserContext<T> withLazyNumbers() {
			lazyNumbers = true;
			return this;
		}

		/** Parses the current JSON type from a {@link String}. */
		public T from(String s) throws JsonParserException {
			return new JsonParser(new JsonTokener(new StringReader(s)), lazyNumbers).parse(clazz);
		}

		/** Parses the current` JSON type from a {@link Reader}. */
		public T from(Reader r) throws JsonParserException {
			return new JsonParser(new JsonTokener(r), lazyNumbers).parse(clazz);
		}

		/** Parses the current JSON type from a {@link URL}. */
		public T from(URL url) throws JsonParserException {
			try (var stm = url.openStream()) {
				return from(stm);
			} catch (IOException e) {
				throw new JsonParserException(e, "IOException opening URL", 1, 1, 0);
			}
		}

		/** Parses the current JSON type from a {@link InputStream}. 
		 * Detects the encoding from the input stream. */
		public T from(InputStream stm) throws JsonParserException {
			return new JsonParser(new JsonTokener(stm), lazyNumbers).parse(clazz);
		}
	}

	private final JsonTokener tokener;
	private final boolean lazyNumbers;

	JsonParser(JsonTokener tokener, boolean lazyNumbers) 
	throws JsonParserException {
		this.tokener = tokener;
		this.lazyNumbers = lazyNumbers;
	}

	/** Parses a {@link JsonObject} from a source.
	 * 
	 * <pre>
	 * JsonObject json = {@link JsonParser}.object().from("{\"a\":[true,false], \"b\":1}");
	 * </pre> */
	public static JsonParserContext<JsonObject> object() {
		return new JsonParserContext<>(JsonObject.class);
	}

	/** Parses a {@link JsonArray} from a source.
	 * 
	 * <pre>
	 * JsonArray json = {@link JsonParser}.array().from("[1, {\"a\":[true,false], \"b\":1}]");
	 * </pre> */
	public static JsonParserContext<Object[]> array() {
		return new JsonParserContext<>(Object[].class);
	}

	/** Parses any object from a source. For any valid JSON, returns either a 
	 * null (for the JSON string 'null'), a {@link String}, a {@link Number}, 
	 * a {@link Boolean}, a {@link JsonObject} or a {@link JsonArray}.
	 * 
	 * <pre>
	 * Object json = {@link JsonParser}.any().from("{\"a\":[true,false], \"b\":1}");
	 * Number json = ({@link Number}){@link JsonParser}.any().from("123.456e7");
	 * </pre> */
	public static JsonParserContext<Object> any() {
		return new JsonParserContext<>(Object.class);
	}

	/** Parse a single JSON value from the string, expecting an EOF at the end. */
	<T> T parse(Class<T> cls) throws JsonParserException {
		var parsed = parse();
		expectType(TOK_EOF, tokener.next());
		if (cls != Object.class && (parsed == null || !cls.isAssignableFrom(parsed.getClass())))
			throw tokener.parseException(
					"JSON did not contain the correct type, expected " + cls.getSimpleName() + ".", 
					true);
		return cls.cast(parsed);
	}
	
	private Object parse() throws JsonParserException {
		return parse(tokener.next());
	}

	/** Consumes a token, first eating up any whitespace ahead of it. 
	 * Note that number tokens are not necessarily valid numbers. */
	private Object parse(Token tok) throws JsonParserException {
		return switch (tok.type()) {
			case TOK_ARRAY_START -> { // Inlined function to avoid additional stack
				tok = tokener.next();
				if (tok.type() == TOK_ARRAY_END) yield new Object[0];
				var list = new Object[10];
				var pos = 0;
				while (true) {
					if (pos >= list.length) list = copyOf(list, list.length * 2);
					list[pos++] = parse(tok);
					if (TOK_ARRAY_END == (tok = tokener.next()).type()) break;
					if (TOK_COMMA != tok.type()) throw tokener.parseException(
						"Expected a comma or end of the array instead of " + tok, true);
					tok = tokener.next();
				}
				yield list.length==pos ? list : copyOf(list, pos);
			}
			case TOK_OBJECT_START -> { // Inlined function to avoid additional stack
				var map = new JsonObject();
				tok = tokener.next();
				if (tok.type() == TOK_OBJECT_END) yield map;
				while (true) {
					expectType(TOK_STRING, tok);
					String key = tok.string();
					tok = tokener.next();
					expectType(TOK_COLON, tok);
					map.put(key, parse());
					tok = tokener.next();
					if (tok.type() == TOK_OBJECT_END) yield map;
					expectType(TOK_COMMA, tok);
					tok = tokener.next();
				}
			}
			case TOK_TRUE -> TRUE;
			case TOK_FALSE -> FALSE;
			case TOK_NULL -> null;
			case TOK_STRING -> tok.string();
			case TOK_NUMBER, TOK_DOUBLE -> 
				!lazyNumbers ? parseNumber(tok)
					: new JsonLazyNumber(tok.string(), tok.isDouble());
			default -> throw tokener.parseException("Unexpected token " + tok.type());
		};
	}
	
	private void expectType(Type type, Token token) throws JsonParserException {
		if (type != token.type()) throw tokener.parseException(
				"Expected " + type +", got " + token.type(), true);
	}

	private Number parseNumber(Token tok) throws JsonParserException {
		try {
			var chars = tok.text();
			// Quick parse for single-digits
			if (chars.length == 1) return chars[0] - '0';
			
			var negative = chars[0] == '-';
			if (chars.length == 2 && negative) return '0' - chars[1];

			if (tok.type()==TOK_DOUBLE) return parseDouble(tok.string());

			// HACK: Attempt to parse using the approximate best type for this
			int length = negative ? chars.length - 1 : chars.length;
			int firstDigit = chars[negative ? 1 : 0];
			
			// CHECKSTYLE_OFF: MagicNumber
			if (length < 10 || (length == 10 && firstDigit < '2')) // 2 147 483 647
				return parseInt(tok.string());
			if (length < 19 || (length == 19 && firstDigit < '9')) // 9 223 372 036 854 775 807
				return parseLong(tok.string());
			// CHECKSTYLE_ON: MagicNumber
			return new BigInteger(tok.string());
		} catch (NumberFormatException e) {
			throw tokener.parseException(e, "Malformed number: " + tok.string(), true);
		}
	}
}
