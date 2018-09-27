package utils.arrays;

import static org.junit.Assert.*;
import static utils.arrays.Ints.*;

import org.junit.Test;

import utils.arrays.Ints;
import utils.arrays.Objs;

public class IntsTest {

	@Test
	public void testGettingCoverageTo100() {
		new Ints();
	}

	@Test
	public void testToArray() {
	}

	int[] i = { -9, 8, 50001, 3 };
	String[] s = { "-9", "8", "50001", "3" };
	int[] iPlusOne = { -8, 9, 50002, 4 };
	int[] i9 = { -9 };
	int[] iNo8 = { -9, 50001, 3 };
	
	@Test
	public void testCreate() {
		assertArrayEquals(i, toArray(-9, 8, 50001, 3));
		assertArrayEquals(i9, toArray(-9));
	}
	
	@Test
	public void testForEach() {
		int[] sum = { 0 };
		forEach(i, j->sum[0]+=j);
		assertEquals(50003, sum[0]);
	}
	
	@Test
	public void testFirstLastNthLthLast() {
		assertEquals(-9, first(i).getAsInt());
		assertFalse(first(EMPTY).isPresent());

		assertEquals(3, last(i).getAsInt());
		assertFalse(first(EMPTY).isPresent());

		assertEquals(50001, nth(i, 2).getAsInt());
		assertFalse(nth(i, 5).isPresent());

		assertEquals(50001, nthLast(i, 1).getAsInt());
		assertFalse(nthLast(i, 5).isPresent());
	}

	@Test
	public void testMap() {
		assertArrayEquals(iPlusOne, map(i, j->j+1));
//		assertArrayEquals(
//			Arrays.stream(iPlusOne).mapToLong(i->i).toArray(), 
//			mapLong(i, j->j+1));
		assertArrayEquals(s, mapStr(i));
	}

	@Test
	public void testSubArray() {
		assertArrayEquals(toArray(-9, 8), subArrayLen(i, 0, 2));
		assertArrayEquals(toArray(50001, 3), subArray(i, 2));
		assertArrayEquals(toArray(8, 50001), subArrayLen(i, 1, 2));
		assertArrayEquals(EMPTY, subArrayLen(i, 8, 2));
	}

	@Test
	public void testSpliced() {
		assertArrayEquals(toArray(50001,3), spliced(i, 0, 2));
		assertArrayEquals(iNo8, spliced(i, 1, 1));
		assertTrue(empty(spliced(EMPTY, 1, 1)));
	}

	@Test
	public void testStringStruff() {
		assertArrayEquals("streamAsString",
			s, streamAsString(i).toArray(String[]::new));

		assertEquals("join", "-9, 8, 50001, 3", join(", ", i));
	}

	@Test
	public void testIndexOf() {
		assertEquals(1, indexOf(i, 8));
		assertEquals(2, indexOf(i, 50001));
		assertEquals(-1, indexOf(EMPTY, 50001));

		assertArrayEquals(toArray(2, 1), indexOf(i, toArray(8, 50001)));
		assertArrayEquals(EMPTY, indexOf(EMPTY, toArray(8, 50001)));
	}

	@Test
	public void testContains() {
		assertTrue(contains(i, 50001));
		assertFalse(contains(i, -50001));

		assertFalse(contains(EMPTY, 50001));
		assertFalse(contains(EMPTY, -50001));
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
		assertArrayEquals(i9, remove(i, toArray(8, 50001, 3)));
		assertEquals(EMPTY, remove(EMPTY, toArray(8, 100102)));

		assertArrayEquals(iNo8, remove(i, j->j==8));
		assertArrayEquals(i, remove(i, j->false));
		assertArrayEquals(i9, remove(i, j->j!=-9));
		assertEquals(EMPTY, remove(EMPTY, j->true));
	}

//	@Test
//	public void testUnionMap() {
//		int[] all = { 1, 3, 4, 12, 15 }; 
//		int[] a = { 1, 2, 3, 4 }; 
//		int[] b = { 12, 15 }; 
//		assertArrayEquals(all, unionMapLong(a, b, s->s%10));
//		assertArrayEquals(all, unionMap(a, b, s->(int)s%10));
//		assertArrayEquals(all, unionMapObj(a, b, s->s%10));
//
//		assertArrayEquals(i, unionMapLong(i, null, s->s+1));
//		assertArrayEquals(i, unionMap(i, null, s->(int)s+1));
//		assertArrayEquals(i, unionMapObj(i, null, s->s+1));
//
//		assertArrayEquals(i, unionMapLong(null, i, s->s+1));
//		assertArrayEquals(i, unionMap(null, i, s->(int)s+1));
//		assertArrayEquals(i, unionMapObj(null, i, s->s+1));
//
//		assertArrayEquals(toArray(
//			65548, 65546, 65544, 65542, 65540, 65538, 
//			73742, 73740, 73739, 73737, 73735, 73733, 73731, 73728, 
//			65551, 65536, 74254, 74252, 74250, 74248, 74246, 74244, 74243, 74240
//		), unionMap(
//			toArray(
//				65550, 65548, 65546, 65544, 65542, 65540, 65538, 65537, 
//				73742, 73740, 73739, 73737, 73735, 73733, 73731, 73728
//			),
//			toArray(
//				65551, 65536, 
//				74254, 74252, 74250, 74248, 74246, 74244, 74243, 74240
//			),
//			s->s & ~1
//		));
//	}
//
	@Test
	public void testFoldL() {
		assertEquals(50003, foldl(i, 0, (k, j)->k+j));
		assertEquals(0, foldl(EMPTY, 0, (k, j)->k+j));

		assertEquals("-98500013", foldl(i, "", (k, j)->k+j));
		assertEquals("", foldl(EMPTY, "", (k, j)->k+j));
	}

	@Test
	public void testSum() {
		assertEquals(50003, sum(i));
		assertEquals(0, sum(EMPTY));
	}

	@Test
	public void testPair() {
		assertArrayEquals(toArray(-1, 50004), pair(i, (k, j)->k+j));
		assertArrayEquals(EMPTY, pair(EMPTY, (k, j)->k+j));
	}

	@Test
	public void testPairLeft() {
		assertArrayEquals(
			toArray(-2, -1, 50009, 50004), pairLeft(i, 7, (k, j)->k+j));
		assertArrayEquals(EMPTY, pairLeft(EMPTY, 7, (k, j)->k+j));
	}

	@Test
	public void testFilter() {
		assertArrayEquals(iNo8, filter(i, j->j!=8));
		assertArrayEquals(i, filter(i, j->j!=1010));

		assertEquals(EMPTY, filter(EMPTY, j->j!=8));
	}

	@Test
	public void testReversed() {
		int[] r = { 3, 50001, 8, -9 };
		assertArrayEquals(r, reversed(i));
		assertEquals(EMPTY, reversed(EMPTY));
	}
	
	@Test
	public void testAppend() {
		int[] i = { -9, 8, 5, 3 };
		int[] j = { 4, 8, 5, 2 };
		assertArrayEquals(toArray(-9, 8, 5, 3, 4, 8, 5, 2), append(i, j));
		assertArrayEquals(toArray(-9, 8, 5, 3, 4, 2), union(i, j));

		assertArrayEquals(toArray(-9, 8, 5, 3, 8), append(i, 8));
		assertArrayEquals(toArray(-9, 8, 5, 3), union(i, 8));
		assertArrayEquals(toArray(-9, 8, 5, 3, 880000), union(i, 880000));
		
		assertArrayEquals(j, append(EMPTY, j));
		assertArrayEquals(toArray(8), append(EMPTY, 8));
	}
}
