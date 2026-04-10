/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.DataUpdateInfo;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseDataUpdateInfo
implements Comparable,
Serializable {
    public static String REF = "DataUpdateInfo";
    public static String PROP_ID = "id";
    public static String PROP_LAST_UPDATE_TIME = "lastUpdateTime";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Date lastUpdateTime;

    public BaseDataUpdateInfo() {
        this.initialize();
    }

    public BaseDataUpdateInfo(Integer id) {
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

    public Date getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof DataUpdateInfo)) {
            return false;
        }
        DataUpdateInfo dataUpdateInfo = (DataUpdateInfo)obj;
        if (null == this.getId() || null == dataUpdateInfo.getId()) {
            return false;
        }
        return this.getId().equals(dataUpdateInfo.getId());
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

