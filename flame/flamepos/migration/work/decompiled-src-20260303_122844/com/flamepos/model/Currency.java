/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseCurrency;

public class Currency
extends BaseCurrency {
    private static final long serialVersionUID = 1L;

    public Currency() {
    }

    public Currency(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

