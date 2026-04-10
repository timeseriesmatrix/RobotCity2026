/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

import java.util.Date;

public class SalesBalanceReport {
    private Date fromDate;
    private Date toDate;
    private Date reportTime;
    private double grossTaxableSalesAmount;
    private double grossNonTaxableSalesAmount;
    private double discountAmount;
    private double netSalesAmount;
    private double salesTaxAmount;
    private double totalRevenueAmount;
    private double giftCertSalesAmount;
    private double payInsAmount;
    private double chargedTipsAmount;
    private double grossReceiptsAmount;
    private double cashReceiptsAmount;
    private double creditCardReceiptsAmount;
    private double arReceiptsAmount;
    private double giftCertReturnAmount;
    private double giftCertChangeAmount;
    private double cashBackAmount;
    private double receiptDiffAmount;
    private double grossTipsPaidAmount;
    private double tipsDiscountAmount;
    private double cashPayoutAmount;
    private double cashAccountableAmount;
    private double drawerPullsAmount;
    private double coCurrentAmount;
    private double coPreviousAmount;
    private double overShortAmount;
    private double visaCreditCardAmount;
    private double masterCardAmount;
    private double amexAmount;
    private double discoveryAmount;

    public double getArReceiptsAmount() {
        return this.arReceiptsAmount;
    }

    public void setArReceiptsAmount(double arReceiptsAmount) {
        this.arReceiptsAmount = arReceiptsAmount;
    }

    public double getCashAccountableAmount() {
        return this.cashAccountableAmount;
    }

    public void setCashAccountableAmount(double cashAccountableAmount) {
        this.cashAccountableAmount = cashAccountableAmount;
    }

    public double getCashBackAmount() {
        return this.cashBackAmount;
    }

    public void setCashBackAmount(double cashBackAmount) {
        this.cashBackAmount = cashBackAmount;
    }

    public double getCashPayoutAmount() {
        return this.cashPayoutAmount;
    }

    public void setCashPayoutAmount(double cashPayoutAmount) {
        this.cashPayoutAmount = cashPayoutAmount;
    }

    public double getCashReceiptsAmount() {
        return this.cashReceiptsAmount;
    }

    public void setCashReceiptsAmount(double cashReceiptsAmount) {
        this.cashReceiptsAmount = cashReceiptsAmount;
    }

    public double getChargedTipsAmount() {
        return this.chargedTipsAmount;
    }

    public void setChargedTipsAmount(double chargedTipsAmount) {
        this.chargedTipsAmount = chargedTipsAmount;
    }

    public double getCoCurrentAmount() {
        return this.coCurrentAmount;
    }

    public void setCoCurrentAmount(double coCurrentAmount) {
        this.coCurrentAmount = coCurrentAmount;
    }

    public double getCoPreviousAmount() {
        return this.coPreviousAmount;
    }

    public void setCoPreviousAmount(double coPreviousAmount) {
        this.coPreviousAmount = coPreviousAmount;
    }

    public double getCreditCardReceiptsAmount() {
        return this.creditCardReceiptsAmount;
    }

    public void setCreditCardReceiptsAmount(double creditCardReceiptsAmount) {
        this.creditCardReceiptsAmount = creditCardReceiptsAmount;
    }

