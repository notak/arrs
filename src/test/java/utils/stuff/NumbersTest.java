package utils.stuff;

import static org.junit.Assert.*;
import static utils.arrays.Ints.toArray;
import static utils.arrays.Objs.toArray;
import static utils.stuff.Numbers.*;

import org.junit.Test;

public class NumbersTest {

	@Test
	public void testGettingCoverageTo100() {
		new Numbers();
	}
	
	@Test
	public void testBetween() {
		assertTrue(between(2,1,3));
		assertTrue(between(1,1,3));
		assertFalse(between(3,1,3));

		assertTrue(between(-3,-3,-2));
		assertFalse(between(-2,-3,-2));
	}

	@Test
	public void testLengthBetween() {
		assertTrue(lengthBetween("AB", 1, 3));
		assertTrue(lengthBetween("AB", 2, 3));
		assertFalse(lengthBetween("AB", 3, 4));
		assertFalse(lengthBetween("AB", 1, 2));
	}

	@Test
	public void testParseInt() {
		assertEquals(9, (int)parseInt("9").get());
		assertFalse(parseInt("A9").isPresent());
		assertFalse(parseInt("9A").isPresent());
		assertEquals(-9, (int)parseInt("-9").get());
		assertFalse(parseInt("9.4").isPresent());
		assertFalse(parseInt(null).isPresent());
	}

	@Test
	public void testParseDouble() {
		assertEquals(9, (double)parseDouble("9").get(), 0.00001);
		assertFalse(parseDouble("A9").isPresent());
		assertFalse(parseDouble("9A").isPresent());
		assertEquals(-9, (double)parseDouble("-9").get(), 0.00001);
		assertEquals(9.4, (double)parseDouble("9.4").get(), 0.00001);
		assertFalse(parseDouble(null).isPresent());
	}

	@Test
	public void testParseLong() {
		assertEquals(9, (long)parseLong("9").get());
		assertFalse(parseLong("A9").isPresent());
		assertFalse(parseLong("9A").isPresent());
		assertEquals(-9, (long)parseLong("-9").get());
		assertFalse(parseLong("9.4").isPresent());
		assertFalse(parseLong(null).isPresent());
	}

	@Test
	public void testLongOr0() {
		assertEquals(9L, longOr0("9"));
		assertEquals(0L, longOr0("A9"));
		assertEquals(0L, longOr0("9A"));
		assertEquals(-9L, longOr0("-9"));
		assertEquals(0L, longOr0("9.4"));
		assertEquals(0L, longOr0(null));
	}

	@Test
	public void testIntOr0String() {
		assertEquals(9, intOr0("9"));
		assertEquals(0, intOr0("A9"));
		assertEquals(0, intOr0("9A"));
		assertEquals(-9, intOr0("-9"));
		assertEquals(0, intOr0("9.4"));
		assertEquals(0, intOr0((String)null));

		int[] na = toArray(-9, 8, 5, 3);
		String[] sa = toArray("-9", "8", "5", "3");
		assertArrayEquals(na, intOr0(sa));
	}

	@Test
	public void testIntOrMin() {
		assertEquals(9, intOrMin("9"));
		assertEquals(Integer.MIN_VALUE, intOrMin("A9"));
		assertEquals(Integer.MIN_VALUE, intOrMin("9A"));
		assertEquals(-9, intOrMin("-9"));
		assertEquals(Integer.MIN_VALUE, intOrMin("9.4"));
		assertEquals(Integer.MIN_VALUE, intOrMin(null));
	}

	@Test
	public void testDoubleOr0() {
		assertEquals(9, (double)doubleOr0("9"), 0.00001);
		assertEquals(0, doubleOr0("A9"), 0.00001);
		assertEquals(0, doubleOr0("9A"), 0.00001);
		assertEquals(-9, (double)doubleOr0("-9"), 0.00001);
		assertEquals(9.4, (double)doubleOr0("9.4"), 0.00001);
		assertEquals(0, doubleOr0(null), 0.00001);
	}

}
