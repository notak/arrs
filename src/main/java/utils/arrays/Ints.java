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

import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Ints {
	@FunctionalInterface
	public static interface UnaryOp{
		public int apply(int a);
	}

	@FunctionalInterface
	public static interface BinaryOp {
		public int apply(int a, int b);
	}

	@FunctionalInterface
	public static interface Consumer {
		void accept(int val);
	}
	
	@FunctionalInterface
	public static interface Pred {
		boolean test(int val);
	}
	
	@FunctionalInterface
	public static interface BiPred {
		boolean test(int val, int val2);
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
		return okPos(in, n) 
			? OptionalInt.of(in[n]) : OptionalInt.empty();
	}

	/** get the nth element, returns def for array len<n+1 */
	public static int nth(int[] in, int n, int def) { 
		return okPos(in, n) ? in[n] : def;
	}

	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static OptionalInt nthLast(int[] in, int n) {
		return nth(in, in.length-(n+1));
	}
	
	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static int nthLast(int[] in, int n, int def) {
		return nth(in, in.length-(n+1), def);
	}
	
	/** Optionally get the first element, returning empty for an empty array */
	public static OptionalInt first(int[] in) { return nth(in, 0); }
	
	/** Get the first element, or returning the provided default */
	public static int first(int[] in, int def) { 
		return nth(in, 0, def); 
	}
	
	/** Optionally get the last element, returns empty for empty array */
	public static OptionalInt last(int[] in) { return nthLast(in, 0); }

	/** Get the last element, or returning the provided default */
	public static int last(int[] in, int def) { 
		return nthLast(in, 0, def); 
	}

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
	public static int[] mapInt(int[] in, ToIntFn mapper) {
		var out = new int[in.length];
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
		arraycopy(in, pos+len, out, pos, out.length-pos);
		return out;
	}
	
	public static int[] insert(int[] in, int pos, int item) {
		int[] out = copyOfRange(in, 0, in.length+1);
		arraycopy(in, pos, out, pos+1, in.length-pos);
		out[pos] = item;
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

	
	public static int max(int[] in) {
		return foldl(in, (int)0, Math::max);
	}
	

	/**Pair up elements in the array and map the pairs to a new value.
	 * If there are an odd number of elements, the last one will be ignored
	 * */
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
		arraycopy(b, 0, out, a.length, b.length);
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

	/** Breaks an array up into groups of size length and streams them. If the 
	 * array length is not a multiple of size, the remainder elements will be 
	 * discarded */
	public static <T> Stream<int[]> streamGroups(int size, int[] from) {
		return from==null ? Stream.empty() 
			: IntStream.iterate(0, i->i<from.length, i->i+=size)
				.mapToObj(i->copyOfRange(from, i, i+size));
	}
	
	@FunctionalInterface
	public static interface HeadTailFn<T> {
		public T apply(int head, int[] tail);
	}

	@FunctionalInterface
	public static interface HeadHeadTailFn<T> {
		public T apply(int head, int subHead, int[] tail);
	}

	/** Apply a mapping function which expects the array to be broken into
	 * a single leading int and an array of the remaining ints
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than one element */
	public static <T> Optional<T> headTailMap(int[] arr, HeadTailFn<T> map) {
		return arr.length<1 ? Optional.empty() :
			Optional.of(map.apply(arr[0], subArray(arr, 1)));
	}
	
	/** Apply a mapping function which expects the first two items of the array  
	 * with the tail dumped.
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than two elements */
	public static <T> Optional<T> headHeadMap(int[] arr, BiFn<T> map) {
		return arr.length<2 ? Optional.empty() :
			Optional.of(map.apply(arr[0], arr[1]));
	}

	/** Apply a mapping function which expects the first two items of the array  
	 * with the tail dumped.
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than two elements */
	public static <T> Optional<T> tailTailMap(int[] arr, BiFn<T> map) {
		return arr.length<2 ? Optional.empty() :
			Optional.of(map.apply(arr[arr.length-1], arr[arr.length-2]));
	}

	/** Apply a mapping function which expects the array to be broken into
	 * two leading ints and an array of the remaining ints
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than two elements */
	public static <T> Optional<T> 
	headHeadTailMap(int[] arr, HeadHeadTailFn<T> map) {
		return arr.length<2 ? Optional.empty() :
			Optional.of(map.apply(arr[0], arr[1], subArray(arr, 2)));
	}

	public static class ObjMap<T> {
		private int[] keys = EMPTY;
		private T[] vals;
		public final IntFunction<T[]> cons;
		
		public ObjMap(IntFunction<T[]> cons) {
			this.cons = cons;
			vals = cons.apply(0);
		}

		public T put(int key, T val) {
			var pos = binarySearch(keys, key);
			if (pos>=0) {
				var out = vals[pos];
				vals[pos] = val;
				return out;
			} else {
				insertAt(pos, key, val);
				return null;
			}
		}
		
		public T remove(int key) {
			var pos = binarySearch(keys, key);
			if (pos<0) return null;
			var out = vals[pos];
			keys = spliced(keys, pos, 1);
			vals = Objs.spliced(vals, pos, 1);
			return out;
		}
		
		public T get(int key) {
			return getOrDefault(key, null);
		}
		
		public T getOrDefault(int key, T def) {
			var pos = binarySearch(keys, key);
			return pos>=0 ? vals[pos] : def;
		}
		
		public boolean containsKey(int key) {
			return binarySearch(keys, key)>=0;
		}
		
		public T computeIfAbsent(int key, Fn<T> gen) {
			var pos = binarySearch(keys, key);
			if (pos>=0) return vals[pos];
			else return insertAt(pos, key, gen.apply(key));
		}
		
		private T insertAt(int pos, int key, T val) {
			pos = -(pos+1);
			keys = insert(keys, pos, key);
			vals = Objs.insert(vals, pos, val);
			return val;
		}
		
		public T compute(int key, BiFunction<Integer, T, T> gen) {
			var pos = binarySearch(keys, key);
			if (pos>=0) return vals[pos] = gen.apply(key, vals[pos]);
			else return insertAt(pos, key, gen.apply(key, null));
		}
		
		public void clear() {
			keys = EMPTY;
			vals = cons.apply(0);
		}
		
		public int[] keys() {
			return keys.clone();
		}
		
		public IntStream streamKeys() {
			return stream(keys);
		}
		
		public T[] vals() {
			return vals.clone();
		}

		public Stream<T> streamVals() {
			return Arrays.stream(vals);
		}

		public static class Entry<T> {
			public final int key;
			public final T val;

			public Entry(int key, T val) {
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
		
		public void forEach(BiConsumer<Integer, T> action) {
			for (int i=0; i<keys.length; i++) action.accept(keys[i], vals[i]);
		}
	}

	public static class Sorted {
		public static int[] with(int[] vals, int val) {
			var pos = binarySearch(vals, val);
			return pos>=0 ? vals : insert(vals, -(pos+1), val);
		}

		public static int[] without(int[] vals, int val) {
			var pos = binarySearch(vals, val);
			return pos<0 ? vals : spliced(vals, pos, 1);
		}

		public static boolean contains(int[] vals, int val) {
			return binarySearch(vals, val)>=0;
		}
	}

	public static int[] sorted(int[] in) {
		var out = copyOf(in, in.length);
		sort(out);
		return out;
	}
}
