package utils.stuff;

import static java.util.Optional.empty;
import static utils.arrays.Objs.mapInt;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.ToIntFunction;

public class Numbers {
	public static boolean between(int num, int lower, int higherExc) {
		return num >=lower && num < higherExc;
	}
	public static boolean lengthBetween(String str, int lower, int higherExc) {
		return between(str.length(), lower, higherExc);
	}
	
	public static Optional<Integer> parseInt(String in) {
		return parseInt(in, 10);
	}
	
	public static Optional<Integer> parseHexInt(String in) {
		return parseInt(in, 16);
	}
	
	public static Optional<Double> parseDouble(String in) {
		try {
			return Optional.of(Double.parseDouble(in));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	public static OptionalInt optInt(String in) {
		return optInt(in, 10);
	}

	public static OptionalInt optInt(String in, int base) {
		try {
			return OptionalInt.of(Integer.parseInt(in, base));
		} catch (Exception e) {
			return OptionalInt.empty();
		}
	}
	
	public static Optional<Integer> parseInt(String in, int base) {
		try {
			return Optional.of(Integer.parseInt(in, base));
		} catch (Exception e) { return empty(); }
	}
	
	public static Optional<Short> parseShort(String in) {
		try {
			return Optional.of(Short.parseShort(in));
		} catch (Exception e) { return empty(); }
	}
	
	public static Optional<Byte> parseByte(String in) {
		try {
			return Optional.of(Byte.parseByte(in));
		} catch (Exception e) { return empty(); }
	}
	
	public static Optional<Long> parseLong(String in) {
		return parseLong(in, 10);
	}

	public static Optional<Long> parseLong(String in, int base) {
		try {
			return Optional.of(Long.parseLong(in, base));
		} catch (Exception e) { return empty(); }
	}
	
	public static long longOr0(String in) {
		try {
			return (in==null) ? 0 : Long.parseLong(in);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static int intOr0(String in) {
		return parseInt(in).orElse(0);
	}
	
	public static short shortOr0(String in) {
		return parseShort(in).orElse((short)0);
	}
	
	public static byte byteOr0(String in) {
		return parseByte(in).orElse((byte)0);
	}
	
	public static int[] intOr0(String[] in) {
		return mapInt(in, (ToIntFunction<String>)Numbers::intOr0);
	}
	
	public static int intOrMin(String in) {
		return parseInt(in).orElse(Integer.MIN_VALUE);
	}
	
	public static Double doubleOr0(String in) {
		return parseDouble(in).orElse(0.0);
	}
}
