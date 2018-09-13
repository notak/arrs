package utils.stuff;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.Test;

public class UTF8FileTest {
	@Test
	public void testGettingCoverageTo100() {
		new UTF8File();
	}

	private static class Close implements Closeable {
		@Override
		public void close() throws IOException {
			throw new IOException("Bangin");
		}
		
		@Override
		public String toString() {
			return "Bongin";
		}
	}
	
	@Test
	public void testLines() throws Exception {
		InputStream i = new ByteArrayInputStream("I\nam\nnot\nthe\ndoctor".getBytes("UTF-8"));
		Stream<String> lines = UTF8File.lines(i, s->{});
		assertEquals("I am not the doctor", Lists.join(" ", lines));
	}

	@Test
	public void testSilentClose() throws Exception {
		String[] r = new String[1];
		UTF8File.silentClose(new Close(), s->{
			r[0] = s;
		});
		assertEquals("couldn't close Bongin at ", r[0].substring(0, 25));
	}
}
