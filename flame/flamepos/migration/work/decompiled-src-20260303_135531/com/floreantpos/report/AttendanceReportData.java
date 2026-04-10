/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.model.User;
import java.util.Date;

public class AttendanceReportData {
    User user;
    String name;
    Date clockIn;
    Date clockOut;
    double workTime;

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getClockIn() {
        return this.clockIn;
    }

    public void setClockIn(Date clockIn) {
        this.clockIn = clockIn;
    }

    public Date getClockOut() {
        return this.clockOut;
    }

    public void setClockOut(Date clockOut) {
        this.clockOut = clockOut;
    }

    public double getWorkTime() {
        return this.workTime;
    }

    public void setWorkTime(double workTime) {
        this.workTime = workTime;
    }

    public void calculate() {
        double hours;
        long cin = this.clockIn.getTime();
        long cout = this.clockOut.getTime();
        long milliseconds = cout - cin;
        if (milliseconds < 0L) {
            this.workTime = 0.0;
            return;
        }
        double seconds = (double)milliseconds / 1000.0;
        double minutes = seconds / 60.0;
        this.workTime = hours = minutes / 60.0;
    }
}

