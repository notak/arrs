package utils.arrays;

import static org.junit.Assert.*;
import static utils.arrays.Shorts.*;

import java.util.Arrays;

import org.junit.Test;

import utils.arrays.Shorts;
import utils.arrays.Ints;
import utils.arrays.Objs;

public class ShortsTest {
	@Test
	public void testStreamGroups() {
		short[] b2 = { 0x01, (short)0xFF, 0x45, 0x23, 0x24, 0x34, 0x56 };
		short[][] g = streamGroups(3, b2).toArray(i->new short[i][]);
		assertArrayEquals(Arrays.copyOfRange(b2, 0, 3), g[0]);
		assertArrayEquals(Arrays.copyOfRange(b2, 3, 6), g[1]);
		assertEquals(0, streamGroups(3, Shorts.EMPTY).count());
	}


	@Test
	public void testGettingCoverageTo100() {
		new Shorts();
	}

	short[] i = { -9, 8, 51, 3 };
	String[] s = { "-9", "8", "51", "3" };
	short[] iPlusOne = { -8, 9, 52, 4 };
	short[] i9 = { -9 };
	short[] iNo8 = { -9, 51, 3 };
	
	@Test
	public void testCreate() {
		assertArrayEquals(i, toArray((short)-9, (short)8, (short)51, (short)3));
		assertArrayEquals(i9, toArray((short)-9));
	}
	
	@Test
	public void testStream() {
		int[] iU = { Short.toUnsignedInt((short)-9), 8, 51, 3 };
		int[] ii = { -9, 8, 51, 3 };
		assertArrayEquals(ii, stream(i).toArray());
		assertArrayEquals(iU, unsignedStream(i).toArray());
	}
	
	@Test
	public void testForEach() {
		int[] sum = { 0 };
		forEach(i, j->sum[0]+=j);
		assertEquals(53, sum[0]);
	}
	
	@Test
	public void testFirstLastNthLthLast() {
		assertEquals(-9, first(i).get().shortValue());
		assertFalse(first(EMPTY).isPresent());

		assertEquals(3, last(i).get().shortValue());
		assertFalse(first(EMPTY).isPresent());

		assertEquals(51, nth(i, 2).get().shortValue());
		assertFalse(nth(i, 5).isPresent());

		assertEquals(51, nthLast(i, 1).get().shortValue());
		assertFalse(nthLast(i, 5).isPresent());
	}

	@Test
	public void testMap() {
		assertArrayEquals(iPlusOne, map(i, j->(short)(j+1)));
		assertArrayEquals(s, mapStr(i));
		int[] ii = { -9, 8, 51, 3 };
		assertArrayEquals(ii, mapInt(i, i->i));
	}

	@Test
	public void testSubArray() {
		assertArrayEquals(toArray((short)-9, (short)8), subArrayLen(i, 0, 2));
		assertArrayEquals(toArray((short)51, (short)3), subArray(i, 2));
		assertArrayEquals(toArray((short)8, (short)51), subArrayLen(i, 1, 2));
		assertArrayEquals(EMPTY, subArrayLen(i, 8, 2));
	}

	@Test
	public void testSpliced() {
		assertArrayEquals(toArray((short)51,(short)3), spliced(i, 0, 2));
		assertArrayEquals(iNo8, spliced(i, 1, 1));
	}

	@Test
	public void testStringStruff() {
		assertArrayEquals("streamAsString",
			s, streamAsString(i).toArray(String[]::new));

		assertEquals("join", "-9, 8, 51, 3", join(", ", i));
	}

	@Test
	public void testIndexOf() {
		assertEquals(1, indexOf(i, (short)8));
		assertEquals(2, indexOf(i, (short)51));
		assertEquals(-1, indexOf(EMPTY, (short)51));

		assertArrayEquals(Ints.toArray(2, 1), 
			indexOf(i, toArray((short)8, (short)51)));
		assertEquals(Ints.EMPTY, indexOf(EMPTY, toArray((short)8, (short)51)));
	}

