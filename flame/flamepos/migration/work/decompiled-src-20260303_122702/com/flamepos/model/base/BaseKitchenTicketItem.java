/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.KitchenTicketItem;
import java.io.Serializable;

public abstract class BaseKitchenTicketItem
implements Comparable,
Serializable {
    public static String REF = "KitchenTicketItem";
    public static String PROP_UNIT_NAME = "unitName";
    public static String PROP_TICKET_ITEM_ID = "ticketItemId";
    public static String PROP_QUANTITY = "quantity";
    public static String PROP_SORT_ORDER = "sortOrder";
    public static String PROP_MENU_ITEM_GROUP_NAME = "menuItemGroupName";
    public static String PROP_COOKABLE = "cookable";
    public static String PROP_TICKET_ITEM_MODIFIER_ID = "ticketItemModifierId";
    public static String PROP_FRACTIONAL_UNIT = "fractionalUnit";
    public static String PROP_STATUS = "status";
    public static String PROP_MENU_ITEM_GROUP_ID = "menuItemGroupId";
    public static String PROP_FRACTIONAL_QUANTITY = "fractionalQuantity";
    public static String PROP_ID = "id";
    public static String PROP_VOIDED = "voided";
    public static String PROP_MENU_ITEM_NAME = "menuItemName";
    public static String PROP_MENU_ITEM_CODE = "menuItemCode";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Boolean cookable;
    protected Integer ticketItemId;
    protected Integer ticketItemModifierId;
    protected String menuItemCode;
    protected String menuItemName;
    protected Integer menuItemGroupId;
    protected String menuItemGroupName;
    protected Integer quantity;
    protected Double fractionalQuantity;
    protected Boolean fractionalUnit;
    protected String unitName;
    protected Integer sortOrder;
    protected Boolean voided;
    protected String status;

    public BaseKitchenTicketItem() {
        this.initialize();
    }

    public BaseKitchenTicketItem(Integer id) {
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

    public Boolean isCookable() {
        return this.cookable == null ? Boolean.FALSE : this.cookable;
    }

    public void setCookable(Boolean cookable) {
        this.cookable = cookable;
    }

    public Integer getTicketItemId() {
        return this.ticketItemId == null ? Integer.valueOf(0) : this.ticketItemId;
    }

    public void setTicketItemId(Integer ticketItemId) {
        this.ticketItemId = ticketItemId;
    }

    public Integer getTicketItemModifierId() {
        return this.ticketItemModifierId == null ? Integer.valueOf(0) : this.ticketItemModifierId;
    }

    public void setTicketItemModifierId(Integer ticketItemModifierId) {
        this.ticketItemModifierId = ticketItemModifierId;
    }

    public String getMenuItemCode() {
        return this.menuItemCode;
    }

    public void setMenuItemCode(String menuItemCode) {
        this.menuItemCode = menuItemCode;
    }

    public String getMenuItemName() {
        return this.menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public Integer getMenuItemGroupId() {
        return this.menuItemGroupId == null ? Integer.valueOf(0) : this.menuItemGroupId;
    }

    public void setMenuItemGroupId(Integer menuItemGroupId) {
        this.menuItemGroupId = menuItemGroupId;
    }

    public String getMenuItemGroupName() {
        return this.menuItemGroupName;
    }

    public void setMenuItemGroupName(String menuItemGroupName) {
        this.menuItemGroupName = menuItemGroupName;
    }

    public Integer getQuantity() {
        return this.quantity == null ? Integer.valueOf(0) : this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getFractionalQuantity() {
        return this.fractionalQuantity == null ? Double.valueOf(0.0) : this.fractionalQuantity;
    }

    public void setFractionalQuantity(Double fractionalQuantity) {
        this.fractionalQuantity = fractionalQuantity;
    }

    public Boolean isFractionalUnit() {
        return this.fractionalUnit == null ? Boolean.FALSE : this.fractionalUnit;
    }

    public void setFractionalUnit(Boolean fractionalUnit) {
        this.fractionalUnit = fractionalUnit;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Integer getSortOrder() {
        return this.sortOrder == null ? Integer.valueOf(0) : this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean isVoided() {
        return this.voided == null ? Boolean.FALSE : this.voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof KitchenTicketItem)) {
            return false;
        }
        KitchenTicketItem kitchenTicketItem = (KitchenTicketItem)obj;
        if (null == this.getId() || null == kitchenTicketItem.getId()) {
            return false;
        }
        return this.getId().equals(kitchenTicketItem.getId());
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

