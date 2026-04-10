/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseCreditCardTransaction;

public class CreditCardTransaction
extends BaseCreditCardTransaction {
    private static final long serialVersionUID = 1L;

    public CreditCardTransaction() {
    }

    public CreditCardTransaction(Integer id) {
        super(id);
    }

    public CreditCardTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }
}

