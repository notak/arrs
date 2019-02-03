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

import static java.util.Map.entry;
import static utils.escaping.JavaString.invert;
import static utils.escaping.Translator.aggregate;
import static utils.escaping.Translator.lookup;
import static utils.escaping.Translator.translate;
import static utils.escaping.XmlString.BASIC_ESCAPE;
import static utils.escaping.XmlString.BASIC_UNESCAPE;
import static utils.escaping.XmlString.UNICODE_DECODE;

import java.util.Map;

/** <p>Escapes and unescapes {@code String}s for Java
 * <p>#ThreadSafe#</p>
 * <p>This code has been adapted from Apache Commons Lang 3.5.</p> */
public class HtmlString {
    public static final Map<String, String> ISO8859_1_ESCAPE = Map.ofEntries(
    	entry("\u00A0", "&nbsp;"), // non-breaking space
    	entry("\u00A1", "&iexcl;"), // inverted exclamation mark
    	entry("\u00A2", "&cent;"), // cent sign
    	entry("\u00A3", "&pound;"), // pound sign
    	entry("\u00A4", "&curren;"), // currency sign
    	entry("\u00A5", "&yen;"), // yen sign = yuan sign
    	entry("\u00A6", "&brvbar;"), // broken bar = broken vertical bar
    	entry("\u00A7", "&sect;"), // section sign
    	entry("\u00A8", "&uml;"), // diaeresis = spacing diaeresis
    	entry("\u00A9", "&copy;"), // © - copyright sign
    	entry("\u00AA", "&ordf;"), // feminine ordinal indicator
    	entry("\u00AB", "&laquo;"), // left-pointing double angle quotation mark = left pointing guillemet
    	entry("\u00AC", "&not;"), // not sign
    	entry("\u00AD", "&shy;"), // soft hyphen = discretionary hyphen
    	entry("\u00AE", "&reg;"), // ® - registered trademark sign
    	entry("\u00AF", "&macr;"), // macron = spacing macron = overline = APL overbar
    	entry("\u00B0", "&deg;"), // degree sign
    	entry("\u00B1", "&plusmn;"), // plus-minus sign = plus-or-minus sign
    	entry("\u00B2", "&sup2;"), // superscript two = superscript digit two = squared
    	entry("\u00B3", "&sup3;"), // superscript three = superscript digit three = cubed
    	entry("\u00B4", "&acute;"), // acute accent = spacing acute
    	entry("\u00B5", "&micro;"), // micro sign
    	entry("\u00B6", "&para;"), // pilcrow sign = paragraph sign
    	entry("\u00B7", "&middot;"), // middle dot = Georgian comma = Greek middle dot
    	entry("\u00B8", "&cedil;"), // cedilla = spacing cedilla
    	entry("\u00B9", "&sup1;"), // superscript one = superscript digit one
    	entry("\u00BA", "&ordm;"), // masculine ordinal indicator
    	entry("\u00BB", "&raquo;"), // right-pointing double angle quotation mark = right pointing guillemet
    	entry("\u00BC", "&frac14;"), // vulgar fraction one quarter = fraction one quarter
    	entry("\u00BD", "&frac12;"), // vulgar fraction one half = fraction one half
    	entry("\u00BE", "&frac34;"), // vulgar fraction three quarters = fraction three quarters
    	entry("\u00BF", "&iquest;"), // inverted question mark = turned question mark
    	entry("\u00C0", "&Agrave;"), // À - uppercase A, grave accent
    	entry("\u00C1", "&Aacute;"), // Á - uppercase A, acute accent
    	entry("\u00C2", "&Acirc;"), // Â - uppercase A, circumflex accent
    	entry("\u00C3", "&Atilde;"), // Ã - uppercase A, tilde
    	entry("\u00C4", "&Auml;"), // Ä - uppercase A, umlaut
    	entry("\u00C5", "&Aring;"), // Å - uppercase A, ring
    	entry("\u00C6", "&AElig;"), // Æ - uppercase AE
    	entry("\u00C7", "&Ccedil;"), // Ç - uppercase C, cedilla
    	entry("\u00C8", "&Egrave;"), // È - uppercase E, grave accent
    	entry("\u00C9", "&Eacute;"), // É - uppercase E, acute accent
    	entry("\u00CA", "&Ecirc;"), // Ê - uppercase E, circumflex accent
    	entry("\u00CB", "&Euml;"), // Ë - uppercase E, umlaut
    	entry("\u00CC", "&Igrave;"), // Ì - uppercase I, grave accent
    	entry("\u00CD", "&Iacute;"), // Í - uppercase I, acute accent
    	entry("\u00CE", "&Icirc;"), // Î - uppercase I, circumflex accent
    	entry("\u00CF", "&Iuml;"), // Ï - uppercase I, umlaut
    	entry("\u00D0", "&ETH;"), // Ð - uppercase Eth, Icelandic
    	entry("\u00D1", "&Ntilde;"), // Ñ - uppercase N, tilde
    	entry("\u00D2", "&Ograve;"), // Ò - uppercase O, grave accent
    	entry("\u00D3", "&Oacute;"), // Ó - uppercase O, acute accent
    	entry("\u00D4", "&Ocirc;"), // Ô - uppercase O, circumflex accent
    	entry("\u00D5", "&Otilde;"), // Õ - uppercase O, tilde
    	entry("\u00D6", "&Ouml;"), // Ö - uppercase O, umlaut
    	entry("\u00D7", "&times;"), // multiplication sign
    	entry("\u00D8", "&Oslash;"), // Ø - uppercase O, slash
    	entry("\u00D9", "&Ugrave;"), // Ù - uppercase U, grave accent
    	entry("\u00DA", "&Uacute;"), // Ú - uppercase U, acute accent
    	entry("\u00DB", "&Ucirc;"), // Û - uppercase U, circumflex accent
    	entry("\u00DC", "&Uuml;"), // Ü - uppercase U, umlaut
    	entry("\u00DD", "&Yacute;"), // Ý - uppercase Y, acute accent
    	entry("\u00DE", "&THORN;"), // Þ - uppercase THORN, Icelandic
    	entry("\u00DF", "&szlig;"), // ß - lowercase sharps, German
    	entry("\u00E0", "&agrave;"), // à - lowercase a, grave accent
    	entry("\u00E1", "&aacute;"), // á - lowercase a, acute accent
    	entry("\u00E2", "&acirc;"), // â - lowercase a, circumflex accent
    	entry("\u00E3", "&atilde;"), // ã - lowercase a, tilde
    	entry("\u00E4", "&auml;"), // ä - lowercase a, umlaut
    	entry("\u00E5", "&aring;"), // å - lowercase a, ring
    	entry("\u00E6", "&aelig;"), // æ - lowercase ae
    	entry("\u00E7", "&ccedil;"), // ç - lowercase c, cedilla
    	entry("\u00E8", "&egrave;"), // è - lowercase e, grave accent
    	entry("\u00E9", "&eacute;"), // é - lowercase e, acute accent
    	entry("\u00EA", "&ecirc;"), // ê - lowercase e, circumflex accent
    	entry("\u00EB", "&euml;"), // ë - lowercase e, umlaut
    	entry("\u00EC", "&igrave;"), // ì - lowercase i, grave accent
    	entry("\u00ED", "&iacute;"), // í - lowercase i, acute accent
    	entry("\u00EE", "&icirc;"), // î - lowercase i, circumflex accent
    	entry("\u00EF", "&iuml;"), // ï - lowercase i, umlaut
    	entry("\u00F0", "&eth;"), // ð - lowercase eth, Icelandic
    	entry("\u00F1", "&ntilde;"), // ñ - lowercase n, tilde
    	entry("\u00F2", "&ograve;"), // ò - lowercase o, grave accent
    	entry("\u00F3", "&oacute;"), // ó - lowercase o, acute accent
    	entry("\u00F4", "&ocirc;"), // ô - lowercase o, circumflex accent
    	entry("\u00F5", "&otilde;"), // õ - lowercase o, tilde
    	entry("\u00F6", "&ouml;"), // ö - lowercase o, umlaut
    	entry("\u00F7", "&divide;"), // division sign
    	entry("\u00F8", "&oslash;"), // ø - lowercase o, slash
    	entry("\u00F9", "&ugrave;"), // ù - lowercase u, grave accent
    	entry("\u00FA", "&uacute;"), // ú - lowercase u, acute accent
    	entry("\u00FB", "&ucirc;"), // û - lowercase u, circumflex accent
    	entry("\u00FC", "&uuml;"), // ü - lowercase u, umlaut
    	entry("\u00FD", "&yacute;"), // ý - lowercase y, acute accent
    	entry("\u00FE", "&thorn;"), // þ - lowercase thorn, Icelandic
    	entry("\u00FF", "&yuml;")
    );

