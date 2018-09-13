package utils.stuff;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public class Str {
	public static Function<String, String> quoted = Str::quoted;

	public static String quoted(String in) {
		return "\"" + in + "\"";
	}

	public static boolean notEmpty(String in) {
		return in!=null && in.length()>0;
	}

	public static String lSpace(String in, String space) {
		return in!=null && in.length()>0 ? space + in : "";
	}

	public static String lSpace(Optional<String> in, String space) {
		return lSpace(in.orElse(""), space);
	}

	public static String lSpace(String in) {
		return lSpace(in, " ");
	}

	public static String rSpace(String in) {
		return in!=null && in.length()>0 ? in + " ": "";
	}
	
	public static String stringOf(char c, int len) {
		char[] backspaces = new char[len];
		Arrays.fill(backspaces, c);
		return new String(backspaces);
	}
}