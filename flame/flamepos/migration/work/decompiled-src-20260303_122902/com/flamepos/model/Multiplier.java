/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseMultiplier;

public class Multiplier
extends BaseMultiplier {
    private static final long serialVersionUID = 1L;
    public static final String REGULAR = "Regular";

    public Multiplier() {
    }

    public Multiplier(String name) {
        super(name);
    }

    @Override
    public Integer getButtonColor() {
        return this.buttonColor;
    }

    @Override
    public Integer getTextColor() {
        return this.textColor;
    }
}

