/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseInventoryLocation;

public class InventoryLocation
extends BaseInventoryLocation {
    private static final long serialVersionUID = 1L;

    public InventoryLocation() {
    }

    public InventoryLocation(Integer id) {
        super(id);
    }

    public InventoryLocation(Integer id, String name) {
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

