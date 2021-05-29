package utils.stuff;

import static java.util.Collections.emptyMap;
import static utils.arrays.Objs.toArray;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utils.arrays.IntToObjMap;
import utils.arrays.Objs;
import utils.stuff.Emitter.BaseEmitter;

public class MapEmitter<K, V> 
extends BaseEmitter<MapEmitter.MapUpdate<K, V>> 
implements Map<K, V> {
	private MapEmitter(Map<K, V> init) {
		super(MapUpdate::with, new MapUpdate<>(init, null));
	}

	public static class MapUpdate<K, V> {
		@SuppressWarnings("rawtypes")
		public static MapUpdate[] NONE = new MapUpdate[0];
		public final Map<K, V> map;
		public final K[] removals;

		public MapUpdate(Map<K, V> map, K[] removals) {
			this.map = map;
			this.removals = removals;
		}
		
		public MapUpdate<K, V> with(MapUpdate<K, V> add) {
			HashMap<K, V> out = new HashMap<>(map);
			//removes on null, unlike addAll
			add.map.forEach((k, v)->out.merge(k, v, (a, b)->b));
			if (add.removals!=null) Objs.forEach(add.removals, out::remove);
			return new MapUpdate<>(out, null);
		}
	}

	public static <K, V> MapEmitter<K, V> build(Map<K, V> init) {
		return new MapEmitter<>(init);
	}
	
	public static <K, V> MapEmitter<K, V> build() {
		return build(new HashMap<>());
	}
	
	@Override
	public V get(Object k) {
		return current().map.get(k);
	}
	
	public void send(Map<K, V>map, boolean init) {
		send(new MapUpdate<>(map, null), init);
	}

	public void send(IntToObjMap<V>map, boolean init) {
		send(new MapUpdate<>(map, null), init);
	}

	public void send(Map<K, V>map) {
		send(map, false);
	}

	public void send(K key, V value) {
		send(Map.of(key, value));
	}

	@Override
	public V remove(Object k) {
		if (!containsKey(k)) return null;
		V old = get(k);
		@SuppressWarnings("unchecked") K kk = (K)k;
		send(new MapUpdate<>(emptyMap(), toArray(kk)));
		return old;
	}

	@Override
	public int size() {
		return current().map.size();
	}

	@Override
	public void clear() {
		send(Collections.emptyMap(), true);
	}

	@Override
	public boolean containsKey(Object key) {
		return current().map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return current().map.containsValue(value);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return current().map.entrySet();
	}

	@Override
	public boolean isEmpty() {
		return current().map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return current().map.keySet();
	}

	@Override
	public V put(K k, V v) {
		V old = get(k);
		send(Map.of(k, v));
		return old;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		send(new HashMap<>(m)); //this will probably break;
	}

	@Override
	public Collection<V> values() {
		return current().map.values();
	}
}