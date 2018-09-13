package utils.bytes;

import java.util.function.Consumer;

public class LittleEndian {
	public static int toInt(byte[] bytes, int start, int len) {
		int out = 0;
		for (int i=0; i<len; i++) {
			out |= ((int)(bytes[start+i] & 0xFF)) << (8*i);
		}
		return out;
	}

	/** little-endian conversion of byte[] to int, assumes a byte[4] */
	public static int toInt(byte[] bytes) {
		return toInt(bytes, 0 , 4);
	}

	/** little-endian conversion of byte[] to int, assumes a byte[4] */
	public static int[] toInts(byte[] bytes) {
		int[] out = new int[bytes.length/4];
		for (int i=0; i<out.length; i++) out[i] = toInt(bytes, i*4 , 4);
		return out;
	}

	/** little-endian conversion of byte[] to int, assumes a byte[4] */
	public static long[] toLongs(byte[] bytes) {
		long[] out = new long[bytes.length/8];
		for (int i=0; i<out.length; i++) out[i] = toLong(bytes, i*8 , 8);
		return out;
	}

	/** create a byte[4], pass it into a function to populate it, and
	 * then perform little-endian conversion to int 
	 * useful for reading from streams etc */
	public static int readInt(Consumer<byte[]> reader) {
		byte[] out = new byte[4];
		reader.accept(out);
		return toInt(out);
	}

	/** create a byte[8], pass it into a function to populate it, and
	 * then perform little-endian conversion to int 
	 * useful for reading from streams etc */
	public static long readLong(Consumer<byte[]> reader) {
		byte[] out = new byte[8];
		reader.accept(out);
		return toLong(out);
	}

	/** little-endian conversion of int to byte[]*/
	public static void putInt(byte[] bytes, int start, int val) {
		putInt(bytes, start, 4, val);
	}

	/** little-endian conversion of int to byte[]*/
	public static void putInt(byte[] bytes, int start, int len, int val) {
		for (int i=0; i<len; i++) {
			bytes[start+i] = (byte)(val >>> (8*i));
		}
	}

	/** little-endian conversion of int to byte[]*/
	public static byte[] fromInt(int val) {
		byte[] out = new byte[4];
		putInt(out, 0, val);
		return out;
	}

	/** little-endian conversion of int to byte[]*/
	public static byte[] fromInts(int[] vals) {
		byte[] out = new byte[4*vals.length];
		for (int i=0; i<vals.length; i++) putInt(out, i*4, vals[i]);
		return out;
	}

	public static long toLong(byte[] bytes, int start, int len) {
		long out = 0;
		for (int i=0; i<len; i++) {
			out |= ((long)(bytes[start+i] & 0xFF)) << (8*i);
		}
		return out;
	}

	/** little-endian conversion of byte[] to long, assumes a byte[4] */
	public static long toLong(byte[] bytes) {
		return toLong(bytes, 0 , 8);
	}

	public static void putLong(byte[] bytes, int start, int len, long val) {
		for (int i=0; i<len; i++) {
			bytes[start+i] = (byte)(val >>> (8*i));
		}
	}

	public static byte[] fromLong(long val) {
		byte[] out = new byte[8];
		putLong(out, 0, 8, val);
		return out;
	}
	
	/** little-endian conversion of int to byte[]*/
	public static byte[] fromLongs(long[] vals) {
		byte[] out = new byte[8*vals.length];
		for (int i=0; i<vals.length; i++) putLong(out, i*8, 8, vals[i]);
		return out;
	}
}
