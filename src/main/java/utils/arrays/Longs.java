package utils.arrays;

import static java.lang.Math.min;
import static java.lang.System.arraycopy;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
	
import static java.util.Arrays.asList;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.sort;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.OptionalLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Longs {
	@FunctionalInterface
	public static interface UnaryOp{
		public long apply(long a);
	}

	@FunctionalInterface
	public static interface BinaryOp {
		public long apply(long a, long b);
	}

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
		return okPos(in, n) 
			? OptionalLong.of(in[n]) : OptionalLong.empty();
	}

	/** get the nth element, returns def for array len<n+1 */
	public static long nth(long[] in, int n, long def) { 
		return okPos(in, n) ? in[n] : def;
	}

	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static OptionalLong nthLast(long[] in, int n) {
		return nth(in, in.length-(n+1));
	}
	
	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static long nthLast(long[] in, int n, long def) {
		return nth(in, in.length-(n+1), def);
	}
	
	/** Optionally get the first element, returning empty for an empty array */
	public static OptionalLong first(long[] in) { return nth(in, 0); }
	
	/** Get the first element, or returning the provided default */
	public static long first(long[] in, long def) { 
		return nth(in, 0, def); 
	}
	
	/** Optionally get the last element, returns empty for empty array */
	public static OptionalLong last(long[] in) { return nthLast(in, 0); }

	/** Get the last element, or returning the provided default */
	public static long last(long[] in, long def) { 
		return nthLast(in, 0, def); 
	}

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
		arraycopy(in, pos+len, out, pos, out.length-pos);
		return out;
	}
	
	public static long[] insert(long[] in, int pos, long item) {
		long[] out = copyOfRange(in, 0, in.length+1);
		arraycopy(in, pos, out, pos+1, in.length-pos);
		out[pos] = item;
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

	
	public static long max(long[] in) {
		return foldl(in, (long)0, Math::max);
	}
	

	/**Pair up elements in the array and map the pairs to a new value.
	 * If there are an odd number of elements, the last one will be ignored
	 * */
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
		arraycopy(b, 0, out, a.length, b.length);
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
			: IntStream.iterate(0, i->i<from.length, i->i+=size)
				.mapToObj(i->copyOfRange(from, i, i+size));
	}
	
	@FunctionalInterface
	public static interface HeadTailFn<T> {
		public T apply(long head, long[] tail);
	}

	@FunctionalInterface
	public static interface HeadHeadTailFn<T> {
		public T apply(long head, long subHead, long[] tail);
	}

	/** Apply a mapping function which expects the array to be broken into
	 * a single leading long and an array of the remaining longs
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than one element */
	public static <T> Optional<T> headTailMap(long[] arr, HeadTailFn<T> map) {
		return arr.length<1 ? Optional.empty() :
			Optional.of(map.apply(arr[0], subArray(arr, 1)));
	}
	
	/** Apply a mapping function which expects the first two items of the array  
	 * with the tail dumped.
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than two elements */
	public static <T> Optional<T> headHeadMap(long[] arr, BiFn<T> map) {
		return arr.length<2 ? Optional.empty() :
			Optional.of(map.apply(arr[0], arr[1]));
	}

	/** Apply a mapping function which expects the array to be broken into
	 * two leading longs and an array of the remaining longs
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than two elements */
	public static <T> Optional<T> 
	headHeadTailMap(long[] arr, HeadHeadTailFn<T> map) {
		return arr.length<2 ? Optional.empty() :
			Optional.of(map.apply(arr[0], arr[1], subArray(arr, 2)));
	}

	public static class ObjMap<T> {
		private long[] keys = EMPTY;
		private T[] vals;
		public final IntFunction<T[]> cons;
		
		public ObjMap(IntFunction<T[]> cons) {
			this.cons = cons;
			vals = cons.apply(0);
		}

		public T put(long key, T val) {
			var pos = binarySearch(keys, key);
			if (pos>=0) {
				var out = vals[pos];
				vals[pos] = val;
				return out;
			} else {
				pos = -(pos+1);
				keys = insert(keys, pos, key);
				vals = Objs.insert(vals, pos, val);
				return null;
			}
		}
		
		public T remove(long key) {
			var pos = binarySearch(keys, key);
			if (pos<0) return null;
			var out = vals[pos];
			keys = spliced(keys, pos, 1);
			vals = Objs.spliced(vals, pos, 1);
			return out;
		}
		
		public T get(long key) {
			return getOrDefault(key, null);
		}
		
		public T getOrDefault(long key, T def) {
			var pos = binarySearch(keys, key);
			return pos>=0 ? vals[pos] : def;
		}
		
		public boolean containsKey(long key) {
			return binarySearch(keys, key)>=0;
		}
		
		public T computeIfAbsent(long key, Fn<T> gen) {
			var pos = binarySearch(keys, key);
			if (pos>=0) return vals[pos];
			var val = gen.apply(key);
			pos = -(pos+1);
			keys = insert(keys, pos, key);
			vals = Objs.insert(vals, pos, val);
			return val;
		}
		
		public void clear() {
			keys = EMPTY;
			vals = cons.apply(0);
		}
		
		public long[] keys() {
			return keys.clone();
		}
		
		public LongStream streamKeys() {
			return stream(keys);
		}
		
		public T[] vals() {
			return vals.clone();
		}

		public Stream<T> streamVals() {
			return Arrays.stream(vals);
		}

		public static class Entry<T> {
			public final long key;
			public final T val;

			public Entry(long key, T val) {
				this.key = key;
				this.val = val;
			}
		}
		
		public Stream<Entry<T>> streamEntries() {
			return IntStream.range(0, keys.length)
				.mapToObj(i->new Entry<>(keys[i], vals[i]));
		}

		public Iterator<T> iterVals() {
			return asList(vals).iterator();
		}
		
		public void forEach(BiConsumer<Long, T> action) {
			for (int i=0; i<keys.length; i++) action.accept(keys[i], vals[i]);
		}
	}

	public static class Sorted {
		public static long[] with(long[] vals, long val) {
			var pos = binarySearch(vals, val);
			return pos>=0 ? vals : insert(vals, -(pos+1), val);
		}

		public static long[] without(long[] vals, long val) {
			var pos = binarySearch(vals, val);
			return pos<0 ? vals : spliced(vals, pos, 1);
		}

		public static boolean contains(long[] vals, long val) {
			return binarySearch(vals, val)>=0;
		}
	}

	public static long[] sorted(long[] in) {
		var out = copyOf(in, in.length);
		sort(out);
		return out;
	}
}
