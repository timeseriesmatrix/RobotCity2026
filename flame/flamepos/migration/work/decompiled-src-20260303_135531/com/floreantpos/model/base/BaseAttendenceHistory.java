/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.User;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseAttendenceHistory
implements Comparable,
Serializable {
    public static String REF = "AttendenceHistory";
    public static String PROP_USER = "user";
    public static String PROP_CLOCK_IN_TIME = "clockInTime";
    public static String PROP_CLOCK_OUT_TIME = "clockOutTime";
    public static String PROP_TERMINAL = "terminal";
    public static String PROP_CLOCK_IN_HOUR = "clockInHour";
    public static String PROP_CLOCKED_OUT = "clockedOut";
    public static String PROP_SHIFT = "shift";
    public static String PROP_ID = "id";
    public static String PROP_CLOCK_OUT_HOUR = "clockOutHour";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    private Date clockInTime;
    private Date clockOutTime;
    private Short clockInHour;
    private Short clockOutHour;
    private Boolean clockedOut;
    private User user;
    private Shift shift;
    private Terminal terminal;

    public BaseAttendenceHistory() {
        this.initialize();
    }

    public BaseAttendenceHistory(Integer id) {
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

    public Date getClockInTime() {
        return this.clockInTime;
    }

    public void setClockInTime(Date clockInTime) {
        this.clockInTime = clockInTime;
    }

    public Date getClockOutTime() {
        return this.clockOutTime;
    }

    public void setClockOutTime(Date clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public Short getClockInHour() {
        return this.clockInHour;
    }

    public void setClockInHour(Short clockInHour) {
        this.clockInHour = clockInHour;
    }

    public Short getClockOutHour() {
        return this.clockOutHour;
    }

    public void setClockOutHour(Short clockOutHour) {
        this.clockOutHour = clockOutHour;
    }

    public Boolean isClockedOut() {
        return this.clockedOut == null ? Boolean.FALSE : this.clockedOut;
    }

    public void setClockedOut(Boolean clockedOut) {
        this.clockedOut = clockedOut;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shift getShift() {
        return this.shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof AttendenceHistory)) {
            return false;
        }
        AttendenceHistory attendenceHistory = (AttendenceHistory)obj;
        if (null == this.getId() || null == attendenceHistory.getId()) {
            return false;
        }
        return this.getId().equals(attendenceHistory.getId());
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

