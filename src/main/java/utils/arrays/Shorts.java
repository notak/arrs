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
/*BIGONLYimport static java.util.Arrays.stream;/BIGONLY*/
import static java.util.stream.Collectors.joining;

/*BIGONLYimport java.util.OptionalLong;/BIGONLY*/
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
/*BIGONLYimport java.util.stream.LongStream;/BIGONLY*/
import java.util.stream.Stream;

public class Shorts {
	@FunctionalInterface
	public static interface UnaryOp{
		public short apply(short a);
	}

	@FunctionalInterface
	public static interface BinaryOp {
		public short apply(short a, short b);
	}

	@FunctionalInterface
	public static interface Consumer {
		void accept(short val);
	}
	
	@FunctionalInterface
	public static interface Pred {
		boolean test(short val);
	}
	
	@FunctionalInterface
	public static interface BiPred {
		boolean test(short val, short val2);
	}
	
	@FunctionalInterface
	public static interface Fn<S> {
		S apply(short val);
	}
	
	@FunctionalInterface
	public static interface BiFn<S> {
		S apply(short val, short val2);
	}
	
	@FunctionalInterface
	public static interface ToIntFn {
		int apply(short val);
	}
	
	@FunctionalInterface
	public static interface ToShortFn {
		short apply(short val);
	}
	
	@FunctionalInterface
	public static interface ToShortBiFn {
		short apply(short val, short val2);
	}
	
	public static final short[] EMPTY = {};

	@SafeVarargs
	public static <T> short[] toArray(short... in) { return in; }

	public static <T> short[] toArray(short in) { 
		short[] out = { in }; return out;
	}

	/** Stream as an IntStream using signed conversion */
	public static IntStream stream(short[] us) {
		return IntStream.range(0, us.length).map(i->us[i]);
	}

	/** Stream as an IntStream using unsigned conversion  */
	public static IntStream unsignedStream(short[] us) {
		return IntStream.range(0, us.length).map(i -> Short.toUnsignedInt(us[i]));
	}
	
	public static void forEach(short[] us, Consumer fn) {
		for (int i=0; i<us.length; i++) fn.accept(us[i]);
	}
	
	/** Optionally get the nth element, returns empty for array len<n+1 */
	public static Optional<Short> nth(short[] in, int n) { 
		return okPos(in, n) 
			? Optional/*Short*/.of(in[n]) : Optional/*Short*/.empty();
	}

	/** get the nth element, returns def for array len<n+1 */
	public static short nth(short[] in, int n, short def) { 
		return okPos(in, n) ? in[n] : def;
	}

	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static Optional<Short> nthLast(short[] in, int n) {
		return nth(in, in.length-(n+1));
	}
	
	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static short nthLast(short[] in, int n, short def) {
		return nth(in, in.length-(n+1), def);
	}
	
	/** Optionally get the first element, returning empty for an empty array */
	public static Optional<Short> first(short[] in) { return nth(in, 0); }
	
	/** Get the first element, or returning the provided default */
	public static short first(short[] in, short def) { 
		return nth(in, 0, def); 
	}
	
	/** Optionally get the last element, returns empty for empty array */
	public static Optional<Short> last(short[] in) { return nthLast(in, 0); }

	/** Get the last element, or returning the provided default */
	public static short last(short[] in, short def) { 
		return nthLast(in, 0, def); 
	}

	/** Maps an array of shorts to an array of T */ 
	public static <T> T[] 
	map(short[] in, Fn<T> mapper, IntFunction<T[]> cons) {
		var out = cons.apply(in.length);
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}
	/** Maps an array of shorts to an array of strings */ 
	public static String[] mapStr(short[] in, Fn<String> mapper) {
		return map(in, mapper, String[]::new);
	}
	/** Maps an array of shorts to an array of strings */ 
	public static String[] mapStr(short[] in) {
		return mapStr(in, i->"" + i);
	}
	/** Maps an array of shorts to an array of shorts */ 
	public static short[] map(short[] in, ToShortFn mapper) {
		var out = new short[in.length];
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}

	/** Maps an array of longs to an array of ints */ 
	public static int[] mapInt(short[] in, ToIntFn mapper) {
		var out = new int[in.length];
		for (int i=0; i<in.length; i++) out[i]=mapper.apply(in[i]);
		return out;
	}

