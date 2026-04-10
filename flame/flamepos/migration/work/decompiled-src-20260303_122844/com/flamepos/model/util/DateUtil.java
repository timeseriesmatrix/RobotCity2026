/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date startOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        return new Date(cal.getTimeInMillis());
    }

    public static Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(11, 23);
        cal.set(12, 0);
        cal.set(13, 0);
        return new Date(cal.getTimeInMillis());
    }

    public static boolean isStartOfWeek(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        c1.set(11, 0);
        c1.set(12, 0);
        c1.set(13, 0);
        return c1.get(7) == 1;
    }

    public static boolean isStartOfMonth(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        c1.set(11, 0);
        c1.set(12, 0);
        c1.set(13, 0);
        return c1.get(5) == 1;
    }

    public static boolean between(Date startDate, Date endDate, Date guniping) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return !(!guniping.equals(startDate) && !guniping.after(startDate) || !guniping.equals(endDate) && !guniping.before(endDate));
    }

    public static String getReportDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d h:mm:ss a");
        String date = dateFormat.format(new Date());
        return date;
    }

    public static boolean isToday(Date date) {
        return DateUtil.isSameDay(date, Calendar.getInstance().getTime());
    }

    public static boolean isToday(Calendar cal) {
        return DateUtil.isSameDay(cal, Calendar.getInstance());
    }

    public static String formatDateAsString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String dateString = dateFormat.format(date);
        return "TODAY " + dateString;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return DateUtil.isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
    }
}

