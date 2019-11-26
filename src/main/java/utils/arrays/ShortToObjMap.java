package utils.arrays;

import static java.util.Arrays.asList;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.stream;
import static utils.arrays.Shorts.EMPTY;
import static utils.arrays.Shorts.insert;
import static utils.arrays.Shorts.spliced;

import java.util.Iterator;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import utils.arrays.Shorts.Fn;

public class ShortToObjMap<T> {
	@FunctionalInterface
	public static interface ForEachCons<T> {
		public void accept(int i, T t);
	}

	private short[] keys = EMPTY;
	private T[] vals;
	public final IntFunction<T[]> cons;
	
	public ShortToObjMap(IntFunction<T[]> cons) {
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
		return stream(vals);
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
	
	public void forEach(ForEachCons<T> action) {
		for (int i=0; i<keys.length; i++) action.accept(keys[i], vals[i]);
	}
}