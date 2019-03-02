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

import static java.lang.Character.charCount;
import static java.lang.Character.codePointAt;
import static java.lang.Character.isHighSurrogate;
import static java.lang.Character.isLowSurrogate;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.sort;
import static utils.arrays.Ints.max;
import static utils.arrays.Ints.min;
import static utils.arrays.Objs.mapChar;
import static utils.arrays.Objs.mapInt;
import static utils.arrays.Objs.sorted;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/** Translate a set of codepoints, represented by an int index into a CharSequence,
 * into another set of codepoints. The number of codepoints consumed must be returned,
 * and the only IOExceptions thrown must be from interacting with the Writer so that
 * the top level API may reliably ignore StringWriter IOExceptions.
 *
 * @param input CharSequence that is being translated
 * @param index int representing the current point of translation
 * @param out Writer to translate the text to
 * @return int count of codepoints consumed
 * @throws IOException if and only if the Writer produces an IOException */
@FunctionalInterface
public interface Translator {
    public abstract int translate(CharSequence input, int index, Writer out)
        throws IOException;
	
    /** Specify the translators to be used at creation time.
     * @param translators CharSequenceTranslator array to aggregate */
    public static Translator aggregate(Translator... translators) {
	     return (input, index, out)->{ 
	        for (var translator : translators) {
	            var consumed = translator.translate(input, index, out);
	            if (consumed != 0) return consumed;
	        }
	        return 0;
	    };
	}

    /** Define the lookup table to be used in translation
     * @param lookups Map&lt;CharSequence, CharSequence&gt; table of translator
     *                  mappings
     */
    public static Translator lookup(Map<String, String> lookups) {
    	var keys = lookups.keySet().toArray(String[]::new);
    	var keyLens = mapInt(keys, String::length);
        var shortest = min(keyLens);
        var longest = max(keyLens);
    	var prefixSet = mapChar(keys, s->s.charAt(0));
    	sort(prefixSet); //TODO, should also eliminate dups
        
	    return (in, idx, out)->{
	        // check if translation exists for the input at position index
	        if (binarySearch(prefixSet, in.charAt(idx))<0) return 0;

	        int max = (idx + longest <= in.length()) ? longest 
	        	: in.length() - idx;

	        // implement greedy algorithm by trying maximum match first
            for (int i = max; i >= shortest; i--) {
                var res = lookups.get(in.subSequence(idx, idx + i).toString());
                if (res == null) continue;

                out.write(res);
                return i;
            }
	        return 0;
	    };
    }

    /** Define the lookup table to be used in translation
     * @param lookups Map&lt;CharSequence, CharSequence&gt; table of translator
     *                  mappings
     */
    public static Translator ignore(String... tokens) {
    	var lens = mapInt(tokens, String::length);
        var shortest = min(lens);
        var longest = max(lens);
    	var prefixSet = mapChar(tokens, s->s.charAt(0));
    	sort(prefixSet); //TODO, should also eliminate dups
    	var sortedTokens = sorted(tokens); //TODO, should also eliminate dups
        
	    return (in, idx, out)->{
	        // check if translation exists for the input at position index
	        if (binarySearch(prefixSet, in.charAt(idx))<0) return 0;

	        int max = (idx + longest <= in.length()) ? longest 
	        	: in.length() - idx;

	        // implement greedy algorithm by trying maximum match first
            for (int i = max; i >= shortest; i--) {
            	var cand = in.subSequence(idx, idx + i).toString();
            	if (binarySearch(sortedTokens, cand)>=0) return i;
            }
	        return 0;
	    };
    }

    /** Helper for non-Writer usage.
     * @param input CharSequence to be translated
     * @return String output of translation */
    public static String translate(CharSequence input, Translator translator) {
        if (input == null) return null;
        try {
            var writer = new StringWriter(input.length() * 2);
            translate(input, writer, translator);
            return writer.toString();
        } catch (final IOException ioe) { // impossible with StringWriter
            throw new RuntimeException(ioe);
        }
    }

    /** Translate an input onto a Writer
     * @param input CharSequence that is being translated
     * @param out Writer to translate the text to
     * @throws IOException if and only if the Writer produces an IOException */
    static void translate(CharSequence input, Writer out, Translator translator)
    throws IOException {
        if (input == null || out==null) return;
        int len = input.length();
        for (int pos = 0; pos < len;) {
            int consumed = translator.translate(input, pos, out);
            if (consumed == 0) {
                // inlined implementation of Character.toChars(Character.codePointAt(input, pos))
                // avoids allocating temp char arrays and duplicate checks
                var c1 = input.charAt(pos++);
                out.write(c1);
                if (isHighSurrogate(c1) && pos < len) {
                    var c2 = input.charAt(pos);
                    if (isLowSurrogate(c2)) {
                      out.write(c2);
                      pos++;
                    }
                }
                continue;
            }
            // contract with translators is that they have to understand codepoints
            // and they just took care of a surrogate pair
            for (int pt = 0; pt < consumed; pt++) {
                pos += charCount(codePointAt(input, pos));
            }
        }
    }

    public static Translator unicodeUnescape = (in, idx, out)->{
    	var len = in.length();
    	int i = idx;
        if (in.charAt(i)!='\\' || ++i>=len || in.charAt(i)!='u') return 0;

        while (++i<len && in.charAt(i)=='u'); //consume any extra 'u' chars

        if (i<len && in.charAt(i)=='+') i++; //optional + character

        if (i+4 > len) throw new IllegalArgumentException(
        	"Less than 4 hex digits in unicode value: '"
                + in.subSequence(idx, len)
                + "' due to end of CharSequence");

        // Get 4 hex digits
        var unicode = in.subSequence(i, i+4).toString();

        try {
            out.write((char) parseInt(unicode, 16));
        } catch (final NumberFormatException nfe) {
            throw new IllegalArgumentException(
            	"Unable to parse unicode value: " + unicode, nfe);
        }
        return i + 4 - idx;
    };
	
}