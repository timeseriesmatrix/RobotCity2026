/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseDeliveryInstruction;

public class DeliveryInstruction
extends BaseDeliveryInstruction {
    private static final long serialVersionUID = 1L;

    public DeliveryInstruction() {
    }

    public DeliveryInstruction(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return super.getNotes();
    }
}

