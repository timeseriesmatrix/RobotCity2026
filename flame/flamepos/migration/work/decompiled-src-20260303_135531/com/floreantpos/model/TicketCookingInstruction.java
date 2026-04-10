/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseTicketCookingInstruction;

public class TicketCookingInstruction
extends BaseTicketCookingInstruction {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return this.getDescription();
    }
}