	public static short[] subArray(short[] in, int start) {
		return subArray(in, start, in.length);
	}
	public static short[] subArray(short[] in, int start, int end) {
		return copyOfRange(in, min(start, in.length), min(end, in.length));
	}

	public static short[] subArrayLen(short[] in, int start, int len) {
		return subArray(in, start, start + len);
	}

	public static short[] spliced(short[] in, int pos, int len) {
		if (in.length==0) return in;
		if (pos==0) return copyOfRange(in, len, in.length);
		else if (pos+len>=in.length) return copyOfRange(in, 0, pos);
		
		short[] out = copyOfRange(in, 0, in.length-len);
		arraycopy(in, pos+len, out, pos, out.length-pos);
		return out;
	}
	
	public static short[] insert(short[] in, int pos, short item) {
		short[] out = copyOfRange(in, 0, in.length+1);
		arraycopy(in, pos, out, pos+1, in.length-pos);
		out[pos] = item;
		return out;
	}
	
	public static Stream<String> streamAsString(short[] in) {
		return stream(in).mapToObj(i->"" + i);
	}
	
	public static String join(String glue, short[] a) {
		return join(a, glue);
	}

	public static String join(short[]in, String glue) {
		return streamAsString(in).collect(joining(glue));
	}

	public static int indexOf(short[] hay, short needle) {
		for (int i=hay.length-1; i>=0; i--) if (needle==hay[i]) return i;
		return -1;
	}

	public static int[] indexOf(short[] hay, short[] needles) {
		int[] out = Ints.EMPTY;
		for (int i=hay.length-1; i>=0; i--) {
			if (contains(needles, hay[i])) out = Ints.append(out, i);
		}
		return out;
	}
	
	public static boolean contains(short[] hay, short needle) {
		return indexOf(hay, needle) >= 0;
	}
	
	public static boolean okPos(short[] a, int i) { return i>=0 && i<a.length; }
	
	public static boolean empty(short[] in) { return in.length==0; }
	
	public static short[] remove(short[] hay, short needle) {
		if (empty(hay)) return hay;
		int pos = indexOf(hay, needle);
		return pos<0 ? hay : spliced(hay, pos, 1);
	}
	
