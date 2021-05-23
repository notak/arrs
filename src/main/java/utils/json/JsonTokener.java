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

import static java.lang.Math.max;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static utils.json.CharsetDetect.detectCharset;
import static utils.json.JsonTokener.Token.Type.TOK_ARRAY_END;
import static utils.json.JsonTokener.Token.Type.TOK_ARRAY_START;
import static utils.json.JsonTokener.Token.Type.TOK_COLON;
import static utils.json.JsonTokener.Token.Type.TOK_COMMA;
import static utils.json.JsonTokener.Token.Type.TOK_DOUBLE;
import static utils.json.JsonTokener.Token.Type.TOK_EOF;
import static utils.json.JsonTokener.Token.Type.TOK_FALSE;
import static utils.json.JsonTokener.Token.Type.TOK_NULL;
import static utils.json.JsonTokener.Token.Type.TOK_NUMBER;
import static utils.json.JsonTokener.Token.Type.TOK_OBJECT_END;
import static utils.json.JsonTokener.Token.Type.TOK_OBJECT_START;
import static utils.json.JsonTokener.Token.Type.TOK_STRING;
import static utils.json.JsonTokener.Token.Type.TOK_TRUE;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import utils.json.JsonParser.JsonParserException;

/** Internal class for tokenizing JSON. 
 * Used by both {@link JsonParser} and {@link JsonReader}. */
final class JsonTokener {
	// Used by tests
	static final int BUFFER_SIZE = 32 * 1024;

	static final int BUFFER_ROOM = 256;
	static final int MAX_ESCAPE = 5; // uXXXX (don't need the leading slash)

	static final char[] TRUE_CHARS = { 'r', 'u', 'e' };
	static final char[] FALSE_CHARS = { 'a', 'l', 's', 'e' };
	static final char[] NULL_CHARS = { 'u', 'l', 'l' };
	
	static final char[] EMPTY = {};

	public static final record Token(Type type, char[] text) {
		public static enum Type {
			TOK_EOF, TOK_COMMA, TOK_COLON, TOK_OBJECT_END, TOK_ARRAY_END,
			TOK_NULL, TOK_TRUE, TOK_FALSE, TOK_STRING, TOK_NUMBER, TOK_DOUBLE,
			TOK_OBJECT_START, TOK_ARRAY_START,
		}
		
		public boolean isValueStart() {
			return type != TOK_EOF && type != TOK_COMMA 
				&& type != TOK_COLON && type != TOK_OBJECT_END
				&& type != TOK_ARRAY_END;
		}

		public Token {}
		public Token(Type type) {
			this(type, EMPTY);
		}
		public String string() {
			return new String(text);
		}
		public boolean isDouble() {
			return type==TOK_DOUBLE;
		}
	}
	
	public static final Token EOF = new Token(TOK_EOF);
	public static final Token COMMA = new Token(TOK_COMMA);
	public static final Token COLON = new Token(TOK_COLON);
	public static final Token OBJECT_START = new Token(TOK_OBJECT_START);
	public static final Token ARRAY_START = new Token(TOK_ARRAY_START);
	public static final Token OBJECT_END = new Token(TOK_OBJECT_END);
	public static final Token ARRAY_END = new Token(TOK_ARRAY_END);
	public static final Token NULL = new Token(TOK_NULL);
	public static final Token TRUE = new Token(TOK_TRUE);
	public static final Token FALSE = new Token(TOK_FALSE);

	private int linePos = 1, rowPos, charOffset, utf8adjust;
	private int tokenCharPos, tokenCharOffset;

	private boolean eof;
	private int index;
	private final Reader reader;
	private final char[] buffer = new char[BUFFER_SIZE];
	private int bufferLength;

	JsonTokener(Reader reader) throws JsonParserException {
		this.reader = reader;
		init();
	}
	
	JsonTokener(InputStream stm) throws JsonParserException {
		final var buff = 
			stm instanceof BufferedInputStream bis ? bis
			: stm instanceof ByteArrayInputStream bais ? bais 
			: new BufferedInputStream(stm);

		var charset = detectCharset(buff);
		if (charset == null) throw parseException("charset not detected");
		reader = new InputStreamReader(buff, charset);
		init();
	}

