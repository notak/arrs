package arrays;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

public class Arrs {
//	@SafeVarargs
//	public static <T> T[] toArray(T... in) { return Objs.toArray(in); }
//	public static <T> int[] toArray(int... in) { return in; }
//	public static <T> int[] toArray(int in) { return Ints.toArray(in); }
//
//	public static <U> Stream<U> stream(U[] us) { return Objs.stream(us); }
//	
//	public static <U> void forEach(U[] us, Consumer<U> fn) {
//		Objs.forEach(us, fn);
//	}
//	
//	/** Optionally get the nth element, returns empty for array len<n+1 */
//	public static <T> Optional<T> nth(T[] in, int n) { return Objs.nth(in, n); }
//	/** Optionally get the nth element from the end (empty if array len<n+1) */
//	public static <T> Optional<T> nthLast(T[] in, int n) {
//		return Objs.nthLast(in, n);
//	}
//	/** Optionally get the first element, returning empty for an empty array */
//	public static <T> Optional<T> first(T[] in) { return Objs.first(in); }
//	/** Optionally get the last element, returns empty for empty array */
//	public static <T> Optional<T> last(T[] in) { return Objs.last(in); }
//	
//	/** Maps an array of objects to an array of another obj */ 
//	public static <T, U> U[] 
//	map(T[] in, Function<T, U> map, IntFunction<U[]> cons) {
//		return Objs.map(in, map, cons);
//	}
//	/** Maps an array of objects to an array of strings */ 
//	public static <T> String[] mapStr(T[] in, Function<T, String> mapper) {
//		return Objs.mapStr(in, mapper);
//	}
//	
//	/** Maps an array of objects to an array of ints */ 
//	public static <U> int[] mapInt(U[] in, ToIntFunction<U> map) {
//		return Objs.mapInt(in, map);
//	}
//	/** Maps an array of objects to an array of another longs */ 
//	public static <U> long[] mapLong(U[] in, ToLongFunction<U> map) {
//		return Objs.mapLong(in, map);
//	}
//
//	public static <T> T[] subArray(T[] in, int start) {
//		return Objs.subArray(in, start);
//	}
//	public static <T> T[] subArray(T[] in, int start, int end) {
//		return Objs.subArray(in, start, end);
//	}
//	public static <T> T[] subArrayLen(T[] in, int start, int len) {
//		return Objs.subArray(in, start, len);
//	}
//
//	public static <U> U[] spliced(U[] in, int pos, int len) {
//		return Objs.spliced(in, pos, len);
//	}
//	
//	public static <U> Stream<String> streamAsString(U[] in) {
//		return Objs.streamAsString(in);
//	}
//	
//	public static <T> String join(String glue, T[] a) { return join(a, glue); }
//	public static <T> String join(T[]in, String glue) {
//		return Objs.join(in, glue);
//	}
//
//	public static <U> int indexOf(U[] hay, U needle) {
//		return Objs.indexOf(hay, needle);
//	}
//	public static <U> int[] indexesOf(U[] hay, U[] needles) {
//		return Objs.indexesOf(hay, needles);
//	}
//	
//	public static <U> boolean contains(U[] hay, U needle) {
//		return Objs.contains(hay, needle);
//	}
//	
//	public static <U> int len(U[] in) { return Objs.len(in); }
//	
//	public static <U> boolean okPos(U[] a, int i) { return Objs.okPos(a, i); }
//	
//	public static <U> boolean empty(U[] in) { return Objs.empty(in); }
//	
//	public static <U> U[] remove(U[] hay, U needle) {
//		return Objs.remove(hay, needle);
//	}
//
//	public static <U, V> U[] remove(U[] hay, V[] needles, Function<U, V>map) {
//		return Objs.remove(hay, needles, map);
//	}
//	public static <U, V> U[] remove(U[] hay, int[] needles, ToIntFunction<U>map) {
//		return Objs.remove(hay, needles, map);
//	}
//	public static <U> U[] remove(U[] hay, U[] needles) {
//		return Objs.remove(hay, needles);
//	}
//	
//	public static <T> T[] remove(T[] old, Predicate<T> test) {
//		return Objs.remove(old, test);
//	}
//	
//	public static <T, N> T[]
//	unionMap(T[] old, T[] toAdd, Function<T, N> map) {
//		return Objs.unionMap(old, toAdd, map);
//	}
//	
//	public static <T> T[]
//	unionMapInt(T[] old, T[] add, ToIntFunction<T> map) {
//		return Objs.unionMapInt(old, add, map);
//	}
//	
//	public static <T> T[]
//	unionMapLong(T[] old, T[] add, ToLongFunction<T> map) {
//		return Objs.unionMapLong(old, add, map);
//	}
//	
//	public static <T, U> U foldl(T[] in, U ident, BiFunction<U, T, U> fold) {
//		return Objs.foldl(in, ident, fold);
//	}
//
//	/**Pair up elements in the array and map the pairs to a new value */
//	public static <T, U> U[] pair(
//		T[] in, BiFunction<T, T, U> pair, IntFunction<U[]> anew
//	) {
//		return Objs.pair(in, pair, anew);
//	}
//
//	public static <U, V> V[] pairLeft(
//		U[] in, U ident, BiFunction<U, U, V> pair, IntFunction<V[]> cons
//	) {
//		return Objs.pairLeft(in, ident, pair, cons);
//	}
//
//	/** Map each of the elements to an element of the same type using
//	 * a withX style of function  */
//	public static <T> T[] withMap(T[] in, Function<T, T> mapper) {
//		return Objs.withMap(in, mapper);
//	}
//
//	public static <U> U[] filter(U[] in, Predicate<U> map) {
//		return Objs.filter(in, map);
//	}
//	
//	public static <U> U[] reversed(U[] a) { return Objs.reversed(a); }
//
//	public static <U> U[] append(U a, U[] b, IntFunction<U[]> cons) {
//		return Objs.append(a, b, cons);
//	}
//	
//	public static <U> U[] append(U[] a, U[] b) { return Objs.append(a, b); }
//
//	public static <T> T[] append(T[] a, T b, IntFunction<T[]> cons) {
//		return Objs.append(a, b, cons);
//	}
//
//	public static <T> T[] union(T[] a, T b, IntFunction<T[]> cons) {
//		return Objs.union(a, b, cons);
//	}
//	public static <T> T[] union(T[] a, T[] b) { return Objs.union(a, b); }
//
}
