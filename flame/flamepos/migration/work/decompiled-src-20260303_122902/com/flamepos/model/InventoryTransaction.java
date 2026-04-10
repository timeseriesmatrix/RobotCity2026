/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.InventoryTransactionType;
import com.floreantpos.model.base.BaseInventoryTransaction;

public class InventoryTransaction
extends BaseInventoryTransaction {
    private static final long serialVersionUID = 1L;
    public static String PROP_TYPE = "type";
    protected InventoryTransactionType type;

    public InventoryTransaction() {
    }

    public InventoryTransaction(Integer id) {
        super(id);
    }

    public InventoryTransactionType getType() {
        return this.type;
    }

    public void setType(InventoryTransactionType type) {
        this.type = type;
    }
}

