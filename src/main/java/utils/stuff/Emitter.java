package utils.stuff;

import static utils.arrays.Objs.empty;
import static utils.arrays.Objs.forEach;
import static utils.arrays.Objs.remove;

import java.util.function.Function;

import utils.arrays.Objs;

public interface Emitter<T> {
	public static <T> Emitter<T> get() {
		return get((current, changes)->changes);
	}
	
	public static <T> Emitter<T> get(Combiner<T> combiner) {
		return get(combiner, null);
	}
	
	public static <T> Emitter<T> get(Combiner<T> combiner, T init) {
		return new BaseEmitter<>(combiner, init);
	}
	
	public static <U, N> Emitter<U[]> 
	unionMap(Function<U, N> matchOn, U[] init) {
		return get((c, n)->Objs.unionMap(c, n, matchOn), init);
	}

//	//this is probably broken
//	public static <N> Emitter<int[]> 
//	unionMap(IntFunction<N> matchOn, int[] init) {
//		return get((c, n)->Ints.unionMap(c, n, matchOn), init);
//	}
	
	public static <U> Emitter<U[]> append(U[] init) {
		return new BaseEmitter<>(Objs::append, init);
	}
	
	public static <U> Emitter<U[]> union(U[] init) {
		return new BaseEmitter<>(Objs::append, init);
	}
	public static <U> Emitter<U[]> union() { return append(null); }
	

	public default Emitter<T> init(T initial) {
		return init(initial, null, null);
	}
	public default Emitter<T> init(T initial, Fns.SideEffect onEmpty) {
		return init(initial, onEmpty, null);
	}
	public Emitter<T> init(T initial, Fns.SideEffect onEmpty, Fns.SideEffect onFirstSub);

	/** send a non-initing update */
	public default void send(T t) {
		send(t, false);
	}
	public void send(T t, boolean init);
	
	public Subs<T> subs();
	
	public void subscribe(Listener<T> listener);

	public void unsubscribe(Listener<T> subscriber);
	
	public T current();


	@FunctionalInterface
	public static interface Listener<T> {
		public void update(T t, boolean message);
	}
	
	@FunctionalInterface
	public static interface Combiner<T> {
		public T combine(T t, T u);
	}
	
	
	public class Subs<T> {
		private final Emitter<T> emitter;
		
		public Subs(Emitter<T> emitter) {
			this.emitter = emitter;
		}

		public synchronized void subscribe(Listener<T> listener) {
			emitter.subscribe(listener);
		}
	
		public synchronized void unsubscribe(Listener<T> listener) {
			emitter.unsubscribe(listener);
		}
		
		public T get() {
			return emitter.current();
		}
	}
	
	
	public class BaseEmitter<T> implements Emitter<T> {
		public BaseEmitter(Combiner<T> combiner, T init) {
			this.combiner = combiner;
			this.current = init;
		}
		
		/** listeners is copy-on-write to avoid concurrent modifications */
		@SuppressWarnings("unchecked")
		private Listener<T>[] listeners = new Listener[0];
		private Fns.SideEffect onEmpty;
		private Fns.SideEffect onFirstSub;
		private Combiner<T> combiner;
		private T current;
		
		public Emitter<T> init(T initial, Fns.SideEffect onEmpty, Fns.SideEffect onFirstSub) {
			this.onEmpty = onEmpty;
			this.onFirstSub = onFirstSub;
			if (listeners!=null && onFirstSub!=null) onFirstSub.send();
			send(initial, true);
			return this;
		}
	
		private final Subs<T> subs = new Subs<>(this);
		
		public Subs<T> subs() {
			return subs;
		}
		
		public synchronized void subscribe(Listener<T> listener) {
			if (empty(listeners) && onFirstSub!=null) onFirstSub.send();
			listeners = Objs.union(listeners, listener);
			if (current!=null) listener.update(current, true);
		}
	
		public synchronized void unsubscribe(Listener<T> subscriber) {
			listeners = remove(listeners, subscriber);
//			log.info("unsubscription. {} Now has {}", Emitter.this, listeners.length);
			if (listeners.length==0 && onEmpty!=null) onEmpty.send();
		}
		
		public T current() {
			return current;
		}

		synchronized public void send(T t, boolean init) {
//			log.error("sending {} {} {} {}", init, t, current, combiner);
			current = init ? t : combiner.combine(current, t);
//			log.error("current {} ", current);
			forEach(listeners, l->l.update(t, init));
		}
	}
}