    public double getDiscountAmount() {
        return this.discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getDrawerPullsAmount() {
        return this.drawerPullsAmount;
    }

    public void setDrawerPullsAmount(double drawerPullsAmount) {
        this.drawerPullsAmount = drawerPullsAmount;
    }

    public Date getFromDate() {
        return this.fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public double getGiftCertChangeAmount() {
        return this.giftCertChangeAmount;
    }

    public void setGiftCertChangeAmount(double giftCertChangeAmount) {
        this.giftCertChangeAmount = giftCertChangeAmount;
    }

    public double getGiftCertReturnAmount() {
        return this.giftCertReturnAmount;
    }

    public void setGiftCertReturnAmount(double giftCertReturnAmount) {
        this.giftCertReturnAmount = giftCertReturnAmount;
    }

    public double getGiftCertSalesAmount() {
        return this.giftCertSalesAmount;
    }

    public void setGiftCertSalesAmount(double giftCertSalesAmount) {
        this.giftCertSalesAmount = giftCertSalesAmount;
    }

    public double getGrossNonTaxableSalesAmount() {
        return this.grossNonTaxableSalesAmount;
    }

    public void setGrossNonTaxableSalesAmount(double grossNonTaxableSalesAmount) {
        this.grossNonTaxableSalesAmount = grossNonTaxableSalesAmount;
    }

    public double getGrossReceiptsAmount() {
        return this.grossReceiptsAmount;
    }

    public void setGrossReceiptsAmount(double grossReceiptsAmount) {
        this.grossReceiptsAmount = grossReceiptsAmount;
    }

    public double getGrossTaxableSalesAmount() {
        return this.grossTaxableSalesAmount;
    }

    public void setGrossTaxableSalesAmount(double grossTaxableSalesAmount) {
        this.grossTaxableSalesAmount = grossTaxableSalesAmount;
    }

    public double getGrossTipsPaidAmount() {
        return this.grossTipsPaidAmount;
    }

    public void setGrossTipsPaidAmount(double grossTipsPaidAmount) {
        this.grossTipsPaidAmount = grossTipsPaidAmount;
    }

    public double getNetSalesAmount() {
        return this.netSalesAmount;
    }

    public void setNetSalesAmount(double netSalesAmount) {
        this.netSalesAmount = netSalesAmount;
    }

    public double getOverShortAmount() {
        return this.overShortAmount;
    }

    public void setOverShortAmount(double overShortAmount) {
        this.overShortAmount = overShortAmount;
    }

    public double getPayInsAmount() {
        return this.payInsAmount;
    }

    public void setPayInsAmount(double payInsAmount) {
        this.payInsAmount = payInsAmount;
    }

    public double getReceiptDiffAmount() {
        return this.receiptDiffAmount;
    }

    public void setReceiptDiffAmount(double receiptDiffAmount) {
        this.receiptDiffAmount = receiptDiffAmount;
    }

    public Date getReportTime() {
        return this.reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public double getSalesTaxAmount() {
        return this.salesTaxAmount;
    }

    public void setSalesTaxAmount(double salesTaxAmount) {
        this.salesTaxAmount = salesTaxAmount;
    }

    public double getTipsDiscountAmount() {
        return this.tipsDiscountAmount;
    }

    public void setTipsDiscountAmount(double tipsDiscountAmount) {
        this.tipsDiscountAmount = tipsDiscountAmount;
    }

    public Date getToDate() {
        return this.toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public double getTotalRevenueAmount() {
        return this.totalRevenueAmount;
    }

    public void setTotalRevenueAmount(double totalRevenueAmount) {
        this.totalRevenueAmount = totalRevenueAmount;
    }

    public void calculate() {
        this.netSalesAmount = this.grossTaxableSalesAmount + this.grossNonTaxableSalesAmount - this.discountAmount;
        this.totalRevenueAmount = this.netSalesAmount + this.salesTaxAmount;
        this.grossReceiptsAmount = this.totalRevenueAmount + this.payInsAmount + this.chargedTipsAmount;
        this.receiptDiffAmount = this.grossReceiptsAmount - this.cashReceiptsAmount - this.creditCardReceiptsAmount - this.arReceiptsAmount - this.giftCertReturnAmount + this.giftCertChangeAmount + this.cashBackAmount;
        this.cashAccountableAmount = this.cashReceiptsAmount - this.grossTipsPaidAmount + this.tipsDiscountAmount - this.cashPayoutAmount - this.giftCertChangeAmount - this.cashBackAmount;
        this.overShortAmount = this.cashAccountableAmount - this.drawerPullsAmount - this.coCurrentAmount + this.coPreviousAmount;
    }

    public double getVisaCreditCardAmount() {
        return this.visaCreditCardAmount;
    }

    public void setVisaCreditCardAmount(double visaCreditCardAmount) {
        this.visaCreditCardAmount = visaCreditCardAmount;
    }

    public double getMasterCardAmount() {
        return this.masterCardAmount;
    }

    public void setMasterCardAmount(double masterCardAmount) {
        this.masterCardAmount = masterCardAmount;
    }

    public double getAmexAmount() {
        return this.amexAmount;
    }

    public void setAmexAmount(double amexAmount) {
        this.amexAmount = amexAmount;
    }

    public double getDiscoveryAmount() {
        return this.discoveryAmount;
    }

    public void setDiscoveryAmount(double discoveryAmount) {
        this.discoveryAmount = discoveryAmount;
    }
}

