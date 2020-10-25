package utils.bytes;

import static java.lang.Math.min;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Display {
	/** prints the value of a byte[] as a big-endian hex string. 
	 * Return an empty string if bytes is null */
	public static String bytesToHex(byte[] bytes) {
		if (bytes==null) return "";
	    char[] out = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        out[j * 2] = hexDigits[v >>> 4];
	        out[j * 2 + 1] = hexDigits[v & 0x0F];
	    }
	    return new String(out);
	}
	private final static char[] hexDigits = "0123456789ABCDEF".toCharArray();
	
	/** prints the binary value of a byte 0-padded to the full 8 bits */
	public static String paddedBinary(byte in) {
		return String.format("%8s", Integer.toBinaryString(in & 0xFF))
				.replace(' ', '0');
	}
	
	/** prints a portion of a byte array as a series of padded binary strings, 
	 * each prefixed with the byte's index in the array */
	public static String paddedBinaryLine(byte[] in, int start, int width) {
		return IntStream.range(start, min(start + width, in.length))
			.mapToObj(j->String.format("%3d %s", j, paddedBinary(in[j])))
			.collect(Collectors.joining(", "));
	}
	
	/** prints a byte array as a series of padded binary strings, each prefixed 
	 * with the byte's index in the array. The output is broken into lines of 
	 * width bytes */
	public static String paddedBinary(byte[] in, int width) {
		return IntStream.range(0, in.length/width + (in.length % width>0 ? 1 : 0))
		.mapToObj(i->paddedBinaryLine(in, i * width, width))
		.collect(Collectors.joining("\n"));
	}

	/** prints a byte array as a series of padded binary strings, each prefixed 
	 * with the byte's index in the array. The output is broken into lines of 8 
	 * bytes */
	public static String paddedBinary(byte[] in) {
		return paddedBinary(in, 8);
	}
}
