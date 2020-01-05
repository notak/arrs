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
		return okPos(in, n) 
			? Optional/*Byte*/.of(in[n]) : Optional/*Byte*/.empty();
	}

	/** get the nth element, returns def for array len<n+1 */
	public static byte nth(byte[] in, int n, byte def) { 
		return okPos(in, n) ? in[n] : def;
	}

	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static Optional<Byte> nthLast(byte[] in, int n) {
		return nth(in, in.length-(n+1));
	}
	
	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static byte nthLast(byte[] in, int n, byte def) {
		return nth(in, in.length-(n+1), def);
	}
	
	/** Optionally get the first element, returning empty for an empty array */
	public static Optional<Byte> first(byte[] in) { return nth(in, 0); }
	
	/** Get the first element, or returning the provided default */
	public static byte first(byte[] in, byte def) { 
		return nth(in, 0, def); 
	}
	
	/** Get the first element, or returning the provided default */
	public static byte firstOr0(byte[] in) { 
		return nth(in, 0, (byte)0); 
	}
	
	/** Optionally get the last element, returns empty for empty array */
	public static Optional<Byte> last(byte[] in) { return nthLast(in, 0); }

	/** Get the last element, or returning the provided default */
	public static byte last(byte[] in, byte def) { 
		return nthLast(in, 0, def); 
	}

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
		arraycopy(in, pos+len, out, pos, out.length-pos);
		return out;
	}
	
	public static byte[] insert(byte[] in, int pos, byte item) {
		byte[] out = copyOfRange(in, 0, in.length+1);
		arraycopy(in, pos, out, pos+1, in.length-pos);
		out[pos] = item;
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

	/*BIGONLY
	public static byte max(byte[] in) {
		return foldl(in, (byte)0, Math::max);
	}
	/BIGONLY*/

	/**Pair up elements in the array and map the pairs to a new value.
	 * If there are an odd number of elements, the last one will be ignored
	 * */
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

	public static byte[] append(byte[]... a) {
		if (a.length==0) return EMPTY; 
		else if (a.length==1) return a[0];
		var sum = 0;
		for (int i=0; i<a.length; i++) sum+=a[i].length;
		byte[] out = new byte[sum];
		for (int i=0, pos = 0; i<a.length; pos+=a[i++].length) {
			arraycopy(a[i], 0, out, pos, a[i].length);
		}
		return out;
	}

	public static byte[] append(byte[] a, byte[] b) {
		if (a.length==0) return b; else if (b.length==0) return a;
		byte[] out = Arrays.copyOf(a, a.length + b.length);
		arraycopy(b, 0, out, a.length, b.length);
		return out;
	}
	
	public static byte[] append(byte[] a, byte b) {
		byte[] out = copyOf(a, a.length + 1);
		out[a.length] = b;
		return out;
	}

	public static byte fromBool(boolean set) {
		return set ? (byte)1 : (byte)0;
	}

	public static boolean toBool(byte b) {
		return b!=0;
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
			: IntStream.iterate(0, i->i<from.length, i->i+=size)
				.mapToObj(i->copyOfRange(from, i, i+size));
	}
	
	@FunctionalInterface
	public static interface HeadTailFn<T> {
		public T apply(byte head, byte[] tail);
	}

	@FunctionalInterface
	public static interface HeadHeadTailFn<T> {
		public T apply(byte head, byte subHead, byte[] tail);
	}

	/** Apply a mapping function which expects the array to be broken into
	 * a single leading byte and an array of the remaining bytes
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than one element */
	public static <T> Optional<T> headTailMap(byte[] arr, HeadTailFn<T> map) {
		return arr.length<1 ? Optional.empty() :
			Optional.of(map.apply(arr[0], subArray(arr, 1)));
	}
	
	/** Apply a mapping function which expects the first two items of the array  
	 * with the tail dumped.
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than two elements */
	public static <T> Optional<T> headHeadMap(byte[] arr, BiFn<T> map) {
		return arr.length<2 ? Optional.empty() :
			Optional.of(map.apply(arr[0], arr[1]));
	}

	/** Apply a mapping function which expects the array to be broken into
	 * two leading bytes and an array of the remaining bytes
	 * @return an optional containing the result of the mapping, or empty if 
	 * 		the array contains less than two elements */
	public static <T> Optional<T> 
	headHeadTailMap(byte[] arr, HeadHeadTailFn<T> map) {
		return arr.length<2 ? Optional.empty() :
			Optional.of(map.apply(arr[0], arr[1], subArray(arr, 2)));
	}

	public static class ObjMap<T> {
		private byte[] keys = EMPTY;
		private T[] vals;
		public final IntFunction<T[]> cons;
		
		public ObjMap(IntFunction<T[]> cons) {
			this.cons = cons;
			vals = cons.apply(0);
		}

		public T put(byte key, T val) {
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
		
		public T remove(byte key) {
			var pos = binarySearch(keys, key);
			if (pos<0) return null;
			var out = vals[pos];
			keys = spliced(keys, pos, 1);
			vals = Objs.spliced(vals, pos, 1);
			return out;
		}
		
		public T get(byte key) {
			return getOrDefault(key, null);
		}
		
		public T getOrDefault(byte key, T def) {
			var pos = binarySearch(keys, key);
			return pos>=0 ? vals[pos] : def;
		}
		
		public boolean containsKey(byte key) {
			return binarySearch(keys, key)>=0;
		}
		
		public T computeIfAbsent(byte key, Fn<T> gen) {
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
		
		public byte[] keys() {
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
		
		public void forEach(BiConsumer<Byte, T> action) {
			for (int i=0; i<keys.length; i++) action.accept(keys[i], vals[i]);
		}
	}

	public static class Sorted {
		public static byte[] with(byte[] vals, byte val) {
			var pos = binarySearch(vals, val);
			return pos>=0 ? vals : insert(vals, -(pos+1), val);
		}

		public static byte[] without(byte[] vals, byte val) {
			var pos = binarySearch(vals, val);
			return pos<0 ? vals : spliced(vals, pos, 1);
		}

		public static boolean contains(byte[] vals, byte val) {
			return binarySearch(vals, val)>=0;
		}
	}

	public static byte[] sorted(byte[] in) {
		var out = copyOf(in, in.length);
		sort(out);
		return out;
	}
}
