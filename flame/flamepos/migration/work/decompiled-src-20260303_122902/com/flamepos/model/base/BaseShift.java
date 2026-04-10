/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Shift;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseShift
implements Comparable,
Serializable {
    public static String REF = "Shift";
    public static String PROP_NAME = "name";
    public static String PROP_SHIFT_LENGTH = "shiftLength";
    public static String PROP_ID = "id";
    public static String PROP_END_TIME = "endTime";
    public static String PROP_START_TIME = "startTime";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    private String name;
    private Date startTime;
    private Date endTime;
    private Long shiftLength;

    public BaseShift() {
        this.initialize();
    }

    public BaseShift(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseShift(Integer id, String name) {
        this.setId(id);
        this.setName(name);
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getShiftLength() {
        return this.shiftLength;
    }

    public void setShiftLength(Long shiftLength) {
        this.shiftLength = shiftLength;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Shift)) {
            return false;
        }
        Shift shift = (Shift)obj;
        if (null == this.getId() || null == shift.getId()) {
            return false;
        }
        return this.getId().equals(shift.getId());
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

