package com.backend.easy_to_web.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
  public static Date getDate(int year, int month, int date){
    Calendar cal = Calendar.getInstance();
    cal.set(year, month - 1, date);
    return new Date(cal.getTimeInMillis());
  }

}
