package utils.arrays;

import static org.junit.Assert.*;
import static utils.arrays.Objs.*;

import java.util.Arrays;

import org.junit.Test;

public class ObjsTest {
	@Test
	public void testStreamGroups() {
		String[] s = toArray("A", "B", "C", "D", "E", "F", "G", "H");
		String[][] ss = streamGroups(3, s).toArray(i->new String[i][]);
		assertArrayEquals(Arrays.copyOfRange(s, 0, 3), ss[0]);
		assertArrayEquals(Arrays.copyOfRange(s, 3, 6), ss[1]);
		assertEquals(0, streamGroups(3, new String[0]).count());
	}


	@Test
	public void testGettingCoverageTo100() {
		new Objs();
	}

	@Test
	public void testToArray() {
	}

	String[] i = { "I", "am", "not", "the", "doctor" };
	String[] iPluse = { "Ie", "ame", "note", "thee", "doctore" };
	String[] i9 = { "-9" };
	String[] iNoNot = { "I", "am", "the", "doctor" };
	String[] EMPTY = { };
	
	@Test
	public void testCreate() {
		assertArrayEquals(i, "I am not the doctor".split(" "));
		assertArrayEquals(i9, toArray("-9"));
	}
	
	@Test
	public void testForEach() {
		String[] sum = { "" };
		forEach(i, j->sum[0]+=j+" ");
		assertEquals("I am not the doctor ", sum[0]);
	}
	
	@Test
	public void testFirstLastNthLthLast() {
		assertEquals("I", first(i).get());
		assertFalse(first(EMPTY).isPresent());

		assertEquals("doctor", last(i).get());
		assertFalse(first(EMPTY).isPresent());

		assertEquals("not", nth(i, 2).get());
		assertFalse(nth(i, 6).isPresent());

		assertEquals("not", nthLast(i, 2).get());
		assertFalse(nthLast(i, 6).isPresent());
	}

	@Test
	public void testMap() {
		assertArrayEquals(
			Ints.toArray('I', 'a', 'n', 't', 'd'), mapInt(i, j->j.charAt(0)));
		assertArrayEquals(iPluse, mapStr(i, j->j+"e"));
		
		assertArrayEquals(toArray("DOGE", "FISHE"), 
			withMap(toArray("DOG", "FISH"), d->d+"E"));
		assertTrue(empty(withMap(EMPTY, d->d+"E")));
	}

	@Test
	public void testSubArray() {
		assertArrayEquals(toArray("I", "am"), subArrayLen(i, 0, 2));
		assertArrayEquals(toArray("not", "the", "doctor"), subArray(i, 2));
		assertArrayEquals(toArray("am", "not"), subArrayLen(i, 1, 2));
		assertArrayEquals(EMPTY, subArrayLen(i, 8, 2));
	}

	@Test
	public void testSpliced() {
		assertArrayEquals(toArray("not", "the", "doctor"), spliced(i, 0, 2));
		assertArrayEquals(iNoNot, spliced(i, 2, 1));
	}

	@Test
	public void testSplicedContent() {
		assertArrayEquals(
			toArray("I", "nearly", "the", "doctor"), 
			spliced(i, 1, 2, toArray("nearly")));
		assertArrayEquals(iNoNot, spliced(i, 2, 1));
	}

	@Test
	public void testStringStruff() {
		assertArrayEquals("streamAsString",
			i, streamAsString(i).toArray(String[]::new));

		assertEquals("join", "I am not the doctor", join(" ", i));
	}

	@Test
	public void testIndexOf() {
		assertEquals(2, indexOf(i, "not"));
		assertEquals(3, indexOf(i, "the"));
		assertEquals(-1, indexOf(EMPTY, "the"));

		assertArrayEquals(Ints.toArray(3, 2), indexesOf(i, toArray("not", "the")));
		assertTrue(Ints.empty(indexesOf(EMPTY, toArray(8, "the"))));
	}

	@Test
	public void testContains() {
		assertTrue(contains(i, "am"));
		assertFalse(contains(i, "kitten"));

		assertFalse(contains(EMPTY, "am"));
		assertFalse(contains(EMPTY, "kitten"));
	}

	@Test
	public void testOkPos() {
		assertFalse(okPos(i, -1));
		assertTrue(okPos(i, 0));
		assertTrue(okPos(i, 2));
		assertTrue(okPos(i, 4));
		assertFalse(okPos(i, 5));

		assertFalse(okPos(EMPTY, -1));
		assertFalse(okPos(EMPTY, 0));
		assertFalse(okPos(EMPTY, 2));
		assertFalse(okPos(EMPTY, 4));
		assertFalse(okPos(EMPTY, 5));
	}

	@Test
	public void testEmpty() {
		assertFalse(empty(i));
		assertTrue(empty(EMPTY));
	}

