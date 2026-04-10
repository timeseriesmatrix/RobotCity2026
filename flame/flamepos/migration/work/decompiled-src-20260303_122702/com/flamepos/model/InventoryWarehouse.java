/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseInventoryWarehouse;

public class InventoryWarehouse
extends BaseInventoryWarehouse {
    private static final long serialVersionUID = 1L;

    public InventoryWarehouse() {
    }

    public InventoryWarehouse(Integer id) {
        super(id);
    }

    public InventoryWarehouse(Integer id, String name) {
        super(id, name);
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

