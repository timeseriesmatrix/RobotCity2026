/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.report;

public class TicketPrintProperties {
    private String receiptTypeName;
    private boolean showHeader;
    private boolean showFooter;
    private boolean showSubtotal;
    private String receiptCopyType;
    boolean printModifers = true;
    boolean printCookingInstructions = true;

    public TicketPrintProperties() {
    }

    public TicketPrintProperties(String receiptTypeName, boolean showHeader, boolean showFooter, boolean showSubtotal) {
        this.receiptTypeName = receiptTypeName;
        this.showHeader = showHeader;
        this.showFooter = showFooter;
        this.showSubtotal = showSubtotal;
    }

    public String getReceiptTypeName() {
        return this.receiptTypeName;
    }

    public void setReceiptTypeName(String receiptTypeName) {
        this.receiptTypeName = receiptTypeName;
    }

    public boolean isShowHeader() {
        return this.showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    public boolean isShowFooter() {
        return this.showFooter;
    }

    public void setShowFooter(boolean showFooter) {
        this.showFooter = showFooter;
    }

    public boolean isShowSubtotal() {
        return this.showSubtotal;
    }

    public void setShowSubtotal(boolean showSubtotal) {
        this.showSubtotal = showSubtotal;
    }

    public boolean isPrintModifers() {
        return this.printModifers;
    }

    public void setPrintModifers(boolean printModifers) {
        this.printModifers = printModifers;
    }

    public boolean isPrintCookingInstructions() {
        return this.printCookingInstructions;
    }

    public void setPrintCookingInstructions(boolean printCookingInstructions) {
        this.printCookingInstructions = printCookingInstructions;
    }

    public String getReceiptCopyType() {
        return this.receiptCopyType;
    }

    public void setReceiptCopyType(String receiptCopyName) {
        this.receiptCopyType = receiptCopyName;
    }
}

