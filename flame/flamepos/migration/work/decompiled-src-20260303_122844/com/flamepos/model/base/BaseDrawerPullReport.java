/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.CurrencyBalance;
import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.DrawerPullVoidTicketEntry;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.User;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public abstract class BaseDrawerPullReport
implements Comparable,
Serializable {
    public static String REF = "DrawerPullReport";
    public static String PROP_CREDIT_CARD_RECEIPT_AMOUNT = "creditCardReceiptAmount";
    public static String PROP_TOTAL_VOID_WST = "totalVoidWst";
    public static String PROP_TOTAL_DISCOUNT_RATIO = "totalDiscountRatio";
    public static String PROP_GROSS_RECEIPTS = "grossReceipts";
    public static String PROP_PAY_OUT_AMOUNT = "payOutAmount";
    public static String PROP_SALES_DELIVERY_CHARGE = "salesDeliveryCharge";
    public static String PROP_CASH_RECEIPT_COUNT = "cashReceiptCount";
    public static String PROP_TOTAL_DISCOUNT_PERCENTAGE = "totalDiscountPercentage";
    public static String PROP_TIPS_DIFFERENTIAL = "tipsDifferential";
    public static String PROP_VARIANCE = "variance";
    public static String PROP_TOTAL_DISCOUNT_CHECK_SIZE = "totalDiscountCheckSize";
    public static String PROP_SALES_TAX = "salesTax";
    public static String PROP_DRAWER_BLEED_AMOUNT = "drawerBleedAmount";
    public static String PROP_TOTAL_VOID = "totalVoid";
    public static String PROP_TOTAL_DISCOUNT_AMOUNT = "totalDiscountAmount";
    public static String PROP_TOTAL_DISCOUNT_SALES = "totalDiscountSales";
    public static String PROP_CASH_TAX = "cashTax";
    public static String PROP_DRAWER_ACCOUNTABLE = "drawerAccountable";
    public static String PROP_CASH_BACK = "cashBack";
    public static String PROP_GIFT_CERT_RETURN_AMOUNT = "giftCertReturnAmount";
    public static String PROP_CHARGED_TIPS = "chargedTips";
    public static String PROP_GIFT_CERT_RETURN_COUNT = "giftCertReturnCount";
    public static String PROP_CASH_TIPS = "cashTips";
    public static String PROP_BEGIN_CASH = "beginCash";
    public static String PROP_TOTAL_DISCOUNT_GUEST = "totalDiscountGuest";
    public static String PROP_TICKET_COUNT = "ticketCount";
    public static String PROP_CREDIT_CARD_RECEIPT_COUNT = "creditCardReceiptCount";
    public static String PROP_ASSIGNED_USER = "assignedUser";
    public static String PROP_DEBIT_CARD_RECEIPT_COUNT = "debitCardReceiptCount";
    public static String PROP_REFUND_AMOUNT = "refundAmount";
    public static String PROP_NET_SALES = "netSales";
    public static String PROP_TERMINAL = "terminal";
    public static String PROP_TIPS_PAID = "tipsPaid";
    public static String PROP_DRAWER_BLEED_COUNT = "drawerBleedCount";
    public static String PROP_REPORT_TIME = "reportTime";
    public static String PROP_GIFT_CERT_CHANGE_AMOUNT = "giftCertChangeAmount";
    public static String PROP_CASH_TO_DEPOSIT = "cashToDeposit";
    public static String PROP_TOTAL_DISCOUNT_COUNT = "totalDiscountCount";
    public static String PROP_TOTAL_DISCOUNT_PARTY_SIZE = "totalDiscountPartySize";
    public static String PROP_REG = "reg";
    public static String PROP_DEBIT_CARD_RECEIPT_AMOUNT = "debitCardReceiptAmount";
    public static String PROP_TOTAL_REVENUE = "totalRevenue";
    public static String PROP_RECEIPT_DIFFERENTIAL = "receiptDifferential";
    public static String PROP_CASH_RECEIPT_AMOUNT = "cashReceiptAmount";
    public static String PROP_PAY_OUT_COUNT = "payOutCount";
    public static String PROP_REFUND_RECEIPT_COUNT = "refundReceiptCount";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Date reportTime;
    protected String reg;
    protected Integer ticketCount;
    protected Double beginCash;
    protected Double netSales;
    protected Double salesTax;
    protected Double cashTax;
    protected Double totalRevenue;
    protected Double grossReceipts;
    protected Integer giftCertReturnCount;
    protected Double giftCertReturnAmount;
    protected Double giftCertChangeAmount;
    protected Integer cashReceiptCount;
    protected Double cashReceiptAmount;
    protected Integer creditCardReceiptCount;
    protected Double creditCardReceiptAmount;
    protected Integer debitCardReceiptCount;
    protected Double debitCardReceiptAmount;
    protected Integer refundReceiptCount;
    protected Double refundAmount;
    protected Double receiptDifferential;
    protected Double cashBack;
    protected Double cashTips;
    protected Double chargedTips;
    protected Double tipsPaid;
    protected Double tipsDifferential;
    protected Integer payOutCount;
    protected Double payOutAmount;
    protected Integer drawerBleedCount;
    protected Double drawerBleedAmount;
    protected Double drawerAccountable;
    protected Double cashToDeposit;
    protected Double variance;
    protected Double salesDeliveryCharge;
    protected Double totalVoidWst;
    protected Double totalVoid;
    protected Integer totalDiscountCount;
    protected Double totalDiscountAmount;
    protected Double totalDiscountSales;
    protected Integer totalDiscountGuest;
    protected Integer totalDiscountPartySize;
    protected Integer totalDiscountCheckSize;
    protected Double totalDiscountPercentage;
    protected Double totalDiscountRatio;
    private User assignedUser;
    private Terminal terminal;
    private Set<DrawerPullVoidTicketEntry> voidTickets;
    private Set<CurrencyBalance> currencyBalances;

    public BaseDrawerPullReport() {
        this.initialize();
    }

    public BaseDrawerPullReport(Integer id) {
        this.setId(id);
        this.initialize();
    }

    protected void initialize() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.hashCode = Integer.MIN_VALUE;
    }

    public Date getReportTime() {
        return this.reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public String getReg() {
        return this.reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public Integer getTicketCount() {
        return this.ticketCount == null ? Integer.valueOf(0) : this.ticketCount;
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
    }

    public Double getBeginCash() {
        return this.beginCash == null ? Double.valueOf(0.0) : this.beginCash;
    }

    public void setBeginCash(Double beginCash) {
        this.beginCash = beginCash;
    }

    public Double getNetSales() {
        return this.netSales == null ? Double.valueOf(0.0) : this.netSales;
    }

    public void setNetSales(Double netSales) {
        this.netSales = netSales;
    }

    public Double getSalesTax() {
        return this.salesTax == null ? Double.valueOf(0.0) : this.salesTax;
    }

    public void setSalesTax(Double salesTax) {
        this.salesTax = salesTax;
    }

    public Double getCashTax() {
        return this.cashTax == null ? Double.valueOf(0.0) : this.cashTax;
    }

    public void setCashTax(Double cashTax) {
        this.cashTax = cashTax;
    }

    public Double getTotalRevenue() {
        return this.totalRevenue == null ? Double.valueOf(0.0) : this.totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getGrossReceipts() {
        return this.grossReceipts == null ? Double.valueOf(0.0) : this.grossReceipts;
    }

    public void setGrossReceipts(Double grossReceipts) {
        this.grossReceipts = grossReceipts;
    }

    public Integer getGiftCertReturnCount() {
        return this.giftCertReturnCount == null ? Integer.valueOf(0) : this.giftCertReturnCount;
    }

    public void setGiftCertReturnCount(Integer giftCertReturnCount) {
        this.giftCertReturnCount = giftCertReturnCount;
    }

    public Double getGiftCertReturnAmount() {
        return this.giftCertReturnAmount == null ? Double.valueOf(0.0) : this.giftCertReturnAmount;
    }

    public void setGiftCertReturnAmount(Double giftCertReturnAmount) {
        this.giftCertReturnAmount = giftCertReturnAmount;
    }

    public Double getGiftCertChangeAmount() {
        return this.giftCertChangeAmount == null ? Double.valueOf(0.0) : this.giftCertChangeAmount;
    }

    public void setGiftCertChangeAmount(Double giftCertChangeAmount) {
        this.giftCertChangeAmount = giftCertChangeAmount;
    }

    public Integer getCashReceiptCount() {
        return this.cashReceiptCount == null ? Integer.valueOf(0) : this.cashReceiptCount;
    }

    public void setCashReceiptCount(Integer cashReceiptCount) {
        this.cashReceiptCount = cashReceiptCount;
    }

    public Double getCashReceiptAmount() {
        return this.cashReceiptAmount == null ? Double.valueOf(0.0) : this.cashReceiptAmount;
    }

    public void setCashReceiptAmount(Double cashReceiptAmount) {
        this.cashReceiptAmount = cashReceiptAmount;
    }

    public Integer getCreditCardReceiptCount() {
        return this.creditCardReceiptCount == null ? Integer.valueOf(0) : this.creditCardReceiptCount;
    }

    public void setCreditCardReceiptCount(Integer creditCardReceiptCount) {
        this.creditCardReceiptCount = creditCardReceiptCount;
    }

    public Double getCreditCardReceiptAmount() {
        return this.creditCardReceiptAmount == null ? Double.valueOf(0.0) : this.creditCardReceiptAmount;
    }

    public void setCreditCardReceiptAmount(Double creditCardReceiptAmount) {
        this.creditCardReceiptAmount = creditCardReceiptAmount;
    }

    public Integer getDebitCardReceiptCount() {
        return this.debitCardReceiptCount == null ? Integer.valueOf(0) : this.debitCardReceiptCount;
    }

    public void setDebitCardReceiptCount(Integer debitCardReceiptCount) {
        this.debitCardReceiptCount = debitCardReceiptCount;
    }

    public Double getDebitCardReceiptAmount() {
        return this.debitCardReceiptAmount == null ? Double.valueOf(0.0) : this.debitCardReceiptAmount;
    }

    public void setDebitCardReceiptAmount(Double debitCardReceiptAmount) {
        this.debitCardReceiptAmount = debitCardReceiptAmount;
    }

    public Integer getRefundReceiptCount() {
        return this.refundReceiptCount == null ? Integer.valueOf(0) : this.refundReceiptCount;
    }

    public void setRefundReceiptCount(Integer refundReceiptCount) {
        this.refundReceiptCount = refundReceiptCount;
    }

    public Double getRefundAmount() {
        return this.refundAmount == null ? Double.valueOf(0.0) : this.refundAmount;
    }

    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Double getReceiptDifferential() {
        return this.receiptDifferential == null ? Double.valueOf(0.0) : this.receiptDifferential;
    }

    public void setReceiptDifferential(Double receiptDifferential) {
        this.receiptDifferential = receiptDifferential;
    }

    public Double getCashBack() {
        return this.cashBack == null ? Double.valueOf(0.0) : this.cashBack;
    }

    public void setCashBack(Double cashBack) {
        this.cashBack = cashBack;
    }

    public Double getCashTips() {
        return this.cashTips == null ? Double.valueOf(0.0) : this.cashTips;
    }

    public void setCashTips(Double cashTips) {
        this.cashTips = cashTips;
    }

    public Double getChargedTips() {
        return this.chargedTips == null ? Double.valueOf(0.0) : this.chargedTips;
    }

    public void setChargedTips(Double chargedTips) {
        this.chargedTips = chargedTips;
    }

    public Double getTipsPaid() {
        return this.tipsPaid == null ? Double.valueOf(0.0) : this.tipsPaid;
    }

    public void setTipsPaid(Double tipsPaid) {
        this.tipsPaid = tipsPaid;
    }

    public Double getTipsDifferential() {
        return this.tipsDifferential == null ? Double.valueOf(0.0) : this.tipsDifferential;
    }

    public void setTipsDifferential(Double tipsDifferential) {
        this.tipsDifferential = tipsDifferential;
    }

    public Integer getPayOutCount() {
        return this.payOutCount == null ? Integer.valueOf(0) : this.payOutCount;
    }

    public void setPayOutCount(Integer payOutCount) {
        this.payOutCount = payOutCount;
    }

    public Double getPayOutAmount() {
        return this.payOutAmount == null ? Double.valueOf(0.0) : this.payOutAmount;
    }

    public void setPayOutAmount(Double payOutAmount) {
        this.payOutAmount = payOutAmount;
    }

    public Integer getDrawerBleedCount() {
        return this.drawerBleedCount == null ? Integer.valueOf(0) : this.drawerBleedCount;
    }

    public void setDrawerBleedCount(Integer drawerBleedCount) {
        this.drawerBleedCount = drawerBleedCount;
    }

    public Double getDrawerBleedAmount() {
        return this.drawerBleedAmount == null ? Double.valueOf(0.0) : this.drawerBleedAmount;
    }

    public void setDrawerBleedAmount(Double drawerBleedAmount) {
        this.drawerBleedAmount = drawerBleedAmount;
    }

    public Double getDrawerAccountable() {
        return this.drawerAccountable == null ? Double.valueOf(0.0) : this.drawerAccountable;
    }

    public void setDrawerAccountable(Double drawerAccountable) {
        this.drawerAccountable = drawerAccountable;
    }

    public Double getCashToDeposit() {
        return this.cashToDeposit == null ? Double.valueOf(0.0) : this.cashToDeposit;
    }

    public void setCashToDeposit(Double cashToDeposit) {
        this.cashToDeposit = cashToDeposit;
    }

    public Double getVariance() {
        return this.variance == null ? Double.valueOf(0.0) : this.variance;
    }

    public void setVariance(Double variance) {
        this.variance = variance;
    }

    public Double getSalesDeliveryCharge() {
        return this.salesDeliveryCharge == null ? Double.valueOf(0.0) : this.salesDeliveryCharge;
    }

    public void setSalesDeliveryCharge(Double salesDeliveryCharge) {
        this.salesDeliveryCharge = salesDeliveryCharge;
    }

    public Double getTotalVoidWst() {
        return this.totalVoidWst == null ? Double.valueOf(0.0) : this.totalVoidWst;
    }

    public void setTotalVoidWst(Double totalVoidWst) {
        this.totalVoidWst = totalVoidWst;
    }

    public Double getTotalVoid() {
        return this.totalVoid == null ? Double.valueOf(0.0) : this.totalVoid;
    }

    public void setTotalVoid(Double totalVoid) {
        this.totalVoid = totalVoid;
    }

    public Integer getTotalDiscountCount() {
        return this.totalDiscountCount == null ? Integer.valueOf(0) : this.totalDiscountCount;
    }

    public void setTotalDiscountCount(Integer totalDiscountCount) {
        this.totalDiscountCount = totalDiscountCount;
    }

    public Double getTotalDiscountAmount() {
        return this.totalDiscountAmount == null ? Double.valueOf(0.0) : this.totalDiscountAmount;
    }

    public void setTotalDiscountAmount(Double totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public Double getTotalDiscountSales() {
        return this.totalDiscountSales == null ? Double.valueOf(0.0) : this.totalDiscountSales;
    }

    public void setTotalDiscountSales(Double totalDiscountSales) {
        this.totalDiscountSales = totalDiscountSales;
    }

    public Integer getTotalDiscountGuest() {
        return this.totalDiscountGuest == null ? Integer.valueOf(0) : this.totalDiscountGuest;
    }

    public void setTotalDiscountGuest(Integer totalDiscountGuest) {
        this.totalDiscountGuest = totalDiscountGuest;
    }

    public Integer getTotalDiscountPartySize() {
        return this.totalDiscountPartySize == null ? Integer.valueOf(0) : this.totalDiscountPartySize;
    }

    public void setTotalDiscountPartySize(Integer totalDiscountPartySize) {
        this.totalDiscountPartySize = totalDiscountPartySize;
    }

    public Integer getTotalDiscountCheckSize() {
        return this.totalDiscountCheckSize == null ? Integer.valueOf(0) : this.totalDiscountCheckSize;
    }

    public void setTotalDiscountCheckSize(Integer totalDiscountCheckSize) {
        this.totalDiscountCheckSize = totalDiscountCheckSize;
    }

    public Double getTotalDiscountPercentage() {
        return this.totalDiscountPercentage == null ? Double.valueOf(0.0) : this.totalDiscountPercentage;
    }

    public void setTotalDiscountPercentage(Double totalDiscountPercentage) {
        this.totalDiscountPercentage = totalDiscountPercentage;
    }

    public Double getTotalDiscountRatio() {
        return this.totalDiscountRatio == null ? Double.valueOf(0.0) : this.totalDiscountRatio;
    }

    public void setTotalDiscountRatio(Double totalDiscountRatio) {
        this.totalDiscountRatio = totalDiscountRatio;
    }

    public User getAssignedUser() {
        return this.assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Set<DrawerPullVoidTicketEntry> getVoidTickets() {
        return this.voidTickets;
    }

    public void setVoidTickets(Set<DrawerPullVoidTicketEntry> voidTickets) {
        this.voidTickets = voidTickets;
    }

    public Set<CurrencyBalance> getCurrencyBalances() {
        return this.currencyBalances;
    }

    public void setCurrencyBalances(Set<CurrencyBalance> currencyBalances) {
        this.currencyBalances = currencyBalances;
    }

    public void addTocurrencyBalances(CurrencyBalance currencyBalance) {
        if (null == this.getCurrencyBalances()) {
            this.setCurrencyBalances(new TreeSet<CurrencyBalance>());
        }
        this.getCurrencyBalances().add(currencyBalance);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof DrawerPullReport)) {
            return false;
        }
        DrawerPullReport drawerPullReport = (DrawerPullReport)obj;
        if (null == this.getId() || null == drawerPullReport.getId()) {
            return false;
        }
        return this.getId().equals(drawerPullReport.getId());
    }

    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getId()) {
                return super.hashCode();
            }
            String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
            this.hashCode = hashStr.hashCode();
        }
        return this.hashCode;
    }

    public int compareTo(Object obj) {
        if (obj.hashCode() > this.hashCode()) {
            return 1;
        }
        if (obj.hashCode() < this.hashCode()) {
            return -1;
        }
        return 0;
    }

    public String toString() {
        return super.toString();
    }
}

