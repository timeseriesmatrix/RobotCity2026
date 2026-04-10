/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseDebitCardTransaction;

public class DebitCardTransaction
extends BaseDebitCardTransaction {
    private static final long serialVersionUID = 1L;

    public DebitCardTransaction() {
    }

    public DebitCardTransaction(Integer id) {
        super(id);
    }

    public DebitCardTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }
}

