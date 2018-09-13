package utils.encoders;

public class ZigZag {
	public static int encode(int n) {
		return (n << 1) ^ (n >> 31);
	}
	
	public static int decode(int in) {
		long n = Integer.toUnsignedLong(in);
		return (int)((n >> 1) ^ (-(n & 1)));
	}
}