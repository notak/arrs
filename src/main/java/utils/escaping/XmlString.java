package utils.escaping;
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
 * limitations under the License. */

import static java.lang.Character.MAX_SURROGATE;
import static java.lang.Character.MIN_SURROGATE;
import static java.lang.Character.codePointAt;
import static utils.escaping.JavaString.invert;
import static utils.escaping.Translator.aggregate;
import static utils.escaping.Translator.ignore;
import static utils.escaping.Translator.lookup;
import static utils.escaping.Translator.translate;

import java.util.Map;

/** <p>Escapes and unescapes {@code String}s for Java
 * <p>#ThreadSafe#</p>
 * <p>This code has been adapted from Apache Commons Lang 3.5.</p> */
public class XmlString {
    /**
     * A Map&lt;String, String&gt; to escape additional
     * <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">character entity
     * references</a>. Note that this must be used with {@link #ISO8859_1_ESCAPE} to get the full list of
     * HTML 4.0 character entities.
     */
    /** A Map&lt;String, String&gt; to escape the basic XML and HTML character 
     * entities,n amely: {@code " & < >} */
    public static final Map<String, String> BASIC_ESCAPE = Map.of(
    	"\"", "&quot;", // " - double-quote
    	"&", "&amp;",   // & - ampersand
    	"<", "&lt;",    // < - less-than
    	">", "&gt;"     // > - greater-than
    );

    /** Reverse of {@link #BASIC_ESCAPE} for unescaping purposes. */
    public static final Map<String, String> BASIC_UNESCAPE = 
    	invert(BASIC_ESCAPE);

    /** A Map&lt;String, String&gt; to escape the apostrophe character to its 
     * XML character entity. */
    public static final Map<String, String> APOS_ESCAPE = Map.of("'", "&apos;");

    /** Reverse of {@link #APOS_ESCAPE} for unescaping purposes. */
    public static final Map<String, String> APOS_UNESCAPE = invert(APOS_ESCAPE);

    public static Translator unicodeEscape(int below, int above) {
	    return (input, index, out)->{
	    	var cp = codePointAt(input, index);
	    	
	        if (cp < below || cp > above) return 0;
	
	        out.write("&#");
	        out.write(Integer.toString(cp, 10));
	        out.write(';');
	        return 1;
	    };
   }

    public static Translator UNICODE_DECODE = (in, idx, out)->{
        final int len = in.length();
        // Uses -2 to ensure there is something after the &#
        if (in.charAt(idx) == '&' && idx < len - 2 && in.charAt(idx + 1) == '#') {
            int start = idx + 2;
            boolean isHex = false;

            var firstChar = in.charAt(start);
            if (firstChar == 'x' || firstChar == 'X') {
                start++;
                isHex = true;

                // Check there's more than just an x after the &#
                if (start == len) {
                    return 0;
                }
            }

            int end = start;
            // Note that this supports character codes without a ; on the end
            while (end < len && (in.charAt(end) >= '0' && in.charAt(end) <= '9'
                || in.charAt(end) >= 'a' && in.charAt(end) <= 'f'
                || in.charAt(end) >= 'A' && in.charAt(end) <= 'F')) {
                end++;
            }

            final boolean semiNext = end != len && in.charAt(end) == ';';

            if (!semiNext) return 0;

            int entityValue;
            try {
                if (isHex) {
                    entityValue = Integer.parseInt(in.subSequence(start, end).toString(), 16);
                } else {
                    entityValue = Integer.parseInt(in.subSequence(start, end).toString(), 10);
                }
            } catch (final NumberFormatException nfe) {
                return 0;
            }

            if (entityValue > 0xFFFF) {
                final char[] chrs = Character.toChars(entityValue);
                out.write(chrs[0]);
                out.write(chrs[1]);
            } else {
                out.write(entityValue);
            }

            return 2 + end - start + (isHex ? 1 : 0) + (semiNext ? 1 : 0);
        }
        return 0;
    };

	public static Translator UNPAIRED_REMOVER = (input, index, out)->{
    	var cp = codePointAt(input, index);

        // If it's a surrogate. Write nothing and say we've translated.
    	return (cp >= MIN_SURROGATE && cp <= MAX_SURROGATE) ? 1: 0;
    };
    
    /** Translator object for escaping XML 1.0.
     *
     * While {@link #escapeXml10(String)} is the expected method of use, this
     * object allows the XML escaping functionality to be used
     * as the foundation for a custom translator. */
    public static final Translator ESCAPE_XML10 = aggregate(
        lookup(BASIC_ESCAPE),
        lookup(APOS_ESCAPE),
        ignore("\u0000", "\u0001", "\u0002", "\u0003", 
        	"\u0004", "\u0005", "\u0006", "\u0007", 
        	"\u0008", "\u000b", 
        	"\u000c", "\u000e", "\u000f", 
        	"\u0010", "\u0011", "\u0012", "\u0013", 
        	"\u0014", "\u0015", "\u0016", "\u0017", 
        	"\u0018", "\u0019", "\u001a", "\u001b", 
        	"\u001c", "\u001d", "\u001e", "\u001f", 
        	"\ufffe", "\uffff"),
        unicodeEscape(0x7f, 0x84),
        unicodeEscape(0x86, 0x9f),
        UNPAIRED_REMOVER
    );

