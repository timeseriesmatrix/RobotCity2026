/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.jdesktop.swingx.JXDatePicker
 */
package com.floreantpos.ui.util;

import java.util.Calendar;
import java.util.Locale;
import org.jdesktop.swingx.JXDatePicker;

public class UiUtil {
    public static JXDatePicker getCurrentMonthStart() {
        Locale locale = Locale.getDefault();
        Calendar c = Calendar.getInstance(locale);
        c.set(5, 1);
        JXDatePicker datePicker = new JXDatePicker(c.getTime(), locale);
        return datePicker;
    }

    public static JXDatePicker getCurrentMonthEnd() {
        Locale locale = Locale.getDefault();
        Calendar c = Calendar.getInstance(locale);
        c.set(5, c.getActualMaximum(5));
        JXDatePicker datePicker = new JXDatePicker(c.getTime(), locale);
        return datePicker;
    }

    public static JXDatePicker getDeafultDate() {
        JXDatePicker datePicker = new JXDatePicker();
        return datePicker;
    }
}

