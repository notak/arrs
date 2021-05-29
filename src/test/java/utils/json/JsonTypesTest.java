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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utils.arrays.Objs.isNull;
import static utils.arrays.Objs.nth;
import static utils.arrays.Objs.nthDouble;
import static utils.arrays.Objs.nthFloat;
import static utils.arrays.Objs.nthInt;
import static utils.arrays.Objs.nthLong;
import static utils.arrays.Objs.nthNumber;
import static utils.arrays.Objs.nthString;
import static utils.arrays.Objs.toArray;
import static org.junit.Assert.assertArrayEquals;

import java.math.BigInteger;

import org.junit.Test;

import utils.arrays.Objs;

/**
 * Test for the various JSON types.
 */
public class JsonTypesTest {
	// CHECKSTYLE_OFF: MagicNumber
	// CHECKSTYLE_OFF: JavadocMethod
	@Test
	public void testObjectInt() {
		JsonObject o = new JsonObject();
		o.put("key", 1);
		assertEquals(1, o.getInt("key"));
		assertEquals(1L, o.getLong("key"));
		assertEquals(1.0, o.getDouble("key"), 0.0001f);
		assertEquals(1.0f, o.getFloat("key"), 0.0001f);
		assertEquals(1, o.getNumber("key"));
		assertEquals(1, o.get("key"));

		assertEquals(null, o.getString("key"));
		assertEquals("foo", o.getString("key", "foo"));
		assertFalse(o.isNull("key"));
	}

	@Test
	public void testObjectString() {
		JsonObject o = new JsonObject();
		o.put("key", "1");
		assertEquals(0, o.getInt("key"));
		assertEquals(0L, o.getLong("key"));
		assertEquals(0, o.getDouble("key"), 0.0001f);
		assertEquals(0f, o.getFloat("key"), 0.0001f);
		assertEquals(null, o.getNumber("key"));
		assertEquals("1", o.get("key"));
		assertFalse(o.isNull("key"));
	}

	@Test
	public void testObjectNull() {
		JsonObject o = new JsonObject();
		o.put("key", null);
		assertEquals(0, o.getInt("key"));
		assertEquals(0L, o.getLong("key"));
		assertEquals(0, o.getDouble("key"), 0.0001f);
		assertEquals(0f, o.getFloat("key"), 0.0001f);
		assertEquals(null, o.getNumber("key"));
		assertEquals(null, o.get("key"));
		assertTrue(o.isNull("key"));
	}

	@Test
	public void testArrayInt() {
		var o = toArray((String) null, null, null, 1);
		assertEquals(1, nthInt(o, 3, 0));
		assertEquals(1L, nthLong(o, 3, 0));
		assertEquals(1, nthDouble(o, 3, 0), 0.0001f);
		assertEquals(1, nthFloat(o, 3, 0), 0.0001f);
		assertEquals(1, nthNumber(o, 3, null));
		assertEquals(1, nth(o, 3, null));
		assertEquals("foo", nthString(o, 3, "foo"));
		assertFalse(isNull(o, 3));
	}

	@Test
	public void testArrayString() {
		var o = toArray((String) null, null, null, "1");
		assertEquals(0, nthInt(o, 3, 0));
		assertEquals(0L, nthLong(o, 3, 0));
		assertEquals(0, nthDouble(o, 3, 0), 0.0001f);
		assertEquals(0, nthFloat(o, 3, 0), 0.0001f);
		assertEquals(0, nthNumber(o, 3, 0));
		assertEquals("1", nth(o, 3, null));
		assertFalse(isNull(o, 3));
	}

	@Test
	public void testArrayNull() {
		var o = toArray((String) null, null, null,null);
		assertEquals(0, nthInt(o, 3, 0));
		assertEquals(0, nthDouble(o, 3, 0), 0.0001f);
		assertEquals(0, nthFloat(o, 3, 0), 0.0001f);
		assertEquals(null, nthNumber(o, 3, null));
		assertEquals(null, nth(o, 3, null));
		assertTrue(isNull(o, 3));
	}

	@Test
	public void testArrayBounds() {
		var o = toArray((String) null, null, null,null);
		assertEquals(0, nthInt(o, 3, 0));
		assertEquals(0, nthDouble(o, 3, 0), 0.0001f);
		assertEquals(0, nthFloat(o, 3, 0), 0.0001f);
		assertEquals(null, nthNumber(o, 3, null));
		assertEquals(null, nth(o, 3, null));
		assertTrue(isNull(o, 3));
	}

	@Test
	public void testJsonObjectBuilder() {
		// @formatter:off
		JsonObject a = JsonObject
				.builder()
				.value("bool", true)
				.value("double", 1.0)
				.value("float", 1.0f)
				.value("int", 1)
				.value("bigint", new BigInteger("1234567890"))
				.value("string", "hi")
				.nul("null")
				.array("existingArray", toArray(1, 2, 3))
				.object("existingObject",
						JsonObject.builder().nul("a").done())
				.done();
		// @formatter:on

		String[] bits = new String[] { 
			"\"bool\":true", 
			"\"double\":1.0", 
			"\"float\":1.0",
			"\"int\":1",
			"\"bigint\":1234567890", 
			"\"string\":\"hi\"",
			"\"null\":null", 
			"\"existingObject\":{\"a\":null}",
			"\"existingArray\":[1,2,3]", 
		};

		String s = JsonWriter.string(a);
		
		for (String bit : bits) {
			assertTrue(s.contains(bit));
		}
	}

	@Test
	public void testJsonKeyOrder() {
		JsonObject a = JsonObject
			.builder()
			.value("key01", 1)
			.value("key02", 2)
			.value("key03", 3)
			.value("key04", 4)
			.done();

		assertArrayEquals(
			new String [] {
				"key01",
				"key02",
				"key03",
				"key04"
			},
			a.keySet().toArray(new String[0]));
	}
}
