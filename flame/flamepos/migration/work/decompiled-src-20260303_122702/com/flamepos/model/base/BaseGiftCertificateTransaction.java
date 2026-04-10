/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.GiftCertificateTransaction;
import com.floreantpos.model.PosTransaction;
import java.io.Serializable;

public abstract class BaseGiftCertificateTransaction
extends PosTransaction
implements Comparable,
Serializable {
    public static String REF = "GiftCertificateTransaction";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;

    public BaseGiftCertificateTransaction() {
        this.initialize();
    }

    public BaseGiftCertificateTransaction(Integer id) {
        super(id);
    }

    public BaseGiftCertificateTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof GiftCertificateTransaction)) {
            return false;
        }
        GiftCertificateTransaction giftCertificateTransaction = (GiftCertificateTransaction)obj;
        if (null == this.getId() || null == giftCertificateTransaction.getId()) {
            return false;
        }
        return this.getId().equals(giftCertificateTransaction.getId());
    }

    @Override
    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getId()) {
                return super.hashCode();
            }
            String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
            this.hashCode = hashStr.hashCode();
        }
        return this.hashCode;
    }

    @Override
    public int compareTo(Object obj) {
        if (obj.hashCode() > this.hashCode()) {
            return 1;
        }
        if (obj.hashCode() < this.hashCode()) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

