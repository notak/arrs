package arrays;

import static java.lang.Math.min;

import java.util.Arrays;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static java.util.stream.Collectors.joining;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

public class Objs {
	
	@SafeVarargs
	public static <T> T[] toArray(T... in) { return in; }
	
	public static <T> boolean eq(T[] a, T[] b) {
		return Arrays.deepEquals(a, b);
	}

	public static <U> Stream<U> stream(U[] us) {
		return /*us==null ? Stream.empty() :*/ Arrays.stream(us);
	}
	
	public static <U> void forEach(U[] us, Consumer<U> fn) {
		stream(us).forEach(fn);
	}
	
	/** Optionally get the nth element, returns empty for array len<n+1 */
	public static <T> Optional<T> nth(T[] in, int n) { 
		return okPos(in, n) ? Optional.of(in[n]) : Optional.empty();
	}

	/** Optionally get the nth element from the end (empty if array len<n+1) */
	public static <T> Optional<T> nthLast(T[] in, int n) {
		return nth(in, in.length-(1+n));
	}
	
	/** Optionally get the first element, returning empty for an empty array */
	public static <T> Optional<T> first(T[] in) { return nth(in, 0); }
	
	/** Optionally get the first element, returning empty for an empty array */
	public static <T> Optional<T> first(T[] in, Predicate<T> matcher) {
		return stream(in).filter(matcher::test).findFirst();
	}
	
	/** Optionally get the last element, returns empty for empty array */
	public static <T> Optional<T> last(T[] in) { return nthLast(in, 0); }
	
	/** Maps an array of objects to an array of another object */ 
	public static <T, U> U[] 
	map(T[] in, Function<T, U> map, IntFunction<U[]> cons) {
		return stream(in).map(map).toArray(cons);
	}
	/** Maps an array of objects to an array of strings */ 
	public static <T> String[] mapStr(T[] in, Function<T, String> mapper) {
		return map(in, mapper, String[]::new);
	}
	/** Maps an array of objects to an array of ints */ 
	public static <U> int[] mapInt(U[] in, ToIntFunction<U> mapper) {
		return stream(in).mapToInt(mapper).toArray();
	}
	/** Maps an array of objects to an array of longs */ 
	public static <U> long[] mapLong(U[] in, ToLongFunction<U> mapper) {
		return stream(in).mapToLong(mapper).toArray();
	}
	
	public static <T> T[] subArray(T[] in, int start) {
		return subArray(in, start, in.length);
	}
	
	public static <T> T[] subArray(T[] in, int start, int end) {
		return copyOfRange(in, min(start, in.length), min(end, in.length));
	}
	
	public static <T> T[] subArrayLen(T[] in, int start, int len) {
		return subArray(in, start, start + len);
	}

	public static <U> U[] spliced(U[] in, int pos, int len) {
		//if (in==null) return null;
		if (pos==0) return copyOfRange(in, len, in.length);
		else if (pos+len>=in.length) return copyOfRange(in, 0, pos);
		
		U[] out = copyOfRange(in, 0, in.length-len);
		for (int i=pos; i<out.length; i++) out[i] = in[i+len];
		return out;
	}
	
	public static <U> U[] spliced(U[] in, int pos, int len, U[] reps) {
		//if (in==null) return null;
		if (pos==0) return append(reps, copyOfRange(in, len, in.length));
		else if (pos+len>=in.length) {
			return append(copyOfRange(in, 0, pos), reps);
		}
		return
			append(append(subArray(in, 0, pos), reps), subArray(in, pos+len));
	}
	
	public static <U> U[] spliced(U[] in, int pos, int len, U rep) {
		//if (in==null) return null;
		return spliced(in, pos, len, toArray(rep));
	}
	
	public static <U> Stream<String> streamAsString(U[] in) {
		return stream(in).map(i->i.toString());
	}
	
	public static <T> String join(String glue, T[] a) { return join(a, glue); }
	public static <T> String join(T[]in, String glue) {
		return Arrays.stream(in).map(Object::toString).collect(joining(glue));
	}

	public static <U> int indexOf(U[] hay, U needle) {
		for (int i=hay.length-1; i>=0; i--) if (needle.equals(hay[i])) return i;
		return -1;
	}

	public static <U> int[] indexesOf(U[] hay, U[] needles) {
		int[] out = Ints.EMPTY;
		for (int i=hay.length-1; i>=0; i--) {
			if (contains(needles, hay[i])) out = Ints.append(out, i);
		}
		return out;
	}
	
	public static <U> boolean contains(U[] hay, U needle) {
		return indexOf(hay, needle) >= 0;
	}
	
	public static <U> boolean okPos(U[] a, int i) { return i>=0 && i<a.length; }
	
	public static <U> boolean empty(U[] in) { return in.length==0; }
	
	public static <U> U[] remove(U[] hay, U needle) {
		int pos = indexOf(hay, needle);
		return pos<0 ? hay : spliced(hay, pos, 1);
	}

