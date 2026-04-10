/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Multiplier;
import java.io.Serializable;

public abstract class BaseMultiplier
implements Comparable,
Serializable {
    public static String REF = "Multiplier";
    public static String PROP_NAME = "name";
    public static String PROP_MAIN = "main";
    public static String PROP_BUTTON_COLOR = "buttonColor";
    public static String PROP_DEFAULT_MULTIPLIER = "defaultMultiplier";
    public static String PROP_SORT_ORDER = "sortOrder";
    public static String PROP_TICKET_PREFIX = "ticketPrefix";
    public static String PROP_TEXT_COLOR = "textColor";
    public static String PROP_RATE = "rate";
    private int hashCode = Integer.MIN_VALUE;
    private String name;
    protected String ticketPrefix;
    protected Double rate;
    protected Integer sortOrder;
    protected Boolean defaultMultiplier;
    protected Boolean main;
    protected Integer buttonColor;
    protected Integer textColor;

    public BaseMultiplier() {
        this.initialize();
    }

    public BaseMultiplier(String name) {
        this.setName(name);
        this.initialize();
    }

    protected void initialize() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.hashCode = Integer.MIN_VALUE;
    }

    public String getTicketPrefix() {
        return this.ticketPrefix;
    }

    public void setTicketPrefix(String ticketPrefix) {
        this.ticketPrefix = ticketPrefix;
    }

    public Double getRate() {
        return this.rate == null ? Double.valueOf(0.0) : this.rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getSortOrder() {
        return this.sortOrder == null ? Integer.valueOf(0) : this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean isDefaultMultiplier() {
        return this.defaultMultiplier == null ? Boolean.FALSE : this.defaultMultiplier;
    }

    public void setDefaultMultiplier(Boolean defaultMultiplier) {
        this.defaultMultiplier = defaultMultiplier;
    }

    public Boolean isMain() {
        return this.main == null ? Boolean.FALSE : this.main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public Integer getButtonColor() {
        return this.buttonColor == null ? Integer.valueOf(0) : this.buttonColor;
    }

    public void setButtonColor(Integer buttonColor) {
        this.buttonColor = buttonColor;
    }

    public Integer getTextColor() {
        return this.textColor == null ? Integer.valueOf(0) : this.textColor;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Multiplier)) {
            return false;
        }
        Multiplier multiplier = (Multiplier)obj;
        if (null == this.getName() || null == multiplier.getName()) {
            return this == obj;
        }
        return this.getName().equals(multiplier.getName());
    }

    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getName()) {
                return super.hashCode();
            }
            String hashStr = this.getClass().getName() + ":" + this.getName().hashCode();
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

