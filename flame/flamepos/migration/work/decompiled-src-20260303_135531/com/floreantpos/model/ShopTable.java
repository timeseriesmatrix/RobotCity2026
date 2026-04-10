/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.ShopFloor;
import com.floreantpos.model.base.BaseShopTable;

public class ShopTable
extends BaseShopTable {
    private static final long serialVersionUID = 1L;
    private boolean isTemporary;

    public ShopTable() {
    }

    public ShopTable(Integer id) {
        super(id);
    }

    public ShopTable(Integer x, Integer y) {
        this.setX(x);
        this.setY(y);
    }

    public ShopTable(ShopFloor floor, Integer x, Integer y, Integer id) {
        this.setCapacity(4);
        this.setId(id);
        this.setFloor(floor);
        this.setX(x);
        this.setY(y);
    }

    public Integer getTableNumber() {
        return this.getId();
    }

    @Override
    public String toString() {
        return String.valueOf(this.getTableNumber());
    }

    public boolean isTemporary() {
        return this.isTemporary;
    }

    public void setTemporary(boolean isTemporary) {
        this.isTemporary = isTemporary;
    }
}

