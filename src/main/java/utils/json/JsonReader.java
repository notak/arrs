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

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static utils.json.JsonReader.Type.ARRAY;
import static utils.json.JsonReader.Type.BOOLEAN;
import static utils.json.JsonReader.Type.NULL;
import static utils.json.JsonReader.Type.NUMBER;
import static utils.json.JsonReader.Type.OBJECT;
import static utils.json.JsonReader.Type.STRING;

import static utils.json.JsonTokener.Token.Type.*;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.BitSet;

import utils.json.JsonParser.JsonParserException;
import utils.json.JsonTokener.Token;

/** Streaming reader for JSON documents. */
public final class JsonReader {
	private JsonTokener tokener;
	private Token token;
	private BitSet states = new BitSet();
	private int stateIndex = 0;
	private boolean inObject;
	private boolean first = true;
	private StringBuilder key = new StringBuilder();

	/** The type of value that the {@link JsonReader} is positioned over. */
	public enum Type {
		OBJECT, ARRAY, STRING, NUMBER, BOOLEAN, NULL,
	};

	/** Create a {@link JsonReader} from an {@link InputStream}. */
	public static JsonReader from(InputStream in) throws JsonParserException {
		return new JsonReader(new JsonTokener(in));
	}

	/** Create a {@link JsonReader} from a {@link String}. */
	public static JsonReader from(String s) throws JsonParserException {
		return new JsonReader(new JsonTokener(new StringReader(s)));
	}

	/** Internal constructor. */
	JsonReader(JsonTokener tokener) throws JsonParserException {
		this.tokener = tokener;
		token = tokener.next();
	}

	/** Returns to the array or object structure above the current one, and
	 *  advances to the next key or value. */
	public boolean pop() throws JsonParserException {
		// CHECKSTYLE_OFF: EmptyStatement
		while (!next());
		// CHECKSTYLE_ON: EmptyStatement
		first = false;
		inObject = states.get(--stateIndex);
		return token.type() != TOK_EOF;
	}

	/** Returns the current type of the value. */
	public Type current() throws JsonParserException {
		return switch (token.type()) {
			case TOK_TRUE, TOK_FALSE -> BOOLEAN;
			case TOK_NULL -> NULL;
			case TOK_NUMBER, TOK_DOUBLE -> NUMBER;
			case TOK_STRING -> STRING;
			case TOK_OBJECT_START -> OBJECT;
			case TOK_ARRAY_START -> ARRAY;
		default -> {			
			throw tokenMismatchException(
				TOK_NULL, TOK_TRUE, TOK_FALSE, TOK_NUMBER, TOK_STRING,
				TOK_OBJECT_START, TOK_ARRAY_START);
		}};
	}

	/** Starts reading an object at the current value. */
	public void object() throws JsonParserException {
		if (token.type() != TOK_OBJECT_START)
			throw tokenMismatchException(TOK_OBJECT_START);
		states.set(stateIndex++, inObject);
		inObject = true;
		first = true;
	}

	/** Reads the key for the object at the current value. 
	 * Does not advance to the next value. */
	public String key() throws JsonParserException {
		if (!inObject)
			throw tokener.parseException(null, "Not reading an object", true);
		return key.toString();
	}

	/** Starts reading an array at the current value. */
	public void array() throws JsonParserException {
		if (token.type() != TOK_ARRAY_START)
			throw tokenMismatchException(TOK_ARRAY_START);
		states.set(stateIndex++, inObject);
		inObject = false;
		first = true;
	}

	/** Returns the current value. */
	public Object value() throws JsonParserException {
		return switch (token.type()) {
		case TOK_TRUE -> true;
		case TOK_FALSE -> false;
		case TOK_NULL -> null;
		case TOK_NUMBER, TOK_DOUBLE -> number();
		case TOK_STRING -> string();
		default -> {				
			throw tokenMismatchException(
				TOK_NULL, TOK_TRUE, TOK_FALSE, TOK_NUMBER, TOK_STRING);
		}};
	}

