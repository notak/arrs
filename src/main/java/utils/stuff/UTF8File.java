package utils.stuff;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import utils.stuff.Fns.LogFn;

public class UTF8File {
	public static final Charset UTF8 = UTF_8;
	
	public static String read(File f) {
		return stream(f).collect(joining("\n"));
	}

	/** Appends a string to a file as UTF8 (creating the file if it doesn't
	 * exist. Errors on problems rather than Excepting */
	public static void append(Path p, String s) {
		try {
			Files.write(p, s.getBytes(UTF_8), APPEND, CREATE);
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	/** Reads the whole of short files to save closing issues */
	public static Stream<String> stream(File f) {
		try {
			return Files.readAllLines(f.toPath()).stream();
		} catch (IOException e) { throw new Error(e); }
	}

	public static void silentClose(Closeable c, LogFn log) {
		try { c.close(); } catch (Exception e) {
			log.log(String.format(
				"couldn't close %s at %s", c, e.getStackTrace()[1]));
		}
	}
	
	private static BufferedReader bufferedInputStreamReader(InputStream is) {
		return new BufferedReader(new InputStreamReader(is));
	}
	
	public static Stream<String> gzippedLines(InputStream is, LogFn log) {
		try {
			var br = bufferedInputStreamReader(new GZIPInputStream(is));
			return br.lines().onClose(()->silentClose(br, log));
		} catch (IOException ioe) {
			log.log(ioe.getMessage());
			return Stream.empty();
		}
	}
	
	public static Stream<String> gzippedLines(File f, LogFn log) {
		try {
			return gzippedLines(new FileInputStream(f), log);
		} catch (IOException ioe) {
			log.log(ioe.getMessage());
			return Stream.empty();
		}
	}
	
	public static Stream<String> lines(InputStream is, LogFn log) {
		var br = bufferedInputStreamReader(is);
		return br.lines().onClose(()->silentClose(br, log));
	}
}
