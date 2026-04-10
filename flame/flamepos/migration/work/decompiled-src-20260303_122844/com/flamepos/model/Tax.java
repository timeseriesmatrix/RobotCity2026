/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseTax;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="tax")
public class Tax
extends BaseTax {
    private static final long serialVersionUID = 1L;

    public Tax() {
    }

    public Tax(Integer id) {
        super(id);
    }

    public Tax(Integer id, String name) {
        super(id, name);
    }

    public String getUniqueId() {
        return ("tax_" + this.getName() + "_" + this.getId()).replaceAll("\\s+", "_");
    }

    @Override
    public String toString() {
        return this.getName() + " (" + this.getRate() + "%)";
    }
}

