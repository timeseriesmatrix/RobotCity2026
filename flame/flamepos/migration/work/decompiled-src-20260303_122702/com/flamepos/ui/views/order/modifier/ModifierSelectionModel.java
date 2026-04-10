/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.order.modifier;

import com.floreantpos.model.MenuItem;
import com.floreantpos.model.TicketItem;

public class ModifierSelectionModel {
    private TicketItem ticketItem;
    private MenuItem menuItem;

    public ModifierSelectionModel() {
    }

    public ModifierSelectionModel(TicketItem ticketItem, MenuItem menuItem) {
        this.ticketItem = ticketItem;
        this.menuItem = menuItem;
    }

    public TicketItem getTicketItem() {
        return this.ticketItem;
    }

    public void setTicketItem(TicketItem ticketItem) {
        this.ticketItem = ticketItem;
    }

    public MenuItem getMenuItem() {
        return this.menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
}

