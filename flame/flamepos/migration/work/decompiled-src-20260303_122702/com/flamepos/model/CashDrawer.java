/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.Currency;
import com.floreantpos.model.CurrencyBalance;
import com.floreantpos.model.base.BaseCashDrawer;
import java.util.Set;

public class CashDrawer
extends BaseCashDrawer {
    private static final long serialVersionUID = 1L;

    public CashDrawer() {
    }

    public CashDrawer(Integer id) {
        super(id);
    }

    public CurrencyBalance getCurrencyBalance(Currency currency) {
        Set<CurrencyBalance> list = this.getCurrencyBalanceList();
        if (list == null) {
            return null;
        }
        for (CurrencyBalance currencyBalance : list) {
            if (!currency.equals(currencyBalance.getCurrency())) continue;
            return currencyBalance;
        }
        return null;
    }
}

