/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseVoidReason;

public class VoidReason
extends BaseVoidReason {
    private static final long serialVersionUID = 1L;

    public VoidReason() {
    }

    public VoidReason(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return this.getReasonText();
    }
}