	/** Parses the current value as a null. */
	public void nul() throws JsonParserException {
		if (token.type() != TOK_NULL)
			throw tokenMismatchException(TOK_NULL);
	}

	/** Parses the current value as a string. */
	public String string() throws JsonParserException {
		if (token.type() == TOK_NULL) return null;
		if (token.type() != TOK_STRING)
			throw tokenMismatchException(TOK_NULL, TOK_STRING);
		return token.string();
	}

	/** Parses the current value as a boolean. */
	public boolean bool() throws JsonParserException {
		return switch (token.type()) {
		case TOK_TRUE -> true;
		case TOK_FALSE -> false;
		default -> {
			throw tokenMismatchException(TOK_TRUE, TOK_FALSE);
		}};
	}

	/** Parses the current value as a {@link Number}. */
	public Number number() throws JsonParserException {
		if (token.type() == TOK_NULL) return null;
		return new JsonLazyNumber(token.string(), token.isDouble());
	}

	/** Parses the current value as a long. */
	public long longVal() throws JsonParserException {
		var s = token.string();
		return token.isDouble() ? (long)parseDouble(s) : parseLong(s);
	}

	/** Parses the current value as an integer. */
	public int intVal() throws JsonParserException {
		var s = token.string();
		return token.isDouble() ? (int)parseDouble(s) : parseInt(s);
	}

	/** Parses the current value as a float. */
	public float floatVal() throws JsonParserException {
		var s = token.string();
		return parseFloat(s);
	}

	/** Parses the current value as a double. */
	public double doubleVal() throws JsonParserException {
		var s = token.string();
		return parseDouble(s);
	}

	/** Advance to the next value in this array or object. If no values remain,
	 * return to the parent array or object.
	 * 
	 * @return true if we still have values to read in this array or object,
	 *         false if we have completed this object (and implicitly moved back
	 *         to the parent array or object) */
	public boolean next() throws JsonParserException {
		if (stateIndex == 0) {
			throw tokener.parseException(null, "Unabled to call next() at the root", true); 
		}
		
		token = tokener.next();

		if (inObject) {
			if (token.type() == TOK_OBJECT_END) {
				inObject = states.get(--stateIndex);
				first = false;
				return false;
			}
			
			if (!first) {
				if (token.type() != TOK_COMMA)
					throw tokenMismatchException(TOK_COMMA, TOK_OBJECT_END);
				token = tokener.next();
			}

			if (token.type() != TOK_STRING)
				throw tokenMismatchException(TOK_STRING);
			key.setLength(0);
			key.append(token.string()); // reduce string garbage 
			if ((token = tokener.next()).type() != TOK_COLON)
				throw tokenMismatchException(TOK_COLON);
			token = tokener.next();
		} else {
			if (token.type() == TOK_ARRAY_END) {
				inObject = states.get(--stateIndex);
				first = false;
				return false;
			}
			if (!first) {
				if (token.type() != TOK_COMMA)
					throw tokenMismatchException(TOK_COMMA, TOK_ARRAY_END);
				token = tokener.next();
			}
		}

		var type = token.type();
		if (type != TOK_NULL && type != TOK_STRING
			&& type != TOK_NUMBER && type != TOK_DOUBLE 
			&& type != TOK_TRUE && type != TOK_FALSE 
			&& type != TOK_OBJECT_START && type != TOK_ARRAY_START)
			throw tokenMismatchException(TOK_NULL, TOK_STRING,
					TOK_NUMBER, TOK_TRUE, TOK_FALSE,
					TOK_OBJECT_START, TOK_ARRAY_START);

		first = false;
		
		return true;
	}
	
	private JsonParserException tokenMismatchException(Token.Type... t) {
		return tokener.parseException(null, "token mismatch (expected " + Arrays.toString(t)
						+ ", was " + token + ")",
				true);
	}
}
