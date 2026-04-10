/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.print;

public enum PrinterType {
    OS_PRINTER("printer"),
    JAVAPOS("javapos");

    private final String name;

    private PrinterType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static PrinterType fromString(String name) {
        PrinterType[] values = PrinterType.values();
        for (int i = 0; i < values.length; ++i) {
            PrinterType printerType = values[i];
            if (!printerType.getName().equals(name)) continue;
            return printerType;
        }
        return null;
    }

    public String toString() {
        return this.name;
    }
}

