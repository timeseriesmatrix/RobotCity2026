/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseKitchenTicketItem;

public class KitchenTicketItem
extends BaseKitchenTicketItem {
    private static final long serialVersionUID = 1L;

    public KitchenTicketItem() {
    }

    public KitchenTicketItem(Integer id) {
        super(id);
    }

    @Override
    public Boolean isCookable() {
        return this.cookable == null ? Boolean.TRUE : this.cookable;
    }

    @Override
    public String getMenuItemGroupName() {
        if (super.getMenuItemGroupName() == null) {
            return "";
        }
        return super.getMenuItemGroupName();
    }
}

