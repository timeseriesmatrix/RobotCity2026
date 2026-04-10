/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import org.apache.commons.lang.StringUtils;

public class NumberUtil {
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private static final NumberFormat numberFormat2 = NumberFormat.getNumberInstance();

    public static double roundToTwoDigit(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double roundToThreeDigit(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(3, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String format3DigitNumber(Double number) {
        if (number == null) {
            return numberFormat2.format(0L);
        }
        String value = numberFormat2.format(number);
        return value;
    }

    public static String formatNumber(Double number) {
        if (number == null) {
            return numberFormat.format(0L);
        }
        String value = numberFormat.format(number);
        if (value.startsWith("-")) {
            return numberFormat.format(0L);
        }
        return value;
    }

    public static Number parse(String number) throws ParseException {
        if (StringUtils.isEmpty((String)number)) {
            return 0;
        }
        return numberFormat.parse(number);
    }

    static {
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat2.setMinimumFractionDigits(3);
        numberFormat2.setMaximumFractionDigits(3);
    }
}

