package utils.stuff;

import static java.lang.System.currentTimeMillis;
import static java.lang.ThreadLocal.withInitial;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/** Class filled with lazy bad wickedness, much of which should never be allowed
 * near an internationalized site, and was only developed to support an 
 * inherently UK-based product where everything is done in local time and 
 * daylight savings is fudged at the layer above */
public class Days {

	/** Given a day since the unix apocalypse (ie. where 1970-01-01 is 0),
	 * return an integer representing the day of the week, where 
		Mon=0, Tue=1 .. Sun=6 */
	public static int dow(int day) {
		return (day+3)%7;
	}
	
	/** Convenience function for creating a Calendar object in the UTC timezone
	 * and initializing it to the unix timestamp provided */
	private static Calendar utcCalendar(long time) {
		var out = utcCalendar();
		out.setTime(new Date(time));
		return out;
	}
	
	/** Convenience function for creating a Calendar object in UTC */
	private static Calendar utcCalendar() {
		return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}
	
	/** Given a day since the unix apocalypse (ie. where 1970-01-01 is 0),
	 * return an integer representing the day of the month in the UTC tz */
	public static int dom(int day) {
		return utcCalendar(((long)day) * 86400 * 1000).get(Calendar.DAY_OF_MONTH);
	}

	/** return an integer representing the current day of the month 
	 * in the UTC tz */
	public static int dom() {
		return utcCalendar().get(Calendar.DAY_OF_MONTH);
	}

	/** The current day since the unix apocalypse (ie. where 1970-01-01 is 0) */
	public static int today() {
		return hourNow()/24;
	}
	
	/** The current number of half-minutes since the start of the day.
	 * <p>This is great if you have data which extensively uses half-minutes. Less
	 * so otherwise */
	public static int timeOfDayHM() {
		return (int)(secsNow()/30)%(24*120);
	}
	
	/** The current number of hours since the unix apocalypse. */
	public static int hourNow() {
		return (int)(secsNow()/3600);
	}

	/** The current number of hours since the unix apocalypse. */
	public static long secsNow() {
		return currentTimeMillis()/1000;
	}

	private static SimpleDateFormat simpleDateFormat(String fmt, TimeZone tz) {
		var out = new SimpleDateFormat(fmt);
		out.setTimeZone(tz);
		return out;
	}
	
	public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
	public static final int DAY_MILLIS = 86400000;
	
	public static final ThreadLocal<SimpleDateFormat> sdf = 
		withInitial(()->simpleDateFormat("yyyy-MM-dd", UTC));
	
	public static final ThreadLocal<SimpleDateFormat> full = 
		withInitial(()->simpleDateFormat("yyyy-MM-dd HH:mm:ss", UTC));
	
	public static final ThreadLocal<SimpleDateFormat> HHmm = 
		withInitial(()->simpleDateFormat("HH:mm", UTC));

    public static int getDaysFromDate(String in) {
	    try {
	    	long time = sdf.get().parse(in.substring(0, 10)).getTime();
			return (int)(time/DAY_MILLIS);
		} catch (Exception e) {
			return Integer.MIN_VALUE;
		}
	}

	public static String hhMM(long ts) {
		return HHmm.get().format(new Date(ts));
	}

	public static String getDateFromDays(int days) {
		return sdf.get().format(new Date((long)days * DAY_MILLIS));
	}

	public static String toUTCDate(long ts) {
		return full.get().format(new Date(ts));
	}
}