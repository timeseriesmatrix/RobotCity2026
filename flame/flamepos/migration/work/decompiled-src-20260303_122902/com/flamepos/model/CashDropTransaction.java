/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseCashDropTransaction;

public class CashDropTransaction
extends BaseCashDropTransaction {
    private static final long serialVersionUID = 1L;

    public CashDropTransaction() {
    }

    public CashDropTransaction(Integer id) {
        super(id);
    }

    public CashDropTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }
}

