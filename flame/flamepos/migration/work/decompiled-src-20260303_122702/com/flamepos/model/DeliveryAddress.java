/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseDeliveryAddress;

public class DeliveryAddress
extends BaseDeliveryAddress {
    private static final long serialVersionUID = 1L;

    public DeliveryAddress() {
    }

    public DeliveryAddress(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return super.getAddress();
    }
}

