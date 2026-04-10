/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.CashDrawerResetHistory;
import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.User;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseCashDrawerResetHistory
implements Comparable,
Serializable {
    public static String REF = "CashDrawerResetHistory";
    public static String PROP_DRAWER_PULL_REPORT = "drawerPullReport";
    public static String PROP_ID = "id";
    public static String PROP_RESET_TIME = "resetTime";
    public static String PROP_RESETED_BY = "resetedBy";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Date resetTime;
    private DrawerPullReport drawerPullReport;
    private User resetedBy;

    public BaseCashDrawerResetHistory() {
        this.initialize();
    }

    public BaseCashDrawerResetHistory(Integer id) {
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

    public Date getResetTime() {
        return this.resetTime;
    }

    public void setResetTime(Date resetTime) {
        this.resetTime = resetTime;
    }

    public DrawerPullReport getDrawerPullReport() {
        return this.drawerPullReport;
    }

    public void setDrawerPullReport(DrawerPullReport drawerPullReport) {
        this.drawerPullReport = drawerPullReport;
    }

    public User getResetedBy() {
        return this.resetedBy;
    }

    public void setResetedBy(User resetedBy) {
        this.resetedBy = resetedBy;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof CashDrawerResetHistory)) {
            return false;
        }
        CashDrawerResetHistory cashDrawerResetHistory = (CashDrawerResetHistory)obj;
        if (null == this.getId() || null == cashDrawerResetHistory.getId()) {
            return false;
        }
        return this.getId().equals(cashDrawerResetHistory.getId());
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

