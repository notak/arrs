package utils.encoders;

import static org.junit.Assert.*;
import static utils.encoders.CapsDigits.decode;
import static utils.encoders.CapsDigits.decodeChar;
import static utils.encoders.CapsDigits.encode;

import org.junit.Test;

public class CapsDigitsTest {
	@Test
	public void testGettingCoverageTo100() {
		new CapsDigits();
	}

	@Test
	public void test() {
		assertEquals("", decode(encode("")));
		assertEquals("12", decode(encode("12")));
		assertEquals("A12", decode(encode("A12")));
		assertEquals("A12Z", decode(encode("A12Z")));
		assertEquals("02Z", decode(encode("02Z")));
		assertEquals("0*2Z", decode(encode("0*2Z")));
		assertEquals(" 0*Z", decode(encode(" 0*Z")));
		assertEquals("0*Z ", decode(encode("0*Z ")));

	
		assertEquals("", decode(encode("", '%', '&'), '%', '&'));
		assertEquals("12", decode(encode("12", '%', '&'), '%', '&'));
		assertEquals("A12", decode(encode("A12", '%', '&'), '%', '&'));
		assertEquals("A12Z", decode(encode("A12Z", '%', '&'), '%', '&'));
		assertEquals("02Z", decode(encode("02Z", '%', '&'), '%', '&'));
		assertEquals("0*2Z", decode(encode("0*2Z", '%', '&'), '%', '&'));
		assertEquals(" 0*Z", decode(encode(" 0*Z", '%', '&'), '%', '&'));
		assertEquals("0*Z ", decode(encode("0*Z ", '%', '&'), '%', '&'));
		assertEquals("0%*&Z ", decode(encode("0%*&Z ", '%', '&'), '%', '&'));
		
		assertEquals(0, encode(null));
		assertEquals(0, encode("\0"));
		assertEquals(0, decodeChar(0));
	}

}
