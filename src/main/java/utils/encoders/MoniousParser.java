package utils.encoders;

import static java.lang.Byte.toUnsignedInt;
import static java.lang.Integer.highestOneBit;
import static java.lang.Math.min;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;

public final class MoniousParser {

	public byte[] getBytes(byte[] buf, int start, int len) {
		return copyOfRange(buf, start, start+len);
	}

	public static int unVarInt(byte[] buf, int[] start) {
		int n = 0;
		int i = 0;
		byte b;
		do n |= (b = buf[start[0]++]) << (7*i++); while (b<0);
		return n;
	}

	public static void varInt(byte[] buf, int[] start, int val) {
		do {
			 buf[start[0]++] = (byte)(val & 127);
			 val>>>=7;
		} while (val!=0);
	}

	@FunctionalInterface
	public static interface Fielder {
		public void onField(int id, byte[] buf, int start, int len);
	}

	public static void decode(Fielder f, byte[] buf) {
		decode(f, buf, 0, buf.length);
	}

	public static void decode(Fielder f, byte[] buf, int start) {
		decode(f, buf, start, buf.length);
	}

	public static void decode(Fielder f, byte[] buf, int start, int end) {
		int[] s = { start }; //pass-by-ref
		byte[] single = { 0 };
		int id = 0;
		while (s[0] < end) {
			var header = toUnsignedInt(buf[s[0]++]);
			var idDiff = header >>> 5;
			if (idDiff==7) idDiff = unVarInt(buf, s) + 7;
			id += idDiff;

			if ((header & 16) != 0) {
				single[0] = (byte)(header & 15);
				f.onField(id, single, 0, 1);
			}

			var len = header & 15;
			if (len==15) len = unVarInt(buf, s) + 15;
			f.onField(id, buf, s[0], len);
			s[0] += len;
		}
	}

	private static int length(int idDiff, byte[] val) {
		var len = 1;
		if (idDiff>=7) len += highestOneBit(idDiff-7)/7 + 1;
		var single = val.length==1 && val[0]<16;
		if (!single) {
			len += val.length;
			if (val.length>=15) len += highestOneBit(val.length-15)/7 + 1;
		}
		return len;
	}
	
	public static byte[] encode(int idDiff, byte[] val, byte[] into, int[] start) {
		var len = length(idDiff, val);
		while (into.length-start[0] < len) into = copyOf(into, into.length * 2);
		into[start[0]++] = (byte)((min(idDiff, 7)<<5)
			| (len==1 ? (16 + val[0]) : min(val.length, 15)));
		if (idDiff>=7) varInt(into, start, idDiff-7);
		if (val.length>=15) varInt(into, start, idDiff-15);
		if (len>1) arraycopy(val, 0, into, start[0], val.length);
		return into;
	}
}
