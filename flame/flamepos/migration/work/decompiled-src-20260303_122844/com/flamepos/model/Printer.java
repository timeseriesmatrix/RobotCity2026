/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.VirtualPrinter;

public class Printer {
    private VirtualPrinter virtualPrinter;
    private String deviceName;
    private boolean defaultPrinter;
    private String type;

    public Printer() {
    }

    public Printer(VirtualPrinter virtualPrinter, String deviceName) {
        this.virtualPrinter = virtualPrinter;
        this.deviceName = deviceName;
    }

    public Printer(VirtualPrinter virtualPrinter, String deviceName, boolean defaultPrinter) {
        this.virtualPrinter = virtualPrinter;
        this.deviceName = deviceName;
        this.defaultPrinter = defaultPrinter;
    }

    public VirtualPrinter getVirtualPrinter() {
        return this.virtualPrinter;
    }

    public void setVirtualPrinter(VirtualPrinter virtualPrinter) {
        this.virtualPrinter = virtualPrinter;
    }

    public String getDeviceName() {
        if (this.deviceName == null) {
            return "No Print";
        }
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isDefaultPrinter() {
        return this.defaultPrinter;
    }

    public void setDefaultPrinter(boolean defaultPrinter) {
        this.defaultPrinter = defaultPrinter;
    }

    public int hashCode() {
        return this.virtualPrinter.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Printer)) {
            return false;
        }
        Printer that = (Printer)obj;
        return this.virtualPrinter.equals(that.virtualPrinter);
    }

    public String toString() {
        return this.virtualPrinter.toString();
    }

    public String getDisplayName() {
        return this.virtualPrinter.toString() + " -    " + this.getDeviceName();
    }

    public String getType() {
        this.type = VirtualPrinter.PRINTER_TYPE_NAMES[this.virtualPrinter.getType()];
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

