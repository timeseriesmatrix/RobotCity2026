/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

public enum PackagingDimension {
    Quantity,
    Weight,
    Length,
    Volume,
    Other;


    public String toString() {
        return this.name();
    }
}

