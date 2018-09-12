package arrays;

import static java.lang.Math.min;

import java.util.Arrays;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class Ints {
	@FunctionalInterface
	public static interface Consumer {
		void accept(int val);
	}
	
	@FunctionalInterface
	public static interface Pred {
		boolean test(int val);
	}
	
	@FunctionalInterface
	public static interface Fn<S> {
		S apply(int val);
	}
	
	@FunctionalInterface
	public static interface BiFn<S> {
		S apply(int val, int val2);
	}
	
	@FunctionalInterface
	public static interface ToIntFn {
		int apply(int val);
	}
	
	@FunctionalInterface
	public static interface ToLongFn {
		long apply(int val);
	}
	
	@FunctionalInterface
	public static interface ToIntBiFn {
		int apply(int val, int val2);
	}
	
	public static final int[] EMPTY = {};

	@SafeVarargs
	public static <T> int[] toArray(int... in) { return in; }

	public static <T> int[] toArray(int in) { 
		int[] out = { in }; return out;
	}

	public static void forEach(int[] us, Consumer fn) {
		for (int i=0; i<us.length; i++) fn.accept(us[i]);
	}
	
	/** Optionally get the nth element, returns empty for array len<n+1 */
	public static OptionalInt nth(int[] in, int n) { 
		return okPos(in, n) ? OptionalInt.of(in[n]) : OptionalInt.empty();
	}

	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static OptionalInt nthLast(int[] in, int n) {
		return nth(in, in.length-(n+1));
	}
	
	/** Optionally get the first element, returning empty for an empty array */
	public static OptionalInt first(int[] in) { return nth(in, 0); }
	
	/** Optionally get the last element, returns empty for empty array */
	public static OptionalInt last(int[] in) { return nthLast(in, 0); }

	/** Maps an array of ints to an array of T */ 
	public static <T> T[] 
	map(int[] in, Fn<T> mapper, IntFunction<T[]> cons) {
		var out = cons.apply(in.length);
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}
	/** Maps an array of ints to an array of strings */ 
	public static String[] mapStr(int[] in, Fn<String> mapper) {
		return map(in, mapper, String[]::new);
	}
	/** Maps an array of ints to an array of strings */ 
	public static String[] mapStr(int[] in) {
		return mapStr(in, i->"" + i);
	}
	/** Maps an array of ints to an array of ints */ 
	public static int[] map(int[] in, ToIntFn mapper) {
		var out = new int[in.length];
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}

	/** Maps an array of ints to an array of ints */ 
	public static long[] mapLong(int[] in, ToLongFn mapper) {
		var out = new long[in.length];
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}

	public static int[] subArray(int[] in, int start) {
		return subArray(in, start, in.length);
	}
	public static int[] subArray(int[] in, int start, int end) {
		return copyOfRange(in, min(start, in.length), min(end, in.length));
	}

	public static int[] subArrayLen(int[] in, int start, int len) {
		return subArray(in, start, start + len);
	}

	public static int[] spliced(int[] in, int pos, int len) {
		if (in.length==0) return in;
		if (pos==0) return copyOfRange(in, len, in.length);
		else if (pos+len>=in.length) return copyOfRange(in, 0, pos);
		
		int[] out = copyOfRange(in, 0, in.length-len);
		for (int i=pos; i<out.length; i++) out[i] = in[i+len];
		return out;
	}
	
	public static Stream<String> streamAsString(int[] in) {
		return stream(in).mapToObj(i->"" + i);
	}
	
	public static String join(String glue, int[] a) {
		return join(a, glue);
	}

	public static String join(int[]in, String glue) {
		return streamAsString(in).collect(joining(glue));
	}

	public static int indexOf(int[] hay, int needle) {
		for (int i=hay.length-1; i>=0; i--) if (needle==hay[i]) return i;
		return -1;
	}

	public static int[] indexOf(int[] hay, int[] needles) {
		int[] out = Ints.EMPTY;
		for (int i=hay.length-1; i>=0; i--) {
			if (contains(needles, hay[i])) out = Ints.append(out, i);
		}
		return out;
	}
	
	public static boolean contains(int[] hay, int needle) {
		return indexOf(hay, needle) >= 0;
	}
	
	public static boolean okPos(int[] a, int i) { return i>=0 && i<a.length; }
	
	public static boolean empty(int[] in) { return in.length==0; }
	
	public static int[] remove(int[] hay, int needle) {
		if (empty(hay)) return hay;
		int pos = indexOf(hay, needle);
		return pos<0 ? hay : spliced(hay, pos, 1);
	}
	
	public static <V> int[] remove(int[] hay, V[] needles, Fn<V>map) {
		for (int j=hay.length; j>0; j--) {
			V test = map.apply(hay[j-1]);
			for (int i=needles.length; i>0; i--) {
				if (needles[i-1].equals(test)) {
					hay = spliced(hay, j-1, 1);
					break;
				}
			}
		}
		return hay;
	}

	public static int[] remove(int[] hay, int[] needles) {
		return foldl(needles, hay, Ints::remove);
	}

	public static int[] remove(int[] old, Pred test) {
		for (int j=old.length-1; j>=0; j--) {
			if (test.test(old[j])) old = spliced(old, j, 1);
		}
		return old;
	}
	
	public static int
	foldl(int[] in, int identity, ToIntBiFn fold) {
		int out = identity;
		for (int t: in) out = fold.apply(t, out);
		return out;
	}
	
	public static <U> U 
	foldl(int[] in, U ident, BiFunction<U, Integer, U> fold) {
		for (int t: in) ident = fold.apply(ident, t);
		return ident;
	}
	
	public static int sum(int[] in) {
		return foldl(in, (int)0, (i, s)->(int)(i+s));
	}

	/**Pair up elements in the array and map the pairs to a new value */
	public static <T, U> int[] pair(int[] in, ToIntBiFn pair) {
		if (in.length==0) return in;
		int[] out = new int[in.length/2];
		for (int i=1; i<in.length; i+=2) {
			out[(i-1)/2] = pair.apply(in[i-1], in[i]);
		}
		return out;
	}

	public static int[] pairLeft(int[] in, int ident, BiFn<Integer> pair) {
		int[] out = new int[in.length];
		for (int i=in.length-1; i>=0; i--) {
			out[i] = pair.apply(i<1 ? ident : in[i-1], in[i]);
		}
		return out;
	}

	public static int[] filter(int[] in, Pred mapper) {
		for (int i=in.length; i>0; i--) {
			if (!mapper.test(in[i-1])) in = spliced(in, i-1, 1);
		}
		return in;
	}
	
	public static int[] reversed(int[] a) {
		if (a.length<2) return a;
		int[] out = new int[a.length];
		for (int i=0; i<out.length; i++) out[i] = a[a.length-(1+i)];
		return out;
	}

	public static int[] append(int[] a, int[] b) {
		if (a.length==0) return b; else if (b.length==0) return a;
		int[] out = Arrays.copyOf(a, a.length + b.length);
		for (int i=0; i<b.length; i++) out[a.length+i] = b[i];
		return out;
	}
	
	public static int[] append(int[] a, int b) {
		int[] out = copyOf(a, a.length + 1);
		out[a.length] = b;
		return out;
	}

	public static int[] union(int[] a, int b) {
		return (contains(a,  b)) ? a : append(a, b);
	}

	public static int[] union(int[] a, int[] b) {
		return foldl(b, a, Ints::union);
	}
}
