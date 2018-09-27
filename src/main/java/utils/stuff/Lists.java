package utils.stuff;

import static java.util.stream.Collectors.joining;
import static utils.arrays.Objs.contains;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import utils.arrays.Ints;

public class Lists {
	public static <I> List<I> filtered(List<I> in, Predicate<I> mapper) {
		return in.stream()
			.filter(mapper)
			.collect(Collectors.toList());
	}
	
	public static <I> List<I> without(List<I> in, Collection<I> exclude) {
		return in.stream()
			.filter(i->!exclude.contains(i))
			.collect(Collectors.toList());
	}
	
	public static List<Integer> without(List<Integer> in, int[] exclude) {
		return in.stream()
			.filter(i->!Ints.contains(exclude, i))
			.collect(Collectors.toList());
	}
	
	public static <I> List<I> without(List<I> in, I[] exclude) {
		return in.stream()
			.filter(i->!contains(exclude, i))
			.collect(Collectors.toList());
	}
	
	@SafeVarargs
	public static <T> List<T> toList(T... in) { return Arrays.asList(in); }
	
	public static int[] toIntArray(List<Integer> l) {
		return l.stream().mapToInt(i->i).toArray();
	}
	
	public static long[] toLongArray(List<Long> l) {
		return l.stream().mapToLong(i->i).toArray();
	}
	
	/** Optionally get the first element, returning empty for an empty array */
	public static <T> Optional<T> first(List<T> in) { 
		return Lists.nth(in, 0);
	}
	
	/** Optionally get the nth element, returns empty for array len<n+1 */
	public static <T> Optional<T> nth(List<T> in, int n) { 
		return in.size()<=n ? Optional.empty() : Optional.of(in.get(n));
	}
	
	public static <A> String asString(
		Collection<A> list,
		Function<A, String> valueMapper,
		String separator
	) {
		return list.stream().map(valueMapper)
			.collect(joining(separator));
	}
	
	public static <T> String join(Collection<T>in, String glue) {
		return Lists.join(in.stream(), glue);
	}
	
	public static <T> String join(Stream<T>in, String glue) {
		return in
			.map(Object::toString)
			.collect(joining(glue));
	}
	
	public static <T> String join(IntStream in, String glue) {
		return join(in.mapToObj(i->""+i), glue);
	}
	
	public static <T> String join(String glue, Stream<T>in) {
		return join(in, glue);
	}
	
	@SafeVarargs
	public static <T> Optional<T> firstOf(Supplier<Optional<T>>... in) {
		return Arrays.stream(in)
			.map(s->s.get())
			.filter(s->s.isPresent())
			.findFirst().orElseGet(Optional::empty);
	}
	
	public static <U> Stream<U> stream(Iterable<U> us) {
		return StreamSupport.stream(us.spliterator(), false);
	}
}
