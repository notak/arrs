package utils.arrays;

import static org.junit.Assert.*;
import static utils.arrays.Longs.*;

import org.junit.Test;

public class LongsTest {

	@Test
	public void testGettingCoverageTo100() {
		new Longs();
	}

	@Test
	public void testToArray() {
	}

	long[] i = { -9, 8, 5000000001l, 3 };
	String[] s = { "-9", "8", "5000000001", "3" };
	long[] iPlusOne = { -8, 9, 5000000002l, 4 };
	long[] i9 = { -9 };
	long[] iNo8 = { -9, 5000000001l, 3 };
	
	@Test
	public void testCreate() {
		assertArrayEquals(i, toArray(-9, 8, 5000000001l, 3));
		assertArrayEquals(i9, toArray(-9));
	}
	
	@Test
	public void testForEach() {
		long[] sum = { 0 };
		forEach(i, j->sum[0]+=j);
		assertEquals(5000000003l, sum[0]);
	}
	
	@Test
	public void testFirstLastNthLthLast() {
		assertEquals(-9, first(i).getAsLong());
		assertFalse(first(EMPTY).isPresent());

		assertEquals(3, last(i).getAsLong());
		assertFalse(first(EMPTY).isPresent());

		assertEquals(5000000001l, nth(i, 2).getAsLong());
		assertFalse(nth(i, 5).isPresent());

		assertEquals(5000000001l, nthLast(i, 1).getAsLong());
		assertFalse(nthLast(i, 5).isPresent());
	}

	@Test
	public void testMap() {
		assertArrayEquals(iPlusOne, map(i, j->j+1));
		assertArrayEquals(
			Ints.toArray(-9, 8, (int)5000000001l, 3), mapInt(i, j->(int)j));
		assertArrayEquals(s, mapStr(i));
		assertArrayEquals(s, map(i, k->""+k, String[]::new));
	}

	@Test
	public void testSubArray() {
		assertArrayEquals(toArray(-9, 8), subArrayLen(i, 0, 2));
		assertArrayEquals(toArray(5000000001l, 3), subArray(i, 2));
		assertArrayEquals(toArray(8, 5000000001l), subArrayLen(i, 1, 2));
		assertArrayEquals(EMPTY, subArrayLen(i, 8, 2));
	}

	@Test
	public void testSpliced() {
		assertArrayEquals(toArray(5000000001l,3), spliced(i, 0, 2));
		assertArrayEquals(iNo8, spliced(i, 1, 1));
	}

	@Test
	public void testStringStruff() {
		assertArrayEquals("streamAsString",
			s, streamAsString(i).toArray(String[]::new));

		assertEquals("join", "-9, 8, 5000000001, 3", join(", ", i));
	}

	@Test
	public void testIndexOf() {
		assertEquals(1, indexOf(i, 8));
		assertEquals(2, indexOf(i, 5000000001l));
		assertEquals(-1, indexOf(EMPTY, 5000000001l));

		assertArrayEquals(Ints.toArray(2, 1), indexOf(i, toArray(8l, 5000000001l)));
		assertEquals(Ints.EMPTY, indexOf(EMPTY, toArray(8l, 5000000001l)));
	}

	@Test
	public void testContains() {
		assertTrue(contains(i, 5000000001l));
		assertFalse(contains(i, -5000000001l));

		assertFalse(contains(EMPTY, 5000000001l));
		assertFalse(contains(EMPTY, -5000000001l));
	}

	@Test
	public void testOkPos() {
		assertFalse(okPos(i, -1));
		assertTrue(okPos(i, 0));
		assertTrue(okPos(i, 2));
		assertTrue(okPos(i, 3));
		assertFalse(okPos(i, 4));

		assertFalse(okPos(EMPTY, -1));
		assertFalse(okPos(EMPTY, 0));
		assertFalse(okPos(EMPTY, 2));
		assertFalse(okPos(EMPTY, 3));
		assertFalse(okPos(EMPTY, 4));
	}

	@Test
	public void testEmpty() {
		assertFalse(empty(i));
		assertTrue(empty(EMPTY));
	}

	@Test
	public void testRemove() {
		assertArrayEquals(iNo8, remove(i, 8));
		assertArrayEquals(i, remove(i, -8));
		assertEquals(EMPTY, remove(EMPTY, 8));

		assertArrayEquals(iNo8, remove(i, Objs.toArray("8"), j->""+j));
		assertArrayEquals(i, remove(i, Objs.toArray("-8"), j->""+j));
		assertEquals(EMPTY, remove(EMPTY, Objs.toArray("8"), j->""+j));

		assertArrayEquals(iNo8, remove(i, toArray(8, 100102)));
		assertArrayEquals(i, remove(i, toArray(83, 100102)));
		assertArrayEquals(i9, remove(i, toArray(8, 5000000001l, 3)));
		assertEquals(EMPTY, remove(EMPTY, toArray(8, 100102)));

		assertArrayEquals(iNo8, remove(i, j->j==8));
		assertArrayEquals(i, remove(i, j->false));
		assertArrayEquals(i9, remove(i, j->j!=-9));
		assertEquals(EMPTY, remove(EMPTY, j->true));
	}

	@Test
	public void testFoldL() {
		assertEquals(5000000003l, foldl(i, 0, (k, j)->k+j));
		assertEquals(0, foldl(EMPTY, 0, (k, j)->k+j));

		assertEquals("-9850000000013", foldl(i, "", (k, j)->k+j));
		assertEquals("", foldl(EMPTY, "", (k, j)->k+j));
	}

	@Test
	public void testSum() {
		assertEquals(5000000003l, sum(i));
		assertEquals(0, sum(EMPTY));
	}

	@Test
	public void testPair() {
		assertArrayEquals(toArray(-1, 5000000004l), pair(i, (k, j)->k+j));
		assertTrue(empty(pair(EMPTY, (k, j)->k+j)));
	}

	@Test
	public void testPairLeft() {
		assertArrayEquals(
			toArray(-2, -1, 5000000009l, 5000000004l), 
			pairLeft(i, 7, (k, j)->k+j));
		assertTrue(empty(pairLeft(EMPTY, 2, (k, j)->k+j)));
	}

	@Test
	public void testFilter() {
		assertArrayEquals(iNo8, filter(i, j->j!=8));
		assertArrayEquals(i, filter(i, j->j!=1010));

		assertEquals(EMPTY, filter(EMPTY, j->j!=8));
	}

	@Test
	public void testReversed() {
		long[] r = { 3, 5000000001l, 8, -9 };
		assertArrayEquals(r, reversed(i));
		assertEquals(EMPTY, reversed(EMPTY));
	}
	
	@Test
	public void testAppend() {
		long[] i = { -9, 8, 5, 3 };
		long[] j = { 4, 8, 5, 2 };
		assertArrayEquals(toArray(-9, 8, 5, 3, 4, 8, 5, 2), append(i, j));
		assertArrayEquals(toArray(-9, 8, 5, 3, 4, 2), union(i, j));

		assertArrayEquals(toArray(-9, 8, 5, 3, 8), append(i, 8));
		assertArrayEquals(toArray(-9, 8, 5, 3), union(i, 8));
		assertArrayEquals(toArray(-9, 8, 5, 3, 880000), union(i, 880000));
		
		assertArrayEquals(j, append(EMPTY, j));
		assertArrayEquals(toArray(8), append(EMPTY, 8));
	}
}
