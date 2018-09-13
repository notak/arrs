package utils.arrays;

import static org.junit.Assert.*;
import static utils.arrays.Bytes.*;

import java.util.Arrays;

import org.junit.Test;

import utils.arrays.Bytes;
import utils.arrays.Ints;
import utils.arrays.Objs;

public class BytesTest {
	@Test
	public void testStreamGroups() {
		byte[] b2 = { 0x01, (byte)0xFF, 0x45, 0x23, 0x24, 0x34, 0x56 };
		byte[][] g = streamGroups(3, b2).toArray(i->new byte[i][]);
		assertArrayEquals(Arrays.copyOfRange(b2, 0, 3), g[0]);
		assertArrayEquals(Arrays.copyOfRange(b2, 3, 6), g[1]);
		assertEquals(0, streamGroups(3, Bytes.EMPTY).count());
	}


	@Test
	public void testGettingCoverageTo100() {
		new Bytes();
	}

	byte[] i = { -9, 8, 51, 3 };
	String[] s = { "-9", "8", "51", "3" };
	byte[] iPlusOne = { -8, 9, 52, 4 };
	byte[] i9 = { -9 };
	byte[] iNo8 = { -9, 51, 3 };
	
	@Test
	public void testCreate() {
		assertArrayEquals(i, toArray((byte)-9, (byte)8, (byte)51, (byte)3));
		assertArrayEquals(i9, toArray((byte)-9));
	}
	
	@Test
	public void testStream() {
		int[] iU = { Byte.toUnsignedInt((byte)-9), 8, 51, 3 };
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
		assertEquals(-9, first(i).get().byteValue());
		assertFalse(first(EMPTY).isPresent());

		assertEquals(3, last(i).get().byteValue());
		assertFalse(first(EMPTY).isPresent());

		assertEquals(51, nth(i, 2).get().byteValue());
		assertFalse(nth(i, 5).isPresent());

		assertEquals(51, nthLast(i, 1).get().byteValue());
		assertFalse(nthLast(i, 5).isPresent());
	}

	@Test
	public void testMap() {
		assertArrayEquals(iPlusOne, map(i, j->(byte)(j+1)));
		assertArrayEquals(s, mapStr(i));
		int[] ii = { -9, 8, 51, 3 };
		assertArrayEquals(ii, mapInt(i, i->i));
	}

	@Test
	public void testSubArray() {
		assertArrayEquals(toArray((byte)-9, (byte)8), subArrayLen(i, 0, 2));
		assertArrayEquals(toArray((byte)51, (byte)3), subArray(i, 2));
		assertArrayEquals(toArray((byte)8, (byte)51), subArrayLen(i, 1, 2));
		assertArrayEquals(EMPTY, subArrayLen(i, 8, 2));
	}

	@Test
	public void testSpliced() {
		assertArrayEquals(toArray((byte)51,(byte)3), spliced(i, 0, 2));
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
		assertEquals(1, indexOf(i, (byte)8));
		assertEquals(2, indexOf(i, (byte)51));
		assertEquals(-1, indexOf(EMPTY, (byte)51));

		assertArrayEquals(Ints.toArray(2, 1), 
			indexOf(i, toArray((byte)8, (byte)51)));
		assertEquals(Ints.EMPTY, indexOf(EMPTY, toArray((byte)8, (byte)51)));
	}

	@Test
	public void testContains() {
		assertTrue(contains(i, (byte)51));
		assertFalse(contains(i, (byte)-51));

		assertFalse(contains(EMPTY, (byte)51));
		assertFalse(contains(EMPTY, (byte)-51));
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
		assertArrayEquals(iNo8, remove(i, (byte)8));
		assertArrayEquals(i, remove(i, (byte)-8));
		assertEquals(EMPTY, remove(EMPTY, (byte)8));

		assertArrayEquals(iNo8, remove(i, Objs.toArray("8"), j->""+j));
		assertArrayEquals(i, remove(i, Objs.toArray("-8"), j->""+j));
		assertEquals(EMPTY, remove(EMPTY, Objs.toArray("8"), j->""+j));

		assertArrayEquals(iNo8, remove(i, toArray((byte)8, (byte)100)));
		assertArrayEquals(i, remove(i, toArray((byte)83, (byte)100)));
		assertArrayEquals(i9, remove(i, toArray((byte)8, (byte)51, (byte)3)));
		assertEquals(EMPTY, remove(EMPTY, toArray((byte)8, (byte)100)));

		assertArrayEquals(iNo8, remove(i, j->j==8));
		assertArrayEquals(i, remove(i, j->false));
		assertArrayEquals(i9, remove(i, j->j!=-9));
		assertEquals(EMPTY, remove(EMPTY, j->true));
	}

	@Test
	public void testFoldL() {
		assertEquals(53, foldl(i, (byte)0, (k, j)->(byte)(k+j)));
		assertEquals(0, foldl(EMPTY, (byte)0, (k, j)->(byte)(k+j)));

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
		assertArrayEquals(toArray((byte)-1, (byte)54), pair(i, (k, j)->(byte)(k+j)));
		assertEquals(EMPTY, pair(EMPTY, (k, j)->(byte)(k+j)));
	}

	@Test
	public void testPairLeft() {
		assertArrayEquals(
			toArray((byte)-2, (byte)-1, (byte)59, (byte)54), 
			pairLeft(i, (byte)7, (k, j)->(byte)(k+j)));
		assertArrayEquals(EMPTY, pairLeft(EMPTY, (byte)2, (k, j)->(byte)(k+j)));
	}

	@Test
	public void testFilter() {
		assertArrayEquals(iNo8, filter(i, j->j!=8));
		assertArrayEquals(i, filter(i, j->j!=1010));

		assertEquals(EMPTY, filter(EMPTY, j->j!=8));
	}

	@Test
	public void testReversed() {
		byte[] r = { 3, 51, 8, -9 };
		assertArrayEquals(r, reversed(i));
		assertEquals(EMPTY, reversed(EMPTY));
	}
	
	@Test
	public void testAppend() {
		byte[] i = { -9, 8, 5, 3 };
		byte[] j = { 4, 8, 5, 2 };
		byte[] i8 = { -9, 8, 5, 3, 8 };
		byte[] i88 = { -9, 8, 5, 3, 88 };
		byte[] ij = { -9, 8, 5, 3, 4, 8, 5, 2 };
		byte[] iuj = { -9, 8, 5, 3, 4, 2 };
		assertArrayEquals(ij, append(i, j));
		assertArrayEquals(iuj, union(i, j));

		assertArrayEquals(i8, append(i, (byte)8));
		assertArrayEquals(i, union(i, (byte)8));
		assertArrayEquals(i88, union(i, (byte)88));
		
		assertArrayEquals(j, append(EMPTY, j));
		assertArrayEquals(toArray((byte)8), append(EMPTY, (byte)8));
	}
}
