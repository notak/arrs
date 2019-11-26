package utils.arrays;

import static java.util.Arrays.asList;
import static java.util.Arrays.binarySearch;
import static java.util.Arrays.stream;
import static utils.arrays.Ints.EMPTY;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import utils.arrays.Ints.Fn;

public class IntToObjMap<T> {
	@FunctionalInterface
	public static interface ForEachCons<T> {
		public void accept(int i, T t);
	}

	@FunctionalInterface
	public static interface Computer<T> {
		public T compute(int i, T t);
	}

	private int[] keys = EMPTY;
	private T[] vals;
	public final IntFunction<T[]> cons;
	
	public IntToObjMap(IntFunction<T[]> cons) {
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
		keys = Ints.spliced(keys, pos, 1);
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
		keys = Ints.insert(keys, pos, key);
		vals = Objs.insert(vals, pos, val);
		return val;
	}
	
	public T compute(int key, Computer<T> gen) {
		var pos = binarySearch(keys, key);
		if (pos>=0) return vals[pos] = gen.compute(key, vals[pos]);
		else return insertAt(pos, key, gen.compute(key, null));
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
	
	public void forEach(ForEachCons<T> action) {
		for (int i=0; i<keys.length; i++) action.accept(keys[i], vals[i]);
	}
	
	public String toString() {
		var out = new StringBuilder();
		forEach((k, v)->out.append(k + ": " + v + ", "));
		return out.toString();
	}
}