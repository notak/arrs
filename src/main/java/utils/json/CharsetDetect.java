package utils.json;

import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import utils.json.JsonParser.JsonParserException;

public class CharsetDetect {
	public static int[] BOM_UTF32BE = { 0x00, 0x00, 0xFE, 0xFF, };
	public static int[] BOM_UTF32LE = { 0xFF, 0xFE, 0x00, 0x00, };
	public static int[] BOM_UTF8 = { 0xEF, 0xBB, 0xBF, };
	public static int[] BOM_UTF16BE = { 0xFE, 0xFF, };
	public static int[] BOM_UTF16LE = { 0xFF, 0xFE, };
	
	public static Charset UTF_32BE = Charset.forName("UTF-32BE");
	public static Charset UTF_32LE = Charset.forName("UTF-32LE");
	
	
	/* If there is a leading Unicode Byte-Order-Mark, this will be consumed and 
	 * the charset returned accordingly */
	public static Charset detectAndConsumeBOM(InputStream buf) 
	throws IOException {
		buf.mark(4);

		int[] sig = { buf.read(), buf.read(), buf.read(), buf.read() };
		
		if (Arrays.equals(sig, BOM_UTF32BE)) return UTF_32BE;
		if (Arrays.equals(sig, BOM_UTF32LE)) return UTF_32LE;

		buf.reset();

		if (sig[0] == 0xEF && sig[1] == 0xBB && sig[2] == 0xBF) {
			buf.skip(3);
			return UTF_8;
		} 
		if (sig[0] == 0xFE && sig[1] == 0xFF) {
			buf.skip(2);
			return UTF_16BE;
		}
		if (sig[0] == 0xFF && sig[1] == 0xFE) {
			buf.skip(2);
			return UTF_16LE;
		} 

		return null;
	}
	
	/* Detect the encoding based on the rules in
	 * http://www.ietf.org/rfc/rfc4627.txt section 3:
	 * 
	 * Since the first two characters of a JSON text will always be ASCII 
	 * characters [RFC0020], it is possible to determine whether an octet 
	 * stream is UTF-8, UTF-16 (BE or LE), or UTF-32 (BE or LE) by looking at 
	 * the pattern of nulls in the first four octets.
	 * 
	 * 00 00 00 xx UTF-32BE 
	 * 00 xx 00 xx UTF-16BE 
	 * xx 00 00 00 UTF-32LE 
	 * xx 00 xx 00 UTF-16LE 
	 * xx xx xx xx UTF-8 */
	public static Charset detectFromNulls(InputStream buf) throws IOException {
		buf.mark(4);
		int[] sig = { buf.read(), buf.read(), buf.read(), buf.read() };
		buf.reset();
		
		if (sig[0] == 0 && sig[1] == 0 && sig[2] == 0 && sig[3] != 0) {
			return UTF_32BE;
		} else if (sig[0] != 0 && sig[1] == 0 && sig[2] == 0 && sig[3] == 0) {
			return UTF_32LE;
		} else if (sig[0] == 0 && sig[1] != 0 && sig[2] == 0 && sig[3] != 0) {
			return UTF_16BE;
		} else if (sig[0] != 0 && sig[1] == 0 && sig[2] != 0 && sig[3] == 0) {
			return UTF_16LE;
		} else if (sig[0] != 0 && sig[1] != 0 && sig[2] != 0 && sig[3] != 0) {
			return UTF_8;
		} else {
			return null;
		}
	}
	
	/* Figure out the character set encoding. using a BOM or leading nulls */
	public static Charset detectCharset(InputStream buf) 
		throws JsonParserException {
		try {
			var fromBom = detectAndConsumeBOM(buf);
			return fromBom!=null ? fromBom : detectFromNulls(buf);
				
		} catch (IOException e) {
			throw new JsonParserException(e, "IOException while detecting charset", 1, 1, 0);
		}
	}
}
