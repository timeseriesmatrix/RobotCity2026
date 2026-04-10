/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseInventoryGroup;

public class InventoryGroup
extends BaseInventoryGroup {
    private static final long serialVersionUID = 1L;

    public InventoryGroup() {
    }

    public InventoryGroup(Integer id) {
        super(id);
    }

    public InventoryGroup(Integer id, String name) {
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

