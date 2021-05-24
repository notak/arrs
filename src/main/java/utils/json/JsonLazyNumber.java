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

import java.math.BigDecimal;

/** Lazily-parsed number for performance. */
@SuppressWarnings("serial")
class JsonLazyNumber extends Number {
	private final String value;
	private final boolean isDouble;

	JsonLazyNumber(String number, boolean isDoubleValue) {
		this.value = number;
		this.isDouble = isDoubleValue;
	}

	@Override
	public double doubleValue() {
		return parseDouble(value);
	}

	@Override
	public float floatValue() {
		return parseFloat(value);
	}

	@Override
	public int intValue() {
		return isDouble ? (int)parseDouble(value) : parseInt(value);
	}

	@Override
	public long longValue() {
		return isDouble ? (long)parseDouble(value) : parseLong(value);
	}

	/** Avoid serializing {@link JsonLazyNumber}. */
	private Object writeReplace() {
		return new BigDecimal(value);
	}
	
	public String toString() {
		return value;
	}
}
