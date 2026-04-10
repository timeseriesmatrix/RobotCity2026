/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.PackagingUnit;
import java.io.Serializable;

public abstract class BasePackagingUnit
implements Comparable,
Serializable {
    public static String REF = "PackagingUnit";
    public static String PROP_NAME = "name";
    public static String PROP_FACTOR = "factor";
    public static String PROP_SHORT_NAME = "shortName";
    public static String PROP_ID = "id";
    public static String PROP_DIMENSION = "dimension";
    public static String PROP_BASE_UNIT = "baseUnit";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected String shortName;
    protected Double factor;
    protected Boolean baseUnit;
    protected String dimension;

    public BasePackagingUnit() {
        this.initialize();
    }

    public BasePackagingUnit(Integer id) {
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

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Double getFactor() {
        return this.factor == null ? Double.valueOf(0.0) : this.factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public Boolean isBaseUnit() {
        return this.baseUnit == null ? Boolean.FALSE : this.baseUnit;
    }

    public void setBaseUnit(Boolean baseUnit) {
        this.baseUnit = baseUnit;
    }

    public String getDimension() {
        return this.dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof PackagingUnit)) {
            return false;
        }
        PackagingUnit packagingUnit = (PackagingUnit)obj;
        if (null == this.getId() || null == packagingUnit.getId()) {
            return false;
        }
        return this.getId().equals(packagingUnit.getId());
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

