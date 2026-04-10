/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.CurrencyBalance;
import com.floreantpos.model.DrawerPullVoidTicketEntry;
import com.floreantpos.model.base.BaseDrawerPullReport;
import java.util.HashSet;
import java.util.Set;

public class DrawerPullReport
extends BaseDrawerPullReport {
    private static final long serialVersionUID = 1L;

    public DrawerPullReport() {
    }

    public DrawerPullReport(Integer id) {
        super(id);
    }

    public void setPayOutNumber(Integer i) {
    }

    public String getCashReceiptNumber() {
        return "";
    }

    public void setCashReceiptNumber(String s) {
    }

    public String getCreditCardReceiptNumber() {
        return "";
    }

    public void setCreditCardReceiptNumber(String s) {
    }

    public String getDrawerBleedNumber() {
        return "";
    }

    public void setDebitCardReceiptNumber(String s) {
    }

    public String getDebitCardReceiptNumber() {
        return "";
    }

    public void setDrawerBleedNumber(String s) {
    }

    public Integer getPayOutNumber() {
        return 0;
    }

    public void addVoidTicketEntry(DrawerPullVoidTicketEntry entry) {
        if (this.getVoidTickets() == null) {
            this.setVoidTickets(new HashSet<DrawerPullVoidTicketEntry>());
        }
        this.getVoidTickets().add(entry);
    }

    public void addCurrencyBalances(Set<CurrencyBalance> currencyBalance) {
        this.getCurrencyBalances().addAll(currencyBalance);
    }

    @Override
    public Set<CurrencyBalance> getCurrencyBalances() {
        Set<CurrencyBalance> curBalanceList = super.getCurrencyBalances();
        if (curBalanceList == null) {
            curBalanceList = new HashSet<CurrencyBalance>();
            super.setCurrencyBalances(curBalanceList);
        }
        return curBalanceList;
    }

    public void calculate() {
        this.setTotalRevenue(this.getNetSales() + this.getSalesTax() + this.getSalesDeliveryCharge());
        this.setGrossReceipts(this.getTotalRevenue() + this.getChargedTips());
        double total = this.getCashReceiptAmount() + this.getCreditCardReceiptAmount() + this.getDebitCardReceiptAmount() + this.getGiftCertReturnAmount() + this.getGiftCertChangeAmount() - this.getCashBack() - this.getRefundAmount();
        this.setReceiptDifferential(this.getGrossReceipts() - total);
        this.setTipsDifferential(this.getChargedTips() - this.getTipsPaid());
        double totalCash = this.getCashReceiptAmount();
        double tips = this.getTipsPaid();
        double totalPayout = this.getPayOutAmount();
        double beginCash = this.getBeginCash();
        double cashBack = this.getCashBack();
        double refundAmount = this.getRefundAmount();
        double drawerBleed = this.getDrawerBleedAmount();
        this.setDrawerAccountable(beginCash + totalCash - tips - totalPayout - cashBack - refundAmount - drawerBleed);
        Set<DrawerPullVoidTicketEntry> voidTickets = this.getVoidTickets();
        if (voidTickets != null) {
            double totalVoidAmount = 0.0;
            for (DrawerPullVoidTicketEntry entry : voidTickets) {
                totalVoidAmount += entry.getAmount().doubleValue();
            }
            this.setTotalVoid(totalVoidAmount);
        }
    }
}

