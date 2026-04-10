/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.Messages;
import com.floreantpos.model.base.BasePrinterConfiguration;

public class PrinterConfiguration
extends BasePrinterConfiguration {
    private static final long serialVersionUID = 1L;
    public static final Integer ID = 1;

    public PrinterConfiguration() {
    }

    public PrinterConfiguration(Integer id) {
        super(id);
    }

    @Override
    public String getReceiptPrinterName() {
        if (super.getReceiptPrinterName() == null) {
            return Messages.getString("PrinterConfiguration.0");
        }
        return super.getReceiptPrinterName();
    }

    @Override
    public String getKitchenPrinterName() {
        if (super.getKitchenPrinterName() == null) {
            return Messages.getString("PrinterConfiguration.1");
        }
        return super.getKitchenPrinterName();
    }
}

