/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.util;

public class TransactionSummary {
    private int count;
    private double amount;
    private double tipsAmount;
    private double changeAmount;

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double totalPrice) {
        this.amount = totalPrice;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int totalTicket) {
        this.count = totalTicket;
    }

    public double getChangeAmount() {
        return this.changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public double getTipsAmount() {
        return this.tipsAmount;
    }

    public void setTipsAmount(double tipsAmount) {
        this.tipsAmount = tipsAmount;
    }
}

