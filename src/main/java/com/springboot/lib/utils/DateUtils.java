package com.springboot.lib.utils;

import java.util.Calendar;

public class DateUtils {

    public static int getCurrentDateAsInt() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return (calendar.get(Calendar.YEAR) * 10000) + ((calendar.get(Calendar.MONTH) + 1) * 100) + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getDayOfWeekAsInt(String dayOfWeek) {
        if ("SUNDAY".equalsIgnoreCase(dayOfWeek)) {
            return Calendar.SUNDAY;
        }
        if ("MONDAY".equalsIgnoreCase(dayOfWeek)) {
            return Calendar.MONDAY;
        }
        if ("TUESDAY".equalsIgnoreCase(dayOfWeek)) {
            return Calendar.TUESDAY;
        }
        if ("WEDNESDAY".equalsIgnoreCase(dayOfWeek)) {
            return Calendar.WEDNESDAY;
        }
        if ("THURSDAY".equalsIgnoreCase(dayOfWeek)) {
            return Calendar.THURSDAY;
        }
        if ("FRIDAY".equalsIgnoreCase(dayOfWeek)) {
            return Calendar.FRIDAY;
        }
        if ("SATURDAY".equalsIgnoreCase(dayOfWeek)) {
            return Calendar.SATURDAY;
        }
        return 0;
    }
}
