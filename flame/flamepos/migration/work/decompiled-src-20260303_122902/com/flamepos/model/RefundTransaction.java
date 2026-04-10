/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseRefundTransaction;

public class RefundTransaction
extends BaseRefundTransaction {
    private static final long serialVersionUID = 1L;

    public RefundTransaction() {
    }

    public RefundTransaction(Integer id) {
        super(id);
    }

    public RefundTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }
}

