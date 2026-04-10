/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BasePayoutReason;

public class PayoutReason
extends BasePayoutReason {
    private static final long serialVersionUID = 1L;

    public PayoutReason() {
    }

    public PayoutReason(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return this.getReason();
    }
}

