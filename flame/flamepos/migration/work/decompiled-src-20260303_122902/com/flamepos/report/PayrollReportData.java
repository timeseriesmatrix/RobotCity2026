/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import com.floreantpos.model.User;
import java.util.Date;

public class PayrollReportData {
    User user;
    Date date;
    Date from;
    Date to;
    double totalHour;
    double rate;
    double payment;

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getFrom() {
        return this.from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return this.to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public double getTotalHour() {
        return this.totalHour;
    }

    public void setTotalHour(double totalHour) {
        this.totalHour = totalHour;
    }

    public double getRate() {
        return this.rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getPayment() {
        return this.payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void calculate() {
        long fromTime = this.from.getTime();
        long toTime = this.to.getTime();
        long milliseconds = toTime - fromTime;
        if (milliseconds < 0L) {
            this.totalHour = 0.0;
            return;
        }
        double seconds = (double)milliseconds / 1000.0;
        double minutes = seconds / 60.0;
        double hours = minutes / 60.0;
        double diff = toTime - fromTime;
        diff /= 8.64E7;
        this.totalHour = hours;
        this.rate = this.user.getCostPerHour();
        this.payment = this.totalHour * this.rate;
    }
}

