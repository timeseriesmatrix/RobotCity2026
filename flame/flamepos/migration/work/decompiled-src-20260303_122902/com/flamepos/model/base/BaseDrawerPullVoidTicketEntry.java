/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import java.io.Serializable;

public abstract class BaseDrawerPullVoidTicketEntry
implements Comparable,
Serializable {
    public static String REF = "DrawerPullVoidTicketEntry";
    public static String PROP_AMOUNT = "amount";
    public static String PROP_HAST = "hast";
    public static String PROP_QUANTITY = "quantity";
    public static String PROP_CODE = "code";
    public static String PROP_REASON = "reason";
    protected Integer code;
    protected String reason;
    protected String hast;
    protected Integer quantity;
    protected Double amount;

    public BaseDrawerPullVoidTicketEntry() {
        this.initialize();
    }

    protected void initialize() {
    }

    public Integer getCode() {
        return this.code == null ? Integer.valueOf(0) : this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getHast() {
        return this.hast;
    }

    public void setHast(String hast) {
        this.hast = hast;
    }

    public Integer getQuantity() {
        return this.quantity == null ? Integer.valueOf(0) : this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getAmount() {
        return this.amount == null ? Double.valueOf(0.0) : this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int compareTo(Object obj) {
        if (obj.hashCode() > this.hashCode()) {
            return 1;
        }
        if (obj.hashCode() < this.hashCode()) {
            return -1;
        }
        return 0;
    }

    public String toString() {
        return super.toString();
    }
}

