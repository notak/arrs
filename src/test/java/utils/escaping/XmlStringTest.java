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
import static utils.escaping.XmlString.escapeXml10;
import static utils.escaping.XmlString.escapeXml11;
import static utils.escaping.XmlString.unescape;

import org.junit.jupiter.api.Test;

class XmlStringTest {
	@Test
    public void testEscapeXml10() {
        assertEquals("a&lt;b&gt;c&quot;d&apos;e&amp;f", escapeXml10("a<b>c\"d'e&f"));
        assertEquals("a\tb\rc\nd", escapeXml10("a\tb\rc\nd"),
                "XML 1.0 should not escape \t \n \r");
        assertEquals("ab", escapeXml10("a\u0000\u0001\u0008\u000b\u000c\u000e\u001fb"),
                "XML 1.0 should omit most #x0-x8 | #xb | #xc | #xe-#x19");
        assertEquals("a\ud7ff  \ue000b", escapeXml10("a\ud7ff\ud800 \udfff \ue000b"),
                "XML 1.0 should omit #xd800-#xdfff");
        assertEquals("a\ufffdb", escapeXml10("a\ufffd\ufffe\uffffb"),
                "XML 1.0 should omit #xfffe | #xffff");
        assertEquals("a\u007e&#127;&#132;\u0085&#134;&#159;\u00a0b",
                escapeXml10("a\u007e\u007f\u0084\u0085\u0086\u009f\u00a0b"),
                "XML 1.0 should escape #x7f-#x84 | #x86 - #x9f, for XML 1.1 compatibility");
    }

    @Test
    public void testEscapeXml11() {
        assertEquals("a&lt;b&gt;c&quot;d&apos;e&amp;f", escapeXml11("a<b>c\"d'e&f"));
        assertEquals("a\tb\rc\nd", escapeXml11("a\tb\rc\nd"),
                "XML 1.1 should not escape \t \n \r");
        assertEquals("ab", escapeXml11("a\u0000b"),
                "XML 1.1 should omit #x0");
        assertEquals("a&#1;&#8;&#11;&#12;&#14;&#31;b",
                escapeXml11("a\u0001\u0008\u000b\u000c\u000e\u001fb"),
                "XML 1.1 should escape #x1-x8 | #xb | #xc | #xe-#x19");
        assertEquals("a\u007e&#127;&#132;\u0085&#134;&#159;\u00a0b",
                escapeXml11("a\u007e\u007f\u0084\u0085\u0086\u009f\u00a0b"),
                "XML 1.1 should escape #x7F-#x84 | #x86-#x9F");
        assertEquals("a\ud7ff  \ue000b", escapeXml11("a\ud7ff\ud800 \udfff \ue000b"),
                "XML 1.1 should omit #xd800-#xdfff");
        assertEquals("a\ufffdb", escapeXml11("a\ufffd\ufffe\uffffb"),
                "XML 1.1 should omit #xfffe | #xffff");
    }

    /**
     * Reverse of the above.
     *
     * @see <a href="https://issues.apache.org/jira/browse/LANG-729">LANG-729</a>
     */
    @Test
    public void testunescapeSupplementaryCharacters() {
        assertEquals("\uD84C\uDFB4", unescape("&#144308;"),
                "Supplementary character must be represented using a single escape");

        assertEquals("a b c \uD84C\uDFB4", unescape("a b c &#144308;"),
                "Supplementary characters mixed with basic characters should be decoded correctly");
    }

    // Tests issue #38569
    // http://issues.apache.org/bugzilla/show_bug.cgi?id=38569
    @Test
    public void testStandaloneAmphersand() {
        assertEquals("<P&O>", unescape("&lt;P&O&gt;"));
        assertEquals("test & <", unescape("test & &lt;"));
    }
}
