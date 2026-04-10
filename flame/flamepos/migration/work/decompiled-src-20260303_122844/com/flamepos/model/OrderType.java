/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseOrderType;

public class OrderType
extends BaseOrderType {
    private static final long serialVersionUID = 1L;
    public static final String BAR_TAB = "BAR_TAB";
    public static final String FOR_HERE = "FOR HERE";
    public static final String TO_GO = "TO GO";

    public OrderType() {
    }

    public OrderType(Integer id) {
        super(id);
    }

    public OrderType(Integer id, String name) {
        super(id, name);
    }

    public String name() {
        return super.getName();
    }

    public OrderType valueOf() {
        return this;
    }

    @Override
    public String toString() {
        return this.getName().replaceAll("_", " ");
    }
}

