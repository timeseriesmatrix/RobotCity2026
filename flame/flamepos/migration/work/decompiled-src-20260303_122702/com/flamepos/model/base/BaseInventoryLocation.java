/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.InventoryLocation;
import com.floreantpos.model.InventoryWarehouse;
import java.io.Serializable;

public abstract class BaseInventoryLocation
implements Comparable,
Serializable {
    public static String REF = "InventoryLocation";
    public static String PROP_NAME = "name";
    public static String PROP_WAREHOUSE = "warehouse";
    public static String PROP_VISIBLE = "visible";
    public static String PROP_SORT_ORDER = "sortOrder";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected Integer sortOrder;
    protected Boolean visible;
    private InventoryWarehouse warehouse;

    public BaseInventoryLocation() {
        this.initialize();
    }

    public BaseInventoryLocation(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseInventoryLocation(Integer id, String name) {
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

    public Integer getSortOrder() {
        return this.sortOrder == null ? Integer.valueOf(0) : this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean isVisible() {
        return this.visible == null ? Boolean.FALSE : this.visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public InventoryWarehouse getWarehouse() {
        return this.warehouse;
    }

    public void setWarehouse(InventoryWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof InventoryLocation)) {
            return false;
        }
        InventoryLocation inventoryLocation = (InventoryLocation)obj;
        if (null == this.getId() || null == inventoryLocation.getId()) {
            return false;
        }
        return this.getId().equals(inventoryLocation.getId());
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

