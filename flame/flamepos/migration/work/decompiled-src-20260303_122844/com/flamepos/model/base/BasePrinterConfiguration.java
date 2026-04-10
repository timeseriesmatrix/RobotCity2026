/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.PrinterConfiguration;
import java.io.Serializable;

public abstract class BasePrinterConfiguration
implements Comparable,
Serializable {
    public static String REF = "PrinterConfiguration";
    public static String PROP_USE_NORMAL_PRINTER_FOR_TICKET = "useNormalPrinterForTicket";
    public static String PROP_USE_NORMAL_PRINTER_FOR_KITCHEN = "useNormalPrinterForKitchen";
    public static String PROP_PRINT_KITCHEN_WHEN_TICKET_SETTLED = "printKitchenWhenTicketSettled";
    public static String PROP_RECEIPT_PRINTER_NAME = "receiptPrinterName";
    public static String PROP_PRINT_RECEIPT_WHEN_TICKET_PAID = "printReceiptWhenTicketPaid";
    public static String PROP_PRINT_RECREIPT_WHEN_TICKET_SETTLED = "printRecreiptWhenTicketSettled";
    public static String PROP_PRINT_KITCHEN_WHEN_TICKET_PAID = "printKitchenWhenTicketPaid";
    public static String PROP_ID = "id";
    public static String PROP_KITCHEN_PRINTER_NAME = "kitchenPrinterName";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    private String receiptPrinterName;
    private String kitchenPrinterName;
    private Boolean printRecreiptWhenTicketSettled;
    private Boolean printKitchenWhenTicketSettled;
    private Boolean printReceiptWhenTicketPaid;
    private Boolean printKitchenWhenTicketPaid;
    private Boolean useNormalPrinterForTicket;
    private Boolean useNormalPrinterForKitchen;

    public BasePrinterConfiguration() {
        this.initialize();
    }

    public BasePrinterConfiguration(Integer id) {
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

    public String getReceiptPrinterName() {
        return this.receiptPrinterName;
    }

    public void setReceiptPrinterName(String receiptPrinterName) {
        this.receiptPrinterName = receiptPrinterName;
    }

    public String getKitchenPrinterName() {
        return this.kitchenPrinterName;
    }

    public void setKitchenPrinterName(String kitchenPrinterName) {
        this.kitchenPrinterName = kitchenPrinterName;
    }

    public Boolean isPrintRecreiptWhenTicketSettled() {
        return this.printRecreiptWhenTicketSettled == null ? Boolean.valueOf(true) : this.printRecreiptWhenTicketSettled;
    }

    public void setPrintRecreiptWhenTicketSettled(Boolean printRecreiptWhenTicketSettled) {
        this.printRecreiptWhenTicketSettled = printRecreiptWhenTicketSettled;
    }

    public static String getPrintRecreiptWhenTicketSettledDefaultValue() {
        return "true";
    }

    public Boolean isPrintKitchenWhenTicketSettled() {
        return this.printKitchenWhenTicketSettled == null ? Boolean.valueOf(true) : this.printKitchenWhenTicketSettled;
    }

    public void setPrintKitchenWhenTicketSettled(Boolean printKitchenWhenTicketSettled) {
        this.printKitchenWhenTicketSettled = printKitchenWhenTicketSettled;
    }

    public static String getPrintKitchenWhenTicketSettledDefaultValue() {
        return "true";
    }

    public Boolean isPrintReceiptWhenTicketPaid() {
        return this.printReceiptWhenTicketPaid == null ? Boolean.valueOf(true) : this.printReceiptWhenTicketPaid;
    }

    public void setPrintReceiptWhenTicketPaid(Boolean printReceiptWhenTicketPaid) {
        this.printReceiptWhenTicketPaid = printReceiptWhenTicketPaid;
    }

    public static String getPrintReceiptWhenTicketPaidDefaultValue() {
        return "true";
    }

    public Boolean isPrintKitchenWhenTicketPaid() {
        return this.printKitchenWhenTicketPaid == null ? Boolean.valueOf(true) : this.printKitchenWhenTicketPaid;
    }

    public void setPrintKitchenWhenTicketPaid(Boolean printKitchenWhenTicketPaid) {
        this.printKitchenWhenTicketPaid = printKitchenWhenTicketPaid;
    }

    public static String getPrintKitchenWhenTicketPaidDefaultValue() {
        return "true";
    }

    public Boolean isUseNormalPrinterForTicket() {
        return this.useNormalPrinterForTicket == null ? Boolean.valueOf(false) : this.useNormalPrinterForTicket;
    }

    public void setUseNormalPrinterForTicket(Boolean useNormalPrinterForTicket) {
        this.useNormalPrinterForTicket = useNormalPrinterForTicket;
    }

    public static String getUseNormalPrinterForTicketDefaultValue() {
        return "false";
    }

    public Boolean isUseNormalPrinterForKitchen() {
        return this.useNormalPrinterForKitchen == null ? Boolean.valueOf(false) : this.useNormalPrinterForKitchen;
    }

    public void setUseNormalPrinterForKitchen(Boolean useNormalPrinterForKitchen) {
        this.useNormalPrinterForKitchen = useNormalPrinterForKitchen;
    }

    public static String getUseNormalPrinterForKitchenDefaultValue() {
        return "false";
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof PrinterConfiguration)) {
            return false;
        }
        PrinterConfiguration printerConfiguration = (PrinterConfiguration)obj;
        if (null == this.getId() || null == printerConfiguration.getId()) {
            return false;
        }
        return this.getId().equals(printerConfiguration.getId());
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

