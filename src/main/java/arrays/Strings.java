package arrays;

import static java.lang.Math.min;

import java.util.Arrays;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

public class Strings {
	public static String substr(String in, int start, int end) {
		return in==null || in.length()<start ? ""
			: in.substring(start, Math.min(end, in.length()));
	}
	
//	public static String[] toArray(String... in) { return in; }
//
////	public static String[] toArray(String in) { return in; }
//
//	public static Stream<String> stream(String[] us) {
//		return us==null ? Stream.empty() : Arrays.stream(us);
//	}
//	
//	public static void forEach(String[] us, Consumer<String> fn) {
//		stream(us).forEach(fn);
//	}
//	
//	/** Optionally get the nth element, returns empty for array len<n+1 */
//	public static Optional<String> nth(String[] in, int n) { 
//		return okPos(in, n) ? Optional.of(in[n]) : Optional.empty();
//	}
//
//	/** Optionally get the nth element from the end (empty if array len<n+1) */
//	public static Optional<String> nthLast(String[] in, int n) {
//		return nth(in, in.length-(1+n));
//	}
//	
//	/** Optionally get the first element, returning empty for an empty array */
//	public static Optional<String> first(String[] in) { return nth(in, 0); }
//	
//	/** Optionally get the last element, returns empty for empty array */
//	public static Optional<String> last(String[] in) { return nthLast(in, 0); }
//	
//	/** Maps an array of objects to an array of strings */ 
//	public static String[] 
//	mapStr(String[] in, Function<String, String> mapper) {
//		return stream(in).map(mapper).toArray(i->new String[i]);
//	}
//	
//	/** Maps an array of objects to an array of ints */ 
//	public static int[] map(String[] in, ToIntFunction<String> mapper) {
//		return stream(in).mapToInt(mapper).toArray();
//	}
//
//	public static <T> T[] subArray(T[] in, int start) {
//		if (in==null) return null;
//		return subArray(in, start, in.length);
//	}
//	
//	public static <T> T[] subArray(T[] in, int start, int end) {
//		return copyOfRange(in, min(start, in.length), min(end, in.length));
//	}
//	
//	public static <T> T[] subArrayLen(T[] in, int start, int len) {
//		return subArray(in, start, start + len);
//	}
//
//	public static String[] spliced(String[] in, int pos, int len) {
//		if (in==null) return null;
//		if (pos==0) return copyOfRange(in, len, in.length);
//		else if (pos+len>=in.length) return copyOfRange(in, 0, pos);
//		
//		String[] out = copyOfRange(in, 0, in.length-len);
//		for (int i=pos; i<out.length; i++) out[i] = in[i+len];
//		return out;
//	}
//	
//	public static Stream<String> streamAsString(String[] in) {
//		return stream(in).map(i->i.toString());
//	}
//	
//	public static <T> String join(String glue, T[] a) { return join(a, glue); }
//	public static <T> String join(T[]in, String glue) {
//		return Lists.join(Arrays.stream(in), glue);
//	}
//
//	public static int indexOf(String[] hay, String needle) {
//		for (int i=len(hay)-1; i>=0; i--) if (needle.equals(hay[i])) return i;
//		return -1;
//	}
//
//	public static int[] indexesOf(String[] hay, String[] needles) {
//		int[] out = Ints.EMPTY;
//		for (int i=len(hay)-1; i>=0; i--) {
//			if (contains(needles, hay[i])) out = Ints.append(out, i);
//		}
//		return out;
//	}
//	
//	public static boolean contains(String[] hay, String needle) {
//		return indexOf(hay, needle) >= 0;
//	}
//	
//	public static int len(String[] in) { return in==null ? 0 : in.length; }
//	
//	public static boolean okPos(String[] a, int i) { return i>=0 && i<len(a); }
//	
//	public static boolean empty(String[] in) { return len(in)==0; }
//	
//	public static String[] remove(String[] hay, String needle) {
//		if (empty(hay)) return hay;
//		int pos = indexOf(hay, needle);
//		return pos<0 ? hay : spliced(hay, pos, 1);
//	}
//
//	public static <V> String[] remove(String[] hay, V[] needles, Function<String, V>map) {
//		for (int j=len(hay); j>0; j--) {
//			V test = map.apply(hay[j-1]);
//			for (int i=Objs.len(needles); i>0; i--) {
//				if (needles[i-1].equals(test)) {
//					hay = spliced(hay, j-1, 1);
//					break;
//				}
//			}
//		}
//		return hay;
//	}
//	
//	protected static <T> IntFunction<T[]> cons(T[] template) {
//		return i->copyOf(template, i);
//	}
//	
//	public static String[] remove(String[] hay, String[] needles) {
//		return foldl(needles, hay, Strings::remove);
//	}
//	
//	public static String[] remove(String[] old, Predicate<String> test) {
//		for (int j=len(old)-1; j>=0; j--) {
//			if (test.test(old[j])) old = spliced(old, j, 1);
//		}
//		return old;
//	}
//	
//	public static <T> T[] 
//	map(String[] in, Function<String, T> map, IntFunction<T[]> cons) {
//		return stream(in).map(map).toArray(cons);
//	}
//	
//	public static <N> String[] replaceOrAppend(
//		String[] old, String[] toAdd, Function<String, N> map, IntFunction<N[]> cons
//	) {
//		return append(remove(old, map(toAdd, map, cons), map), toAdd);
//	}
//	
//	public static <T> T
//	foldl(String[] in, T ident, BiFunction<T, String, T> fold) {
//		if (in!=null) for (String t: in) ident = fold.apply(ident, t);
//		return ident;
//	}
//
//	/**Pair up elements in the array and map the pairs to a new value */
//	public static <T> T[] pair(
//		String[] in, BiFunction<String, String, T> pair, IntFunction<T[]> anew
//	) {
//		T[] out = anew.apply(len(in)/2);
//		for (int i=1; i<len(in); i+=2) {
//			out[(i-1)/2] = pair.apply(in[i-1], in[i]);
//		}
//		return out;
//	}
//
//	public static <V> V[] pairLeft(
//		String[] in, String ident, BiFunction<String, String, V> pair, IntFunction<V[]> cons
//	) {
//		V[] out = cons.apply(len(in));
//		for (int i=len(in)-1; i>=0; i--) {
//			out[i] = pair.apply(i<1 ? ident : in[i-1], in[i]);
//		}
//		return out;
//	}
//
//	public static String[] filter(String[] in, Predicate<String> map) {
//		return empty(in) ? in : stream(in).filter(map).toArray(cons(in));
//	}
//	
//	public static String[] reversed(String[] a) {
//		if (a==null) return null;
//		String[] out = Arrays.copyOf(a, a.length);
//		for (int i=0; i<out.length; i++) out[i] = a[a.length-(1+i)];
//		return out;
//	}
//
//	public static String[] append(String[] a, String b) {
//		if (a==null) {
//			String[] out = { b };
//			return out;
//		}
//		String[] out = Arrays.copyOf(a, a.length + 1);
//		out[out.length-1] = b;
//		return out;
//	}
//	
//	public static String[] append(String a, String[] b) {
//		return append(toArray(a), b);
//	}
//	
//	public static String[] append(String[] a, String[] b) {
//		if (a==null) return b; else if (b==null) return a;
//		String[] out = Arrays.copyOf(a, a.length + b.length);
//		for (int i=0; i<b.length; i++) out[a.length+i] = b[i];
//		return out;
//	}
//
//	public static String[] union(String[] a, String b) {
//		return (contains(a,  b)) ? a : append(a, b);
//	}
//
//	public static String[] union(String[] a, String[] b) {
//		return foldl(b, a, Strings::union);
//	}
}
