package com.keithstric.date;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

	private static Date startDate;
	private static String formattedDate;

	public DateFormatter() {
	}

	/**
	 * Format a date and return the date string
	 * @param formatStr - String - The format of the date
	 * @param origDate - Date - The java date to format
	 * @return String - The formatted date
	 */
	public static String getFormattedDate(String formatStr, Date origDate) {
		startDate = origDate;
		SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
		formattedDate = formatter.format(origDate);
		return formattedDate;
	}
}
