/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseZipCodeVsDeliveryCharge;

public class ZipCodeVsDeliveryCharge
extends BaseZipCodeVsDeliveryCharge {
    private static final long serialVersionUID = 1L;

    public ZipCodeVsDeliveryCharge() {
    }

    public ZipCodeVsDeliveryCharge(Integer id) {
        super(id);
    }

    public ZipCodeVsDeliveryCharge(Integer id, String zipCode, double deliveryCharge) {
        super(id, zipCode, deliveryCharge);
    }
}

