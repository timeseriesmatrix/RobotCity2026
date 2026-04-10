/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuModifierGroup;
import java.io.Serializable;

public abstract class BaseMenuItemModifierGroup
implements Comparable,
Serializable {
    public static String REF = "MenuItemModifierGroup";
    public static String PROP_MIN_QUANTITY = "minQuantity";
    public static String PROP_SORT_ORDER = "sortOrder";
    public static String PROP_ID = "id";
    public static String PROP_MODIFIER_GROUP = "modifierGroup";
    public static String PROP_MAX_QUANTITY = "maxQuantity";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Integer minQuantity;
    protected Integer maxQuantity;
    protected Integer sortOrder;
    private MenuModifierGroup modifierGroup;

    public BaseMenuItemModifierGroup() {
        this.initialize();
    }

    public BaseMenuItemModifierGroup(Integer id) {
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

    public Integer getMinQuantity() {
        return this.minQuantity == null ? Integer.valueOf(0) : this.minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Integer getMaxQuantity() {
        return this.maxQuantity == null ? Integer.valueOf(0) : this.maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public Integer getSortOrder() {
        return this.sortOrder == null ? Integer.valueOf(0) : this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public MenuModifierGroup getModifierGroup() {
        return this.modifierGroup;
    }

    public void setModifierGroup(MenuModifierGroup modifierGroup) {
        this.modifierGroup = modifierGroup;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof MenuItemModifierGroup)) {
            return false;
        }
        MenuItemModifierGroup menuItemModifierGroup = (MenuItemModifierGroup)obj;
        if (null == this.getId() || null == menuItemModifierGroup.getId()) {
            return false;
        }
        return this.getId().equals(menuItemModifierGroup.getId());
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

