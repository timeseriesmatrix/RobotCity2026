/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.model.PosTransaction;

public interface CardProcessor {
    public void preAuth(PosTransaction var1) throws Exception;

    public void captureAuthAmount(PosTransaction var1) throws Exception;

    public void chargeAmount(PosTransaction var1) throws Exception;

    public void voidTransaction(PosTransaction var1) throws Exception;

    public String getCardInformationForReceipt(PosTransaction var1);
}

