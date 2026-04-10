/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.base.BaseDiscount;

public class Discount
extends BaseDiscount {
    private static final long serialVersionUID = 1L;
    public static final int FREE_AMOUNT = 0;
    public static final int FIXED_PER_CATEGORY = 1;
    public static final int FIXED_PER_ITEM = 2;
    public static final int FIXED_PER_ORDER = 3;
    public static final int PERCENTAGE_PER_CATEGORY = 4;
    public static final int PERCENTAGE_PER_ITEM = 5;
    public static final int PERCENTAGE_PER_ORDER = 6;
    public static final int DISCOUNT_TYPE_AMOUNT = 0;
    public static final int DISCOUNT_TYPE_PERCENTAGE = 1;
    public static final int DISCOUNT_TYPE_RE_PRICE = 3;
    public static final int DISCOUNT_TYPE_ALT_PRICE = 4;
    public static final int QUALIFICATION_TYPE_ITEM = 0;
    public static final int QUALIFICATION_TYPE_ORDER = 1;
    public static final String[] COUPON_TYPE_NAMES = new String[]{"AMOUNT", "PERCENTAGE"};
    public static final String[] COUPON_QUALIFICATION_NAMES = new String[]{"ITEM", "ORDER"};

    public Discount() {
    }

    public Discount(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return COUPON_TYPE_NAMES[this.getType()];
    }

    public double calculateDiscount(ITicketItem ticketItem) {
        switch (this.getType()) {
            case 0: {
                return ticketItem.getUnitPriceDisplay();
            }
            case 1: {
                return this.getValue() * ticketItem.getUnitPriceDisplay() / 100.0;
            }
        }
        return 0.0;
    }

    public double getAmountByType(double price) {
        switch (this.getType()) {
            case 0: {
                return price - this.getValue();
            }
            case 1: {
                return price - this.getValue() / 100.0;
            }
        }
        return 0.0;
    }
}

