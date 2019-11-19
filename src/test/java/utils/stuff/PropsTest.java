package utils.stuff;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;

import org.junit.jupiter.api.Test;

class PropsTest {

	@Test
	void testPropString() {
		Props p = new Props(new Properties());
		p.props.setProperty("F", "boo");
		assertEquals("boo", p.prop("F").get());
		assertEquals(true, p.prop("foo").isEmpty());
	}

	@Test
	void testPropStringString() {
		Props p = new Props(new Properties());
		p.props.setProperty("F", "boo");
		assertEquals("boo", p.prop("F", "pang"));
		assertEquals("pang", p.prop("foo", "pang"));
	}

	@Test
	void testPropStringInt() {
		Props p = new Props(new Properties());
		p.props.setProperty("F", "boo");
		assertEquals(9, p.prop("F", 9));
		assertEquals(9, p.prop("G", 9));
		p.props.setProperty("F", "-1");
		assertEquals(-1, p.prop("F", 9));
	}

	@Test
	void testPropStringBoolean() {
		Props p = new Props(new Properties());
		p.props.setProperty("F", "true");
		assertTrue(p.prop("F", false));
		assertFalse(p.prop("G", false));
		assertTrue(p.prop("F", false));
		assertTrue(p.prop("G", true));
		
		p.props.setProperty("F", "false");
		assertFalse(p.prop("F", false));
		assertFalse(p.prop("G", false));
		assertFalse(p.prop("F", false));
		assertTrue(p.prop("G", true));
		
		//And the counter-intuitive tests. Spell false correctly folks
		p.props.setProperty("F", "False");
		assertTrue(p.prop("F", false));
		assertFalse(p.prop("G", false));
		assertTrue(p.prop("F", false));
		assertTrue(p.prop("G", true));
	}

}
