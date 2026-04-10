/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Currency;
import java.io.Serializable;

public abstract class BaseCurrency
implements Comparable,
Serializable {
    public static String REF = "Currency";
    public static String PROP_SALES_PRICE = "salesPrice";
    public static String PROP_BUY_PRICE = "buyPrice";
    public static String PROP_NAME = "name";
    public static String PROP_MAIN = "main";
    public static String PROP_TOLERANCE = "tolerance";
    public static String PROP_EXCHANGE_RATE = "exchangeRate";
    public static String PROP_SYMBOL = "symbol";
    public static String PROP_ID = "id";
    public static String PROP_CODE = "code";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String code;
    protected String name;
    protected String symbol;
    protected Double exchangeRate;
    protected Double tolerance;
    protected Double buyPrice;
    protected Double salesPrice;
    protected Boolean main;

    public BaseCurrency() {
        this.initialize();
    }

    public BaseCurrency(Integer id) {
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

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getExchangeRate() {
        return this.exchangeRate == null ? Double.valueOf(1.0) : this.exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public static String getExchangeRateDefaultValue() {
        return "1";
    }

    public Double getTolerance() {
        return this.tolerance == null ? Double.valueOf(0.0) : this.tolerance;
    }

    public void setTolerance(Double tolerance) {
        this.tolerance = tolerance;
    }

    public Double getBuyPrice() {
        return this.buyPrice == null ? Double.valueOf(0.0) : this.buyPrice;
    }

    public void setBuyPrice(Double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Double getSalesPrice() {
        return this.salesPrice == null ? Double.valueOf(0.0) : this.salesPrice;
    }

    public void setSalesPrice(Double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public Boolean isMain() {
        return this.main == null ? Boolean.FALSE : this.main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Currency)) {
            return false;
        }
        Currency currency = (Currency)obj;
        if (null == this.getId() || null == currency.getId()) {
            return false;
        }
        return this.getId().equals(currency.getId());
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