	public static <U, V> U[] remove(U[] hay, V[] needles, Function<U, V>map) {
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
	
	public static <U, V> U[] remove(U[] hay, int[] needles, ToIntFunction<U>map) {
		for (int j=hay.length; j>0; j--) {
			int test = map.applyAsInt(hay[j-1]);
			for (int i=needles.length; i>0; i--) {
				if (needles[i-1]==test) {
					hay = spliced(hay, j-1, 1);
					break;
				}
			}
		}
		return hay;
	}
	
	protected static <T> IntFunction<T[]> cons(T[] template) {
		return i->copyOf(template, i);
	}
	
	public static <U> U[] remove(U[] hay, U[] needles) {
		return foldl(needles, hay, Objs::remove);
	}
	
	public static <T> T[] remove(T[] old, Predicate<T> test) {
		for (int j=old.length-1; j>=0; j--) {
			if (test.test(old[j])) old = spliced(old, j, 1);
		}
		return old;
	}
	
	/** Return an array containing all add elements, and any old elements
	 *  which don't map to the same value as on of the add elements */
	public static <T, N> T[] unionMap(T[] old, T[] add, Function<T, N> map) {
		Object[] addMap = new Object[add.length];
		for (int i=0; i<addMap.length; i++) addMap[i]=map.apply(add[i]);
		for (int i=old.length-1; i>=0; i--) {
			if (contains(addMap, map.apply(old[i]))) old = spliced(old, i, 1);
		}
		return append(old, add);
	}
	/** Return an array containing all add elements, and any old elements
	 *  which don't map to the same value as on of the add elements */
	public static <T, N> T[] 
	unionMapInt(T[] old, T[] add, ToIntFunction<T> map) {
		int[] addMap = mapInt(add, map);
		for (int i=old.length-1; i>=0; i--) {
			if (Ints.contains(addMap, map.applyAsInt(old[i]))) {
				old = spliced(old, i, 1);
			}
		}
		return append(old, add);
	}
	/** Return an array containing all add elements, and any old elements
	 *  which don't map to the same value as on of the add elements */
	public static <T, N> T[] 
	unionMapLong(T[] old, T[] add, ToLongFunction<T> map) {
		long[] addMap = mapLong(add, map);
		for (int i=old.length-1; i>=0; i--) {
			if (Longs.contains(addMap, map.applyAsLong(old[i]))) {
				old = spliced(old, i, 1);
			}
		}
		return append(old, add);
	}
	
	public static <T, U> U foldl(T[] in, U ident, BiFunction<U, T, U> fold) {
		if (in!=null) for (T t: in) ident = fold.apply(ident, t);
		return ident;
	}

	/**Pair up elements in the array and map the pairs to a new value */
	public static <T, U> U[] pair(
		T[] in, BiFunction<T, T, U> pair, IntFunction<U[]> anew
	) {
		U[] out = anew.apply(in.length/2);
		for (int i=1; i<in.length; i+=2) {
			out[(i-1)/2] = pair.apply(in[i-1], in[i]);
		}
		return out;
	}

	public static <U, V> V[] pairLeft(
		U[] in, U ident, BiFunction<U, U, V> pair, IntFunction<V[]> cons
	) {
		V[] out = cons.apply(in.length);
		for (int i=in.length-1; i>=0; i--) {
			out[i] = pair.apply(i<1 ? ident : in[i-1], in[i]);
		}
		return out;
	}

	/** Map each of the elements to an element of the same type using
	 * a withX style of function
	 */
	public static <T> T[] withMap(T[] in, Function<T, T> mapper) {
		if (in==null) return null;
		T[] out = in.clone();
		for (int i=0; i<in.length; i++) out[i] = mapper.apply(in[i]);
		return out;
	}


	public static <U> U[] filter(U[] in, Predicate<U> map) {
		return empty(in) ? in : stream(in).filter(map).toArray(cons(in));
	}
	
	public static <U> U[] reversed(U[] a) {
		//if (a==null) return null;
		U[] out = Arrays.copyOf(a, a.length);
		for (int i=0; i<out.length; i++) out[i] = a[a.length-(1+i)];
		return out;
	}

	/** Return a new array consisting of a followed by all elements of b */
	public static <U> U[] append(U a, U[] b) {
		var out = Arrays.copyOf(b,  b.length+1);
		for (int i=b.length; i>0; i--) out[i] = out[i-1];
		out[0] = a;
		return out;
	}
	
	/** Return a new array consisting of all elements of a followed by all 
	 * elements of b */
	public static <U> U[] append(U[] a, U[] b) {
		//if (a==null) return b; else if (b==null) return a;
		U[] out = copyOf(a, a.length + b.length);
		for (int i=0; i<b.length; i++) out[a.length+i] = b[i];
		return out;
	}

	/** Return a new array consisting of all elements of a followed by b */
	public static <T> T[] append(T[] a, T b) {
		T[] out = copyOf(a, a.length + 1);
		out[a.length] = b;
		return out;
	}

	public static <T> T[] union(T[] a, T b) {
		return (contains(a,  b)) ? a : append(a, b);
	}

	public static <T> T[] union(T[] a, T[] b) {
		return foldl(b, a, Objs::union);
	}
}
