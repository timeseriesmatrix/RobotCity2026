/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.User;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseActionHistory
implements Comparable,
Serializable {
    public static String REF = "ActionHistory";
    public static String PROP_PERFORMER = "performer";
    public static String PROP_DESCRIPTION = "description";
    public static String PROP_ACTION_NAME = "actionName";
    public static String PROP_ACTION_TIME = "actionTime";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    private Date actionTime;
    private String actionName;
    private String description;
    private User performer;

    public BaseActionHistory() {
        this.initialize();
    }

    public BaseActionHistory(Integer id) {
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

    public Date getActionTime() {
        return this.actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public String getActionName() {
        return this.actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getPerformer() {
        return this.performer;
    }

    public void setPerformer(User performer) {
        this.performer = performer;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ActionHistory)) {
            return false;
        }
        ActionHistory actionHistory = (ActionHistory)obj;
        if (null == this.getId() || null == actionHistory.getId()) {
            return false;
        }
        return this.getId().equals(actionHistory.getId());
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

