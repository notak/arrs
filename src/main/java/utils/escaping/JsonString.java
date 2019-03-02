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

import static utils.escaping.JavaString.CTRL_CHARS_ESCAPER;
import static utils.escaping.JavaString.UNESCAPE_JAVA;
import static utils.escaping.JavaString.pairedUnicodeEscape;
import static utils.escaping.Translator.aggregate;
import static utils.escaping.Translator.lookup;
import static utils.escaping.Translator.translate;

import java.util.Map;

/** <p>Escapes and unescapes {@code String}s for Java
 * <p>#ThreadSafe#</p>
 * <p>This code has been adapted from Apache Commons Lang 3.5.</p> */
public class JsonString {

    /* ESCAPE TRANSLATORS */

    /** Translator object for escaping Json.
     *
     * While {@link #escape(String)} is the expected method of use, this
     * object allows the Java escaping functionality to be used
     * as the foundation for a custom translator. */
    public static final Translator ESCAPE_JSON = aggregate(
        lookup(Map.of("\"", "\\\"", "\\", "\\\\", "/", "\\/")),
        CTRL_CHARS_ESCAPER,
        pairedUnicodeEscape(32, 0x7f)
    );


    /** Translator object for unescaping escaped Json.
    *
    * While {@link #unescape(String)} is the expected method of use, this
    * object allows the Java unescaping functionality to be used
    * as the foundation for a custom translator. 
    * The Java one can safely be re-used as {@code '/'} will be covered by 
    * the final catch-all which just dumps the {@code '\'} */
   public static final Translator UNESCAPE_JSON = UNESCAPE_JAVA;

   /**<p>Escapes the characters in a {@code String} using Json String rules.</p>
    *
    * <p>Deals correctly with quotes and control-chars 
    * (tab, backslash, cr, ff, etc.)  So a tab becomes the characters 
    * {@code '\\'} and {@code 't'}.</p>
    *
    * <p>The only difference from JavaString is that forward-slash {@code '/'}
    * is also escaped.</p>
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
       return translate(input, ESCAPE_JSON);
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
       return translate(input, UNESCAPE_JSON);
   }
}