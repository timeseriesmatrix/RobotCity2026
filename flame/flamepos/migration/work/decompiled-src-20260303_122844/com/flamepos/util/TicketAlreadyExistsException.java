/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import com.floreantpos.model.Ticket;

public class TicketAlreadyExistsException
extends Exception {
    private Ticket ticket;

    public TicketAlreadyExistsException(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return this.ticket;
    }
}

