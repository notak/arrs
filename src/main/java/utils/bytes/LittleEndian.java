package utils.bytes;

import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.Long.numberOfLeadingZeros;
import static java.lang.Short.toUnsignedInt;

import java.util.function.Consumer;

import utils.arrays.Bytes;

public class LittleEndian {
	public static int toInt(byte[] bytes, int start, int len) {
		int out = 0;
		if (start + len > bytes.length) len = bytes.length - start;
		for (int i=0; i<len; i++) {
			out |= ((int)(bytes[start+i] & 0xFF)) << (8*i);
		}
		return out;
	}

	/** little-endian conversion of byte[] to int, assumes a byte[4] */
	public static int toInt(byte[] bytes) {
		return toInt(bytes, 0 , 4);
	}

	/** little-endian conversion of byte[] to int, assumes a byte[2] */
	public static short toShort(byte[] bytes) {
		return (short)toInt(bytes, 0 , 2);
	}

	/** little-endian conversion of byte[] to short */
	public static short toShort(byte[] bytes, int start, int len) {
		return (short)toInt(bytes, start , len);
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

	/** little-endian conversion of short to byte[]*/
	public static void putShort(byte[] bytes, int start, short val) {
		putInt(bytes, start, 2, toUnsignedInt(val));
	}

	/** little-endian conversion of short to byte[]*/
	public static void putShort(byte[] bytes, int start, int len, short val) {
		putInt(bytes, start, len, toUnsignedInt(val));
	}

	/** little-endian conversion of short to byte[]*/
	public static byte[] fromShort(short val) {
		return fromInt(toUnsignedInt(val));
	}

	/** little-endian conversion of short to byte[]*/
	public static byte[] encode(byte val) {
		return val!=0 ? Bytes.toArray(val) : Bytes.EMPTY;
	}

	/** little-endian conversion of short to byte[]*/
	public static byte[] encode(short val) {
		return encode(toUnsignedInt(val));
	}

	/** little-endian conversion of int to byte[]. 
	 * Always returns an array of length 4 */
	public static byte[] fromInt(int val) {
		return fromInt(val, 4);
	}

	/** little-endian conversion of int to byte[]. 
	 * Returns the smallest possible array capable of representing the value. In
	 * the case of 0 a zero-length array is returned */
	public static byte[] encode(int val) {
		var size = 4 - numberOfLeadingZeros(val)/8;
		return fromInt(val, size);
	}

	/** little-endian conversion of int to byte[]. 
	 * Always returns an array of the specified size. If the number
	 * doesn't fit, the higher bits will be cropped */
	public static byte[] fromInt(int val, int size) {
		byte[] out = new byte[size];
		putInt(out, 0, size, val);
		return out;
	}

	/** little-endian conversion of int to byte[]*/
	public static byte[] fromInts(int[] vals) {
		byte[] out = new byte[4*vals.length];
		for (int i=0; i<vals.length; i++) putInt(out, i*4, vals[i]);
		return out;
	}

	public static long toLong(byte[] bytes, int start, int len) {
		if (start + len > bytes.length) len = bytes.length - start;
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

	/** little-endian conversion of long to byte[]. 
	 * Always returns an array of length 8 */
	public static byte[] fromLong(long val) {
		return fromLong(val, 8);
	}

	/** little-endian conversion of long to byte[]. 
	 * Returns the smallest possible array capable of representing the value. In
	 * the case of 0 a zero-length array is returned */
	public static byte[] encode(long val) {
		var size = 8 - numberOfLeadingZeros(val)/8;
		return fromLong(val, size);
	}

	/** little-endian conversion of long to byte[]. 
	 * Always returns an array of length size, discarding higher bits which 
	 * don't fit */
	public static byte[] fromLong(long val, int size) {
		byte[] out = new byte[size];
		putLong(out, 0, size, val);
		return out;
	}
	
	/** little-endian conversion of int to byte[]*/
	public static byte[] fromLongs(long[] vals) {
		byte[] out = new byte[8*vals.length];
		for (int i=0; i<vals.length; i++) putLong(out, i*8, 8, vals[i]);
		return out;
	}
}
