/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.DeliveryCharge;
import java.io.Serializable;

public abstract class BaseDeliveryCharge
implements Comparable,
Serializable {
    public static String REF = "DeliveryCharge";
    public static String PROP_CHARGE_AMOUNT = "chargeAmount";
    public static String PROP_NAME = "name";
    public static String PROP_START_RANGE = "startRange";
    public static String PROP_ID = "id";
    public static String PROP_END_RANGE = "endRange";
    public static String PROP_ZIP_CODE = "zipCode";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected String zipCode;
    protected Double startRange;
    protected Double endRange;
    protected Double chargeAmount;

    public BaseDeliveryCharge() {
        this.initialize();
    }

    public BaseDeliveryCharge(Integer id) {
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Double getStartRange() {
        return this.startRange == null ? Double.valueOf(0.0) : this.startRange;
    }

    public void setStartRange(Double startRange) {
        this.startRange = startRange;
    }

    public Double getEndRange() {
        return this.endRange == null ? Double.valueOf(0.0) : this.endRange;
    }

    public void setEndRange(Double endRange) {
        this.endRange = endRange;
    }

    public Double getChargeAmount() {
        return this.chargeAmount == null ? Double.valueOf(0.0) : this.chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof DeliveryCharge)) {
            return false;
        }
        DeliveryCharge deliveryCharge = (DeliveryCharge)obj;
        if (null == this.getId() || null == deliveryCharge.getId()) {
            return false;
        }
        return this.getId().equals(deliveryCharge.getId());
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

