/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.model.TransactionType;
import com.floreantpos.model.base.BaseVoidTransaction;
import org.apache.commons.lang.StringUtils;

public class VoidTransaction
extends BaseVoidTransaction {
    private static final long serialVersionUID = 1L;

    public VoidTransaction() {
    }

    public VoidTransaction(Integer id) {
        super(id);
    }

    public VoidTransaction(Integer id, String transactionType, String paymentType) {
        super(id, transactionType, paymentType);
    }

    @Override
    public String getTransactionType() {
        String type = super.getTransactionType();
        if (StringUtils.isEmpty((String)type)) {
            return TransactionType.DEBIT.name();
        }
        return type;
    }
}

