/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.InventoryUnit;
import java.io.Serializable;

public abstract class BaseInventoryUnit
implements Comparable,
Serializable {
    public static String REF = "InventoryUnit";
    public static String PROP_SHORT_NAME = "shortName";
    public static String PROP_CONVERSION_FACTOR2 = "conversionFactor2";
    public static String PROP_ALTERNATIVE_NAME = "alternativeName";
    public static String PROP_CONVERSION_FACTOR1 = "conversionFactor1";
    public static String PROP_ID = "id";
    public static String PROP_CONVERSION_FACTOR3 = "conversionFactor3";
    public static String PROP_LONG_NAME = "longName";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String shortName;
    protected String longName;
    protected String alternativeName;
    protected String conversionFactor1;
    protected String conversionFactor2;
    protected String conversionFactor3;

    public BaseInventoryUnit() {
        this.initialize();
    }

    public BaseInventoryUnit(Integer id) {
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

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return this.longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getAlternativeName() {
        return this.alternativeName;
    }

    public void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }

    public String getConversionFactor1() {
        return this.conversionFactor1;
    }

    public void setConversionFactor1(String conversionFactor1) {
        this.conversionFactor1 = conversionFactor1;
    }

    public String getConversionFactor2() {
        return this.conversionFactor2;
    }

    public void setConversionFactor2(String conversionFactor2) {
        this.conversionFactor2 = conversionFactor2;
    }

    public String getConversionFactor3() {
        return this.conversionFactor3;
    }

    public void setConversionFactor3(String conversionFactor3) {
        this.conversionFactor3 = conversionFactor3;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof InventoryUnit)) {
            return false;
        }
        InventoryUnit inventoryUnit = (InventoryUnit)obj;
        if (null == this.getId() || null == inventoryUnit.getId()) {
            return false;
        }
        return this.getId().equals(inventoryUnit.getId());
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

