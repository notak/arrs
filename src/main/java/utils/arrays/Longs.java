package utils.arrays;

import static java.lang.Math.min;

import java.util.Arrays;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.OptionalLong;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Longs {
	@FunctionalInterface
	public static interface Consumer {
		void accept(long val);
	}
	
	@FunctionalInterface
	public static interface Pred {
		boolean test(long val);
	}
	
	@FunctionalInterface
	public static interface BiPred {
		boolean test(long val, long val2);
	}
	
	@FunctionalInterface
	public static interface Fn<S> {
		S apply(long val);
	}
	
	@FunctionalInterface
	public static interface BiFn<S> {
		S apply(long val, long val2);
	}
	
	@FunctionalInterface
	public static interface ToIntFn {
		int apply(long val);
	}
	
	@FunctionalInterface
	public static interface ToLongFn {
		long apply(long val);
	}
	
	@FunctionalInterface
	public static interface ToLongBiFn {
		long apply(long val, long val2);
	}
	
	public static final long[] EMPTY = {};

	@SafeVarargs
	public static <T> long[] toArray(long... in) { return in; }

	public static <T> long[] toArray(long in) { 
		long[] out = { in }; return out;
	}

	public static void forEach(long[] us, Consumer fn) {
		for (int i=0; i<us.length; i++) fn.accept(us[i]);
	}
	
	/** Optionally get the nth element, returns empty for array len<n+1 */
	public static OptionalLong nth(long[] in, int n) { 
		return okPos(in, n) ? OptionalLong.of(in[n]) : OptionalLong.empty();
	}

	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static OptionalLong nthLast(long[] in, int n) {
		return nth(in, in.length-(n+1));
	}
	
	/** Optionally get the first element, returning empty for an empty array */
	public static OptionalLong first(long[] in) { return nth(in, 0); }
	
	/** Optionally get the last element, returns empty for empty array */
	public static OptionalLong last(long[] in) { return nthLast(in, 0); }

	/** Maps an array of longs to an array of T */ 
	public static <T> T[] 
	map(long[] in, Fn<T> mapper, IntFunction<T[]> cons) {
		var out = cons.apply(in.length);
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}
	/** Maps an array of longs to an array of strings */ 
	public static String[] mapStr(long[] in, Fn<String> mapper) {
		return map(in, mapper, String[]::new);
	}
	/** Maps an array of longs to an array of strings */ 
	public static String[] mapStr(long[] in) {
		return mapStr(in, i->"" + i);
	}
	/** Maps an array of longs to an array of longs */ 
	public static long[] map(long[] in, ToLongFn mapper) {
		var out = new long[in.length];
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}

	/** Maps an array of longs to an array of ints */ 
	public static int[] mapInt(long[] in, ToIntFn mapper) {
		var out = new int[in.length];
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}

	public static long[] subArray(long[] in, int start) {
		return subArray(in, start, in.length);
	}
	public static long[] subArray(long[] in, int start, int end) {
		return copyOfRange(in, min(start, in.length), min(end, in.length));
	}

	public static long[] subArrayLen(long[] in, int start, int len) {
		return subArray(in, start, start + len);
	}

	public static long[] spliced(long[] in, int pos, int len) {
		if (in.length==0) return in;
		if (pos==0) return copyOfRange(in, len, in.length);
		else if (pos+len>=in.length) return copyOfRange(in, 0, pos);
		
		long[] out = copyOfRange(in, 0, in.length-len);
		for (int i=pos; i<out.length; i++) out[i] = in[i+len];
		return out;
	}
	
	public static Stream<String> streamAsString(long[] in) {
		return stream(in).mapToObj(i->"" + i);
	}
	
	public static String join(String glue, long[] a) {
		return join(a, glue);
	}

	public static String join(long[]in, String glue) {
		return streamAsString(in).collect(joining(glue));
	}

	public static int indexOf(long[] hay, long needle) {
		for (int i=hay.length-1; i>=0; i--) if (needle==hay[i]) return i;
		return -1;
	}

	public static int[] indexOf(long[] hay, long[] needles) {
		int[] out = Ints.EMPTY;
		for (int i=hay.length-1; i>=0; i--) {
			if (contains(needles, hay[i])) out = Ints.append(out, i);
		}
		return out;
	}
	
	public static boolean contains(long[] hay, long needle) {
		return indexOf(hay, needle) >= 0;
	}
	
	public static boolean okPos(long[] a, int i) { return i>=0 && i<a.length; }
	
	public static boolean empty(long[] in) { return in.length==0; }
	
	public static long[] remove(long[] hay, long needle) {
		if (empty(hay)) return hay;
		int pos = indexOf(hay, needle);
		return pos<0 ? hay : spliced(hay, pos, 1);
	}
	
	public static <V> long[] remove(long[] hay, V[] needles, Fn<V>map) {
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

	public static long[] remove(long[] hay, long[] needles) {
		return foldl(needles, hay, Longs::remove);
	}

	public static long[] remove(long[] old, Pred test) {
		for (int j=old.length-1; j>=0; j--) {
			if (test.test(old[j])) old = spliced(old, j, 1);
		}
		return old;
	}
	
	public static long
	foldl(long[] in, long identity, ToLongBiFn fold) {
		long out = identity;
		for (long t: in) out = fold.apply(t, out);
		return out;
	}
	
	public static <U> U 
	foldl(long[] in, U ident, BiFunction<U, Long, U> fold) {
		for (long t: in) ident = fold.apply(ident, t);
		return ident;
	}
	
	public static long sum(long[] in) {
		return foldl(in, (long)0, (i, s)->(long)(i+s));
	}

	/**Pair up elements in the array and map the pairs to a new value */
	public static <T, U> long[] pair(long[] in, ToLongBiFn pair) {
		if (in.length==0) return in;
		long[] out = new long[in.length/2];
		for (int i=1; i<in.length; i+=2) {
			out[(i-1)/2] = pair.apply(in[i-1], in[i]);
		}
		return out;
	}

	public static long[] pairLeft(long[] in, long ident, BiFn<Long> pair) {
		long[] out = new long[in.length];
		for (int i=in.length-1; i>=0; i--) {
			out[i] = pair.apply(i<1 ? ident : in[i-1], in[i]);
		}
		return out;
	}

	public static long[] filter(long[] in, Pred mapper) {
		for (int i=in.length; i>0; i--) {
			if (!mapper.test(in[i-1])) in = spliced(in, i-1, 1);
		}
		return in;
	}
	
	public static long[] reversed(long[] a) {
		if (a.length<2) return a;
		long[] out = new long[a.length];
		for (int i=0; i<out.length; i++) out[i] = a[a.length-(1+i)];
		return out;
	}

	public static long[] append(long[] a, long[] b) {
		if (a.length==0) return b; else if (b.length==0) return a;
		long[] out = Arrays.copyOf(a, a.length + b.length);
		for (int i=0; i<b.length; i++) out[a.length+i] = b[i];
		return out;
	}
	
	public static long[] append(long[] a, long b) {
		long[] out = copyOf(a, a.length + 1);
		out[a.length] = b;
		return out;
	}

	public static long[] union(long[] a, long b) {
		return (contains(a,  b)) ? a : append(a, b);
	}

	public static long[] union(long[] a, long[] b) {
		return foldl(b, a, Longs::union);
	}

	/** Breaks an array up into groups of size length and streams them. If the 
	 * array length is not a multiple of size, the remainder elements will be 
	 * discarded */
	public static <T> Stream<long[]> streamGroups(int size, long[] from) {
		return from==null ? Stream.empty() 
			//JAVA9:			iterate(0, i->i<from.length, i->i+=size)
			: IntStream.range(0, from.length/size).map(i->i*size)
				.mapToObj(i->Arrays.copyOfRange(from, i, i+size));
	}
}
