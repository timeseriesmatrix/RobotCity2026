/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.util;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PrintServiceUtil {
    public static PrintService getPrintServiceForPrinter(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (int i = 0; i < printServices.length; ++i) {
            PrintService printService = printServices[i];
            if (!printService.getName().equals(printerName)) continue;
            return printService;
        }
        return PrintServiceLookup.lookupDefaultPrintService();
    }
}

