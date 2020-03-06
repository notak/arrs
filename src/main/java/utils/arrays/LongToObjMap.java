package utils.arrays;

import static java.util.Arrays.asList;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.stream;
import static utils.arrays.Longs.EMPTY;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import utils.arrays.Longs.BiSFn;
import utils.arrays.Longs.Fn;

public class LongToObjMap<T> {
	private long[] keys = EMPTY;
	private T[] vals;
	public final IntFunction<T[]> cons;
	
	public LongToObjMap(IntFunction<T[]> cons) {
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
			keys = Longs.insert(keys, pos, key);
			vals = Objs.insert(vals, pos, val);
			return null;
		}
	}
	
	public T remove(long key) {
		var pos = binarySearch(keys, key);
		if (pos<0) return null;
		var out = vals[pos];
		keys = Longs.spliced(keys, pos, 1);
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
		keys = Longs.insert(keys, pos, key);
		vals = Objs.insert(vals, pos, val);
		return val;
	}
	
	public T compute(long key, BiSFn<T> updater) {
		var old = get(key);
		 T newValue = updater.apply(key, old);
		 if (newValue != null) put(key, newValue);
		 else if (old != null) remove(key);
		 return newValue;
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
			.mapToObj(i->new LongToObjMap.Entry<>(keys[i], vals[i]));
	}

	public Iterator<T> iterVals() {
		return asList(vals).iterator();
	}
	
	public void forEach(BiConsumer<Long, T> action) {
		for (int i=0; i<keys.length; i++) action.accept(keys[i], vals[i]);
	}
	
	public int size() {
		return this.keys.length;
	}
}