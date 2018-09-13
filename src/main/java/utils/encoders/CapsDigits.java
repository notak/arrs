package utils.encoders;

public class CapsDigits {

	public static int encode(char c, char ...handleFirst) {
		for (int j=0;j<handleFirst.length;j++) {
			if (handleFirst[j]==c) {
				return j+1;
			}
		}
		if (c>='0' && c<='9') {
			return c + handleFirst.length + 27 - '0';
		} else if (c>='A' && c<='Z') {
			return c + handleFirst.length + 1 - 'A';
		} else if (c==' ') {
			return handleFirst.length + 37;
		} else if (c=='*') {
			return handleFirst.length + 38;
		}	
			return 0;
		}

//	public static long encode(String in) {
//		return encode(in);
//	}
//	
	public static long encode(String in, char ...handleFirst) {
		if (in==null) return 0;
		long out = 0;
		in = in.toUpperCase();
		for (int i=0; i<in.length(); i++) {
			out |= (long)(CapsDigits.encode(in.charAt(i), handleFirst)) << (6 * i);
		}
		return out;
	}

	public static int encodeToInt(String in, char ...handleFirst) {
		return (int)encode(in, handleFirst);
	}

	public static short encodeToShort(String in, char ...handleFirst) {
		return (short)encode(in, handleFirst);
	}

	public static byte encodeToByte(String in, char ...handleFirst) {
		return (byte)encode(in, handleFirst);
	}

	public static char decodeChar(int i, char ...handleFirst) {
		if (--i < 0) return (char)0;
		if (i<handleFirst.length) return handleFirst[i];
		else if ((i-=handleFirst.length)<26) return (char)('A' + i);
		else if ((i-=26)<10) return (char)('0' + i);
		else if ((i-=10)==1) return '*';
		else return ' ';
	}

	public static String decode(long in, char ...handleFirst) {
		StringBuffer out = new StringBuffer();
		int i;
		while (in>0) {
			i = (int)(in & 0b111111);
			in >>>= 6; 
			out.append(CapsDigits.decodeChar(i, handleFirst));
		}
		return out.toString();
	}
	
}