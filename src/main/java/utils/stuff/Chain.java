package utils.stuff;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import utils.stuff.Fns.SideEffect;

/** Used to wrap non-fluent interfaces and make them fluent or to chain 
 * operations. This allows you to act on normal objects like they are
 * Optionals, but with a range of conditional actions on parameters as well
 * <p>Use this class sparingly - the results are often less clear than
 * just writing the code in a procedural style */
public class Chain<T> {
	
	private T wraps;
	
	public Chain(T wraps) {
		this.wraps = wraps;
	}
	
	public static <N> Chain<N> chain(N wraps) {
		return new Chain<N>(wraps);
	}
	
	public static <N> Chain<N> get(N wraps) {
		return new Chain<N>(wraps);
	}
	
	private Chain<T> getd(T newWrap) {
		return newWrap == wraps ? this : Chain.get(newWrap);
	}
	
	/** Get the value which has been wrapped in the Chain container */
	public T get() {
		return wraps;
	}
	
	/** Perform an action which doesn't modify the contents of the chain */
	public Chain<T> 
	chain(SideEffect action) {
		action.send();
		return this;
	}
	
	/** Map the wrapped object into another object and return that wrapped */
	public <R> Chain<R> 
	map(Function<T, R> map) {
		return get(map.apply(wraps));
	}
	
	/** If the optional is non-empty, apply it to the wrapped value using
	 * the provided function, and return that wrapped, otherwise return this */
	public <S> Chain<T> 
	flatMap(Optional<S> opt, BiFunction<T, S, T> map) {
		return opt.map(s->map.apply(wraps, s)).map(Chain::get).orElse(this);
	}
	
	public Chain<T>
	replaceIfNull(Supplier<T> otherwise) {
		Console.printf("in replaceIfNull %s %s %s", wraps, wraps==null, otherwise.get());
		return wraps==null ? getd(otherwise.get()) : this;
	}
	
	public <R> Chain<T> 
	ifSet(R v, BiFunction<T, R, T> consumer) {
		return ifTest(Support::notNull, consumer, v);
	}
	
	public Chain<T> 
	ifNotEmpty(BiFunction<T, String, T> consumer, String value) {
		return ifTest(v->v!=null && !v.isEmpty(), consumer, value);
	}
	
	public Chain<T> 
	ifNotEmpty(String value, BiFunction<T, String, T> consumer) {
		return ifTest(v->v!=null && !v.isEmpty(), consumer, value);
	}
	
	public Chain<T> 
	ifTrue(boolean val, BiFunction<T, Boolean, T> consumer) {
		return ifTest(v->v, consumer, val);
	}
	
	public Chain<T> 
	ifGTE0(int val, BiFunction<T, Integer, T> consumer) {
		return ifTest(v->v>=0, consumer, val);
	}
	
	public Chain<T> 
	ifGT0(int val, BiFunction<T, Integer, T> consumer) {
		return ifTest(v->v>0, consumer, val);
	}
	
	public Chain<T> 
	ifN0(int val, BiFunction<T, Integer, T> consumer) {
		return ifTest(v->v!=0, consumer, val);
	}
	
	public Chain<T> 
	if0(int val, BiFunction<T, Integer, T> consumer) {
		return ifTest(v->v==0, consumer, val);
	}
	
	public <N, M> Chain<T> 
	ifTest(Function<N, Boolean> test, BiFunction<T, N, T> fn, N val) {
		return test.apply(val) ? getd(fn.apply(wraps, val)) : this;
	}
	
	public <N, M> Chain<T>
	ifTest(Supplier<Boolean> test, BiFunction<T, N, T> fn, N value) {
		return test.get() ? getd(fn.apply(wraps, value)) : this;
	}

	public Chain<T> 
	ifGTMin(int val, BiFunction<T, Integer, T> consumer) {
		return ifTest(v->v>Integer.MIN_VALUE, consumer, val);
	}

	public Chain<T> 
	ifGTMin(Supplier<Integer> v, BiFunction<T, Integer, T> consumer) {
		return ifGTMin(v.get(), consumer);
	}

	public <U, V> Chain<T> 
	ifMaps(U value, Map<U, V> map, BiFunction<T, V, T> fn) {
		return value!=null && map.containsKey(value) ? 
			getd(fn.apply(wraps, map.get(value))) : this;
	}
	
	public static <U, V> void ifMaps(U val, Map<U, V> map, Consumer<V> fn) {
		var mapped = map.get(val);
		if (mapped!=null) fn.accept(mapped);
	}
	
	public T complete() {
		return wraps;
	}
}