    /** Translator object for escaping XML 1.1.
     *
     * While {@link #escapeXml11(String)} is the expected method of use, this
     * object allows the XML escaping functionality to be used
     * as the foundation for a custom translator. */
    public static final Translator ESCAPE_XML11 = aggregate(
        lookup(BASIC_ESCAPE),
        lookup(APOS_ESCAPE),
        lookup(Map.of(
            "\u0000", "",
            "\u000b", "&#11;",
            "\u000c", "&#12;",
            "\ufffe", "",
            "\uffff", ""
        )),
        unicodeEscape(0x1, 0x8),
        unicodeEscape(0xe, 0x1f),
        unicodeEscape(0x7f, 0x84),
        unicodeEscape(0x86, 0x9f),
        UNPAIRED_REMOVER
    );
    
    /** Translator object for unescaping escaped XML.
    * While {@link #unescapeXml(String)} is the expected method of use, this
    * object allows the XML unescaping functionality to be used
    * as the foundation for a custom translator.
    */
   public static final Translator UNESCAPE_XML = aggregate(
       lookup(BASIC_UNESCAPE),
       lookup(APOS_UNESCAPE),
       UNICODE_DECODE
   );

    /** <p>Escapes the characters in a {@code String} using XML entities.</p>
     *
     * <p>For example: {@code "bread" & "butter"} =&gt;
     * {@code &quot;bread&quot; &amp; &quot;butter&quot;}.
     * </p>
     *
     * <p>Note that XML 1.0 is a text-only format: it cannot represent control
     * characters or unpaired Unicode surrogate codepoints, even after escaping.
     * {@code escapeXml10} will remove characters that do not fit in the
     * following ranges:</p>
     *
     * <p>{@code #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]}</p>
     *
     * <p>Though not strictly necessary, {@code escapeXml10} will escape
     * characters in the following ranges:</p>
     *
     * <p>{@code [#x7F-#x84] | [#x86-#x9F]}</p>
     *
     * <p>The returned string can be inserted into a valid XML 1.0 or XML 1.1
     * document. If you want to allow more non-text characters in an XML 1.1
     * document, use {@link #escapeXml11(String)}.</p>
     *
     * @param input  the {@code String} to escape, may be null
     * @return a new escaped {@code String}, {@code null} if null string input
     * @see #unescapeXml(java.lang.String) */
    public static final String escapeXml10(final String input) {
        return translate(input, ESCAPE_XML10);
    }

    /** <p>Escapes the characters in a {@code String} using XML entities.</p>
     *
     * <p>For example: {@code "bread" & "butter"} =&gt;
     * {@code &quot;bread&quot; &amp; &quot;butter&quot;}.
     * </p>
     *
     * <p>XML 1.1 can represent certain control characters, but it cannot represent
     * the null byte or unpaired Unicode surrogate codepoints, even after escaping.
     * {@code escapeXml11} will remove characters that do not fit in the following
     * ranges:</p>
     *
     * <p>{@code [#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]}</p>
     *
     * <p>{@code escapeXml11} will escape characters in the following ranges:</p>
     *
     * <p>{@code [#x1-#x8] | [#xB-#xC] | [#xE-#x1F] | [#x7F-#x84] | [#x86-#x9F]}</p>
     *
     * <p>The returned string can be inserted into a valid XML 1.1 document. Do not
     * use it for XML 1.0 documents.</p>
     *
     * @param input  the {@code String} to escape, may be null
     * @return a new escaped {@code String}, {@code null} if null string input
     * @see #unescapeXml(java.lang.String) */
    public static final String escapeXml11(final String input) {
       return translate(input, ESCAPE_XML11);
   }
   
    /** <p>Unescapes a string containing XML entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes.</p>
     *
     * <p>Supports only the five basic XML entities (gt, lt, quot, amp, apos).
     * Does not support DTDs or external entities.</p>
     *
     * <p>Note that numerical \\u Unicode codes are unescaped to their respective
     *    Unicode characters. This may change in future releases. </p>
     *
     * @param input  the {@code String} to unescape, may be null
     * @return a new unescaped {@code String}, {@code null} if null string input
     * @see #escapeXml10(String)
     * @see #escapeXml11(String) */
    public static final String unescape(String input) {
       return translate(input, UNESCAPE_XML);
   }
}