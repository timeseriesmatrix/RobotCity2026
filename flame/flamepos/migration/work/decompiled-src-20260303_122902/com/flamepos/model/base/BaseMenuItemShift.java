/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.MenuItemShift;
import com.floreantpos.model.Shift;
import java.io.Serializable;

public abstract class BaseMenuItemShift
implements Comparable,
Serializable {
    public static String REF = "MenuItemShift";
    public static String PROP_SHIFT_PRICE = "shiftPrice";
    public static String PROP_SHIFT = "shift";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    private Double shiftPrice;
    private Shift shift;

    public BaseMenuItemShift() {
        this.initialize();
    }

    public BaseMenuItemShift(Integer id) {
        this.setId(id);
        this.initialize();
    }

    protected void initialize() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.hashCode = Integer.MIN_VALUE;
    }

    public Double getShiftPrice() {
        return this.shiftPrice == null ? Double.valueOf(0.0) : this.shiftPrice;
    }

    public void setShiftPrice(Double shiftPrice) {
        this.shiftPrice = shiftPrice;
    }

    public Shift getShift() {
        return this.shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof MenuItemShift)) {
            return false;
        }
        MenuItemShift menuItemShift = (MenuItemShift)obj;
        if (null == this.getId() || null == menuItemShift.getId()) {
            return false;
        }
        return this.getId().equals(menuItemShift.getId());
    }

    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getId()) {
                return super.hashCode();
            }
            String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
            this.hashCode = hashStr.hashCode();
        }
        return this.hashCode;
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

