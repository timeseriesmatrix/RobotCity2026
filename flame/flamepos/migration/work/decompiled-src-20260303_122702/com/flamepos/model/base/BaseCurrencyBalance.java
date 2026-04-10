/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.CashDrawer;
import com.floreantpos.model.Currency;
import com.floreantpos.model.CurrencyBalance;
import java.io.Serializable;

public abstract class BaseCurrencyBalance
implements Comparable,
Serializable {
    public static String REF = "CurrencyBalance";
    public static String PROP_ID = "id";
    public static String PROP_CASH_DRAWER = "cashDrawer";
    public static String PROP_CURRENCY = "currency";
    public static String PROP_BALANCE = "balance";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Double balance;
    private Currency currency;
    private CashDrawer cashDrawer;

    public BaseCurrencyBalance() {
        this.initialize();
    }

    public BaseCurrencyBalance(Integer id) {
        this.setId(id);
        this.initialize();
    }

    protected void initialize() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
        this.hashCode = Integer.MIN_VALUE;
    }

    public Double getBalance() {
        return this.balance == null ? Double.valueOf(0.0) : this.balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public CashDrawer getCashDrawer() {
        return this.cashDrawer;
    }

    public void setCashDrawer(CashDrawer cashDrawer) {
        this.cashDrawer = cashDrawer;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof CurrencyBalance)) {
            return false;
        }
        CurrencyBalance currencyBalance = (CurrencyBalance)obj;
        if (null == this.getId() || null == currencyBalance.getId()) {
            return false;
        }
        return this.getId().equals(currencyBalance.getId());
    }

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

    public int compareTo(Object obj) {
        if (obj.hashCode() > this.hashCode()) {
            return 1;
        }
        if (obj.hashCode() < this.hashCode()) {
            return -1;
        }
        return 0;
    }

    public String toString() {
        return super.toString();
    }
}

