package utils.arrays;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.stream;
import static utils.arrays.Ints.EMPTY;
import static utils.arrays.Ints.insert;
import static utils.arrays.Ints.spliced;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import utils.arrays.Longs.ToIntFn;

public class IntToIntMap {
	private int[] keys = EMPTY;
	private int[] vals = EMPTY;
	
	public IntToIntMap() {}
	
	public IntToIntMap(int[] keys, int[] vals) {
		this();
		//TODO: this is quite wasteful
		for (int i=0; i<keys.length; i++) put(keys[i], vals[i]);
	}
	
	public int put(int key, int val) {
		var pos = binarySearch(keys, key);
		if (pos>=0) {
			var out = vals[pos];
			vals[pos] = val;
			return out;
		} else {
			pos = -(pos+1);
			keys = insert(keys, pos, key);
			vals = insert(vals, pos, val);
			return 0;
		}
	}
	
	public int remove(int key) {
		var pos = binarySearch(keys, key);
		if (pos<0) return 0;
		var out = vals[pos];
		keys = spliced(keys, pos, 1);
		vals = spliced(vals, pos, 1);
		return out;
	}
	
	public int get(int key) {
		return getOrDefault(key, 0);
	}
	
	public int getOrDefault(int key, int def) {
		var pos = binarySearch(keys, key);
		return pos>=0 ? vals[pos] : def;
	}
	
	public boolean containsKey(int key) {
		return binarySearch(keys, key)>=0;
	}
	
	public int computeIfAbsent(int key, ToIntFn gen) {
		var pos = binarySearch(keys, key);
		if (pos>=0) return vals[pos];
		var val = gen.apply(key);
		pos = -(pos+1);
		keys = insert(keys, pos, key);
		vals = insert(vals, pos, val);
		return val;
	}
	
	public void clear() {
		keys = vals = EMPTY;
	}
	
	public IntStream streamVals() {
		return stream(vals);
	}

	public IntStream streamKeys() {
		return stream(keys);
	}
	public static class Entry {
		public final long key;
		public final int val;

		public Entry(long key, int val) {
			this.key = key;
			this.val = val;
		}
	}
	
	public Stream<IntToIntMap.Entry> streamEntries() {
		return IntStream.range(0, keys.length)
			.mapToObj(i->new Entry(keys[i], vals[i]));
	}

}