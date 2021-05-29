package utils.arrays;

public class Arrs {
	public static int[] append(int[] a, int b) {
		return Ints.append(a, b);
	}
	public static int[] append(int[] a, int[] b) {
		return Ints.append(a, b);
	}
	public static short[] append(short[] a, short b) {
		return Shorts.append(a, b);
	}
	public static short[] append(short[] a, short[] b) {
		return Shorts.append(a, b);
	}
	public static long[] append(long[] a, long b) {
		return Longs.append(a, b);
	}
	public static long[] append(long[] a, long[] b) {
		return Longs.append(a, b);
	}
	public static byte[] append(byte[] a, byte b) {
		return Bytes.append(a, b);
	}
	public static byte[] append(byte[] a, byte[] b) {
		return Bytes.append(a, b);
	}
	public static <T> T[] append(T[] a, T b) {
		return Objs.append(a, b);
	}
	public static <T> T[] append(T[] a, T[] b) {
		return Objs.append(a, b);
	}
}
