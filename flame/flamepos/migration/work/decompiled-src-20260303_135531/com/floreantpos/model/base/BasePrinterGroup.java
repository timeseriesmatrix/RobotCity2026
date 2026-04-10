/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.PrinterGroup;
import java.io.Serializable;
import java.util.List;

public abstract class BasePrinterGroup
implements Comparable,
Serializable {
    public static String REF = "PrinterGroup";
    public static String PROP_IS_DEFAULT = "isDefault";
    public static String PROP_ID = "id";
    public static String PROP_NAME = "name";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected boolean isDefault;
    private List<String> printerNames;

    public BasePrinterGroup() {
        this.initialize();
    }

    public BasePrinterGroup(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BasePrinterGroup(Integer id, String name) {
        this.setId(id);
        this.setName(name);
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

    public boolean isIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public List<String> getPrinterNames() {
        return this.printerNames;
    }

    public void setPrinterNames(List<String> printerNames) {
        this.printerNames = printerNames;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof PrinterGroup)) {
            return false;
        }
        PrinterGroup printerGroup = (PrinterGroup)obj;
        if (null == this.getId() || null == printerGroup.getId()) {
            return false;
        }
        return this.getId().equals(printerGroup.getId());
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