	@Test
	public void testRemove() {
		assertArrayEquals(iNoNot, remove(i, "not"));
		assertArrayEquals(i, remove(i, "note"));
		assertArrayEquals(EMPTY, remove(EMPTY, "not"));

		assertArrayEquals(iNoNot, remove(i, Objs.toArray("dnot"), j->"d"+j));
		assertArrayEquals(i, remove(i, Objs.toArray("enot"), j->"d"+j));
		assertArrayEquals(EMPTY, remove(EMPTY, Objs.toArray("dnot"), j->"d"+j));

		assertArrayEquals(
			toArray("I", "not", "doctor"), 
			remove(i, Ints.toArray('a', 't'), j->j.charAt(0))
		);

		assertArrayEquals(iNoNot, remove(i, toArray("dnot", "enot"), j->"d"+j));
		assertArrayEquals(i, remove(i, toArray("enot", "fnot")));
		assertArrayEquals(toArray("am"), remove(i, toArray("I", "not", "the", "doctor")));
		assertArrayEquals(EMPTY, remove(EMPTY, toArray("dnot", "enot")));

		assertArrayEquals(iNoNot, remove(i, j->"not".equals(j)));
		assertArrayEquals(i, remove(i, j->false));
		assertArrayEquals(toArray("not"), remove(i, j->!"not".equals(j)));
		assertArrayEquals(EMPTY, remove(EMPTY, j->true));
	}

	@Test
	public void testUnionMap() {
		Integer[] all = toArray(1, 3, 4, 12, 15);
		assertArrayEquals(all, 
			unionMap(toArray(1, 2, 3, 4), toArray(12, 15), s->s%10)
		);
		assertArrayEquals(all, 
			unionMapInt(toArray(1, 2, 3, 4), toArray(12, 15), s->s%10)
		);
		assertArrayEquals(all, 
			unionMapLong(toArray(1, 2, 3, 4), toArray(12, 15), s->s%10)
		);
		
		var EMPTY = new Integer[0];

		assertArrayEquals(all, unionMap(all, EMPTY, s->s+1));
		assertArrayEquals(all, unionMap(EMPTY, all, s->s+1));

		assertArrayEquals(all, unionMapInt(all, EMPTY, s->s+1));
		assertArrayEquals(all, unionMapInt(EMPTY, all, s->s+1));

		assertArrayEquals(all, unionMapLong(all, EMPTY, s->s+1));
		assertArrayEquals(all, unionMapLong(EMPTY, all, s->s+1));
	}

	@Test
	public void testFoldL() {
		assertEquals("Iamnotthedoctor", foldl(i, "", (k, j)->k+j));
		assertEquals("", foldl(EMPTY, "", (k, j)->k+j));
		assertEquals("", foldl(null, "", (k, j)->k+j));

//		assertEquals("-9, 8, 50001, 3", foldl(i, "", (k, j)->k+j));
//		assertEquals(0, foldl(EMPTY, "", (k, j)->k+j));
//		assertEquals(0, foldl(null, "", (k, j)->k+j));
	}

	@Test
	public void testPair() {
		assertArrayEquals(
			toArray("Iam", "notthe"), pair(i, (k, j)->k+j, String[]::new));
		assertArrayEquals(
			EMPTY, pair(EMPTY, (k, j)->k+j, String[]::new));
	}

	@Test
	public void testPairLeft() {
		assertArrayEquals(
			toArray("I", "Iam", "amnot", "notthe", "thedoctor"), 
			pairLeft(i, "", (k, j)->k+j, String[]::new));
		assertArrayEquals(
			EMPTY, pairLeft(EMPTY, "", (k, j)->k+j, String[]::new));
	}

	@Test
	public void testFilter() {
		assertArrayEquals(iNoNot, filter(i, j->!"not".equals(j)));
		assertArrayEquals(i, filter(i, j->!"pig".equals(j)));

		assertTrue(empty(filter(EMPTY, j->!"pig".equals(j))));
	}

	@Test
	public void testReversed() {
		assertArrayEquals(toArray("doctor", "the", "not", "am", "I"), reversed(i));
		assertTrue(empty(reversed(EMPTY)));
	}
	
	@Test
	public void testAppend() {
		assertArrayEquals(
			toArray("I", "am", "not", "the", "doctor", "am", "the", "undertaker"), 
			append(i, toArray("am", "the", "undertaker")));
		assertArrayEquals(
			toArray("I", "am", "not", "the", "doctor", "undertaker"), 
			union(i, toArray("am", "the", "undertaker")));

		assertArrayEquals(
			toArray("I", "am", "not", "the", "doctor", "not"), 
			append(i, "not"));
		assertArrayEquals(
			toArray("not", "I", "am", "not", "the", "doctor"), 
			append("not", i));
		assertArrayEquals(
			toArray("I", "am", "not", "the", "doctor"), 
			union(i, "not"));
		assertArrayEquals(
			toArray("I", "am", "not", "the", "doctor", "who"), 
			union(i, "who"));
		
		assertArrayEquals(i, append(EMPTY, i));
		assertArrayEquals(toArray("not"), append(EMPTY, toArray("not")));

		assertArrayEquals(i, union(EMPTY, i));
		assertArrayEquals(i, union(i, EMPTY));
		assertArrayEquals(toArray("not"), union(EMPTY, toArray("not")));
}
}
