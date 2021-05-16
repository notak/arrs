package utils.bytes;

import static org.junit.Assert.*;

import org.junit.Test;

public class BitsTest {
	@Test
	public void testGettingCoverageTo100() {
		new Bits();
	}

	@Test
	public void testByteBitCOunt() {
		assertEquals(2, Bits.count((byte)0x81));
		assertEquals(1, Bits.count((byte)0x80));
		assertEquals(0, Bits.count((byte)0));
	}

	@Test
	public void testOr() {
		assertEquals((byte)0x81, Bits.or((byte)0x80, (byte)0x1));
		assertEquals((byte)0x81, Bits.or((byte)0x81, (byte)0x1));
	}

	@Test
	public void testXor() {
		assertEquals((byte)0x81, Bits.xor((byte)0x80, (byte)0x1));
		assertEquals((byte)0x80, Bits.xor((byte)0x81, (byte)0x1));
	}

	@Test
	public void testAnd() {
		assertEquals((byte)0x0, Bits.and((byte)0x80, (byte)0x1));
		assertEquals((byte)0x1, Bits.and((byte)0x81, (byte)0x1));
	}

}
