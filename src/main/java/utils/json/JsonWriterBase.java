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

import static java.lang.Character.toCodePoint;
import static java.lang.Integer.toHexString;
import static java.lang.reflect.Array.getLength;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;

import utils.json.JsonWriter.JsonWriterException;

/**
 * Internal class that handles emitting to an {@link Appendable}. Users only see
 * the public subclasses, {@link JsonStringWriter} and
 * {@link JsonAppendableWriter}.
 * 
 * @param <SELF>
 *            A subclass of {@link JsonWriterBase}.
 */
class JsonWriterBase<SELF extends JsonWriterBase<SELF>> implements
		JsonSink<SELF> {
	static final int BUFFER_SIZE = 10 * 1024;
	private static final char[] NULL = { 'n', 'u', 'l', 'l' };
	private static final char[] TRUE = { 't', 'r', 'u', 'e' };
	private static final char[] FALSE = { 'f', 'a', 'l', 's', 'e' };
	private static final char[] HEX = { 
		'0', '1', '2', '3', '4', '5', '6', '7', '8',
		'9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final char[] UNICODE_SMALL = { '\\', 'u', '0', '0' };
	private static final char[] UNICODE_LARGE = { '\\', 'u' };
	protected final Appendable appendable;
	protected final OutputStream out;
	protected final boolean utf8;
	private final StringBuilder buffer;
	private final byte[] bb;
	private int bo = 0;
	private BitSet states = new BitSet();
	private int stateIndex = 0;
	private boolean first = true;
	private boolean inObject;

	/** Sequence to use for indenting. */
	private String indentString;

	/** Current indent amount. */
	private int indent = 0;

	JsonWriterBase(Appendable appendable, String indent) {
		this.appendable = appendable;
		this.out = null;
		this.indentString = indent;
		utf8 = false;
		buffer = new StringBuilder(BUFFER_SIZE);
		bb = null;
	}

	JsonWriterBase(OutputStream out, String indent) {
		this.appendable = null;
		this.out = out;
		this.indentString = indent;
		utf8 = true;
		buffer = null;
		bb = new byte[BUFFER_SIZE];
	}

	/** This is guaranteed to be safe as the type of "this" will always be the
	 * type of "SELF". */
	@SuppressWarnings("unchecked")
	private SELF castThis() {
		return (SELF) this;
	}

	@Override
	public <T> SELF array(T[] c) {
		return array(null, c);
	}

	@Override
	public SELF array(Collection<?> c) {
		return array(null, c);
	}

	@Override
	public <T> SELF array(String key, T[] c) {
		if (key == null) array();
		else array(key);

		for (var o : c) value(o);

		return end();
	}

	@Override
	public SELF array(String key, Collection<?> c) {
		if (key == null)
			array();
		else
			array(key);

		for (Object o : c) {
			value(o);
		}

		return end();
	}

	@Override
	public SELF object(Map<?, ?> map) {
		return object(null, map);
	}

	@Override
	public SELF object(String key, Map<?, ?> map) {
		if (key == null)
			object();
		else
			object(key);

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Object o = entry.getValue();
			if (!(entry.getKey() instanceof String))
				throw new JsonWriterException("Invalid key type for map: "
						+ (entry.getKey() == null ? "null" : entry.getKey()
								.getClass()));
			String k = (String) entry.getKey();
			value(k, o);
		}

		return end();
	}

	@Override
	public SELF nul() {
		preValue();
		raw(NULL);
		return castThis();
	}

	@Override
	public SELF nul(String key) {
		preValue(key);
		raw(NULL);
		return castThis();
	}

	@Override
	public SELF value(Object o) {
		if (o == null)
			return nul();
		else if (o instanceof String s) return value(s);
		else if (o instanceof Number n) return value((n));
		else if (o instanceof Boolean b) return value((boolean) b);
		else if (o instanceof Collection)
			return array((Collection<?>) o);
		else if (o instanceof Map)
			return object((Map<?, ?>) o);
		else if (o.getClass().isArray()) {
			int length = getLength(o);
			array();
			for (int i = 0; i < length; i++)
				value(Array.get(o, i));
			return end();
		} else
			throw new JsonWriterException(
				"Unable to handle type: " + o.getClass());
	}

	@Override
	public SELF value(String key, Object o) {
		if (o == null)
			return nul(key);
		else if (o instanceof String s) return value(key, s);
		else if (o instanceof Number n) return value(key, n);
		else if (o instanceof Boolean b) return value(key, (boolean) b);
		else if (o instanceof Collection)
			return array(key, (Collection<?>) o);
		else if (o instanceof Map)
			return object(key, (Map<?, ?>) o);
		else if (o.getClass().isArray()) {
			int length = getLength(o);
			array(key);
			for (int i = 0; i < length; i++)
				value(Array.get(o, i));
			return end();
		} else
			throw new JsonWriterException("Unable to handle type: "
					+ o.getClass());
	}

	@Override
	public SELF value(String s) {
		if (s == null)
			return nul();
		preValue();
		emitStringValue(s);
		return castThis();
	}

	@Override
	public SELF value(int i) {
		preValue();
		raw(Integer.toString(i));
		return castThis();
	}

	@Override
	public SELF value(long l) {
		preValue();
		raw(Long.toString(l));
		return castThis();
	}

	@Override
	public SELF value(boolean b) {
		preValue();
		raw(b ? TRUE : FALSE);
		return castThis();
	}

	@Override
	public SELF value(double d) {
		preValue();
		raw(Double.toString(d));
		return castThis();
	}

	@Override
	public SELF value(float d) {
		preValue();
		raw(Float.toString(d));
		return castThis();
	}

	@Override
	public SELF value(Number n) {
		preValue();
		if (n == null) raw(NULL);
		else raw(n.toString());
		return castThis();
	}

	@Override
	public SELF value(String key, String s) {
		if (s == null) return nul(key);
		preValue(key);
		emitStringValue(s);
		return castThis();
	}

	@Override
	public SELF value(String key, int i) {
		preValue(key);
		raw(Integer.toString(i));
		return castThis();
	}

	@Override
	public SELF value(String key, long l) {
		preValue(key);
		raw(Long.toString(l));
		return castThis();
	}

	@Override
	public SELF value(String key, boolean b) {
		preValue(key);
		raw(b ? TRUE : FALSE);
		return castThis();
	}

	@Override
	public SELF value(String key, double d) {
		preValue(key);
		raw(Double.toString(d));
		return castThis();
	}

	@Override
	public SELF value(String key, float d) {
		preValue(key);
		raw(Float.toString(d));
		return castThis();
	}

	@Override
	public SELF value(String key, Number n) {
		if (n == null)
			return nul(key);
		preValue(key);
		raw(n.toString());
		return castThis();
	}

	@Override
	public SELF array() {
		preValue();
		states.set(stateIndex++, inObject);
		inObject = false;
		first = true;
		raw('[');
		return castThis();
	}

	@Override
	public SELF object() {
		preValue();
		states.set(stateIndex++, inObject);
		inObject = true;
		first = true;
		raw('{');
		if (indentString != null) {
			indent++;
			appendNewLine();
		}
		return castThis();
	}

	@Override
	public SELF array(String key) {
		preValue(key);
		states.set(stateIndex++, inObject);
		inObject = false;
		first = true;
		raw('[');
		return castThis();
	}

	@Override
	public SELF object(String key) {
		preValue(key);
		states.set(stateIndex++, inObject);
		inObject = true;
		first = true;
		raw('{');
		if (indentString != null) {
			indent++;
			appendNewLine();
		}
		return castThis();
	}

	@Override
	public SELF end() {
		if (stateIndex == 0) throw new JsonWriterException(
			"Invalid call to end()");

		if (inObject) {
			if (indentString != null) {
				indent--;
				appendNewLine();
				appendIndent();
			}
			raw('}');
		} else {
			raw(']');
		}

		first = false;
		inObject = states.get(--stateIndex);
		return castThis();
	}

	/** Ensures that the object is in the finished state.
	 * 
	 * @throws JsonWriterException
	 *             if the written JSON is not properly balanced, ie: all arrays
	 *             and objects that were started have been properly ended. */
	protected void doneInternal() {
		if (stateIndex > 0) throw new JsonWriterException(
			"Unclosed JSON objects and/or arrays when closing writer");
		if (first) throw new JsonWriterException(
			"Nothing was written to the JSON writer");

		flush();
	}

	private void appendIndent() {
		for (int i = 0; i < indent; i++) {
			raw(indentString);
		}
	}

	private void appendNewLine() {
		raw('\n');
	}

	private void raw(String s) {
		if (utf8) {
			int l = s.length();
			if (bo + l > BUFFER_SIZE)
				flush();
			for (int i = 0; i < l; i++)
				bb[bo++] = (byte) s.charAt(i);
		} else {
			buffer.append(s);
			if (buffer.length() > BUFFER_SIZE) {
				flush();
			}
		}
	}

	private void raw(char... c) {
		if (utf8) {
			int l = c.length;
			for (int i = 0; i < l; i++) {
				if (bo + l > BUFFER_SIZE) flush();
				bb[bo++] = (byte) c[i];
			}
		} else {
			buffer.append(c);
			if (buffer.length() > BUFFER_SIZE) {
				flush();
			}
		}
	}

	private void raw(char c) {
		if (utf8) {
			if (bo + 1 > BUFFER_SIZE) flush();
			bb[bo++] = (byte)c;
		} else {
			buffer.append(c);
			if (buffer.length() > BUFFER_SIZE) {
				flush();
			}
		}
	}

	private void flush() {
		try {
			if (utf8) {
				out.write(bb, 0, bo);
				bo = 0;
			} else {
				appendable.append(buffer.toString());
				buffer.setLength(0);
			}
		} catch (IOException e) {
			throw new JsonWriterException(e);
		}
	}

	private void pre() {
		if (first) {
			first = false;
		} else {
			if (stateIndex == 0)
				throw new JsonWriterException(
						"Invalid call to emit a value in a finished JSON writer");
			raw(',');
			if (indentString != null && inObject) {
				appendNewLine();
			}
		}
	}

	private void preValue() {
		if (inObject) throw new JsonWriterException(
			"Invalid call to emit a keyless value while writing an object");

		pre();
	}

	private void preValue(String key) {
		if (!inObject) throw new JsonWriterException(
			"Invalid call to emit a key value while not writing an object");

		pre();

		if (indentString != null) appendIndent();

		emitStringValue(key);
		raw(':');
	}
	
	private void emitEscaped(char c) {
		if (c < 0x100) {
			raw(UNICODE_SMALL);
			raw(HEX[(c >> 4) & 0xf], HEX[c & 0xf]);
		} else {
			raw(UNICODE_LARGE);
			raw(HEX[(c >> 12) & 0xf], HEX[(c >> 8) & 0xf], 
				HEX[(c >> 4) & 0xf], HEX[c & 0xf]);
		}
	}
	
	/** Emits a quoted string value, escaping characters where required */
	private void emitStringValue(String s) {
		raw('"');
		char b = 0, c = 0;
		for (int i = 0; i < s.length(); i++) {
			b = c;
			c = s.charAt(i);

			switch (c) {
				case '\b' -> raw('\\', 'b');
				case '\t' -> raw('\\', 't');
				case '\n' -> raw('\\', 'n');
				case '\f' -> raw('\\', 'f');
				case '\r' -> raw('\\', 'r'); 
				case '\\', '"' -> raw('\\', c);
				case '/' -> { // Special case to escape </script> 
					if (b == '<') raw('\\');
					raw(c);
				}
				default -> {
					if (shouldBeEscaped(c)) emitEscaped(c);
					else if (utf8) {
						if (bo + 4 > BUFFER_SIZE) flush();// 4 is the max char size
						
						if (c < 0x80) { // Basic Ascii range
							bb[bo++] = (byte) c;
						} else if (c < 0x800) { // 2-byte Utf8
							bb[bo++] = (byte) (0xc0 | c >> 6);
							bb[bo++] = (byte) (0x80 | c & 0x3f);
						} else if (c < 0xd800 || c > 0xdfff) { // 3-byte Utf8
							bb[bo++] = (byte) (0xe0 | c >> 12);
							bb[bo++] = (byte) (0x80 | (c >> 6) & 0x3f);
							bb[bo++] = (byte) (0x80 | c & 0x3f);
						} else { //Utf16 extended range
							// TODO: bad surrogates
							int fc = toCodePoint(c, s.charAt(++i));
							if (fc < 0x1fffff) {
								bb[bo++] = (byte) (0xf0 | fc >> 18);
								bb[bo++] = (byte) (0x80 | (fc >> 12) & 0x3f);
								bb[bo++] = (byte) (0x80 | (fc >> 6) & 0x3f);
								bb[bo++] = (byte) (0x80 | fc & 0x3f);
							} else throw new JsonWriterException(
								"Unable to encode character 0x" + toHexString(fc));
						}
					} else raw(c);
				}
			}
		}
		raw('"');
	}

	/** json.org spec says that all control characters must be escaped. */
	private boolean shouldBeEscaped(char c) {
		return c < ' ' || (c >= '\u0080' && c < '\u00a0')
				|| (c >= '\u2000' && c < '\u2100');
	}
}
