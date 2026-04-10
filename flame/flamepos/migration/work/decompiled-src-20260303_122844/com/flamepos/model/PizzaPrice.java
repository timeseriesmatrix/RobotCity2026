/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BasePizzaPrice;

public class PizzaPrice
extends BasePizzaPrice {
    private static final long serialVersionUID = 1L;

    public PizzaPrice() {
    }

    public PizzaPrice(Integer id) {
        super(id);
    }

    public Double getPrice(int defaultSellPortion) {
        return super.getPrice() * (double)defaultSellPortion / 100.0;
    }
}

