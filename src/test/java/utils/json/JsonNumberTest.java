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

import static java.math.BigInteger.ONE;
import static org.junit.Assert.assertEquals;
import static utils.arrays.Objs.toArray;

import java.math.BigInteger;

import org.junit.Test;

import utils.json.JsonParser.JsonParserException;

/** Attempts to test that numbers are correctly round-tripped. */
public class JsonNumberTest {
	// CHECKSTYLE_OFF: MagicNumber
	// CHECKSTYLE_OFF: JavadocMethod
	@Test
	public void testBasicNumberRead() throws JsonParserException {
		var array = JsonParser.array().from("[1, 1.0, 1.00]");
		assertEquals(Integer.class, array[0].getClass());
		assertEquals(Double.class, array[1].getClass());
		assertEquals(Double.class, array[2].getClass());
	}

	@Test
	public void testBasicNumberWrite() {
		var array = toArray(1, 1.0, 1.0f);
		assertEquals("[1,1.0,1.0]", JsonWriter.string().array(array).done());
	}

	@Test
	public void testLargeIntRead() throws JsonParserException {
		var array = JsonParser.array().from("[-300000000,300000000]");
		assertEquals(Integer.class, array[0].getClass());
		assertEquals(-300000000, array[0]);
		assertEquals(Integer.class, array[1].getClass());
		assertEquals(300000000, array[1]);
	}

	@Test
	public void testLargeIntWrite() {
		var array = toArray(-300000000, 300000000);
		assertEquals("[-300000000,300000000]", JsonWriter.string().array(array)
				.done());
	}

	@Test
	public void testLongRead() throws JsonParserException {
		var array = JsonParser.array().from("[-3000000000,3000000000]");
		assertEquals(Long.class, array[0].getClass());
		assertEquals(-3000000000L, array[0]);
		assertEquals(Long.class, array[1].getClass());
		assertEquals(3000000000L, array[1]);
	}

	@Test
	public void testLongWrite() {
		var array = toArray(1L, -3000000000L, 3000000000L);
		assertEquals("[1,-3000000000,3000000000]",
			JsonWriter.string().array(array).done());
	}

	@Test
	public void testBigIntRead() throws JsonParserException {
		var array = JsonParser.array().from(
				"[-30000000000000000000,30000000000000000000]");
		assertEquals(BigInteger.class, array[0].getClass());
		assertEquals(new BigInteger("-30000000000000000000"), array[0]);
		assertEquals(BigInteger.class, array[1].getClass());
		assertEquals(new BigInteger("30000000000000000000"), array[1]);
	}

	@Test
	public void testBigIntWrite() {
		var array = toArray(
			ONE, 
			new BigInteger("-30000000000000000000"),
			new BigInteger("30000000000000000000")
		);
		assertEquals("[1,-30000000000000000000,30000000000000000000]",
				JsonWriter.string().array(array).done());
	}

	/** Tests a bug where longs were silently truncated to floats. */
	@Test
	public void testLongBuilder() {
		var o = JsonObject.builder().value("long", 0xffffffffffffL)
				.done();
		assertEquals(0xffffffffffffL, o.getNumber("long").longValue());
	}

	/** Test around the edges of the integral types. */
	@Test
	public void testAroundEdges() throws JsonParserException {
		var array = toArray(
			Integer.MAX_VALUE,
			((long) Integer.MAX_VALUE) + 1, 
			Integer.MIN_VALUE,
			((long) Integer.MIN_VALUE) - 1, 
			Long.MAX_VALUE, 
			BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE),
			Long.MIN_VALUE,
			BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE)
		);
		String json = JsonWriter.string().array(array).done();
		assertEquals(
				"[2147483647,2147483648,-2147483648,-2147483649,9223372036854775807,"
						+ "9223372036854775808,-9223372036854775808,-9223372036854775809]",
				json);
		var array2 = JsonParser.array().from(json);
		String json2 = JsonWriter.string().array(array2).done();
		assertEquals(json, json2);
	}
}
