/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.PizzaCrust;
import java.io.Serializable;

public abstract class BasePizzaCrust
implements Comparable,
Serializable {
    public static String REF = "PizzaCrust";
    public static String PROP_DESCRIPTION = "description";
    public static String PROP_TRANSLATED_NAME = "translatedName";
    public static String PROP_SORT_ORDER = "sortOrder";
    public static String PROP_DEFAULT_CRUST = "defaultCrust";
    public static String PROP_ID = "id";
    public static String PROP_NAME = "name";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected String translatedName;
    protected String description;
    protected Integer sortOrder;
    protected Boolean defaultCrust;

    public BasePizzaCrust() {
        this.initialize();
    }

    public BasePizzaCrust(Integer id) {
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

    public String getTranslatedName() {
        return this.translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return this.sortOrder == null ? Integer.valueOf(0) : this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean isDefaultCrust() {
        return this.defaultCrust == null ? Boolean.FALSE : this.defaultCrust;
    }

    public void setDefaultCrust(Boolean defaultCrust) {
        this.defaultCrust = defaultCrust;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof PizzaCrust)) {
            return false;
        }
        PizzaCrust pizzaCrust = (PizzaCrust)obj;
        if (null == this.getId() || null == pizzaCrust.getId()) {
            return false;
        }
        return this.getId().equals(pizzaCrust.getId());
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

