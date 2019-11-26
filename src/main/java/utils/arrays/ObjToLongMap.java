package utils.arrays;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.stream;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class ObjToLongMap<T> {
	private T[] keys;
	private long[] vals = Longs.EMPTY;
	private final IntFunction<T[]> keysGen;
	
	public ObjToLongMap(IntFunction<T[]> keysGen) {
		this.keysGen = keysGen;
		keys = keysGen.apply(0);
	}
	
	public ObjToLongMap(T[] keys, long[] vals) {
		this(i->copyOf(keys, i));
		//TODO: this is quite wasteful
		for (int i=0; i<keys.length; i++) put(keys[i], vals[i]);
	}
	
	public long put(T key, long val) {
		var pos = binarySearch(keys, key);
		if (pos>=0) {
			var out = vals[pos];
			vals[pos] = val;
			return out;
		} else {
			pos = -(pos+1);
			keys = Objs.insert(keys, pos, key);
			vals = Longs.insert(vals, pos, val);
			return 0;
		}
	}
	
	public long remove(T key) {
		var pos = binarySearch(keys, key);
		if (pos<0) return 0;
		var out = vals[pos];
		keys = Objs.spliced(keys, pos, 1);
		vals = Longs.spliced(vals, pos, 1);
		return out;
	}
	
	public long get(T key) {
		return getOrDefault(key, 0);
	}
	
	public long getOrDefault(T key, long def) {
		var pos = binarySearch(keys, key);
		return pos>=0 ? vals[pos] : def;
	}
	
	public boolean containsKey(T key) {
		return binarySearch(keys, key)>=0;
	}
	
	public long computeIfAbsent(T key, Function<T, Long> gen) {
		var pos = binarySearch(keys, key);
		if (pos>=0) return vals[pos];
		var val = gen.apply(key);
		pos = -(pos+1);
		keys = Objs.insert(keys, pos, key);
		vals = Longs.insert(vals, pos, val);
		return val;
	}
	
	public void clear() {
		keys = keysGen.apply(0);
		vals = Longs.EMPTY;
	}
	
	public LongStream streamVals() {
		return stream(vals);
	}

	public Stream<T> streamKeys() {
		return stream(keys);
	}
	public static class Entry<T> {
		public final T key;
		public final long val;

		public Entry(T key, long val) {
			this.key = key;
			this.val = val;
		}
	}
	
	public Stream<ObjToLongMap.Entry<T>> streamEntries() {
		return IntStream.range(0, keys.length)
			.mapToObj(i->new Entry<>(keys[i], vals[i]));
	}

}