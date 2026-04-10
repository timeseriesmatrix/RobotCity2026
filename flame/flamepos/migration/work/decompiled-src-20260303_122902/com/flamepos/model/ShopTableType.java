/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseShopTableType;

public class ShopTableType
extends BaseShopTableType {
    private static final long serialVersionUID = 1L;

    public ShopTableType() {
    }

    public ShopTableType(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

