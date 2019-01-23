package utils.arrays;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.stream;

import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import utils.arrays.Longs.ToIntFn;

public class LongToIntMap {
	private long[] keys = Longs.EMPTY;
	private int[] vals = Ints.EMPTY;
	
	public LongToIntMap() {}
	
	public LongToIntMap(long[] keys, int[] vals) {
		this();
		//TODO: this is quite wasteful
		for (int i=0; i<keys.length; i++) put(keys[i], vals[i]);
	}
	
	public int put(long key, int val) {
		var pos = binarySearch(keys, key);
		if (pos>=0) {
			var out = vals[pos];
			vals[pos] = val;
			return out;
		} else {
			pos = -(pos+1);
			keys = Longs.insert(keys, pos, key);
			vals = Ints.insert(vals, pos, val);
			return 0;
		}
	}
	
	public int remove(long key) {
		var pos = binarySearch(keys, key);
		if (pos<0) return 0;
		var out = vals[pos];
		keys = Longs.spliced(keys, pos, 1);
		vals = Ints.spliced(vals, pos, 1);
		return out;
	}
	
	public int get(long key) {
		return getOrDefault(key, 0);
	}
	
	public int getOrDefault(long key, int def) {
		var pos = binarySearch(keys, key);
		return pos>=0 ? vals[pos] : def;
	}
	
	public boolean containsKey(long key) {
		return binarySearch(keys, key)>=0;
	}
	
	public int computeIfAbsent(long key, ToIntFn gen) {
		var pos = binarySearch(keys, key);
		if (pos>=0) return vals[pos];
		var val = gen.apply(key);
		pos = -(pos+1);
		keys = Longs.insert(keys, pos, key);
		vals = Ints.insert(vals, pos, val);
		return val;
	}
	
	public void clear() {
		keys = Longs.EMPTY;
		vals = Ints.EMPTY;
	}
	
	public IntStream streamVals() {
		return stream(vals);
	}

	public LongStream streamKeys() {
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
	
	public Stream<LongToIntMap.Entry> streamEntries() {
		return IntStream.range(0, keys.length)
			.mapToObj(i->new Entry(keys[i], vals[i]));
	}

}