package utils.stuff;

import static org.junit.Assert.*;
import static utils.arrays.Objs.append;
import static utils.arrays.Objs.toArray;

import org.junit.Test;

import utils.stuff.Emitter.Listener;
import utils.stuff.Emitter.Subs;

public class EmitterTest {
	String[] notTheDoc = toArray("I", "am", "not", "the", "doctor");
	String[] nitDoc = toArray("nit", "doctor");

	@Test
	public void testArrays() {
		Emitter<String[]> e = Emitter.append(new String[0]);
		e.send(notTheDoc);
		e.send(nitDoc);
		assertArrayEquals(append(notTheDoc, nitDoc), e.subs().get());

		e = Emitter.unionMap(s->s.charAt(0), new String[0]);
		e.send(notTheDoc);
		e.send(nitDoc);
		assertArrayEquals(toArray("I", "am", "the", "nit", "doctor"), e.subs().get());

//		Emitter<int[]> ei = Emitter.unionMap(s->s%10, EMPTY);
//		ei.send(Ints.toArray(1,2,3,4,5));
//		ei.send(Ints.toArray(22,4,7));
//		assertArrayEquals(Ints.toArray(1,3,5,22,4,7), ei.subs().get());
//
//		ei = Emitter.unionMap(s->s&~1, EMPTY);
//		ei.send(Ints.toArray(1,2,4,7));
//		ei.send(Ints.toArray(0,3,9));
//		assertArrayEquals(Ints.toArray(4,7,0,3,9), ei.subs().get());
	}
	
	@Test
	public void test() {
		boolean[] called = { false };
		boolean[] wasInit = { false, false };
		String[] result = { null, null };
		Listener<String> one = (s, b)->{result[0] = s; wasInit[0] = b; };
		Listener<String> two = (s, b)->{result[1] = s; wasInit[1] = b; };
		
		Emitter<String> em = Emitter.get();
		em.init("hello", ()->{ called[0]=true; });
		Subs<String> e = em.subs();
		e.subscribe(one);
		assertEquals("init message was sent", "hello", result[0]);
		assertTrue("init message was sent as init", wasInit[0]);

		e.subscribe(two);

		em.send("world");
		assertEquals("non-init message was sent", "world", result[0]);
		assertEquals("non-init message was sent to subscriber 2", "world", result[1]);
		assertFalse("non-init message was not sent as init", wasInit[0]);

		e.unsubscribe(two);
		assertFalse("onEmpty not called before empty", called[0]);
		em.send("wednesday");
		assertEquals("message was sent to remaining listener", "wednesday", result[0]);
		assertEquals("message was not sent to unsubscribed listener", "world", result[1]);

		e.unsubscribe(one);
		assertTrue("onEmpty called wen empty", called[0]);
		em.send("friday");
		assertEquals("message was sent to unsubscribed listeners", "wednesday", result[0]);
		assertEquals("message was not sent to unsubscribed listener", "world", result[1]);
		
		em = Emitter.get();
		e = em.subs();
		e.subscribe(one);
		assertEquals("no init message was sent, as expected", "wednesday", result[0]);
		called[0] = false;
		e.unsubscribe(one);
		assertFalse("onEmpty not called because there wasn't one", called[0]);
		assertNull("returns empty to get because there isn't an initer", e.get());

		em = Emitter.get();
		em.init("hobby");
		e = em.subs();
		e.subscribe(one);
		assertEquals("init message was sent after single-value init", "hobby", result[0]);
		assertEquals("returns init value on get", "hobby", e.get());
	}
}
