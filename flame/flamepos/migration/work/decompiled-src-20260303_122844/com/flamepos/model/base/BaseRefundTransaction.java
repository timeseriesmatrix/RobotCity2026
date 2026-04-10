/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.RefundTransaction;
import java.io.Serializable;

public abstract class BaseRefundTransaction
extends PosTransaction
implements Comparable,
Serializable {
    public static String REF = "RefundTransaction";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;

    public BaseRefundTransaction() {
        this.initialize();
    }

    public BaseRefundTransaction(Integer id) {
        super(id);
    }

    public BaseRefundTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof RefundTransaction)) {
            return false;
        }
        RefundTransaction refundTransaction = (RefundTransaction)obj;
        if (null == this.getId() || null == refundTransaction.getId()) {
            return false;
        }
        return this.getId().equals(refundTransaction.getId());
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

