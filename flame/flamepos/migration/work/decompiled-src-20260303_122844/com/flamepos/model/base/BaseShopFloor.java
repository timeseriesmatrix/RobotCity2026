/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.ShopFloor;
import com.floreantpos.model.ShopTable;
import java.io.Serializable;
import java.sql.Blob;
import java.util.Set;
import java.util.TreeSet;

public abstract class BaseShopFloor
implements Comparable,
Serializable {
    public static String REF = "ShopFloor";
    public static String PROP_NAME = "name";
    public static String PROP_IMAGE = "image";
    public static String PROP_OCCUPIED = "occupied";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected Boolean occupied;
    protected Blob image;
    private Set<ShopTable> tables;

    public BaseShopFloor() {
        this.initialize();
    }

    public BaseShopFloor(Integer id) {
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

    public Boolean isOccupied() {
        return this.occupied == null ? Boolean.FALSE : this.occupied;
    }

    public void setOccupied(Boolean occupied) {
        this.occupied = occupied;
    }

    public Blob getImage() {
        return this.image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public Set<ShopTable> getTables() {
        return this.tables;
    }

    public void setTables(Set<ShopTable> tables) {
        this.tables = tables;
    }

    public void addTotables(ShopTable shopTable) {
        if (null == this.getTables()) {
            this.setTables(new TreeSet<ShopTable>());
        }
        this.getTables().add(shopTable);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ShopFloor)) {
            return false;
        }
        ShopFloor shopFloor = (ShopFloor)obj;
        if (null == this.getId() || null == shopFloor.getId()) {
            return false;
        }
        return this.getId().equals(shopFloor.getId());
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