    /**
     * Reverse of {@link #ISO8859_1_ESCAPE} for unescaping purposes.
     */
    public static final Map<String, String> ISO8859_1_UNESCAPE = 
    	invert(ISO8859_1_ESCAPE);

    /**
     * A Map&lt;String, String&gt; to escape additional
     * <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">character entity
     * references</a>. Note that this must be used with {@link #ISO8859_1_ESCAPE} to get the full list of
     * HTML 4.0 character entities.
     */
    public static final Map<String, String> HTML40_EXTENDED_ESCAPE = Map.ofEntries(
        // <!-- Latin Extended-B -->
    	entry("\u0192", "&fnof;"), // latin small f with hook = function= florin, U+0192 ISOtech -->
    // <!-- Greek -->
    	entry("\u0391", "&Alpha;"), // greek capital letter alpha, U+0391 -->
    	entry("\u0392", "&Beta;"), // greek capital letter beta, U+0392 -->
    	entry("\u0393", "&Gamma;"), // greek capital letter gamma,U+0393 ISOgrk3 -->
    	entry("\u0394", "&Delta;"), // greek capital letter delta,U+0394 ISOgrk3 -->
    	entry("\u0395", "&Epsilon;"), // greek capital letter epsilon, U+0395 -->
    	entry("\u0396", "&Zeta;"), // greek capital letter zeta, U+0396 -->
    	entry("\u0397", "&Eta;"), // greek capital letter eta, U+0397 -->
    	entry("\u0398", "&Theta;"), // greek capital letter theta,U+0398 ISOgrk3 -->
    	entry("\u0399", "&Iota;"), // greek capital letter iota, U+0399 -->
    	entry("\u039A", "&Kappa;"), // greek capital letter kappa, U+039A -->
    	entry("\u039B", "&Lambda;"), // greek capital letter lambda,U+039B ISOgrk3 -->
    	entry("\u039C", "&Mu;"), // greek capital letter mu, U+039C -->
    	entry("\u039D", "&Nu;"), // greek capital letter nu, U+039D -->
    	entry("\u039E", "&Xi;"), // greek capital letter xi, U+039E ISOgrk3 -->
    	entry("\u039F", "&Omicron;"), // greek capital letter omicron, U+039F -->
    	entry("\u03A0", "&Pi;"), // greek capital letter pi, U+03A0 ISOgrk3 -->
    	entry("\u03A1", "&Rho;"), // greek capital letter rho, U+03A1 -->
    // <!-- there is no Sigmaf, and no U+03A2 character either -->
    	entry("\u03A3", "&Sigma;"), // greek capital letter sigma,U+03A3 ISOgrk3 -->
    	entry("\u03A4", "&Tau;"), // greek capital letter tau, U+03A4 -->
    	entry("\u03A5", "&Upsilon;"), // greek capital letter upsilon,U+03A5 ISOgrk3 -->
    	entry("\u03A6", "&Phi;"), // greek capital letter phi,U+03A6 ISOgrk3 -->
    	entry("\u03A7", "&Chi;"), // greek capital letter chi, U+03A7 -->
    	entry("\u03A8", "&Psi;"), // greek capital letter psi,U+03A8 ISOgrk3 -->
    	entry("\u03A9", "&Omega;"), // greek capital letter omega,U+03A9 ISOgrk3 -->
    	entry("\u03B1", "&alpha;"), // greek small letter alpha,U+03B1 ISOgrk3 -->
    	entry("\u03B2", "&beta;"), // greek small letter beta, U+03B2 ISOgrk3 -->
    	entry("\u03B3", "&gamma;"), // greek small letter gamma,U+03B3 ISOgrk3 -->
    	entry("\u03B4", "&delta;"), // greek small letter delta,U+03B4 ISOgrk3 -->
    	entry("\u03B5", "&epsilon;"), // greek small letter epsilon,U+03B5 ISOgrk3 -->
    	entry("\u03B6", "&zeta;"), // greek small letter zeta, U+03B6 ISOgrk3 -->
    	entry("\u03B7", "&eta;"), // greek small letter eta, U+03B7 ISOgrk3 -->
    	entry("\u03B8", "&theta;"), // greek small letter theta,U+03B8 ISOgrk3 -->
    	entry("\u03B9", "&iota;"), // greek small letter iota, U+03B9 ISOgrk3 -->
    	entry("\u03BA", "&kappa;"), // greek small letter kappa,U+03BA ISOgrk3 -->
    	entry("\u03BB", "&lambda;"), // greek small letter lambda,U+03BB ISOgrk3 -->
    	entry("\u03BC", "&mu;"), // greek small letter mu, U+03BC ISOgrk3 -->
    	entry("\u03BD", "&nu;"), // greek small letter nu, U+03BD ISOgrk3 -->
    	entry("\u03BE", "&xi;"), // greek small letter xi, U+03BE ISOgrk3 -->
    	entry("\u03BF", "&omicron;"), // greek small letter omicron, U+03BF NEW -->
    	entry("\u03C0", "&pi;"), // greek small letter pi, U+03C0 ISOgrk3 -->
    	entry("\u03C1", "&rho;"), // greek small letter rho, U+03C1 ISOgrk3 -->
    	entry("\u03C2", "&sigmaf;"), // greek small letter final sigma,U+03C2 ISOgrk3 -->
    	entry("\u03C3", "&sigma;"), // greek small letter sigma,U+03C3 ISOgrk3 -->
    	entry("\u03C4", "&tau;"), // greek small letter tau, U+03C4 ISOgrk3 -->
    	entry("\u03C5", "&upsilon;"), // greek small letter upsilon,U+03C5 ISOgrk3 -->
    	entry("\u03C6", "&phi;"), // greek small letter phi, U+03C6 ISOgrk3 -->
    	entry("\u03C7", "&chi;"), // greek small letter chi, U+03C7 ISOgrk3 -->
    	entry("\u03C8", "&psi;"), // greek small letter psi, U+03C8 ISOgrk3 -->
    	entry("\u03C9", "&omega;"), // greek small letter omega,U+03C9 ISOgrk3 -->
    	entry("\u03D1", "&thetasym;"), // greek small letter theta symbol,U+03D1 NEW -->
    	entry("\u03D2", "&upsih;"), // greek upsilon with hook symbol,U+03D2 NEW -->
    	entry("\u03D6", "&piv;"), // greek pi symbol, U+03D6 ISOgrk3 -->
    // <!-- General Punctuation -->
    	entry("\u2022", "&bull;"), // bullet = black small circle,U+2022 ISOpub -->
    // <!-- bullet is NOT the same as bullet operator, U+2219 -->
    	entry("\u2026", "&hellip;"), // horizontal ellipsis = three dot leader,U+2026 ISOpub -->
    	entry("\u2032", "&prime;"), // prime = minutes = feet, U+2032 ISOtech -->
    	entry("\u2033", "&Prime;"), // double prime = seconds = inches,U+2033 ISOtech -->
    	entry("\u203E", "&oline;"), // overline = spacing overscore,U+203E NEW -->
    	entry("\u2044", "&frasl;"), // fraction slash, U+2044 NEW -->
    // <!-- Letterlike Symbols -->
    	entry("\u2118", "&weierp;"), // script capital P = power set= Weierstrass p, U+2118 ISOamso -->
    	entry("\u2111", "&image;"), // blackletter capital I = imaginary part,U+2111 ISOamso -->
    	entry("\u211C", "&real;"), // blackletter capital R = real part symbol,U+211C ISOamso -->
    	entry("\u2122", "&trade;"), // trade mark sign, U+2122 ISOnum -->
    	entry("\u2135", "&alefsym;"), // alef symbol = first transfinite cardinal,U+2135 NEW -->
    // <!-- alef symbol is NOT the same as hebrew letter alef,U+05D0 although the
    // same glyph could be used to depict both characters -->
    // <!-- Arrows -->
    	entry("\u2190", "&larr;"), // leftwards arrow, U+2190 ISOnum -->
    	entry("\u2191", "&uarr;"), // upwards arrow, U+2191 ISOnum-->
    	entry("\u2192", "&rarr;"), // rightwards arrow, U+2192 ISOnum -->
    	entry("\u2193", "&darr;"), // downwards arrow, U+2193 ISOnum -->
    	entry("\u2194", "&harr;"), // left right arrow, U+2194 ISOamsa -->
    	entry("\u21B5", "&crarr;"), // downwards arrow with corner leftwards= carriage return, U+21B5 NEW -->
    	entry("\u21D0", "&lArr;"), // leftwards double arrow, U+21D0 ISOtech -->
    // <!-- ISO 10646 does not say that lArr is the same as the 'is implied by'
    // arrow but also does not have any other character for that function.
    // So ? lArr canbe used for 'is implied by' as ISOtech suggests -->
    	entry("\u21D1", "&uArr;"), // upwards double arrow, U+21D1 ISOamsa -->
    	entry("\u21D2", "&rArr;"), // rightwards double arrow,U+21D2 ISOtech -->
    // <!-- ISO 10646 does not say this is the 'implies' character but does not
    // have another character with this function so ?rArr can be used for
    // 'implies' as ISOtech suggests -->
    	entry("\u21D3", "&dArr;"), // downwards double arrow, U+21D3 ISOamsa -->
    	entry("\u21D4", "&hArr;"), // left right double arrow,U+21D4 ISOamsa -->
    // <!-- Mathematical Operators -->
    	entry("\u2200", "&forall;"), // for all, U+2200 ISOtech -->
    	entry("\u2202", "&part;"), // partial differential, U+2202 ISOtech -->
    	entry("\u2203", "&exist;"), // there exists, U+2203 ISOtech -->
    	entry("\u2205", "&empty;"), // empty set = null set = diameter,U+2205 ISOamso -->
    	entry("\u2207", "&nabla;"), // nabla = backward difference,U+2207 ISOtech -->
    	entry("\u2208", "&isin;"), // element of, U+2208 ISOtech -->
    	entry("\u2209", "&notin;"), // not an element of, U+2209 ISOtech -->
    	entry("\u220B", "&ni;"), // contains as member, U+220B ISOtech -->
    // <!-- should there be a more memorable name than 'ni'? -->
    	entry("\u220F", "&prod;"), // n-ary product = product sign,U+220F ISOamsb -->
    // <!-- prod is NOT the same character as U+03A0 'greek capital letter pi'
    // though the same glyph might be used for both -->
    	entry("\u2211", "&sum;"), // n-ary summation, U+2211 ISOamsb -->
    // <!-- sum is NOT the same character as U+03A3 'greek capital letter sigma'
    // though the same glyph might be used for both -->
    	entry("\u2212", "&minus;"), // minus sign, U+2212 ISOtech -->
    	entry("\u2217", "&lowast;"), // asterisk operator, U+2217 ISOtech -->
    	entry("\u221A", "&radic;"), // square root = radical sign,U+221A ISOtech -->
    	entry("\u221D", "&prop;"), // proportional to, U+221D ISOtech -->
    	entry("\u221E", "&infin;"), // infinity, U+221E ISOtech -->
    	entry("\u2220", "&ang;"), // angle, U+2220 ISOamso -->
    	entry("\u2227", "&and;"), // logical and = wedge, U+2227 ISOtech -->
    	entry("\u2228", "&or;"), // logical or = vee, U+2228 ISOtech -->
    	entry("\u2229", "&cap;"), // intersection = cap, U+2229 ISOtech -->
    	entry("\u222A", "&cup;"), // union = cup, U+222A ISOtech -->
    	entry("\u222B", "&int;"), // integral, U+222B ISOtech -->
    	entry("\u2234", "&there4;"), // therefore, U+2234 ISOtech -->
    	entry("\u223C", "&sim;"), // tilde operator = varies with = similar to,U+223C ISOtech -->
    // <!-- tilde operator is NOT the same character as the tilde, U+007E,although
    // the same glyph might be used to represent both -->
    	entry("\u2245", "&cong;"), // approximately equal to, U+2245 ISOtech -->
    	entry("\u2248", "&asymp;"), // almost equal to = asymptotic to,U+2248 ISOamsr -->
    	entry("\u2260", "&ne;"), // not equal to, U+2260 ISOtech -->
    	entry("\u2261", "&equiv;"), // identical to, U+2261 ISOtech -->
    	entry("\u2264", "&le;"), // less-than or equal to, U+2264 ISOtech -->
    	entry("\u2265", "&ge;"), // greater-than or equal to,U+2265 ISOtech -->
    	entry("\u2282", "&sub;"), // subset of, U+2282 ISOtech -->
    	entry("\u2283", "&sup;"), // superset of, U+2283 ISOtech -->
    // <!-- note that nsup, 'not a superset of, U+2283' is not covered by the
    // Symbol font encoding and is not included. Should it be, for symmetry?
    // It is in ISOamsn -->,
    	entry("\u2284", "&nsub;"), // not a subset of, U+2284 ISOamsn -->
    	entry("\u2286", "&sube;"), // subset of or equal to, U+2286 ISOtech -->
    	entry("\u2287", "&supe;"), // superset of or equal to,U+2287 ISOtech -->
    	entry("\u2295", "&oplus;"), // circled plus = direct sum,U+2295 ISOamsb -->
    	entry("\u2297", "&otimes;"), // circled times = vector product,U+2297 ISOamsb -->
    	entry("\u22A5", "&perp;"), // up tack = orthogonal to = perpendicular,U+22A5 ISOtech -->
    	entry("\u22C5", "&sdot;"), // dot operator, U+22C5 ISOamsb -->
    // <!-- dot operator is NOT the same character as U+00B7 middle dot -->
    // <!-- Miscellaneous Technical -->
    	entry("\u2308", "&lceil;"), // left ceiling = apl upstile,U+2308 ISOamsc -->
    	entry("\u2309", "&rceil;"), // right ceiling, U+2309 ISOamsc -->
    	entry("\u230A", "&lfloor;"), // left floor = apl downstile,U+230A ISOamsc -->
    	entry("\u230B", "&rfloor;"), // right floor, U+230B ISOamsc -->
    	entry("\u2329", "&lang;"), // left-pointing angle bracket = bra,U+2329 ISOtech -->
    // <!-- lang is NOT the same character as U+003C 'less than' or U+2039 'single left-pointing angle quotation
    // mark' -->
    	entry("\u232A", "&rang;"), // right-pointing angle bracket = ket,U+232A ISOtech -->
    // <!-- rang is NOT the same character as U+003E 'greater than' or U+203A
    // 'single right-pointing angle quotation mark' -->
    // <!-- Geometric Shapes -->
    	entry("\u25CA", "&loz;"), // lozenge, U+25CA ISOpub -->
    // <!-- Miscellaneous Symbols -->
    	entry("\u2660", "&spades;"), // black spade suit, U+2660 ISOpub -->
    // <!-- black here seems to mean filled as opposed to hollow -->
    	entry("\u2663", "&clubs;"), // black club suit = shamrock,U+2663 ISOpub -->
    	entry("\u2665", "&hearts;"), // black heart suit = valentine,U+2665 ISOpub -->
    	entry("\u2666", "&diams;"), // black diamond suit, U+2666 ISOpub -->

    // <!-- Latin Extended-A -->
    	entry("\u0152", "&OElig;"), // -- latin capital ligature OE,U+0152 ISOlat2 -->
    	entry("\u0153", "&oelig;"), // -- latin small ligature oe, U+0153 ISOlat2 -->
    // <!-- ligature is a misnomer, this is a separate character in some languages -->
    	entry("\u0160", "&Scaron;"), // -- latin capital letter S with caron,U+0160 ISOlat2 -->
    	entry("\u0161", "&scaron;"), // -- latin small letter s with caron,U+0161 ISOlat2 -->
    	entry("\u0178", "&Yuml;"), // -- latin capital letter Y with diaeresis,U+0178 ISOlat2 -->
    // <!-- Spacing Modifier Letters -->
    	entry("\u02C6", "&circ;"), // -- modifier letter circumflex accent,U+02C6 ISOpub -->
    	entry("\u02DC", "&tilde;"), // small tilde, U+02DC ISOdia -->
    // <!-- General Punctuation -->
    	entry("\u2002", "&ensp;"), // en space, U+2002 ISOpub -->
    	entry("\u2003", "&emsp;"), // em space, U+2003 ISOpub -->
    	entry("\u2009", "&thinsp;"), // thin space, U+2009 ISOpub -->
    	entry("\u200C", "&zwnj;"), // zero width non-joiner,U+200C NEW RFC 2070 -->
    	entry("\u200D", "&zwj;"), // zero width joiner, U+200D NEW RFC 2070 -->
    	entry("\u200E", "&lrm;"), // left-to-right mark, U+200E NEW RFC 2070 -->
    	entry("\u200F", "&rlm;"), // right-to-left mark, U+200F NEW RFC 2070 -->
    	entry("\u2013", "&ndash;"), // en dash, U+2013 ISOpub -->
    	entry("\u2014", "&mdash;"), // em dash, U+2014 ISOpub -->
    	entry("\u2018", "&lsquo;"), // left single quotation mark,U+2018 ISOnum -->
    	entry("\u2019", "&rsquo;"), // right single quotation mark,U+2019 ISOnum -->
    	entry("\u201A", "&sbquo;"), // single low-9 quotation mark, U+201A NEW -->
    	entry("\u201C", "&ldquo;"), // left double quotation mark,U+201C ISOnum -->
    	entry("\u201D", "&rdquo;"), // right double quotation mark,U+201D ISOnum -->
    	entry("\u201E", "&bdquo;"), // double low-9 quotation mark, U+201E NEW -->
    	entry("\u2020", "&dagger;"), // dagger, U+2020 ISOpub -->
    	entry("\u2021", "&Dagger;"), // double dagger, U+2021 ISOpub -->
    	entry("\u2030", "&permil;"), // per mille sign, U+2030 ISOtech -->
    	entry("\u2039", "&lsaquo;"), // single left-pointing angle quotation mark,U+2039 ISO proposed -->
    // <!-- lsaquo is proposed but not yet ISO standardized -->
    	entry("\u203A", "&rsaquo;"), // single right-pointing angle quotation mark,U+203A ISO proposed -->
    // <!-- rsaquo is proposed but not yet ISO standardized -->
    	entry("\u20AC", "&euro;") // -- euro sign, U+20AC NEW -->
    );

