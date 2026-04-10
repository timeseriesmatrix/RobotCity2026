/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.util;

public class TicketSummary {
    private int totalTicket;
    private double totalPrice;

    public double getTotalPrice() {
        return this.totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalTicket() {
        return this.totalTicket;
    }

    public void setTotalTicket(int totalTicket) {
        this.totalTicket = totalTicket;
    }
}

