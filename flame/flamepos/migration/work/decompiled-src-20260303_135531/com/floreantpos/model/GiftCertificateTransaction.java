/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseGiftCertificateTransaction;

public class GiftCertificateTransaction
extends BaseGiftCertificateTransaction {
    private static final long serialVersionUID = 1L;

    public GiftCertificateTransaction() {
    }

    public GiftCertificateTransaction(Integer id) {
        super(id);
    }

    public GiftCertificateTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }
}

