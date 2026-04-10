/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.ModifierMultiplierPrice;
import com.floreantpos.model.PizzaModifierPrice;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePizzaModifierPrice
implements Comparable,
Serializable {
    public static String REF = "PizzaModifierPrice";
    public static String PROP_ID = "id";
    public static String PROP_SIZE = "size";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    private MenuItemSize size;
    private List<ModifierMultiplierPrice> multiplierPriceList;

    public BasePizzaModifierPrice() {
        this.initialize();
    }

    public BasePizzaModifierPrice(Integer id) {
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

    public MenuItemSize getSize() {
        return this.size;
    }

    public void setSize(MenuItemSize size) {
        this.size = size;
    }

    public List<ModifierMultiplierPrice> getMultiplierPriceList() {
        return this.multiplierPriceList;
    }

    public void setMultiplierPriceList(List<ModifierMultiplierPrice> multiplierPriceList) {
        this.multiplierPriceList = multiplierPriceList;
    }

    public void addTomultiplierPriceList(ModifierMultiplierPrice modifierMultiplierPrice) {
        if (null == this.getMultiplierPriceList()) {
            this.setMultiplierPriceList(new ArrayList<ModifierMultiplierPrice>());
        }
        this.getMultiplierPriceList().add(modifierMultiplierPrice);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof PizzaModifierPrice)) {
            return false;
        }
        PizzaModifierPrice pizzaModifierPrice = (PizzaModifierPrice)obj;
        if (null == this.getId() || null == pizzaModifierPrice.getId()) {
            return this == obj;
        }
        return this.getId().equals(pizzaModifierPrice.getId());
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

