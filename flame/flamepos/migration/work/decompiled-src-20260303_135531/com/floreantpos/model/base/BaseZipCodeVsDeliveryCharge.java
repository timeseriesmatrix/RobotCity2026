/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.ZipCodeVsDeliveryCharge;
import java.io.Serializable;

public abstract class BaseZipCodeVsDeliveryCharge
implements Comparable,
Serializable {
    public static String REF = "ZipCodeVsDeliveryCharge";
    public static String PROP_DELIVERY_CHARGE = "deliveryCharge";
    public static String PROP_ID = "id";
    public static String PROP_ZIP_CODE = "zipCode";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String zipCode;
    protected double deliveryCharge;

    public BaseZipCodeVsDeliveryCharge() {
        this.initialize();
    }

    public BaseZipCodeVsDeliveryCharge(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseZipCodeVsDeliveryCharge(Integer id, String zipCode, double deliveryCharge) {
        this.setId(id);
        this.setZipCode(zipCode);
        this.setDeliveryCharge(deliveryCharge);
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

    public String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public double getDeliveryCharge() {
        return this.deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ZipCodeVsDeliveryCharge)) {
            return false;
        }
        ZipCodeVsDeliveryCharge zipCodeVsDeliveryCharge = (ZipCodeVsDeliveryCharge)obj;
        if (null == this.getId() || null == zipCodeVsDeliveryCharge.getId()) {
            return false;
        }
        return this.getId().equals(zipCodeVsDeliveryCharge.getId());
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

