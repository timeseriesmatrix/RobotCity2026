/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Terminal;
import com.floreantpos.model.TerminalPrinters;
import com.floreantpos.model.VirtualPrinter;
import java.io.Serializable;

public abstract class BaseTerminalPrinters
implements Comparable,
Serializable {
    public static String REF = "TerminalPrinters";
    public static String PROP_PRINTER_NAME = "printerName";
    public static String PROP_ID = "id";
    public static String PROP_TERMINAL = "terminal";
    public static String PROP_VIRTUAL_PRINTER = "virtualPrinter";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String printerName;
    private Terminal terminal;
    private VirtualPrinter virtualPrinter;

    public BaseTerminalPrinters() {
        this.initialize();
    }

    public BaseTerminalPrinters(Integer id) {
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

    public String getPrinterName() {
        return this.printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public VirtualPrinter getVirtualPrinter() {
        return this.virtualPrinter;
    }

    public void setVirtualPrinter(VirtualPrinter virtualPrinter) {
        this.virtualPrinter = virtualPrinter;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof TerminalPrinters)) {
            return false;
        }
        TerminalPrinters terminalPrinters = (TerminalPrinters)obj;
        if (null == this.getId() || null == terminalPrinters.getId()) {
            return false;
        }
        return this.getId().equals(terminalPrinters.getId());
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