	public static <V> short[] remove(short[] hay, V[] needles, Fn<V>map) {
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

	public static short[] remove(short[] hay, short[] needles) {
		return foldl(needles, hay, Shorts::remove);
	}

	public static short[] remove(short[] old, Pred test) {
		for (int j=old.length-1; j>=0; j--) {
			if (test.test(old[j])) old = spliced(old, j, 1);
		}
		return old;
	}
	
	public static short
	foldl(short[] in, short identity, ToShortBiFn fold) {
		short out = identity;
		for (short t: in) out = fold.apply(t, out);
		return out;
	}
	
	public static <U> U 
	foldl(short[] in, U ident, BiFunction<U, Short, U> fold) {
		for (short t: in) ident = fold.apply(ident, t);
		return ident;
	}
	
	public static short sum(short[] in) {
		return foldl(in, (short)0, (i, s)->(short)(i+s));
	}

	/**BIGONLY
	public static int max(short[] in) {
		return foldl(in, (short)0, Math::max);
	}
	/BIGONLY*/

	/**Pair up elements in the array and map the pairs to a new value.
	 * If there are an odd number of elements, the last one will be ignored
	 * */
	public static <T, U> short[] pair(short[] in, ToShortBiFn pair) {
		if (in.length==0) return in;
		short[] out = new short[in.length/2];
		for (int i=1; i<in.length; i+=2) {
			out[(i-1)/2] = pair.apply(in[i-1], in[i]);
		}
		return out;
	}

	public static short[] pairLeft(short[] in, short ident, BiFn<Short> pair) {
		short[] out = new short[in.length];
		for (int i=in.length-1; i>=0; i--) {
			out[i] = pair.apply(i<1 ? ident : in[i-1], in[i]);
		}
		return out;
	}

	public static short[] filter(short[] in, Pred mapper) {
		for (int i=in.length; i>0; i--) {
			if (!mapper.test(in[i-1])) in = spliced(in, i-1, 1);
		}
		return in;
	}
	
	public static short[] reversed(short[] a) {
		if (a.length<2) return a;
		short[] out = new short[a.length];
		for (int i=0; i<out.length; i++) out[i] = a[a.length-(1+i)];
		return out;
	}

	public static short[] append(short[] a, short[] b) {
		if (a.length==0) return b; else if (b.length==0) return a;
		short[] out = Arrays.copyOf(a, a.length + b.length);
		arraycopy(b, 0, out, a.length, b.length);
		return out;
	}
	
	public static short[] append(short[] a, short b) {
		short[] out = copyOf(a, a.length + 1);
		out[a.length] = b;
		return out;
	}

	public static short[] union(short[] a, short b) {
		return (contains(a,  b)) ? a : append(a, b);
	}

	public static short[] union(short[] a, short[] b) {
		return foldl(b, a, Shorts::union);
	}

	/** Breaks an array up into groups of size length and streams them. If the 
	 * array length is not a multiple of size, the remainder elements will be 
	 * discarded */
	public static <T> Stream<short[]> streamGroups(int size, short[] from) {
		return from==null ? Stream.empty() 
			: IntStream.iterate(0, i->i<from.length, i->i+=size)
				.mapToObj(i->copyOfRange(from, i, i+size));
	}
	
	@FunctionalInterface
	public static interface HeadTailFn<T> {
		public T apply(short head, short[] tail);
	}

	@FunctionalInterface
	public static interface HeadHeadTailFn<T> {
		public T apply(short head, short subHead, short[] tail);
	}

	/** Apply a mapping function which expects the array to be broken into
	 * a single leading short and an array of the remaining shorts
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than one element */
	public static <T> Optional<T> headTailMap(short[] arr, HeadTailFn<T> map) {
		return arr.length<1 ? Optional.empty() :
			Optional.of(map.apply(arr[0], subArray(arr, 1)));
	}
	
	/** Apply a mapping function which expects the first two items of the array  
	 * with the tail dumped.
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than two elements */
	public static <T> Optional<T> headHeadMap(short[] arr, BiFn<T> map) {
		return arr.length<2 ? Optional.empty() :
			Optional.of(map.apply(arr[0], arr[1]));
	}

	/** Apply a mapping function which expects the array to be broken into
	 * two leading shorts and an array of the remaining shorts
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than two elements */
	public static <T> Optional<T> 
	headHeadTailMap(short[] arr, HeadHeadTailFn<T> map) {
		return arr.length<2 ? Optional.empty() :
			Optional.of(map.apply(arr[0], arr[1], subArray(arr, 2)));
	}

	public static class ObjMap<T> {
		private short[] keys = EMPTY;
		private T[] vals;
		public final IntFunction<T[]> cons;
		
		public ObjMap(IntFunction<T[]> cons) {
			this.cons = cons;
			vals = cons.apply(0);
		}

		public T put(short key, T val) {
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
		
		public T remove(short key) {
			var pos = binarySearch(keys, key);
			if (pos<0) return null;
			var out = vals[pos];
			keys = spliced(keys, pos, 1);
			vals = Objs.spliced(vals, pos, 1);
			return out;
		}
		
		public T get(short key) {
			return getOrDefault(key, null);
		}
		
		public T getOrDefault(short key, T def) {
			var pos = binarySearch(keys, key);
			return pos>=0 ? vals[pos] : def;
		}
		
		public boolean containsKey(short key) {
			return binarySearch(keys, key)>=0;
		}
		
		public T computeIfAbsent(short key, Fn<T> gen) {
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
		
		public short[] keys() {
			return keys.clone();
		}
		/*BIGONLY
		public LongStream streamKeys() {
			return stream(keys);
		}
		/BIGONLY*/
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
		
		public void forEach(BiConsumer<Short, T> action) {
			for (int i=0; i<keys.length; i++) action.accept(keys[i], vals[i]);
		}
	}

	public static class Sorted {
		public static short[] with(short[] vals, short val) {
			var pos = binarySearch(vals, val);
			return pos>=0 ? vals : insert(vals, -(pos+1), val);
		}

		public static short[] without(short[] vals, short val) {
			var pos = binarySearch(vals, val);
			return pos<0 ? vals : spliced(vals, pos, 1);
		}

		public static boolean contains(short[] vals, short val) {
			return binarySearch(vals, val)>=0;
		}
	}

	public static short[] sorted(short[] in) {
		var out = copyOf(in, in.length);
		sort(out);
		return out;
	}
}
