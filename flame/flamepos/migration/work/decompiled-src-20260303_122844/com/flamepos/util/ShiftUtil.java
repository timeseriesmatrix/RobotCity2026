/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import com.floreantpos.model.Shift;
import com.floreantpos.model.dao.ShiftDAO;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ShiftUtil {
    private static final String DEFAULT_SHIFT = "DEFAULT SHIFT";
    private static final Calendar calendar = Calendar.getInstance();
    private static final Calendar calendar2 = Calendar.getInstance();
    private static final NumberFormat format = new DecimalFormat("00");

    public static Date formatShiftTime(Date shiftTime) {
        calendar.clear();
        calendar2.setTime(shiftTime);
        calendar.set(10, calendar2.get(10));
        calendar.set(12, calendar2.get(12));
        calendar.set(9, calendar2.get(9));
        return calendar.getTime();
    }

    public static Date buildShiftStartTime(int startHour, int startMin, int startAmPm, int endHour, int endMin, int endAmPm) {
        startHour = startHour == 12 ? 0 : startHour;
        calendar.clear();
        calendar.set(10, startHour);
        calendar.set(12, startMin);
        calendar.set(9, startAmPm);
        return calendar.getTime();
    }

    public static Date buildShiftEndTime(int startHour, int startMin, int startAmPm, int endHour, int endMin, int endAmPm) {
        endHour = endHour == 12 ? 0 : endHour;
        calendar.clear();
        calendar.set(10, endHour);
        calendar.set(12, endMin);
        calendar.set(9, endAmPm);
        if (startAmPm == 1 && endAmPm == 0) {
            calendar.add(5, 1);
        }
        return calendar.getTime();
    }

    public static String buildShiftTimeRepresentation(Date shiftTime) {
        calendar.setTime(shiftTime);
        String s = "";
        s = format.format(calendar.get(10) == 0 ? 12L : (long)calendar.get(10));
        s = s + ":" + format.format(calendar.get(12));
        s = s + (calendar.get(9) == 0 ? " AM" : " PM");
        return s;
    }

    public static String getDateRepresentation(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss a");
        return formatter.format(date);
    }

    public static Shift getCurrentShift() {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar.clear();
        calendar.set(10, calendar2.get(10));
        calendar.set(12, calendar2.get(12));
        calendar.set(9, calendar2.get(9));
        Date currentTime = calendar.getTime();
        ShiftDAO shiftDAO = new ShiftDAO();
        List<Shift> shifts = shiftDAO.findAll();
        Shift defaultShift = ShiftUtil.findDefaultShift(shifts);
        Shift currentShift = ShiftUtil.findCurrentShift(currentTime, shifts);
        if (currentShift != null) {
            return currentShift;
        }
        calendar.add(5, 1);
        currentTime = calendar.getTime();
        currentShift = ShiftUtil.findCurrentShift(currentTime, shifts);
        if (currentShift != null) {
            return currentShift;
        }
        if (defaultShift == null) {
            return ShiftUtil.getDefaultShift();
        }
        return defaultShift;
    }

    private static Shift findDefaultShift(List<Shift> shifts) {
        Iterator<Shift> iterator = shifts.iterator();
        while (iterator.hasNext()) {
            Shift shift = iterator.next();
            if (!DEFAULT_SHIFT.equalsIgnoreCase(shift.getName()) || shift.getShiftLength() != 86400000L) continue;
            iterator.remove();
            return shift;
        }
        return null;
    }

    private static Shift findCurrentShift(Date currentTime, List<Shift> shifts) {
        for (Shift shift : shifts) {
            Date startTime = new Date(shift.getStartTime().getTime());
            Date endTime = new Date(shift.getEndTime().getTime());
            if (!currentTime.after(startTime) || !currentTime.before(endTime)) continue;
            return shift;
        }
        return null;
    }

    private static Shift getDefaultShift() {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar.clear();
        calendar.set(10, 0);
        calendar.set(12, 0);
        calendar.set(9, 0);
        calendar2.clear();
        calendar2.add(5, 1);
        calendar2.set(10, 0);
        calendar2.set(12, 0);
        calendar2.set(9, 0);
        Shift defaultShift = new Shift();
        defaultShift.setName(DEFAULT_SHIFT);
        defaultShift.setStartTime(calendar.getTime());
        defaultShift.setEndTime(calendar2.getTime());
        defaultShift.setShiftLength(calendar2.getTimeInMillis() - calendar.getTimeInMillis());
        ShiftDAO shiftDAO = ShiftDAO.getInstance();
        shiftDAO.saveOrUpdate(defaultShift);
        return defaultShift;
    }

    static {
        calendar.clear();
    }
}

