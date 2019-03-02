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
import static utils.escaping.JavaString.ESCAPE_JAVA;
import static utils.escaping.JavaString.UNESCAPE_JAVA;
import static utils.escaping.JavaString.escape;
import static utils.escaping.JavaString.unescape;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import utils.escaping.JavaString;
import utils.escaping.Translator;

class JavaStringTest {
    private static final String FOO = "foo";

    @Test
    public void testEscapeJava() throws IOException {
        assertNull(escape(null));
        assertEscapeJava("", "", "empty string");
        assertEscapeJava(FOO, FOO);
        assertEscapeJava("\\t", "\t", "tab");
        assertEscapeJava("\\\\", "\\", "backslash");
        assertEscapeJava("'", "'", "single quote should not be escaped");
        assertEscapeJava("\\\\\\b\\t\\r", "\\\b\t\r");
        assertEscapeJava("\\u1234", "\u1234");
        assertEscapeJava("\\u0234", "\u0234");
        assertEscapeJava("\\u00EF", "\u00ef");
        assertEscapeJava("\\u0001", "\u0001");
        assertEscapeJava("\\uABCD", "\uabcd", "Should use capitalized Unicode hex");

        assertEscapeJava("He didn't say, \\\"stop!\\\"",
                "He didn't say, \"stop!\"");
        assertEscapeJava("This space is non-breaking:" + "\\u00A0", "This space is non-breaking:\u00a0",
                "non-breaking space");
        assertEscapeJava("\\uABCD\\u1234\\u012C",
                "\uABCD\u1234\u012C");
    }

    /** Tests https://issues.apache.org/jira/browse/LANG-421 */
    @Test
    public void testEscapeJavaWithSlash() {
        final String input = "String with a slash (/) in it";

        final String expected = input;
        final String actual = JavaString.escape(input);

        /** In 2.4 StringEscapeUtils.escapeJava(String) escapes '/' characters, 
         * which are not a valid character to escape in a Java string. */
        assertEquals(expected, actual);
    }

    private void assertEscapeJava(String escaped, String original) 
    throws IOException {
        assertEscapeJava(escaped, original, null);
    }

    private void assertEscapeJava(String exp, String original, String msg) 
    throws IOException {
        final String converted = JavaString.escape(original);
        msg = "escapeJava(String) failed" + (msg == null ? "" : (": " + msg));
        assertEquals(exp, converted, msg);

        final StringWriter writer = new StringWriter();
        Translator.translate(original, writer, ESCAPE_JAVA);
        assertEquals(exp, writer.toString());
    }

    @Test
    public void testUnescapeJava() throws IOException {
        assertNull(unescape(null));
        assertThrows(RuntimeException.class, ()->unescape("\\u02-3"));

        assertUnescapeJava("", "");
        assertUnescapeJava("test", "test");
        assertUnescapeJava("\ntest\b", "\\ntest\\b");
        assertUnescapeJava("\u123425foo\ntest\b", "\\u123425foo\\ntest\\b");
        assertUnescapeJava("'\foo\teste\r", "\\'\\foo\\teste\\r");
        assertUnescapeJava("", "\\");
        //foo
        assertUnescapeJava("\uABCDx", "\\uabcdx", "lowercase Unicode");
        assertUnescapeJava("\uABCDx", "\\uABCDx", "uppercase Unicode");
        assertUnescapeJava("\uABCD", "\\uabcd", "Unicode as final character");
    }

    private void assertUnescapeJava(String unescaped, String original) 
    throws IOException {
        assertUnescapeJava(unescaped, original, null);
    }

    private void assertUnescapeJava(String exp, String original, String msg)
    throws IOException {
        String actual = JavaString.unescape(original);

        assertEquals(exp, actual, 
        	"unescape(String) failed"
            + (msg == null ? "" : (": " + msg))
            + ": expected '" + escape(exp)
            // we escape this so we can see it in the error message
            + "' actual '" + escape(actual) + "'"
        );

        final StringWriter writer = new StringWriter();
        Translator.translate(original, writer, UNESCAPE_JAVA);
        assertEquals(exp, writer.toString());
    }
}
