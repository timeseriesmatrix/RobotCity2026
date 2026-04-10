/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.PayoutReason;
import java.io.Serializable;

public abstract class BasePayoutReason
implements Comparable,
Serializable {
    public static String REF = "PayoutReason";
    public static String PROP_ID = "id";
    public static String PROP_REASON = "reason";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    private String reason;

    public BasePayoutReason() {
        this.initialize();
    }

    public BasePayoutReason(Integer id) {
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

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof PayoutReason)) {
            return false;
        }
        PayoutReason payoutReason = (PayoutReason)obj;
        if (null == this.getId() || null == payoutReason.getId()) {
            return false;
        }
        return this.getId().equals(payoutReason.getId());
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

