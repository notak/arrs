package utils.encoders;

public class ZigZag {
	public static byte encode(byte n) {
		return (byte)encode((int)n);
	}
	
	public static byte decode(byte in) {
		return (byte)decode(Byte.toUnsignedInt(in));
	}

	public static short encode(short n) {
		return (short)encode((int)n);
	}
	
	public static short decode(short in) {
		return (short)decode(Short.toUnsignedInt(in));
	}

	public static int encode(int n) {
		return (n << 1) ^ (n >> 31);
	}
	
	public static int decode(int in) {
		long n = Integer.toUnsignedLong(in);
		return (int)((n >> 1) ^ (-(n & 1)));
	}

	public static long encode(long n) {
		return (n << 1L) ^ (n >> 63);
	}
	
	public static long decode(long n) {
		return ((n >> 1L) ^ (-(n & 1L)));
	}
}