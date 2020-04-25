package com.trackcovid19.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Formatter {

  public static boolean isSameDay(Date date1, Date date2) {
    if (date1 == null || date2 == null) {
      throw new IllegalArgumentException("The dates must not be null");
    }
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date1);
    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date2);
    return isSameDay(cal1, cal2);
  }

  public static boolean isSameDay(Calendar cal1, Calendar cal2) {
    if (cal1 == null || cal2 == null) {
      throw new IllegalArgumentException("The dates must not be null");
    }
    return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
        && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
  }

  /**
   * Checks if a date is today.
   *
   * @param date the date, not altered, not null.
   * @return true if the date is today.
   * @throws IllegalArgumentException if the date is <code>null</code>
   */
  public static boolean isToday(Date date) {
    return isSameDay(date, Calendar.getInstance().getTime());
  }

  public static String getISTDate(Date date) {
    SimpleDateFormat sd = new SimpleDateFormat(
            "dd MMM");

    // TODO: Avoid using the abbreviations when fetching time zones.
    // Use the full Olson zone ID instead.
    sd.setTimeZone(TimeZone.getTimeZone("IST"));
    return sd.format(date);
  }
}
