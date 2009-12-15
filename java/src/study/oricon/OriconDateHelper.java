package study.oricon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import study.lang.ArrayHelper;
import study.lang.Messages;

public class OriconDateHelper {
	private static final Calendar BASE_TIME = newCalendar();
	public static final long BASE_MSEC = BASE_TIME.getTimeInMillis();
	public static final int BASE_YEAR = BASE_TIME.get(Calendar.YEAR);
	public static final int BASE_DAY_OF_YEAR = BASE_TIME
			.get(Calendar.DAY_OF_YEAR);
	public static final int BASE_DAY_OF_WEEK = BASE_TIME
			.get(Calendar.DAY_OF_WEEK);
	public static final int BASE_DAY_OF_WEEK_IN_MONTH = BASE_TIME
			.get(Calendar.DAY_OF_WEEK_IN_MONTH);
	public static final int END_YEAR = BASE_YEAR + 126;
	public static final int SECOND_IN_MSEC = 1000;
	public static final int MINUT_IN_MSEC = 60 * SECOND_IN_MSEC;
	public static final int HOUR_IN_MSEC = 60 * MINUT_IN_MSEC;
	public static final int DAY_IN_MSEC = 24 * HOUR_IN_MSEC;
	public static final int WEEK_IN_MSEC = 7 * DAY_IN_MSEC;
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	private static int[] firstDaysFromBase;

	public static String format(Calendar date) {
		return dateFormat.format(date.getTime());
	}
	public static String format(int day) {
		final Calendar date = newCalendar();
		dayToSystemTime(date, day);
		return format(date);
	}
	/**
	 * @param year
	 * @param month 1 base.
	 * @param day
	 * @return
	 */
	public static Calendar newCalendar(int year, int month, int day) {
		final Calendar date = newCalendar();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month - 1);
		date.set(Calendar.DAY_OF_MONTH, day);
		return date;
	}
	protected static Calendar newCalendar() {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(1);
		return calendar;
	}
	public static long dayToSystemTime(Calendar date, int day) {
		final long msec = dayToSystemTime(day);
		date.setTimeInMillis(msec);
		return msec;
	}
	public static long dayToSystemTime(int day) {
		// the following 1-line is important for the compiler.
		final long d = day;
		return d * DAY_IN_MSEC + BASE_MSEC;
	}
	public static int systemTimeToDay(long msec) {
		return (int) ((msec - BASE_MSEC) / DAY_IN_MSEC);
	}

	public static int getToday() {
		final Calendar today = Calendar.getInstance();
		final Calendar date = newCalendar();
		date.set(Calendar.YEAR, today.get(Calendar.YEAR));
		date.set(Calendar.MONTH, today.get(Calendar.MONTH));
		date.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
		return systemTimeToDay(date.getTimeInMillis());
	}
	/**
	 * @param year
	 * @return The elpased day at 1/1 of the specified year from the
	 *         {@link #BASE_TIME}.
	 */
	public static int getFirstDay(int year) {
		if (year < BASE_YEAR || END_YEAR <= year) {
			throw new IndexOutOfBoundsException(Messages.getIndexOutOfRange(
					BASE_YEAR, year, END_YEAR));
		}
		return OriconDateHelper.getFirstDaysFromBase()[year - BASE_YEAR];
	}
	/**
	 * 
	 * @param year
	 * @return
	 * @return The elpased day at the first sunday of the specified year from the
	 *         {@link #BASE_TIME}.
	 */
	public static int getFirstSunday(int year) {
		final int day = OriconDateHelper.getFirstDay(year);
		int offset = day % 7;
		switch (BASE_DAY_OF_WEEK) {
		case Calendar.MONDAY:
			offset = 6 - offset;
			break;
		case Calendar.TUESDAY:
			offset = 5 - offset;
			break;
		case Calendar.WEDNESDAY:
			offset = 4 - offset;
			break;
		case Calendar.THURSDAY:
			offset = 3 - offset;
			break;
		case Calendar.FRIDAY:
			offset = 2 - offset;
			break;
		case Calendar.SATURDAY:
			offset = 1 - offset;
			break;
		default:
			break;
		}
		return 0 <= offset ? day + offset : day + offset + 7;
	}
	/**
	 * @return elapsed days from {@link #BASE_TIME}.
	 */
	protected static int[] getFirstDaysFromBase() {
		if (OriconDateHelper.firstDaysFromBase == null) {
			OriconDateHelper.firstDaysFromBase = OriconDateHelper
					.newFirstDaysFromBase();
		}
		return OriconDateHelper.firstDaysFromBase;
	}
	protected static int[] newFirstDaysFromBase() {
		final int[] array = new int[END_YEAR - BASE_YEAR];
		final Calendar calendar = OriconDateHelper.newCalendar();
		for (int year = BASE_YEAR; year < END_YEAR; ++year) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			final int day = (int) ((calendar.getTimeInMillis() - BASE_MSEC) / DAY_IN_MSEC);
			array[year - BASE_YEAR] = day;
		}
		return array;
	}
	public static int getYear(int day) {
		final int[] days = getFirstDaysFromBase();
		final int index = ArrayHelper.getLowerBound(days, day);
		if (index == days.length) {
			throw new IndexOutOfBoundsException("around " + (BASE_YEAR + day / 365));
		} else if (index == 0 && day < days[0]) {
			throw new IndexOutOfBoundsException("around " + (BASE_YEAR - day / 365));
		} else if (day == days[index]) {
			return BASE_YEAR + index;
		}
		return BASE_YEAR + index - 1;
	}
}
