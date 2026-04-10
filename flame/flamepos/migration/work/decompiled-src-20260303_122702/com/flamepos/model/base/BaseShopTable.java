/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.ShopFloor;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.ShopTableType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseShopTable
implements Comparable,
Serializable {
    public static String REF = "ShopTable";
    public static String PROP_NAME = "name";
    public static String PROP_DESCRIPTION = "description";
    public static String PROP_DISABLE = "disable";
    public static String PROP_ID = "id";
    public static String PROP_DIRTY = "dirty";
    public static String PROP_CAPACITY = "capacity";
    public static String PROP_BOOKED = "booked";
    public static String PROP_Y = "y";
    public static String PROP_X = "x";
    public static String PROP_FLOOR = "floor";
    public static String PROP_SERVING = "serving";
    public static String PROP_FREE = "free";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected String description;
    protected Integer capacity;
    protected Integer x;
    protected Integer y;
    protected Boolean free;
    protected Boolean serving;
    protected Boolean booked;
    protected Boolean dirty;
    protected Boolean disable;
    private ShopFloor floor;
    private List<ShopTableType> types;

    public BaseShopTable() {
        this.initialize();
    }

    public BaseShopTable(Integer id) {
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacity() {
        return this.capacity == null ? Integer.valueOf(0) : this.capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getX() {
        return this.x == null ? Integer.valueOf(0) : this.x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return this.y == null ? Integer.valueOf(0) : this.y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Boolean isFree() {
        return this.free == null ? Boolean.FALSE : this.free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public Boolean isServing() {
        return this.serving == null ? Boolean.FALSE : this.serving;
    }

    public void setServing(Boolean serving) {
        this.serving = serving;
    }

    public Boolean isBooked() {
        return this.booked == null ? Boolean.FALSE : this.booked;
    }

    public void setBooked(Boolean booked) {
        this.booked = booked;
    }

    public Boolean isDirty() {
        return this.dirty == null ? Boolean.FALSE : this.dirty;
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    public Boolean isDisable() {
        return this.disable == null ? Boolean.FALSE : this.disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public ShopFloor getFloor() {
        return this.floor;
    }

    public void setFloor(ShopFloor floor) {
        this.floor = floor;
    }

    public List<ShopTableType> getTypes() {
        return this.types;
    }

    public void setTypes(List<ShopTableType> types) {
        this.types = types;
    }

    public void addTotypes(ShopTableType shopTableType) {
        if (null == this.getTypes()) {
            this.setTypes(new ArrayList<ShopTableType>());
        }
        this.getTypes().add(shopTableType);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ShopTable)) {
            return false;
        }
        ShopTable shopTable = (ShopTable)obj;
        if (null == this.getId() || null == shopTable.getId()) {
            return false;
        }
        return this.getId().equals(shopTable.getId());
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

