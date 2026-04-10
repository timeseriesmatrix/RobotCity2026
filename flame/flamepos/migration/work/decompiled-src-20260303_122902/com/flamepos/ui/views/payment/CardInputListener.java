/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.ui.views.payment;

import com.floreantpos.model.PaymentType;
import com.floreantpos.ui.views.payment.CardInputProcessor;

public interface CardInputListener {
    public void cardInputted(CardInputProcessor var1, PaymentType var2);
}

