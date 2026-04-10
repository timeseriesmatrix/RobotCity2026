/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BasePayoutRecepient;

public class PayoutRecepient
extends BasePayoutRecepient {
    private static final long serialVersionUID = 1L;

    public PayoutRecepient() {
    }

    public PayoutRecepient(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

