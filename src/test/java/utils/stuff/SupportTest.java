package utils.stuff;

import static org.junit.Assert.*;
import static utils.stuff.Str.lSpace;
import static utils.stuff.Str.rSpace;
import static utils.stuff.Support.booleanOrFalse;
import static utils.stuff.Support.isY;
import static utils.stuff.Support.mapBoth;
import static utils.stuff.Support.notNull;
import static utils.stuff.Support.orElse;

import java.util.Optional;

import org.junit.Test;

public class SupportTest {
	@Test
	public void testGettingCoverageTo100() {
		new Support();
	}

	@Test
	public void testLSpace() {
		assertEquals(" bb", lSpace("bb"));
		assertEquals("", lSpace(""));
		assertEquals("", lSpace(null));
		assertEquals("cbb", lSpace("bb", "c"));
		assertEquals("", lSpace("", "c"));
		assertEquals("", lSpace((String)null, "c"));
		assertEquals("cbb", lSpace(Optional.of("bb"), "c"));
		assertEquals("", lSpace(Optional.empty(), "c"));
	}

	@Test
	public void testRSpace() {
		assertEquals("bb ", rSpace("bb"));
		assertEquals("", rSpace(""));
		assertEquals("", rSpace(null));
	}

	@Test
	public void testOrElse() {
		assertEquals("bb", orElse("bb"));
		assertEquals("", orElse(""));
		assertEquals("", orElse(null));

		assertEquals("bb", orElse("bb", "ee"));
		assertEquals("", orElse("", "ee"));
		assertEquals("ee", orElse(null, "ee"));

		assertEquals("bb", orElse("bb", ()->"ee"));
		assertEquals("", orElse("", ()->"ee"));
		assertEquals("ee", orElse(null, ()->"ee"));
	}

	@Test
	public void testMapBoth() {
		assertEquals(Optional.of("ab"), mapBoth(Optional.of("a"), Optional.of("b"), (a,b)->a+b));
		assertEquals(Optional.empty(), mapBoth(Optional.empty(), Optional.of("b"), (a,b)->a+b));
		assertEquals(Optional.empty(), mapBoth(Optional.of("b"), Optional.empty(), (a,b)->a+b));
	}

	@Test
	public void testNotNull() {
		assertTrue(notNull(""));
		assertFalse(notNull(null));
	}

	@Test
	public void testBooleanOrFalse() {
		assertTrue(booleanOrFalse("true"));
		assertFalse(booleanOrFalse("false"));
		assertFalse(booleanOrFalse("jam"));
		assertFalse(booleanOrFalse(null));
	}

	@Test
	public void testGetDaysFromDate() {
		assertEquals(40, Days.getDaysFromDate("1970-02-10"));
		assertEquals(Integer.MIN_VALUE, Days.getDaysFromDate("197002-10"));
	}

	@Test
	public void testGetDateFromDays() {
		assertEquals("1970-02-10", Days.getDateFromDays(40));
	}

	@Test
	public void testToGMTDate() {
		assertEquals("1970-02-10 01:00:02", Days.toUTCDate(40l*86400000l+3602000l));
	}

	@Test
	public void testIsY() {
		assertTrue(isY("Y"));
		assertFalse(isY("N"));
		assertFalse(isY("jam"));
		assertFalse(isY(null));
	}
	
	@Test
	public void testTrySleep() throws InterruptedException {
		long before = System.currentTimeMillis();
		boolean res = Support.trySleep(100);
		long after = System.currentTimeMillis();
		assertTrue("Returns true if allowed to sleep", res);
		assertTrue("Does sleep for the expected time", after-before>=100);
		
		boolean[] run = { false };
		System.out.println("before create");
		Thread t = new Thread(new Runnable() {
			public void run() {
				long before = System.currentTimeMillis();
				boolean res = Support.trySleep(100);
				long after = System.currentTimeMillis();
				assertFalse("Doesn't sleep for the expected time", after-before>=100);
				assertFalse("Returns false if interrupted", res);
				run[0] = true;
			}
		});
		t.start();
		t.interrupt();
		t.join();
		assertTrue("Interrupted tests actually ran", run[0]);
	}
}
