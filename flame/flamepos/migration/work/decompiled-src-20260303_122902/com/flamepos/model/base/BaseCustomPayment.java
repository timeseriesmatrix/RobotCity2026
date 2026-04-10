/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.CustomPayment;
import java.io.Serializable;

public abstract class BaseCustomPayment
implements Comparable,
Serializable {
    public static String REF = "CustomPayment";
    public static String PROP_NAME = "name";
    public static String PROP_REQUIRED_REF_NUMBER = "requiredRefNumber";
    public static String PROP_REF_NUMBER_FIELD_NAME = "refNumberFieldName";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected Boolean requiredRefNumber;
    protected String refNumberFieldName;

    public BaseCustomPayment() {
        this.initialize();
    }

    public BaseCustomPayment(Integer id) {
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

    public Boolean isRequiredRefNumber() {
        return this.requiredRefNumber == null ? Boolean.FALSE : this.requiredRefNumber;
    }

    public void setRequiredRefNumber(Boolean requiredRefNumber) {
        this.requiredRefNumber = requiredRefNumber;
    }

    public String getRefNumberFieldName() {
        return this.refNumberFieldName;
    }

    public void setRefNumberFieldName(String refNumberFieldName) {
        this.refNumberFieldName = refNumberFieldName;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof CustomPayment)) {
            return false;
        }
        CustomPayment customPayment = (CustomPayment)obj;
        if (null == this.getId() || null == customPayment.getId()) {
            return false;
        }
        return this.getId().equals(customPayment.getId());
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

