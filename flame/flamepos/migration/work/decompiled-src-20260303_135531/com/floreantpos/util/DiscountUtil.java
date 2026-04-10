/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import com.floreantpos.model.TicketDiscount;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemDiscount;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DiscountUtil {
    public static Double calculateDiscountAmount(TicketItemDiscount ticketItemDiscount) {
        TicketItem ticketItem = ticketItemDiscount.getTicketItem();
        int itemCount = ticketItem.getItemCount();
        double subtotalAmount = ticketItem.getSubtotalAmount();
        double amountToBeDiscounted = subtotalAmount / (double)itemCount;
        if (ticketItemDiscount.getMinimumQuantity() > 0) {
            int minQuantity = ticketItemDiscount.getMinimumQuantity();
            switch (ticketItemDiscount.getType()) {
                case 0: {
                    return Math.floor(itemCount / minQuantity) * ticketItemDiscount.getValue();
                }
                case 1: {
                    return Math.floor(itemCount / minQuantity) * (amountToBeDiscounted * ticketItemDiscount.getValue() / 100.0);
                }
            }
        }
        switch (ticketItemDiscount.getType()) {
            case 0: {
                return ticketItemDiscount.getValue();
            }
            case 1: {
                return amountToBeDiscounted * ticketItemDiscount.getValue() / 100.0;
            }
        }
        return 0.0;
    }

    public static Double calculateDiscountAmount(double price, TicketDiscount discount) {
        switch (discount.getType()) {
            case 0: {
                return discount.getValue();
            }
            case 1: {
                return price * discount.getValue() / 100.0;
            }
        }
        return price * discount.getValue() / 100.0;
    }

    public static TicketItemDiscount getMaxDiscount(List<TicketItemDiscount> discounts) {
        if (discounts == null || discounts.isEmpty()) {
            return null;
        }
        TicketItemDiscount maxDiscount = Collections.max(discounts, new Comparator<TicketItemDiscount>(){

            @Override
            public int compare(TicketItemDiscount o1, TicketItemDiscount o2) {
                return (int)(o1.getSubTotalAmountDisplay() - o2.getSubTotalAmountDisplay());
            }
        });
        return maxDiscount;
    }

    public static TicketDiscount getMaxDiscount(List<TicketDiscount> discounts, final double price) {
        if (discounts == null || discounts.isEmpty()) {
            return null;
        }
        TicketDiscount maxDiscount = Collections.max(discounts, new Comparator<TicketDiscount>(){

            @Override
            public int compare(TicketDiscount o1, TicketDiscount o2) {
                return (int)(DiscountUtil.calculateDiscountAmount(price, o1) - DiscountUtil.calculateDiscountAmount(price, o2));
            }
        });
        return maxDiscount;
    }
}

