/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos;

import com.floreantpos.model.Ticket;

public interface ITicketList {
    public Ticket getSelectedTicket();

    public void updateTicketList();

    public void updateCustomerTicketList(Integer var1);
}