	private void init() throws JsonParserException {
		eof = refillBuffer();
		consumeWhitespace();
	}

	/** Expects a given string at the current position. */
	void consumeKeyword(char first, char[] expected) throws JsonParserException {
		if (ensureBuffer(expected.length) < expected.length) {
			throw helpfulException(first, expected, 0);
		}

		for (int i = 0; i < expected.length; i++)
			if (buffer[index++] != expected[i])
				throw helpfulException(first, expected, i);

		fixupAfterRawBufferRead();

		// The token should end with something other than an ASCII letter
		if (isAsciiLetter(peekChar())) throw helpfulException(
			first, expected, expected.length);
	}

	/** Steps through to the end of the current number token (a non-digit token) */
	Token consumeTokenNumber(char savedChar) throws JsonParserException {
		var out = SizedChars.create(16, savedChar);
		var isDouble = false;

		// The JSON spec is way stricter about number formats than
		// Double.parseDouble(). This is a hand-rolled pseudo-parser that
		// verifies numbers we read.
		int state = switch(savedChar) {
			case '-' -> 1;
			case '0' -> 3;
			default -> 2;
		};
		
		outer: while (true) {
			int n = ensureBuffer(BUFFER_ROOM);
			if (n == 0) break outer;

			for (int i = 0; i < n; i++) {
				char nc = buffer[index];
				if (!isDigitCharacter(nc)) break outer;

				int ns = -1;
				sw:
				switch (state) {
				case 1: // start leading negative
					if (nc == '0') {
						ns = 3; break sw;
					}
					if (nc > '0' && nc <= '9') {
						ns = 2; break sw;
					}
					break;
				case 2: // no leading zero
				case 3: // leading zero
					if ((nc >= '0' && nc <= '9') && state == 2) {
						ns = 2; break sw;
					}
					if (nc == '.') {
						isDouble = true;
						ns = 4; break sw;
					}
					if (nc == 'e' || nc == 'E') {
						isDouble = true;
						ns = 6; break sw;
					}
					break;
				case 4: // after period
				case 5: // after period, one digit read
					if (nc >= '0' && nc <= '9') {
						ns = 5; break sw;
					}
					if ((nc == 'e' || nc == 'E') && state == 5) {
						isDouble = true;
						ns = 6; break sw;
					}
					break;
				case 6: // after exponent
				case 7: // after exponent and sign
					if (nc == '+' || nc == '-' && state == 6) {
						ns = 7; break sw;
					}
					if (nc >= '0' && nc <= '9') {
						ns = 8; break sw;
					}
					break;
				case 8: // after digits
					if (nc >= '0' && nc <= '9') {
						ns = 8; break sw;
					}
					break;
				default:
					assert false : "Impossible"; // will throw malformed number
				}
				out = SizedChars.append(out, nc);
				index++;
				if (ns == -1)
					throw parseException("Malformed number: " + new String(out), true);
				state = ns;
			}
		}
		
		if (state != 2 && state != 3 && state != 5 && state != 8)
			throw parseException("Malformed number: " + new String(out), true);
		
		// Special case for -0
		if (state == 3 && savedChar == '-')
			isDouble = true;
		
		fixupAfterRawBufferRead();
		return new Token(
			isDouble ? TOK_DOUBLE : TOK_NUMBER, 
			SizedChars.toChars(out)
		);
	}

	private int consumeEscapeSequence() throws JsonParserException {
		char escape = buffer[index++];
		return switch (escape) {
			case 'b' -> '\b';
			case 'f' -> '\f';
			case 'n' -> '\n';
			case 'r' -> '\r';
			case 't' -> '\t';
			case '"', '/', '\\' -> escape;
			case 'u' -> {
				int escaped = 0;

				for (int j = 0; j < 4; j++) {
					escaped <<= 4;
					int digit = buffer[index++];
					if (digit >= '0' && digit <= '9') {
						escaped |= (digit - '0');
					} else if (digit >= 'A' && digit <= 'F') {
						escaped |= (digit - 'A') + 10;
					} else if (digit >= 'a' && digit <= 'f') {
						escaped |= (digit - 'a') + 10;
					} else {
						throw parseException(
							"Expected unicode hex escape character: "
							+ (char)digit + " (" + digit + ")");
					}
				}
				yield escaped;
			}
			default -> throw parseException("Invalid escape: \\" + escape);
		};
	}
	
