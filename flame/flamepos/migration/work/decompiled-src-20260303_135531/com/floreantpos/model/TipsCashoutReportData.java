/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.Messages;

public class TipsCashoutReportData {
    private Integer ticketId;
    private String saleType;
    private Double ticketTotal;
    private Double tips;
    private boolean paid;

    public String getSaleType() {
        return this.saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
        this.saleType = this.saleType == null ? Messages.getString("TipsCashoutReportData.0") : this.saleType.replaceAll("_", " ");
    }

    public Integer getTicketId() {
        return this.ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Double getTicketTotal() {
        return this.ticketTotal;
    }

    public void setTicketTotal(Double ticketTotal) {
        this.ticketTotal = ticketTotal;
    }

    public Double getTips() {
        return this.tips;
    }

    public void setTips(Double tips) {
        this.tips = tips;
    }

    public boolean isPaid() {
        return this.paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}

