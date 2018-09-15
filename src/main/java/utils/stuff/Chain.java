package utils.stuff;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
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
	
	public static <N> Chain<N> get(N wraps) {
		return new Chain<N>(wraps);
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
	
	public <R> Chain<T> 
	ifSet(R v, BiConsumer<T, R> consumer) {
		return ifTest(Support::notNull, consumer, v);
	}
	
	public <R> Chain<T> 
	ifSetS(Supplier<R> v, BiConsumer<T, R> consumer) {
		return ifSet(v.get(), consumer);
	}
	
	public Chain<T> 
	ifNotEmpty(BiConsumer<T, String> consumer, String value) {
		return ifTest(v->v!=null && !v.isEmpty(), consumer, value);
	}
	
	public Chain<T> 
	ifTrue(boolean val, BiConsumer<T, Boolean> consumer) {
		return ifTest(v->v, consumer, val);
	}
	
	public Chain<T> 
	ifTrue(Supplier<Boolean> val, BiConsumer<T, Boolean> consumer) {
		return ifTrue(val.get(), consumer);
	}
	
	public Chain<T> 
	ifGTE0(int val, BiConsumer<T, Integer> consumer) {
		return ifTest(v->v>=0, consumer, val);
	}
	
	public Chain<T> 
	ifGTE0(Supplier<Integer> val, BiConsumer<T, Integer> consumer) {
		return ifGTE0(val.get(), consumer);
	}
	
	public Chain<T> 
	ifGT0(int val, BiConsumer<T, Integer> consumer) {
		return ifTest(v->v>0, consumer, val);
	}
	
	public Chain<T> 
	ifGT0(Supplier<Integer> val, BiConsumer<T, Integer> consumer) {
		return ifGT0(val.get(), consumer);
	}
	
	public Chain<T> 
	ifN0(int val, BiConsumer<T, Integer> consumer) {
		return ifTest(v->v!=0, consumer, val);
	}
	
	public Chain<T> 
	ifN0(Supplier<Integer> val, BiConsumer<T, Integer> consumer) {
		return ifN0(val.get(), consumer);
	}
	
	public Chain<T> 
	if0(int val, BiConsumer<T, Integer> consumer) {
		return ifTest(v->v==0, consumer, val);
	}
	
	public Chain<T> 
	if0(Supplier<Integer> val, BiConsumer<T, Integer> consumer) {
		return if0(val.get(), consumer);
	}
	
	public <N, M> Chain<T> 
	ifTest(Function<N, Boolean> test, BiConsumer<T, N> consumer, N val) {
		if (test.apply(val)) consumer.accept(wraps, val);
		return this;
	}
	
	public <N, M> Chain<T>
	ifTest(Supplier<Boolean> test, BiConsumer<T, N> consumer, N value) {
		if (test.get()) consumer.accept(wraps, value);
		return this;
	}

	public <N, M> Chain<T> 
	ifTest(BooleanSupplier test, Consumer<T> consumer) {
		if (test.getAsBoolean()) consumer.accept(wraps);
		return this;
	}
	
	public Chain<T> 
	ifGTMin(int val, BiConsumer<T, Integer> consumer) {
		return ifTest(v->v>Integer.MIN_VALUE, consumer, val);
	}

	public Chain<T> 
	ifGTMin(Supplier<Integer> v, BiConsumer<T, Integer> consumer) {
		return ifGTMin(v.get(), consumer);
	}

	public <U, V> Chain<T> 
	ifMaps(U value, Map<U, V> map, BiConsumer<T, V> consumer) {
		if (map.containsKey(value)) consumer.accept(wraps, map.get(value));
		return this;
	}
	
	public <U, V> Chain<T> 
	ifMapsS(Supplier<U> supplier, Map<U, V> map, BiConsumer<T, V> consumer) {
		U value = supplier.get();
		if (map.containsKey(value)) consumer.accept(wraps, map.get(value));
		return this;
	}
	
	public T complete() {
		return wraps;
	}
}