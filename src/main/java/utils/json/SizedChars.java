package utils.json;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;

public class SizedChars {
	public static char[] create(int size, char initial) {
		var out = create(size);
		out[0]++;
		out[2] = initial;
		return out;
	}
	
	public static char[] create(int size) {
		return new char[size + 2];
	}
	
	public static int length(char[] in) {
		return (in[1] << 16) | in[0];
	}
	
	public static char[] toChars(char[] in) {
		return copyOfRange(in, 2, 2 + length(in));
	}
	
	public static char[] append(char[] a, char toAdd) {
		var pos = length(a) + 2;
		if (pos >= a.length) a = copyOf(a, a.length * 2);
		a[pos] = toAdd;
		if (++a[0] == 0) a[1]++;
		return a;
	}
	
	public static char[] append(char[] a, int codepoint) {
		var pos = length(a) + 2;
		if (pos + 1 >= a.length) a = copyOf(a, a.length * 2);
		if (++a[0] == 0) a[1]++;
		if (2 == Character.toChars(codepoint, a, pos) && ++a[0] == 0) a[1]++;
		return a;
	}
}
