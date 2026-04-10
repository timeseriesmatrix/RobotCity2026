/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseDeliveryCharge;

public class DeliveryCharge
extends BaseDeliveryCharge {
    private static final long serialVersionUID = 1L;

    public DeliveryCharge() {
    }

    public DeliveryCharge(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return super.getName();
    }
}

