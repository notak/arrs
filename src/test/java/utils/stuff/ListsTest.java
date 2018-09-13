package utils.stuff;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import utils.arrays.Ints;

import static utils.arrays.Ints.toArray;
import static utils.arrays.Longs.toArray;
import static utils.arrays.Objs.toArray;
import static utils.stuff.Lists.*;

public class ListsTest {
	@Test
	public void testGettingCoverageTo100() {
		new Lists();
	}

	@Test
	public void testToList() {
		List<String> l = toList("I", "am", "not", "the", "doctor");
		assertEquals("not", l.get(2));
		assertEquals("I am not the doctor", 
			l.stream().collect(Collectors.joining(" ")));
	}

	@Test
	public void testJoin() {
		assertEquals(
			"Collection<T> -> String",
			"I am not the doctor", 
			join(toList("I", "am", "not", "the", "doctor"), " "));
		assertEquals(
			"Stream<T> -> String",
			"I am not the doctor", 
			join(toList("I", "am", "not", "the", "doctor").stream(), " "));
		assertEquals(
				"Stream<T> -> String (params reversed",
				"I am not the doctor", 
				join(" ", toList("I", "am", "not", "the", "doctor").stream()));
		assertEquals("IntStream -> String", 
				"-9, 8, 5, 3", join(IntStream.of(-9, 8, 5, 3), ", "));
	}

	@Test
	public void testFiltered() {
		assertArrayEquals(
			toArray("I", "am", "the", "doctor"),
			filtered(
				toList("I", "am", "not", "the", "doctor"), 
				i->!"not".equals(i)
			).toArray(new String[0])
		);
	}

	@Test
	public void testToArray() {
		assertArrayEquals(
			toArray(2324, 49494, 1, 45),
			toIntArray(toList(2324, 49494, 1, 45))
		);

		assertArrayEquals(
			toArray(2324L, 49494L, 1L, 45L),
			toLongArray(toList(2324L, 49494L, 1L, 45L))
		);
	}

	@Test
	public void testWithout() {
		assertArrayEquals(
			toArray("I", "am", "the", "doctor"),
			without(
				toList("I", "am", "not", "the", "doctor"),
				toList("not")
			).toArray(new String[0])
		);

		assertArrayEquals(
			toArray("I", "am", "the", "doctor"),
			without(
				toList("I", "am", "not", "the", "doctor"),
				toArray("not")
			).toArray(new String[0])
		);

		assertArrayEquals(
			Ints.toArray(2324, 1),
			toIntArray(without(
				toList(2324, 49494, 1, 45),
				Ints.toArray(49494, 45)
			))
		);
	}

	@Test
	public void testFirstOf() {
		Optional<String> empty = Optional.empty();
		Optional<String> full = Optional.of("full");
		Optional<String> fuller = Optional.of("fuller");
		assertEquals(full, Lists.firstOf(()->empty, ()->full, ()->fuller));
		assertEquals(fuller, Lists.firstOf(()->empty, ()->fuller, ()->full));
		assertEquals(empty, Lists.firstOf(()->empty, ()->empty, ()->empty));
	}

	@Test
	public void testAsStringCollectionOfAFunctionOfAStringString() {
		List<Integer> l = Lists.toList(-9, 8, 5, 3);
		assertEquals(" -9, 8, 5, 3",Lists.asString(l, i->" "+i, ","));
	}
}
