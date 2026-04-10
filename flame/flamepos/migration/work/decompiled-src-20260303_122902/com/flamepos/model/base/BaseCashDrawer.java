/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.CashDrawer;
import com.floreantpos.model.CurrencyBalance;
import com.floreantpos.model.Terminal;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public abstract class BaseCashDrawer
implements Comparable,
Serializable {
    public static String REF = "CashDrawer";
    public static String PROP_TERMINAL = "terminal";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    private Terminal terminal;
    private Set<CurrencyBalance> currencyBalanceList;

    public BaseCashDrawer() {
        this.initialize();
    }

    public BaseCashDrawer(Integer id) {
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

    public Terminal getTerminal() {
        return this.terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Set<CurrencyBalance> getCurrencyBalanceList() {
        return this.currencyBalanceList;
    }

    public void setCurrencyBalanceList(Set<CurrencyBalance> currencyBalanceList) {
        this.currencyBalanceList = currencyBalanceList;
    }

    public void addTocurrencyBalanceList(CurrencyBalance currencyBalance) {
        if (null == this.getCurrencyBalanceList()) {
            this.setCurrencyBalanceList(new TreeSet<CurrencyBalance>());
        }
        this.getCurrencyBalanceList().add(currencyBalance);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof CashDrawer)) {
            return false;
        }
        CashDrawer cashDrawer = (CashDrawer)obj;
        if (null == this.getId() || null == cashDrawer.getId()) {
            return false;
        }
        return this.getId().equals(cashDrawer.getId());
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

