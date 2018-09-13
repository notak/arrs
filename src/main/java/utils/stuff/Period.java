package utils.stuff;

import java.util.Arrays;

/** Slightly hacky way of breaking the day up into chunks, and tying it 
 * into Half Mintues. */
public class Period {
	public final int start;
	public final int end;

	public Period(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public int startHM(int day) {
		return day * 24 * 120 + start * 120;
	}
	
	public int endHM(int day) {
		return day * 24 * 120 + end * 120;
	}
	
	public long startSecs(int day) {
		return startHM(day) * 30;
	}
	
	public int endSecs(int day) {
		return endHM(day) * 30;
	}
	
	public static Period starting(int start) {
		return Arrays.stream(periods)
			.filter(p->p.end > start)
			.findFirst().get();
	}

	public static Period startingHM(int start) {
		Period p =  starting((start/120) % 24);
		return p;
	}

	public static Period[] periods = {
		new Period(0, 5),
		new Period(5, 7),
		new Period(7, 8),
		new Period(8, 9),
		new Period(9, 10),
		new Period(10, 11),
		new Period(11, 12),
		new Period(12, 13),
		new Period(13, 14),
		new Period(14, 15),
		new Period(15, 16),
		new Period(16, 17),
		new Period(17, 18),
		new Period(18, 19),
		new Period(19, 20),
		new Period(20, 22),
		new Period(22, 24),
	};
	
	public String toString() {
		return "["+start+"->"+end+"]";
	}
}