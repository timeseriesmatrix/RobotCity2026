/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseInventoryVendor;

public class InventoryVendor
extends BaseInventoryVendor {
    private static final long serialVersionUID = 1L;

    public InventoryVendor() {
    }

    public InventoryVendor(Integer id) {
        super(id);
    }

    public InventoryVendor(Integer id, String name, String address, String city, String state, String zip, String country, String email, String phone) {
        super(id, name, address, city, state, zip, country, email, phone);
    }

    @Override
    public Boolean isVisible() {
        return this.visible == null ? true : this.visible;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

