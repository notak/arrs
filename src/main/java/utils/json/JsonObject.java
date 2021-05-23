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

import java.util.LinkedHashMap;
import java.util.Map;

/** Extends a {@link LinkedHashMap} with helper methods to determine the 
 * underlying JSON type of the map element. */
public class JsonObject extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	/** Returns the {@link JsonObject} at the given index, 
	 * or the default if it does not exist or is the wrong type. */
	public static JsonObject nthObject(Object[] from, int key, JsonObject def) {
		return from.length>=key && from[key] instanceof JsonObject s ? s : def;
	}

	/** Creates an empty {@link JsonObject} with the default capacity. */
	public JsonObject() { }

	/** Creates a {@link JsonObject} from an existing {@link Map}. */
	public JsonObject(Map<? extends String, ?> map) {
		super(map);
	}

	/** Creates a {@link JsonObject} with the given initial capacity. */
	public JsonObject(int initialCapacity) {
		super(initialCapacity);
	}

	/** Creates a {@link JsonObject} with the given initial capacity and load factor. */
	public JsonObject(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/** Creates a {@link JsonBuilder} for a {@link JsonObject}. */
	public static JsonBuilder<JsonObject> builder() {
		return new JsonBuilder<>(new JsonObject());
	}

	/** {@link JsonArray} at the given key, 
	 * or null if it does not exist or is the wrong type. */
	public Object[] getArray(String key) {
		return getArray(key, null);
	}

	/** {@link JsonArray} at the given key, 
	 * or the default if it does not exist or is the wrong type. */
	public Object[] getArray(String key, Object[] default_) {
		return get(key) instanceof Object[] a ? a : default_;
	}

	/** {@link Boolean} at the given key, 
	 * or false if it does not exist or is the wrong type. */
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	/** {@link Boolean} at the given key, 
	 * or the default if it does not exist or is the wrong type. */
	public boolean getBoolean(String key, Boolean default_) {
		return get(key) instanceof Boolean b ?  b : default_;
	}

	/** {@link Double} at the given key, 
	 * or 0.0 if it does not exist or is the wrong type. */
	public double getDouble(String key) {
		return getDouble(key, 0);
	}

	/** {@link Double} at the given key, 
	 * or the default if it does not exist or is the wrong type. */
	public double getDouble(String key, double default_) {
		return get(key) instanceof Number n ? n.doubleValue() : default_;
	}

	/** {@link Float} at the given key, 
	 * or 0.0f if it does not exist or is the wrong type. */
	public float getFloat(String key) {
		return getFloat(key, 0);
	}

	/** {@link Float} at the given key, 
	 * or the default if it does not exist or is the wrong type. */
	public float getFloat(String key, float default_) {
		return get(key) instanceof Number n ? n.floatValue() : default_;
	}

	/** {@link Integer} at the given key, 
	 * or 0 if it does not exist or is the wrong type. */
	public int getInt(String key) {
		return getInt(key, 0);
	}

	/** {@link Integer} at the given key, 
	 * or the default if it does not exist or is the wrong type. */
	public int getInt(String key, int default_) {
		return get(key) instanceof Number n ? n.intValue() : default_;
	}

	/** {@link Long} at the given key, 
	 * or 0 if it does not exist or is the wrong type. */
	public long getLong(String key) {
		return getLong(key, 0);
	}

	/** {@link Long} at the given key, 
	 * or the default if it does not exist or is the wrong type. */
	public long getLong(String key, long default_) {
		return get(key) instanceof Number n ? n.longValue() : default_;
	}

	/** {@link Number} at the given key, 
	 * or null if it does not exist or is the wrong type. */
	public Number getNumber(String key) {
		return getNumber(key, null);
	}

	/** {@link Number} at the given key, 
	 * or the default if it does not exist or is the wrong type. */
	public Number getNumber(String key, Number default_) {
		return get(key) instanceof Number n ? n : default_;
	}

	/** {@link JsonObject} at the given key, 
	 * or null if it does not exist or is the wrong type. */
	public JsonObject getObject(String key) {
		return getObject(key, null);
	}

	/** {@link JsonObject} at the given key, 
	 * or the default if it does not exist or is the wrong type. */
	public JsonObject getObject(String key, JsonObject default_) {
		return get(key) instanceof JsonObject j ? j : default_;
	}

	/** {@link String} at the given key, 
	 * or null if it does not exist or is the wrong type. */
	public String getString(String key) {
		return getString(key, null);
	}

	/** {@link String} at the given key, 
	 * or the default if it does not exist or is the wrong type. */
	public String getString(String key, String default_) {
		return get(key) instanceof String s ? s : default_;
	}

	/** true if the object has an element at that key */
	public boolean has(String key) {
		return super.containsKey(key);
	}

	/** true if the object has a boolean element at that key. */
	public boolean isBoolean(String key) {
		return get(key) instanceof Boolean;
	}

	/** true if the object has a null element at that key. */
	public boolean isNull(String key) {
		return super.containsKey(key) && get(key) == null;
	}

	/** true if the object has a number element at that key. */
	public boolean isNumber(String key) {
		return get(key) instanceof Number;
	}

	/** true if the object has a string element at that key. */
	public boolean isString(String key) {
		return get(key) instanceof String;
	}
}
