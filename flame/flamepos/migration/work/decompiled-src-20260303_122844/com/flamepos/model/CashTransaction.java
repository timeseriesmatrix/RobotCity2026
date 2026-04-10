/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseCashTransaction;

public class CashTransaction
extends BaseCashTransaction {
    private static final long serialVersionUID = 1L;

    public CashTransaction() {
    }

    public CashTransaction(Integer id) {
        super(id);
    }

    public CashTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }
}

