package utils.bytes;

import static java.lang.Short.toUnsignedInt;

public class Bits {
	public static int count(byte b) {
		return Integer.bitCount(Byte.toUnsignedInt(b));
	}
	
	public static byte or(byte a, byte b) {
		return (byte)(Byte.toUnsignedInt(a) | Byte.toUnsignedInt(b));
	}

	public static byte or(byte a, int b) {
		return (byte)(Byte.toUnsignedInt(a) | b);
	}

	public static byte xor(byte a, byte b) {
		return (byte)(Byte.toUnsignedInt(a) ^ Byte.toUnsignedInt(b));
	}

	public static byte and(byte a, byte b) {
		return (byte)(Byte.toUnsignedInt(a) & Byte.toUnsignedInt(b));
	}

	public static byte not(byte a) {
		return (byte)(~Byte.toUnsignedInt(a));
	}
	
	public static boolean isSet(long value, int bit) {
		return (value & (1L << bit))!=0;
	}
	
	public static boolean isSet(int value, int bit) {
		return (value & (1 << bit))!=0;
	}
	
	public static boolean isSet(byte value, int bit) {
		return (Byte.toUnsignedInt(value) & (1 << bit))!=0;
	}

	public static long set(long value, int bit) {
		return value | (1L << bit);
	}
	
	public static int set(int value, int bit) {
		return value | (1 << bit);
	}
	
	public static short set(short value, int bit) {
		return or(value, 1 << bit);
	}
	
	public static byte set(byte value, int bit) {
		return or(value, 1 << bit);
	}

	public static short or(short a, int b) {
		return (short)(toUnsignedInt(a) | b);
	}

	public static short or(short a, short b) {
		return (short)(toUnsignedInt(a) | toUnsignedInt(b));
	}
	
	public static short and(short a, int b) {
		return (short)(toUnsignedInt(a) & b);
	}
	
	public static short and(short a, short b) {
		return (short)(toUnsignedInt(a) & toUnsignedInt(b));
	}
}
