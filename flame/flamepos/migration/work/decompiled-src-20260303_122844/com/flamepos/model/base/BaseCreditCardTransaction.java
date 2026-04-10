/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.model.PosTransaction;
import java.io.Serializable;

public abstract class BaseCreditCardTransaction
extends PosTransaction
implements Comparable,
Serializable {
    public static String REF = "CreditCardTransaction";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;

    public BaseCreditCardTransaction() {
        this.initialize();
    }

    public BaseCreditCardTransaction(Integer id) {
        super(id);
    }

    public BaseCreditCardTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof CreditCardTransaction)) {
            return false;
        }
        CreditCardTransaction creditCardTransaction = (CreditCardTransaction)obj;
        if (null == this.getId() || null == creditCardTransaction.getId()) {
            return false;
        }
        return this.getId().equals(creditCardTransaction.getId());
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

