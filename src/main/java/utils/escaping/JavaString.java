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

import static java.lang.Character.codePointAt;
import static java.lang.Character.toChars;
import static java.lang.Integer.parseInt;
import static java.lang.Integer.toHexString;
import static java.util.Collections.unmodifiableMap;
import static java.util.Locale.ENGLISH;
import static utils.escaping.Translator.aggregate;
import static utils.escaping.Translator.lookup;
import static utils.escaping.Translator.translate;
import static utils.escaping.Translator.unicodeUnescape;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

/** <p>Escapes and unescapes {@code String}s for Java
 * <p>#ThreadSafe#</p>
 * <p>This code has been adapted from Apache Commons Lang 3.5.</p> */
public class JavaString {
	/**
     * A Map&lt;CharSequence, CharSequence&gt; to escape the Java
     * control characters.
     *
     * Namely: {@code \b \n \t \f \r}
     */
    public static final Map<String, String> CTRL_CHARS = Map.of(
        "\b", "\\b",
        "\n", "\\n",
        "\t", "\\t",
        "\f", "\\f",
        "\r", "\\r"
    );
    
    public static final Translator CTRL_CHARS_ESCAPER = lookup(CTRL_CHARS);
    
    public static final Translator CTRL_CHARS_UNESCAPER = 
    	lookup(invert(CTRL_CHARS));

    /** <p>Translates unicode in the specified range. </p>
     * @param below 		the lowest codepoint boundary
     * @param above 		the highest codepoint boundary
     * @param utf16Escape 	function to use for escaping large codepoints */
    public static Translator unicodeEscape(
    	int below, int above, IntFunction<String> utf16Escape
    ) {
 	    return (input, index, out)->{
 	    	var codepoint = codePointAt(input, index);
 	    	
 	        if (codepoint >= below && codepoint <= above) return 0;
 	
 	        if (codepoint > 0xffff) {
 	            out.write(utf16Escape.apply(codepoint));
 	        } else {
 	          out.write("\\u");
 	          out.write(HEX_DIGITS[(codepoint >> 12) & 15]);
 	          out.write(HEX_DIGITS[(codepoint >> 8) & 15]);
 	          out.write(HEX_DIGITS[(codepoint >> 4) & 15]);
 	          out.write(HEX_DIGITS[(codepoint) & 15]);
 	        }
 	        return 1;
 	    };
    }

    /** <p>Translates unicode in the specified range, converting large 
     * codepoints to a single long escaped hex value. </p>
     * @param below 		the lowest codepoint boundary
     * @param above 		the highest codepoint boundary */
    public static Translator unicodeEscape(int below, int above) {
        return unicodeEscape(below, above, (int cp)->"\\u" + hex(cp));
    }

    /** <p>Translates unicode in the specified range, converting large 
     * codepoints to a pair of escaped hex values. </p>
     * @param below 		the lowest codepoint boundary
     * @param above 		the highest codepoint boundary */
    public static Translator pairedUnicodeEscape(int below, int above) {
        return unicodeEscape(below, above, (int cp)->{
            var surrogatePair = toChars(cp);
            return "\\u" + hex(surrogatePair[0]) 
            	+ "\\u" + hex(surrogatePair[1]);
        });
    }

    public static Translator octalUnescape = (in, idx, out)->{ 
        int remaining = in.length() - idx - 1; //chars left ignoring the first \
        var builder = new StringBuilder();
        if (in.charAt(idx) == '\\' && remaining > 0 && isOctalDigit(in.charAt(idx + 1))) {
            int next = idx + 1;
            int next2 = idx + 2;
            int next3 = idx + 3;

            // we know this is good as we checked it in the if block above
            builder.append(in.charAt(next));

            if (remaining > 1 && isOctalDigit(in.charAt(next2))) {
                builder.append(in.charAt(next2));
                if (remaining > 2 && isZeroToThree(in.charAt(next)) && isOctalDigit(in.charAt(next3))) {
                    builder.append(in.charAt(next3));
                }
            }

            out.write(parseInt(builder.toString(), 8));
            return 1 + builder.length();
        }
        return 0;
    };

    private static boolean isOctalDigit(final char ch) {
        return ch >= '0' && ch <= '7';
    }

    private static boolean isZeroToThree(final char ch) {
        return ch >= '0' && ch <= '3';
    }
    
 	public static final char[] HEX_DIGITS = {
    	'0', '1', '2', '3', '4', '5', '6', '7', 
    	'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
 	
     /** <p>Returns an upper case hexadecimal <code>String</code> for the given
      * character.</p>
      *
      * @param codepoint The codepoint to convert.
      * @return An upper case hexadecimal <code>String</code> */
     private static String hex(final int codepoint) {
         return toHexString(codepoint).toUpperCase(ENGLISH);
     }

    /** Used to invert an escape Map into an unescape Map.
     * @param map Map&lt;String, String&gt; to be inverted
     * @return Map&lt;String, String&gt; inverted array */
    public static <Y> Map<Y, Y> invert(Map<Y, Y> map) {
        var out = new HashMap<Y, Y>();
        map.entrySet().forEach(pair->out.put(pair.getValue(), pair.getKey()));
        return unmodifiableMap(out);
    }

    /* ESCAPE TRANSLATORS */

    /** Translator object for escaping Java.
     *
     * While {@link #escape(String)} is the expected method of use, this
     * object allows the Java escaping functionality to be used
     * as the foundation for a custom translator. */
    public static final Translator ESCAPE_JAVA = aggregate(
        lookup(Map.of("\"", "\\\"", "\\", "\\\\")),
        CTRL_CHARS_ESCAPER,
        pairedUnicodeEscape(32, 0x7f)
    );

    /** Translator object for unescaping escaped Java.
    *
    * While {@link #unescape(String)} is the expected method of use, this
    * object allows the Java unescaping functionality to be used
    * as the foundation for a custom translator. */
   public static final Translator UNESCAPE_JAVA = aggregate(
       octalUnescape,     // .between('\1', '\377'),
       unicodeUnescape,
       CTRL_CHARS_UNESCAPER,
       lookup(Map.of("\\\\", "\\", "\\\"", "\"", "\\'", "'", "\\", ""))
   );

   /**<p>Escapes the characters in a {@code String} using Java String rules.</p>
    *
    * <p>Deals correctly with quotes and control-chars 
    * (tab, backslash, cr, ff, etc.)  So a tab becomes the characters 
    * {@code '\\'} and {@code 't'}.</p>
    *
    * <p>The only difference between Java strings and JavaScript strings
    * is that in JavaScript, a single quote and forward-slash (/) are escaped.</p>
    *
    * <p>Example:</p>
    * <pre>
    * input string: He didn't say, "Stop!"
    * output string: He didn't say, \"Stop!\"
    * </pre>
    *
    * @param input  String to escape values in, may be null
    * @return String with escaped values, {@code null} if null string input */
   public static final String escape(final String input) {
       return translate(input, ESCAPE_JAVA);
   }
   
   /** <p>Unescapes any Java literals found in the {@code String}.
    * For example, it will turn a sequence of {@code '\'} and
    * {@code 'n'} into a newline character, unless the {@code '\'}
    * is preceded by another {@code '\'}.</p>
    *
    * @param input  the {@code String} to unescape, may be null
    * @return a new unescaped {@code String}, {@code null} if null string input
    */
   public static final String unescape(String input) {
       return translate(input, UNESCAPE_JAVA);
   }
}