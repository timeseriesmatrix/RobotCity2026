/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.EmployeeInOutHistory;
import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.User;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseEmployeeInOutHistory
implements Comparable,
Serializable {
    public static String REF = "EmployeeInOutHistory";
    public static String PROP_USER = "user";
    public static String PROP_OUT_TIME = "outTime";
    public static String PROP_IN_HOUR = "inHour";
    public static String PROP_TERMINAL = "terminal";
    public static String PROP_CLOCK_OUT = "clockOut";
    public static String PROP_SHIFT = "shift";
    public static String PROP_OUT_HOUR = "outHour";
    public static String PROP_IN_TIME = "inTime";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Date outTime;
    protected Date inTime;
    protected Short outHour;
    protected Short inHour;
    protected Boolean clockOut;
    private User user;
    private Shift shift;
    private Terminal terminal;

    public BaseEmployeeInOutHistory() {
        this.initialize();
    }

    public BaseEmployeeInOutHistory(Integer id) {
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

    public Date getOutTime() {
        return this.outTime;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }

    public Date getInTime() {
        return this.inTime;
    }

    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }

    public Short getOutHour() {
        return this.outHour;
    }

    public void setOutHour(Short outHour) {
        this.outHour = outHour;
    }

    public Short getInHour() {
        return this.inHour;
    }

    public void setInHour(Short inHour) {
        this.inHour = inHour;
    }

    public Boolean isClockOut() {
        return this.clockOut == null ? Boolean.FALSE : this.clockOut;
    }

    public void setClockOut(Boolean clockOut) {
        this.clockOut = clockOut;
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
        if (!(obj instanceof EmployeeInOutHistory)) {
            return false;
        }
        EmployeeInOutHistory employeeInOutHistory = (EmployeeInOutHistory)obj;
        if (null == this.getId() || null == employeeInOutHistory.getId()) {
            return false;
        }
        return this.getId().equals(employeeInOutHistory.getId());
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

