/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseVirtualPrinter;
import java.util.Iterator;
import java.util.List;

public class VirtualPrinter
extends BaseVirtualPrinter {
    private static final long serialVersionUID = 1L;
    public static final int REPORT = 0;
    public static final int RECEIPT = 1;
    public static final int KITCHEN = 2;
    public static final int PACKING = 3;
    public static final int KITCHEN_DISPLAY = 4;
    public static final String[] PRINTER_TYPE_NAMES = new String[]{"Report", "Receipt", "Kitchen", "Packing", "KDS"};

    public VirtualPrinter() {
    }

    public VirtualPrinter(Integer id) {
        super(id);
    }

    public VirtualPrinter(Integer id, String name) {
        super(id, name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VirtualPrinter)) {
            return false;
        }
        VirtualPrinter other = (VirtualPrinter)obj;
        return this.name.equalsIgnoreCase(other.name);
    }

    public String getDisplayName() {
        return PRINTER_TYPE_NAMES[this.getType()];
    }

    @Override
    public String toString() {
        String name = this.getName();
        List<String> typeNames = this.getOrderTypeNames();
        if (typeNames != null && typeNames.size() > 0) {
            name = name + " (";
            Iterator<String> iterator = typeNames.iterator();
            while (iterator.hasNext()) {
                String string = iterator.next();
                name = name + string;
                if (!iterator.hasNext()) continue;
                name = name + ", ";
            }
            name = name + ")";
        }
        return name;
    }
}

