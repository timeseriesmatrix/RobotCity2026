/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.MenuItemSize;
import java.io.Serializable;

public abstract class BaseMenuItemSize
implements Comparable,
Serializable {
    public static String REF = "MenuItemSize";
    public static String PROP_DESCRIPTION = "description";
    public static String PROP_TRANSLATED_NAME = "translatedName";
    public static String PROP_SIZE_IN_INCH = "sizeInInch";
    public static String PROP_SORT_ORDER = "sortOrder";
    public static String PROP_ID = "id";
    public static String PROP_NAME = "name";
    public static String PROP_DEFAULT_SIZE = "defaultSize";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected String translatedName;
    protected String description;
    protected Integer sortOrder;
    protected Double sizeInInch;
    protected Boolean defaultSize;

    public BaseMenuItemSize() {
        this.initialize();
    }

    public BaseMenuItemSize(Integer id) {
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

    public Double getSizeInInch() {
        return this.sizeInInch == null ? Double.valueOf(0.0) : this.sizeInInch;
    }

    public void setSizeInInch(Double sizeInInch) {
        this.sizeInInch = sizeInInch;
    }

    public Boolean isDefaultSize() {
        return this.defaultSize == null ? Boolean.FALSE : this.defaultSize;
    }

    public void setDefaultSize(Boolean defaultSize) {
        this.defaultSize = defaultSize;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof MenuItemSize)) {
            return false;
        }
        MenuItemSize menuItemSize = (MenuItemSize)obj;
        if (null == this.getId() || null == menuItemSize.getId()) {
            return false;
        }
        return this.getId().equals(menuItemSize.getId());
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

