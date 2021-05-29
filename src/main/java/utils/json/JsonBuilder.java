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

import java.util.Collection;
import java.util.Map;
import java.util.Stack;

import utils.json.JsonWriter.JsonWriterException;

/**
 * Builds a {@link JsonObject} or {@link JsonArray}.
 * 
 * @param <T>
 *            The type of JSON object to build.
 */
public final class JsonBuilder<T> {
	private Stack<Object> json = new Stack<>();
	private T root;

	JsonBuilder(T root) {
		this.root = root;
		json.push(root);
	}

	/** Completes this builder, closing any unclosed objects and returns the 
	 * built object. */
	public T done() {
		return root;
	}
	
	public <Q> JsonBuilder<T> array(String key, Q[] c) {
		return value(key, c);
	}
	
	public JsonBuilder<T> array(String key, Collection<?> c) {
		return value(key, c);
	}

	public JsonBuilder<T> object(String key, Map<?, ?> map) {
		return value(key, map);
	}

	public JsonBuilder<T> nul(String key) {
		return value(key, (Object)null);
	}

	public JsonBuilder<T> value(String key, Object o) {
		obj().put(key, o);
		return this;
	}

	public JsonBuilder<T> value(String key, String s) {
		return value(key, (Object)s);
	}

	public JsonBuilder<T> value(String key, int i) {
		return value(key, (Object)i);
	}

	public JsonBuilder<T> value(String key, long l) {
		return value(key, (Object)l);
	}

	public JsonBuilder<T> value(String key, boolean b) {
		return value(key, (Object)b);
	}

	public JsonBuilder<T> value(String key, double d) {
		return value(key, (Object)d);
	}

	public JsonBuilder<T> value(String key, float f) {
		return value(key, (Object)f);
	}

	public JsonBuilder<T> value(String key, Number n) {
		return value(key, (Object)n);
	}

	private JsonObject obj() {
		try {
			return (JsonObject)json.peek();
		} catch (ClassCastException e) {
			throw new JsonWriterException("Attempted to write a keyed value to a JsonArray");
		}
	}
}