    /**
     * Reverse of {@link #HTML40_EXTENDED_ESCAPE} for unescaping purposes.
     */
    public static final Map<String, String> HTML40_EXTENDED_UNESCAPE = 
    	invert(HTML40_EXTENDED_ESCAPE);

    /**
     * Translator object for escaping HTML version 3.0.
     *
     * While {@link #escapeHtml3(String)} is the expected method of use, this
     * object allows the HTML escaping functionality to be used
     * as the foundation for a custom translator.
     */
    public static final Translator ESCAPE_HTML3 = aggregate(
	    lookup(BASIC_ESCAPE),
	    lookup(ISO8859_1_ESCAPE)
    );

    /**
     * Translator object for escaping HTML version 4.0.
     *
     * While {@link #escapeHtml4(String)} is the expected method of use, this
     * object allows the HTML escaping functionality to be used
     * as the foundation for a custom translator.
     */
    public static final Translator ESCAPE_HTML4 = aggregate(
    	lookup(BASIC_ESCAPE),
    	lookup(ISO8859_1_ESCAPE),
    	lookup(HTML40_EXTENDED_ESCAPE)
    );

    /**
     * Translator object for unescaping escaped HTML 3.0.
     *
     * While {@link #unescapeHtml3(String)} is the expected method of use, this
     * object allows the HTML unescaping functionality to be used
     * as the foundation for a custom translator.
     */
    public static final Translator UNESCAPE_HTML3 = aggregate(
        lookup(BASIC_UNESCAPE),
        lookup(ISO8859_1_UNESCAPE),
        UNICODE_DECODE
    );

