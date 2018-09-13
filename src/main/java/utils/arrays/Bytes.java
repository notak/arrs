package utils.arrays;

import static java.lang.Math.min;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.stream.Collectors.joining;

import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Bytes {
	@FunctionalInterface
	public static interface UnaryOp{
		public byte apply(byte a);
	}

	@FunctionalInterface
	public static interface BinaryOp {
		public byte apply(byte a, byte b);
	}

	@FunctionalInterface
	public static interface Consumer {
		void accept(byte val);
	}
	
	@FunctionalInterface
	public static interface Pred {
		boolean test(byte val);
	}
	
	@FunctionalInterface
	public static interface BiPred {
		boolean test(byte val, byte val2);
	}
	
	@FunctionalInterface
	public static interface Fn<S> {
		S apply(byte val);
	}
	
	@FunctionalInterface
	public static interface BiFn<S> {
		S apply(byte val, byte val2);
	}
	
	@FunctionalInterface
	public static interface ToIntFn {
		int apply(byte val);
	}
	
	@FunctionalInterface
	public static interface ToByteFn {
		byte apply(byte val);
	}
	
	@FunctionalInterface
	public static interface ToByteBiFn {
		byte apply(byte val, byte val2);
	}
	
	public static final byte[] EMPTY = {};

	@SafeVarargs
	public static <T> byte[] toArray(byte... in) { return in; }

	public static <T> byte[] toArray(byte in) { 
		byte[] out = { in }; return out;
	}

	/** Stream as an IntStream using signed conversion */
	public static IntStream stream(byte[] us) {
		return IntStream.range(0, us.length).map(i->us[i]);
	}

	/** Stream as an IntStream using unsigned conversion  */
	public static IntStream unsignedStream(byte[] us) {
		return IntStream.range(0, us.length).map(i -> Byte.toUnsignedInt(us[i]));
	}
	
	public static void forEach(byte[] us, Consumer fn) {
		for (int i=0; i<us.length; i++) fn.accept(us[i]);
	}
	
	/** Optionally get the nth element, returns empty for array len<n+1 */
	public static Optional<Byte> nth(byte[] in, int n) { 
		return okPos(in, n) ? Optional.of(in[n]) : Optional.empty();
	}

	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static Optional<Byte> nthLast(byte[] in, int n) {
		return nth(in, in.length-(n+1));
	}
	
	/** Optionally get the first element, returning empty for an empty array */
	public static Optional<Byte> first(byte[] in) { return nth(in, 0); }
	
	/** Optionally get the last element, returns empty for empty array */
	public static Optional<Byte> last(byte[] in) { return nthLast(in, 0); }

	/** Maps an array of bytes to an array of T */ 
	public static <T> T[] 
	map(byte[] in, Fn<T> mapper, IntFunction<T[]> cons) {
		var out = cons.apply(in.length);
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}
	/** Maps an array of bytes to an array of strings */ 
	public static String[] mapStr(byte[] in, Fn<String> mapper) {
		return map(in, mapper, String[]::new);
	}
	/** Maps an array of bytes to an array of strings */ 
	public static String[] mapStr(byte[] in) {
		return mapStr(in, i->"" + i);
	}
	/** Maps an array of bytes to an array of bytes */ 
	public static byte[] map(byte[] in, ToByteFn mapper) {
		var out = new byte[in.length];
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}

	/** Maps an array of longs to an array of ints */ 
	public static int[] mapInt(byte[] in, ToIntFn mapper) {
		var out = new int[in.length];
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}

	public static byte[] subArray(byte[] in, int start) {
		return subArray(in, start, in.length);
	}
	public static byte[] subArray(byte[] in, int start, int end) {
		return copyOfRange(in, min(start, in.length), min(end, in.length));
	}

	public static byte[] subArrayLen(byte[] in, int start, int len) {
		return subArray(in, start, start + len);
	}

	public static byte[] spliced(byte[] in, int pos, int len) {
		if (in.length==0) return in;
		if (pos==0) return copyOfRange(in, len, in.length);
		else if (pos+len>=in.length) return copyOfRange(in, 0, pos);
		
		byte[] out = copyOfRange(in, 0, in.length-len);
		for (int i=pos; i<out.length; i++) out[i] = in[i+len];
		return out;
	}
	
	public static Stream<String> streamAsString(byte[] in) {
		return stream(in).mapToObj(i->"" + i);
	}
	
	public static String join(String glue, byte[] a) {
		return join(a, glue);
	}

	public static String join(byte[]in, String glue) {
		return streamAsString(in).collect(joining(glue));
	}

	public static int indexOf(byte[] hay, byte needle) {
		for (int i=hay.length-1; i>=0; i--) if (needle==hay[i]) return i;
		return -1;
	}

	public static int[] indexOf(byte[] hay, byte[] needles) {
		int[] out = Ints.EMPTY;
		for (int i=hay.length-1; i>=0; i--) {
			if (contains(needles, hay[i])) out = Ints.append(out, i);
		}
		return out;
	}
	
	public static boolean contains(byte[] hay, byte needle) {
		return indexOf(hay, needle) >= 0;
	}
	
	public static boolean okPos(byte[] a, int i) { return i>=0 && i<a.length; }
	
	public static boolean empty(byte[] in) { return in.length==0; }
	
	public static byte[] remove(byte[] hay, byte needle) {
		if (empty(hay)) return hay;
		int pos = indexOf(hay, needle);
		return pos<0 ? hay : spliced(hay, pos, 1);
	}
	
	public static <V> byte[] remove(byte[] hay, V[] needles, Fn<V>map) {
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

	public static byte[] remove(byte[] hay, byte[] needles) {
		return foldl(needles, hay, Bytes::remove);
	}

	public static byte[] remove(byte[] old, Pred test) {
		for (int j=old.length-1; j>=0; j--) {
			if (test.test(old[j])) old = spliced(old, j, 1);
		}
		return old;
	}
	
	public static byte
	foldl(byte[] in, byte identity, ToByteBiFn fold) {
		byte out = identity;
		for (byte t: in) out = fold.apply(t, out);
		return out;
	}
	
	public static <U> U 
	foldl(byte[] in, U ident, BiFunction<U, Byte, U> fold) {
		for (byte t: in) ident = fold.apply(ident, t);
		return ident;
	}
	
	public static byte sum(byte[] in) {
		return foldl(in, (byte)0, (i, s)->(byte)(i+s));
	}

	/**Pair up elements in the array and map the pairs to a new value */
	public static <T, U> byte[] pair(byte[] in, ToByteBiFn pair) {
		if (in.length==0) return in;
		byte[] out = new byte[in.length/2];
		for (int i=1; i<in.length; i+=2) {
			out[(i-1)/2] = pair.apply(in[i-1], in[i]);
		}
		return out;
	}

	public static byte[] pairLeft(byte[] in, byte ident, BiFn<Byte> pair) {
		byte[] out = new byte[in.length];
		for (int i=in.length-1; i>=0; i--) {
			out[i] = pair.apply(i<1 ? ident : in[i-1], in[i]);
		}
		return out;
	}

	public static byte[] filter(byte[] in, Pred mapper) {
		for (int i=in.length; i>0; i--) {
			if (!mapper.test(in[i-1])) in = spliced(in, i-1, 1);
		}
		return in;
	}
	
	public static byte[] reversed(byte[] a) {
		if (a.length<2) return a;
		byte[] out = new byte[a.length];
		for (int i=0; i<out.length; i++) out[i] = a[a.length-(1+i)];
		return out;
	}

	public static byte[] append(byte[] a, byte[] b) {
		if (a.length==0) return b; else if (b.length==0) return a;
		byte[] out = Arrays.copyOf(a, a.length + b.length);
		for (int i=0; i<b.length; i++) out[a.length+i] = b[i];
		return out;
	}
	
	public static byte[] append(byte[] a, byte b) {
		byte[] out = copyOf(a, a.length + 1);
		out[a.length] = b;
		return out;
	}

	public static byte[] union(byte[] a, byte b) {
		return (contains(a,  b)) ? a : append(a, b);
	}

	public static byte[] union(byte[] a, byte[] b) {
		return foldl(b, a, Bytes::union);
	}

	/** Breaks an array up into groups of size length and streams them. If the 
	 * array length is not a multiple of size, the remainder elements will be 
	 * discarded */
	public static <T> Stream<byte[]> streamGroups(int size, byte[] from) {
		return from==null ? Stream.empty() 
			//JAVA9:			iterate(0, i->i<from.length, i->i+=size)
			: IntStream.range(0, from.length/size).map(i->i*size)
				.mapToObj(i->Arrays.copyOfRange(from, i, i+size));
	}
}