	/** Consume to next unescaped double quote */
	private Token consumeTokenString() throws JsonParserException {
		var out = SizedChars.create(128);
		var complete = false;

		while (!complete) {
			int end = 0, n = 0;
			if (index >= end) {
				n = ensureBuffer(BUFFER_ROOM);
				if (n == 0) throw parseException(
					"String was not terminated before end of input", true);
		
				end = index + n;
			}
			
			char c = stringChar();
			
			if (c=='\"') break;
			
			int processed = c;

			if (c=='\\') {
				// Ensure that we have at least MAX_ESCAPE here in the buffer
				if (end - index < MAX_ESCAPE) {
					// Re-adjust the buffer end, unlikely path
					n = ensureBuffer(MAX_ESCAPE);
					end = index + n;
					// Make sure that there's enough chars for a \\uXXXX escape
					if (buffer[index] == 'u' && n < MAX_ESCAPE) {
						index = bufferLength; // Reset index to last valid location
						throw parseException(
							"EOF encountered in the middle of a string escape");
					}
				}
				processed = consumeEscapeSequence();
			}

			out = SizedChars.append(out, processed);
			
			if (index > bufferLength) {
				index = bufferLength; // Reset index to last valid location
				throw parseException(
					"EOF encountered in the middle of a string escape");
			}
		}
		fixupAfterRawBufferRead();
		return new Token(TOK_STRING, SizedChars.toChars(out));
	}

	/**
	 * Advances a character, throwing if it is illegal in the context of a JSON string.
	 */
	private char stringChar() throws JsonParserException {
		char c = buffer[index++];
		if (c < 32)
			throwControlCharacterException(c);
		return c;
	}

	private void throwControlCharacterException(char c) throws JsonParserException {
		// Need to ensure that we position this at the correct location for the error
		if (c == '\n') {
			linePos++;
			rowPos = index + 1 + charOffset;
			utf8adjust = 0;
		}
		throw parseException(
				"Strings may not contain control characters: 0x" + Integer.toString(c, 16));
	}

	/** Quick test for digits. */
	private static boolean isDigit(int c) {
		return (c >= '0' && c <= '9');
	}

	/** Quick test for digit characters. */
	private static boolean isDigitCharacter(int c) {
		return isDigit(c) 
			|| c == 'e' || c == 'E' || c == '.' || c == '+' || c == '-';
	}

	/** Quick test for whitespace characters. */
	private static boolean isWhitespace(int c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}

