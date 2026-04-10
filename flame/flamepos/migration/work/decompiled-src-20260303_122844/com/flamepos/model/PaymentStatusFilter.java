/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import org.apache.commons.lang.StringUtils;

public enum PaymentStatusFilter {
    OPEN,
    PAID,
    CLOSED;


    public static PaymentStatusFilter fromString(String s) {
        if (StringUtils.isEmpty((String)s)) {
            return OPEN;
        }
        try {
            PaymentStatusFilter filter = PaymentStatusFilter.valueOf(s);
            return filter;
        }
        catch (Exception e) {
            return OPEN;
        }
    }

    public String toString() {
        return this.name().replaceAll("_", " ");
    }
}

