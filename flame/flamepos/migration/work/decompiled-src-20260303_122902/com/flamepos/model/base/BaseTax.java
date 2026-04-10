/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Tax;
import java.io.Serializable;

public abstract class BaseTax
implements Comparable,
Serializable {
    public static String REF = "Tax";
    public static String PROP_NAME = "name";
    public static String PROP_ID = "id";
    public static String PROP_RATE = "rate";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected Double rate;

    public BaseTax() {
        this.initialize();
    }

    public BaseTax(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseTax(Integer id, String name) {
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

    public Double getRate() {
        return this.rate == null ? Double.valueOf(0.0) : this.rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Tax)) {
            return false;
        }
        Tax tax = (Tax)obj;
        if (null == this.getId() || null == tax.getId()) {
            return false;
        }
        return this.getId().equals(tax.getId());
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

