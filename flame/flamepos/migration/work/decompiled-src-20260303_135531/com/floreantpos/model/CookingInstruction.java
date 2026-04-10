/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseCookingInstruction;

public class CookingInstruction
extends BaseCookingInstruction {
    private static final long serialVersionUID = 1L;

    public CookingInstruction() {
    }

    public CookingInstruction(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return this.getDescription();
    }
}