    /**
     * Translator object for unescaping escaped HTML 4.0.
     *
     * While {@link #unescapeHtml4(String)} is the expected method of use, this
     * object allows the HTML unescaping functionality to be used
     * as the foundation for a custom translator.
     */
    public static final Translator UNESCAPE_HTML4 = aggregate(
        lookup(BASIC_UNESCAPE),
        lookup(ISO8859_1_UNESCAPE),
        lookup(HTML40_EXTENDED_UNESCAPE),
        UNICODE_DECODE
    );

  //--------------------------------------------------------------------------
    /**
     * <p>Escapes the characters in a {@code String} using HTML entities.</p>
     *
     * <p>
     * For example:
     * </p>
     * <p><code>"bread" &amp; "butter"</code></p>
     * becomes:
     * <p>
     * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
     * </p>
     *
     * <p>Supports all known HTML 4.0 entities, including funky accents.
     * Note that the commonly used apostrophe escape character (&amp;apos;)
     * is not a legal entity and so is not supported). </p>
     *
     * @param input  the {@code String} to escape, may be null
     * @return a new escaped {@code String}, {@code null} if null string input
     *
     * @see <a href="http://hotwired.lycos.com/webmonkey/reference/special_characters/">ISO Entities</a>
     * @see <a href="http://www.w3.org/TR/REC-html32#latin1">HTML 3.2 Character Entities for ISO Latin-1</a>
     * @see <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">HTML 4.0 Character entity references</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#h-5.3">HTML 4.01 Character References</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#code-position">HTML 4.01 Code positions</a>
     */
    public static final String escapeHtml4(final String input) {
        return translate(input, ESCAPE_HTML4);
    }

