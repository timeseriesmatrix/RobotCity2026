/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.DeliveryConfiguration;
import java.io.Serializable;

public abstract class BaseDeliveryConfiguration
implements Comparable,
Serializable {
    public static String REF = "DeliveryConfiguration";
    public static String PROP_UNIT_NAME = "unitName";
    public static String PROP_ID = "id";
    public static String PROP_CHARGE_BY_ZIP_CODE = "chargeByZipCode";
    public static String PROP_UNIT_SYMBOL = "unitSymbol";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String unitName;
    protected String unitSymbol;
    protected Boolean chargeByZipCode;

    public BaseDeliveryConfiguration() {
        this.initialize();
    }

    public BaseDeliveryConfiguration(Integer id) {
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

    public String getUnitName() {
        return this.unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitSymbol() {
        return this.unitSymbol;
    }

    public void setUnitSymbol(String unitSymbol) {
        this.unitSymbol = unitSymbol;
    }

    public Boolean isChargeByZipCode() {
        return this.chargeByZipCode == null ? Boolean.FALSE : this.chargeByZipCode;
    }

    public void setChargeByZipCode(Boolean chargeByZipCode) {
        this.chargeByZipCode = chargeByZipCode;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof DeliveryConfiguration)) {
            return false;
        }
        DeliveryConfiguration deliveryConfiguration = (DeliveryConfiguration)obj;
        if (null == this.getId() || null == deliveryConfiguration.getId()) {
            return false;
        }
        return this.getId().equals(deliveryConfiguration.getId());
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

