package com.github.bordertech.wcomponents.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Date-related utility methods.
 *
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public final class DateUtilities {

	/**
	 * Prevent instantiation of this utility class.
	 */
	private DateUtilities() {
	}

	/**
	 * Creates a date from the given components.
	 *
	 * @param day the day of the month, 1-31.
	 * @param month the month, 1-12.
	 * @param year the year.
	 * @return a date with the specified settings.
	 */
	public static Date createDate(final int day, final int month, final int year) {
		Calendar cal = Calendar.getInstance();
		cal.clear();

		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.YEAR, year);

		return cal.getTime();
	}

	/**
	 * "Rounds" a date to a day boundary by truncating the time component.
	 *
	 * @param date the date to round.
	 * @return the rounded date.
	 */
	public static Date roundToDay(final Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);

		// Clear out the time component
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		return cal.getTime();
	}
}
