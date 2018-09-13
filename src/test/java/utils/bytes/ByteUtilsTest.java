package utils.bytes;

import static org.junit.Assert.*;
import static utils.bytes.Display.bytesToHex;
import static utils.bytes.Display.paddedBinary;
import static utils.bytes.Display.paddedBinaryLine;
import static utils.bytes.LittleEndian.fromInts;
import static utils.bytes.LittleEndian.fromLongs;
import static utils.bytes.LittleEndian.putLong;
import static utils.bytes.LittleEndian.toInt;
import static utils.bytes.LittleEndian.toInts;
import static utils.bytes.LittleEndian.toLong;
import static utils.bytes.LittleEndian.toLongs;

import java.util.Arrays;

import org.junit.Test;

import utils.bytes.LittleEndian;
import utils.encoders.Base64;

public class ByteUtilsTest {
	byte[] b = { 0x01, (byte)0xFF, 0x45, 0x23 };
	byte[] b2 = { 0x01, (byte)0xFF, 0x45, 0x23, 0x24, 0x34, 0x56 };

	@Test
	public void testGettingCoverageTo100() {
		new Base64();
	}

	@Test
	public void testBytesToHex() {
		assertEquals("", bytesToHex(new byte[0]));
		assertEquals("01FF4523", bytesToHex(b));
	}

	@Test
	public void testBase64OrEmpty() {
		assertEquals("", Base64.encode(new byte[0]));
		assertEquals("Af9FIw==", Base64.encode(b));
	}

	@Test
	public void testPaddedBinary() {
		assertEquals(
			"  0 00000001,   1 11111111,   2 01000101\n  3 00100011,   4 00100100,   5 00110100",
			paddedBinary(b2, 3));
	}

	@Test
	public void testPaddedBinaryByte() {
		assertEquals("00000000", paddedBinary((byte)0));
		assertEquals("11111111", paddedBinary((byte)0xFF));
		assertEquals("00000001", paddedBinary((byte)0x01));
		assertEquals("10000001", paddedBinary((byte)0x81));
	}

	@Test
	public void testPaddedBinaryLine() {
		assertEquals("  2 01000101,   3 00100011,   4 00100100", paddedBinaryLine(b2, 2, 3));
	}

	@Test
	public void testBytesToLong() {
		assertEquals("converts a little-endian array of bytes into a long", 
			0x5634242345l, toLong(b2, 2, 5));
	}

	@Test
	public void testLongToBytes() {
		byte[] bb = new byte[5];
		putLong(bb, 0, 5, 0x5634242345l);
		assertArrayEquals("converts a little-endian array of bytes into a long", 
				Arrays.copyOfRange(b2, 2, 7), bb);
	}

	@Test
	public void testBytesToInt() {
		assertEquals("reveribly converts ints to bytes", 
			-432434333, toInt(LittleEndian.fromInt(-432434333)));
	}
	@Test
	public void testBytesToInts() {
		int[] b = { -432434333, 232434333, 0 };
		assertArrayEquals("reveribly converts int[]s to bytes", 
			b, toInts(fromInts(b)));
	}
	@Test
	public void testBytesToLongs() {
		long[] b = { -432434333333l, 7777232434333l, 0 };
		assertArrayEquals("reveribly converts int[]s to bytes", 
			b, toLongs(fromLongs(b)));
	}
}
