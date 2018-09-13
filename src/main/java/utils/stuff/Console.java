package utils.stuff;

public class Console {
	/** Alias for System.out.print for nicer code */
	public static void print(String str) {
		System.out.print(str);
	}

	/** Alias for System.out.printf for nicer code */
	public static void printf(String format, Object... args) {
		System.out.printf(format, args);
	}

	/** Alias for System.out.println for nicer code */
	public static void println(String str) {
		System.out.println(str);
	}

	/** Print n characters on top of the last n characters output. Useful for
	 * status info */
	public static void printTransient(String s) {
		print(s + Str.stringOf('\b', s.length()));
	}

	private static final String spinStates = "/-\\|";

	/** A spinner will show a character on the output alternating between /-\|
	 * This function will show a new state on the spinner every time it is 
	 * called, and will return the new incremented position of the counter. */
	public static int spinner(int pos) {
		return spinner(pos, 1);
	}

	/** A spinner will show a character on the output alternating between /-\|
	 * This function will show a new state on the spinner if the current 
	 * position of the counter reaches the step, and will return the new
	 * incremented position of the counter. So if you want to indicate to the 
	 * user when a loop has gone through a thousand iterations for example you 
	 * could include
	 * var pos=0 
	 * prior to the loop, and 
	 * Console.spinner(pos, 1000)
	 * within the loop */
	public static int spinner(int pos, int step) {
		if (pos%step==0) {
			printTransient("" + spinStates.charAt((pos/step)%4));
		}
		return pos+1;
	}

	/** This performs the same action as spinner(int), but it takes the
	 * counter position from a single-element array instead of directly
	 * and stores the incremented value back into the array. This is mostly
	 * useful for spinners in lambdas, where the counter would have to be 
	 * effectively final */
	public static void spinner(int[] pos) {
		pos[0] = spinner(pos[0]);
	}

	/** This performs the same action as spinner(int, int), but it takes the
	 * counter position from a single-element array instead of directly
	 * and stores the incremented value back into the array. This is mostly
	 * useful for spinners in lambdas, where the counter would have to be 
	 * effectively final */
	public static void spinner(int[] pos, int step) {
		pos[0] = spinner(pos[0], step);
	}
}