	/** Quick test for ASCII letter characters. */
	private static boolean isAsciiLetter(int c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	/** Returns true if EOF. */
	private boolean refillBuffer() throws JsonParserException {
		try {
			int r = reader.read(buffer, 0, buffer.length);
			if (r <= 0) return true;
			charOffset += bufferLength;
			index = 0;
			bufferLength = r;
			return false;
		} catch (IOException e) {
			throw parseException(e, "IOException", true);
		}
	}

	/** Peek one char ahead, don't advance, returns {@code EOF} (-1) on end of input. */
	private int peekChar() {
		return eof ? -1 : buffer[index];
	}

	/** Ensures that there is enough room in the buffer to directly access the 
	 * next N chars via buffer[]. */
	private int ensureBuffer(int n) throws JsonParserException {
		if (bufferLength - n >= index) return n; // It's fine

		// Need to read more, but also retain existing buffer
		if (index > 0) {
			charOffset += index;
			bufferLength = bufferLength - index;
			arraycopy(buffer, index, buffer, 0, bufferLength);
			index = 0;
		}
		try {
			while (buffer.length > bufferLength) {
				int r = reader.read(buffer, bufferLength, buffer.length - bufferLength);
				if (r <= 0) {
					return bufferLength - index;
				}
				bufferLength += r;
				if (bufferLength > n)
					return bufferLength - index;
			}

			// Should be impossible
			assert false : "Unexpected internal error";
			throw new IOException("Unexpected internal error");
		} catch (IOException e) {
			throw parseException(e, "IOException", true);
		}
	}

	/** Advance one character ahead, or return {@code EOF} (-1) on end of input. */
	private int advanceChar() throws JsonParserException {
		if (eof)
			return -1;

		int c = buffer[index];
		if (c == '\n') {
			linePos++;
			rowPos = index + 1 + charOffset;
			utf8adjust = 0;
		}

		// Prepare for next read
		if (++index >= bufferLength) eof = refillBuffer();

		return c;
	}
	
	int advanceCharFast() {
		int c = buffer[index];
		if (c == '\n') {
			linePos++;
			rowPos = index + 1 + charOffset;
			utf8adjust = 0;
		}

		index++;
		return c;
	}
	
	private void consumeWhitespace() throws JsonParserException {
		int n;
		do {
			n = ensureBuffer(BUFFER_ROOM);
			for (int i = 0; i < n; i++) {
				char c = buffer[index];
				if (!isWhitespace(c)) {
					fixupAfterRawBufferRead();
					return;
				}
				if (c == '\n') {
					linePos++;
					rowPos = index + 1 + charOffset;
					utf8adjust = 0;
				}
				index++;
			}
		} while (n > 0);
		eof = true;
	}
	
	/** Consumes a token, first eating up any whitespace ahead of it. 
	 * Note that number tokens are not necessarily valid numbers. */
	Token next() throws JsonParserException {
		int c = advanceChar();
		while (isWhitespace(c))
			c = advanceChar();

		tokenCharPos = index + charOffset - rowPos - utf8adjust;
		tokenCharOffset = charOffset + index;
		
		return switch (c) {
		case -1 -> EOF;
		case '[' -> ARRAY_START;
		case ']' -> ARRAY_END;
		case ',' -> COMMA;
		case ':' -> COLON;
		case '{' -> OBJECT_START;
		case '}' -> OBJECT_END;
		case 't' -> {
			consumeKeyword((char)c, TRUE_CHARS);
			yield TRUE;
		}
		case 'f' -> {
			consumeKeyword((char)c, FALSE_CHARS);
			yield FALSE;
		}
		case 'n' -> {
			consumeKeyword((char)c, NULL_CHARS);
			yield NULL;
		}
		case '\"' -> {
			yield consumeTokenString();
		}
		case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'-> {
			yield consumeTokenNumber((char)c);
		}
		case '+', '.' ->
			throw parseException("Numbers may not start with '" + (char)c + "'", true);
		default ->
			throw isAsciiLetter(c) ? helpfulException((char)c, null, 0)
				: parseException("Unexpected character: " + (char)c, true);
		};
	}

	int tokenChar() throws JsonParserException {
		int c = advanceChar();
		while (isWhitespace(c)) c = advanceChar();
		return c;
	}

	/** Helper function to fixup eof after reading buffer directly. */
	void fixupAfterRawBufferRead() throws JsonParserException {
		if (index >= bufferLength) eof = refillBuffer();
	}

	/** Throws a helpful exception based on the current alphanumeric token. */
	JsonParserException helpfulException(char first, char[] expected, int failurePos)
	throws JsonParserException {
		// Build the first part of the token
		var errorToken = first
			+ (expected == null ? "" : new String(expected, 0, failurePos));

		// Consume the whole pseudo-token to make a better error message
		while (isAsciiLetter(peekChar()) && errorToken.length() < 15)
			errorToken += (char)advanceChar();
		
		var suggestion = expected == null ? "" 
			: ". Did you mean '" + first + new String(expected) + "'?";

		return parseException(
			"Unexpected token '" + errorToken + "'" + suggestion, true);
	}

	/** Creates a {@link JsonParserException} and fills it from the current line 
	 * and char position. */
	JsonParserException parseException(String message) {
		return parseException(null, message, false);
	}

	/** Creates a {@link JsonParserException} and fills it from the current line 
	 * and char position. */
	JsonParserException parseException(String message, boolean tokenPos) {
		return parseException(null, message, tokenPos);
	}

	/** Creates a {@link JsonParserException} and fills it from the current line 
	 * and char position. */
	JsonParserException parseException(Exception e, String msg, boolean tokenPos) {
		int charPos = tokenPos ? tokenCharPos
			: max(1, index + charOffset - rowPos - utf8adjust);
		int offset = tokenPos ? tokenCharOffset : index + charOffset;

		return new JsonParserException(e, 
			format("%s on line %s, char %s", msg, linePos, charPos),
			linePos, charPos, offset);
	}
}