	@Test
	public void testContains() {
		assertTrue(contains(i, (short)51));
		assertFalse(contains(i, (short)-51));

		assertFalse(contains(EMPTY, (short)51));
		assertFalse(contains(EMPTY, (short)-51));
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
		assertArrayEquals(iNo8, remove(i, (short)8));
		assertArrayEquals(i, remove(i, (short)-8));
		assertEquals(EMPTY, remove(EMPTY, (short)8));

		assertArrayEquals(iNo8, remove(i, Objs.toArray("8"), j->""+j));
		assertArrayEquals(i, remove(i, Objs.toArray("-8"), j->""+j));
		assertEquals(EMPTY, remove(EMPTY, Objs.toArray("8"), j->""+j));

		assertArrayEquals(iNo8, remove(i, toArray((short)8, (short)100)));
		assertArrayEquals(i, remove(i, toArray((short)83, (short)100)));
		assertArrayEquals(i9, remove(i, toArray((short)8, (short)51, (short)3)));
		assertEquals(EMPTY, remove(EMPTY, toArray((short)8, (short)100)));

		assertArrayEquals(iNo8, remove(i, j->j==8));
		assertArrayEquals(i, remove(i, j->false));
		assertArrayEquals(i9, remove(i, j->j!=-9));
		assertEquals(EMPTY, remove(EMPTY, j->true));
	}

	@Test
	public void testFoldL() {
		assertEquals(53, foldl(i, (short)0, (k, j)->(short)(k+j)));
		assertEquals(0, foldl(EMPTY, (short)0, (k, j)->(short)(k+j)));

		assertEquals("-98513", foldl(i, "", (k, j)->k+j));
		assertEquals("", foldl(EMPTY, "", (k, j)->k+j));
	}

	@Test
	public void testSum() {
		assertEquals(53, sum(i));
		assertEquals(0, sum(EMPTY));
	}

	@Test
	public void testPair() {
		assertArrayEquals(toArray((short)-1, (short)54), pair(i, (k, j)->(short)(k+j)));
		assertEquals(EMPTY, pair(EMPTY, (k, j)->(short)(k+j)));
	}

	@Test
	public void testPairLeft() {
		assertArrayEquals(
			toArray((short)-2, (short)-1, (short)59, (short)54), 
			pairLeft(i, (short)7, (k, j)->(short)(k+j)));
		assertArrayEquals(EMPTY, pairLeft(EMPTY, (short)2, (k, j)->(short)(k+j)));
	}

	@Test
	public void testFilter() {
		assertArrayEquals(iNo8, filter(i, j->j!=8));
		assertArrayEquals(i, filter(i, j->j!=1010));

		assertEquals(EMPTY, filter(EMPTY, j->j!=8));
	}

	@Test
	public void testReversed() {
		short[] r = { 3, 51, 8, -9 };
		assertArrayEquals(r, reversed(i));
		assertEquals(EMPTY, reversed(EMPTY));
	}
	
	@Test
	public void testAppend() {
		short[] i = { -9, 8, 5, 3 };
		short[] j = { 4, 8, 5, 2 };
		short[] i8 = { -9, 8, 5, 3, 8 };
		short[] i88 = { -9, 8, 5, 3, 88 };
		short[] ij = { -9, 8, 5, 3, 4, 8, 5, 2 };
		short[] iuj = { -9, 8, 5, 3, 4, 2 };
		assertArrayEquals(ij, append(i, j));
		assertArrayEquals(iuj, union(i, j));

		assertArrayEquals(i8, append(i, (short)8));
		assertArrayEquals(i, union(i, (short)8));
		assertArrayEquals(i88, union(i, (short)88));
		
		assertArrayEquals(j, append(EMPTY, j));
		assertArrayEquals(toArray((short)8), append(EMPTY, (short)8));
	}
}
