/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

public enum InventoryTransactionType {
    IN(1),
    OUT(-1),
    UNCHANGED(0);

    private int type;

    private InventoryTransactionType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static InventoryTransactionType fromInt(int type) {
        InventoryTransactionType[] values;
        for (InventoryTransactionType inOutEnum : values = InventoryTransactionType.values()) {
            if (inOutEnum.type != type) continue;
            return inOutEnum;
        }
        return UNCHANGED;
    }

    public String toString() {
        return this.name();
    }
}

