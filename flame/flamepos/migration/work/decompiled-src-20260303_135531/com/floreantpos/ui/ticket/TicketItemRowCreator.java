/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.ticket;

import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemCookingInstruction;
import com.floreantpos.model.TicketItemDiscount;
import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.util.DiscountUtil;
import java.util.List;
import java.util.Map;

public class TicketItemRowCreator {
    public static void calculateTicketRows(Ticket ticket, Map<String, ITicketItem> tableRows) {
        TicketItemRowCreator.calculateTicketRows(ticket, tableRows, true, true, true);
    }

    public static void calculateTicketRows(Ticket ticket, Map<String, ITicketItem> tableRows, boolean includeModifiers, boolean includeAddOns, boolean includeCookingInstructions) {
        TicketItemRowCreator.calculateTicketRows(ticket, tableRows, true, true, true, true);
    }

    public static void calculateTicketRows(Ticket ticket, Map<String, ITicketItem> tableRows, boolean includeModifiers, boolean includeAddOns, boolean includeCookingInstructions, boolean includeDiscounts) {
        tableRows.clear();
        int rowNum = 0;
        if (ticket == null || ticket.getTicketItems() == null) {
            return;
        }
        List<TicketItem> ticketItems = ticket.getTicketItems();
        for (TicketItem ticketItem : ticketItems) {
            ticketItem.setTableRowNum(rowNum);
            tableRows.put(String.valueOf(rowNum), ticketItem);
            ++rowNum;
            if (includeDiscounts) {
                rowNum = TicketItemRowCreator.includeDiscounts(ticketItem, tableRows, rowNum);
            }
            if (includeModifiers) {
                rowNum = TicketItemRowCreator.includeModifiers(ticketItem, tableRows, rowNum, false);
            }
            if (includeAddOns) {
                rowNum = TicketItemRowCreator.includeAddOns(ticketItem, tableRows, rowNum);
            }
            if (!includeCookingInstructions) continue;
            rowNum = TicketItemRowCreator.includeCookintInstructions(ticketItem, tableRows, rowNum);
        }
    }

    private static int includeCookintInstructions(TicketItem ticketItem, Map<String, ITicketItem> tableRows, int rowNum) {
        List<TicketItemCookingInstruction> cookingInstructions = ticketItem.getCookingInstructions();
        if (cookingInstructions != null) {
            for (TicketItemCookingInstruction ticketItemCookingInstruction : cookingInstructions) {
                ticketItemCookingInstruction.setTableRowNum(rowNum);
                tableRows.put(String.valueOf(rowNum), ticketItemCookingInstruction);
                ++rowNum;
            }
        }
        return rowNum;
    }

    private static int includeDiscounts(TicketItem ticketItem, Map<String, ITicketItem> tableRows, int rowNum) {
        TicketItemDiscount maxDiscount = DiscountUtil.getMaxDiscount(ticketItem.getDiscounts());
        if (maxDiscount != null) {
            tableRows.put(String.valueOf(rowNum), maxDiscount);
            ++rowNum;
        }
        return rowNum;
    }

    private static int includeModifiers(TicketItem ticketItem, Map<String, ITicketItem> tableRows, int rowNum, boolean kitchenPrint) {
        List<TicketItemModifier> ticketItemModifiers = ticketItem.getTicketItemModifiers();
        if (ticketItemModifiers != null && ticketItemModifiers.size() > 0) {
            for (TicketItemModifier itemModifier : ticketItemModifiers) {
                if (kitchenPrint && (itemModifier.isPrintedToKitchen().booleanValue() || !itemModifier.isShouldPrintToKitchen().booleanValue())) continue;
                itemModifier.setTableRowNum(rowNum);
                tableRows.put(String.valueOf(rowNum), itemModifier);
                ++rowNum;
            }
        }
        return rowNum;
    }

    private static int includeAddOns(TicketItem ticketItem, Map<String, ITicketItem> tableRows, int rowNum) {
        List<TicketItemModifier> ticketItemAddOns = ticketItem.getAddOns();
        if (ticketItemAddOns != null) {
            for (TicketItemModifier ticketItemDiscount : ticketItemAddOns) {
                ticketItemDiscount.setTableRowNum(rowNum);
                tableRows.put(String.valueOf(rowNum), ticketItemDiscount);
                ++rowNum;
            }
        }
        return rowNum;
    }
}