    /**
     * <p>Escapes the characters in a {@code String} using HTML entities.</p>
     * <p>Supports only the HTML 3.0 entities. </p>
     *
     * @param input  the {@code String} to escape, may be null
     * @return a new escaped {@code String}, {@code null} if null string input
     */
    public static final String escapeHtml3(final String input) {
        return translate(input, ESCAPE_HTML3);
    }

    //-----------------------------------------------------------------------
    /**
     * <p>Unescapes a string containing entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes. Supports HTML 4.0 entities.</p>
     *
     * <p>For example, the string {@code "&lt;Fran&ccedil;ais&gt;"}
     * will become {@code "<Fran�ais>"}</p>
     *
     * <p>If an entity is unrecognized, it is left alone, and inserted
     * verbatim into the result string. e.g. {@code "&gt;&zzzz;x"} will
     * become {@code ">&zzzz;x"}.</p>
     *
     * @param input  the {@code String} to unescape, may be null
     * @return a new unescaped {@code String}, {@code null} if null string input
     */
    public static final String unescapeHtml4(final String input) {
        return translate(input, UNESCAPE_HTML4);
    }

    /**
     * <p>Unescapes a string containing entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes. Supports only HTML 3.0 entities.</p>
     *
     * @param input  the {@code String} to unescape, may be null
     * @return a new unescaped {@code String}, {@code null} if null string input
     */
    public static final String unescapeHtml3(final String input) {
        return translate(input, UNESCAPE_HTML3);
    }
	}