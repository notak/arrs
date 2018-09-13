package utils.stuff;

import static org.junit.Assert.*;
import static utils.stuff.Days.*;

import java.util.Date;

import org.junit.Test;

public class DaysTest {
	@Test
	public void testGettingCoverageTo100() {
		new Days();
	}

	@Test
	public void testDow() {
		assertEquals(6, dow(Days.getDaysFromDate("2017-09-17")));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testDomInt() {
		//everyone runs in a Gregorian calendar round here right?
		assertEquals(new Date().getDate(), dom());
	}

	@Test
	public void testDom() {
		assertEquals(17, dom(Days.getDaysFromDate("2017-09-17")));
	}

	@Test
	public void testToday() {
		assertEquals(today(), new Date().getTime()/86400000L); 
	}

	@Test
	public void testTimeOfDayHM() {
		assertEquals(timeOfDayHM()/120, hourNow() - today()*24);
	}
	
	@Test
	public void testPeriod() {
		Period[] p = Period.periods;
		for (int i=1; i<p.length; i++) {
			assertEquals(p[i-1].end, p[i].start);
		}
		assertEquals(0, p[0].start);
		assertEquals(24, p[p.length-1].end);
		assertEquals(p[2].start*120+120*24*4, p[2].startHM(4));
		assertEquals(p[2].end*120+120*24*4, p[2].endHM(4));
		assertEquals(p[0], Period.starting(p[0].start+1));
		assertEquals(p[3], Period.startingHM(p[3].start * 120));
	}
}
