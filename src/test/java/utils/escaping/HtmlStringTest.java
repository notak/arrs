/* THIS CODE IS A HEAVILY MODIFIED VERSION OF the Apache Commons Text
 * library, located at https://github.com/apache/commons-text. The
 * following license applies to that code
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utils.escaping;

import static org.junit.jupiter.api.Assertions.*;
import static utils.escaping.HtmlString.ESCAPE_HTML3;
import static utils.escaping.HtmlString.ESCAPE_HTML4;
import static utils.escaping.HtmlString.UNESCAPE_HTML3;
import static utils.escaping.HtmlString.UNESCAPE_HTML4;
import static utils.escaping.HtmlString.escapeHtml4;
import static utils.escaping.HtmlString.unescapeHtml3;
import static utils.escaping.HtmlString.unescapeHtml4;
import static utils.escaping.Translator.translate;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

class HtmlStringTest {

    private static final String[][] HTML_ESCAPES = {
        {"no escaping", "plain text", "plain text"},
        {"no escaping", "plain text", "plain text"},
        {"empty string", "", ""},
        {"null", null, null},
        {"ampersand", "bread &amp; butter", "bread & butter"},
        {"quotes", "&quot;bread&quot; &amp; butter", "\"bread\" & butter"},
        {"final character only", "greater than &gt;", "greater than >"},
        {"first character only", "&lt; less than", "< less than"},
        {"apostrophe", "Huntington's chorea", "Huntington's chorea"},
        {"languages", "English,Fran&ccedil;ais,\u65E5\u672C\u8A9E (nihongo)",
            "English,Fran\u00E7ais,\u65E5\u672C\u8A9E (nihongo)"},
        {"8-bit ascii shouldn't number-escape", "\u0080\u009F", "\u0080\u009F"},
	};
	
	@Test
	public void testEscapeHtml3() {
	    for (final String[] element : HTML_ESCAPES) {
	        final String message = element[0];
	        final String expected = element[1];
	        final String original = element[2];
	        assertEquals(expected, escapeHtml4(original), message);
	        final StringWriter sw = new StringWriter();
	        try {
	            translate(original, sw, ESCAPE_HTML3);
	        } catch (final IOException e) {
	        }
	        final String actual = original == null ? null : sw.toString();
	        assertEquals(expected, actual, message);
	    }
	}
	
	@Test
	public void testUnescapeHtml3() {
	    for (final String[] element : HTML_ESCAPES) {
	        final String message = element[0];
	        final String expected = element[2];
	        final String original = element[1];
	        assertEquals(expected, unescapeHtml3(original), message);
	
	        final StringWriter sw = new StringWriter();
	        try {
	        	translate(original, sw, UNESCAPE_HTML3);
	        } catch (final IOException e) {
	        }
	        final String actual = original == null ? null : sw.toString();
	        assertEquals(expected, actual, message);
	    }
	    // \u00E7 is a cedilla (c with wiggle under)
	    // note that the test string must be 7-bit-clean (Unicode escaped) or else it will compile incorrectly
	    // on some locales
	    assertEquals("Fran\u00E7ais", unescapeHtml3("Fran\u00E7ais"), "funny chars pass through OK");
	
	    assertEquals("Hello&;World", unescapeHtml3("Hello&;World"));
	    assertEquals("Hello&#;World", unescapeHtml3("Hello&#;World"));
	    assertEquals("Hello&# ;World", unescapeHtml3("Hello&# ;World"));
	    assertEquals("Hello&##;World", unescapeHtml3("Hello&##;World"));
	}
	
	@Test
	public void testEscapeHtml4() {
	    for (final String[] element : HTML_ESCAPES) {
	        final String message = element[0];
	        final String expected = element[1];
	        final String original = element[2];
	        assertEquals(expected, escapeHtml4(original), message);
	        final StringWriter sw = new StringWriter();
	        try {
	            translate(original, sw, ESCAPE_HTML4);
	        } catch (final IOException e) {
	        }
	        final String actual = original == null ? null : sw.toString();
	        assertEquals(expected, actual, message);
	    }
	}
	
	@Test
	public void testUnescapeHtml4() {
	    for (final String[] element : HTML_ESCAPES) {
	        final String message = element[0];
	        final String expected = element[2];
	        final String original = element[1];
	        assertEquals(expected, unescapeHtml4(original), message);
	
	        final StringWriter sw = new StringWriter();
	        try {
	            translate(original, sw, UNESCAPE_HTML4);
	        } catch (final IOException e) {
	        }
	        final String actual = original == null ? null : sw.toString();
	        assertEquals(expected, actual, message);
	    }
	    // \u00E7 is a cedilla (c with wiggle under)
	    // note that the test string must be 7-bit-clean (Unicode escaped) or else it will compile incorrectly
	    // on some locales
	    assertEquals("Fran\u00E7ais", unescapeHtml4("Fran\u00E7ais"), "funny chars pass through OK");
	
	    assertEquals("Hello&;World", unescapeHtml4("Hello&;World"));
	    assertEquals("Hello&#;World", unescapeHtml4("Hello&#;World"));
	    assertEquals("Hello&# ;World", unescapeHtml4("Hello&# ;World"));
	    assertEquals("Hello&##;World", unescapeHtml4("Hello&##;World"));
	}
	
	@Test
	public void testUnescapeHexCharsHtml() {
	    // Simple easy to grok test
	    assertEquals("\u0080\u009F", unescapeHtml4("&#x80;&#x9F;"), "hex number unescape");
	    assertEquals("\u0080\u009F", unescapeHtml4("&#X80;&#X9F;"), "hex number unescape");
	    // Test all Character values:
	    for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
	        final Character c1 = i;
	        final Character c2 = (char) (i + 1);
	        final String expected = c1.toString() + c2.toString();
	        final String escapedC1 = "&#x" + Integer.toHexString(c1) + ";";
	        final String escapedC2 = "&#x" + Integer.toHexString(c2) + ";";
	        assertEquals(expected, unescapeHtml4(escapedC1 + escapedC2),
	                "hex number unescape index " + i);
	    }
	}
	
	@Test
	public void testUnescapeUnknownEntity() {
	    assertEquals("&zzzz;", unescapeHtml4("&zzzz;"));
	}
	
	@Test
	public void testEscapeHtmlVersions() {
	    assertEquals("&Beta;", escapeHtml4("\u0392"));
	    assertEquals("\u0392", unescapeHtml4("&Beta;"));
	
	    // TODO: refine API for escaping/unescaping specific HTML versions
	}

}
