/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseCustomPaymentTransaction;

public class CustomPaymentTransaction
extends BaseCustomPaymentTransaction {
    private static final long serialVersionUID = 1L;

    public CustomPaymentTransaction() {
    }

    public CustomPaymentTransaction(Integer id) {
        super(id);
    }

    public CustomPaymentTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }
}

