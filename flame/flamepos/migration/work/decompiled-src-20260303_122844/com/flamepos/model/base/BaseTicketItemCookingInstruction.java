/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import java.io.Serializable;

public abstract class BaseTicketItemCookingInstruction
implements Comparable,
Serializable {
    public static String REF = "TicketItemCookingInstruction";
    public static String PROP_DESCRIPTION = "description";
    public static String PROP_PRINTED_TO_KITCHEN = "printedToKitchen";
    protected String description;
    protected Boolean printedToKitchen;

    public BaseTicketItemCookingInstruction() {
        this.initialize();
    }

    protected void initialize() {
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isPrintedToKitchen() {
        return this.printedToKitchen == null ? Boolean.FALSE : this.printedToKitchen;
    }

    public void setPrintedToKitchen(Boolean printedToKitchen) {
        this.printedToKitchen = printedToKitchen;
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

