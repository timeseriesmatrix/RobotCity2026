/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BasePrinterGroup;
import java.util.Iterator;
import java.util.List;

public class PrinterGroup
extends BasePrinterGroup {
    private static final long serialVersionUID = 1L;

    public PrinterGroup() {
    }

    public PrinterGroup(Integer id) {
        super(id);
    }

    public PrinterGroup(Integer id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        String name = this.getName();
        List<String> list = this.getPrinterNames();
        if (list != null && list.size() > 0) {
            name = name + " (";
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                String string = iterator.next();
                name = name + string;
                if (!iterator.hasNext()) continue;
                name = name + ", ";
            }
            name = name + ")";
            if (this.isDefault) {
                name = name + "   -  Default";
            }
        }
        return name;
    }
}

