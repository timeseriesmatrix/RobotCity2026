/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.DrawerAssignedHistory;
import com.floreantpos.model.User;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseDrawerAssignedHistory
implements Comparable,
Serializable {
    public static String REF = "DrawerAssignedHistory";
    public static String PROP_USER = "user";
    public static String PROP_OPERATION = "operation";
    public static String PROP_TIME = "time";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Date time;
    protected String operation;
    private User user;

    public BaseDrawerAssignedHistory() {
        this.initialize();
    }

    public BaseDrawerAssignedHistory(Integer id) {
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

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof DrawerAssignedHistory)) {
            return false;
        }
        DrawerAssignedHistory drawerAssignedHistory = (DrawerAssignedHistory)obj;
        if (null == this.getId() || null == drawerAssignedHistory.getId()) {
            return false;
        }
        return this.getId().equals(drawerAssignedHistory.getId());
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

