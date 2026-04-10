/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.ModifierMultiplierPrice;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.PizzaModifierPrice;
import java.io.Serializable;

public abstract class BaseModifierMultiplierPrice
implements Comparable,
Serializable {
    public static String REF = "ModifierMultiplierPrice";
    public static String PROP_PIZZA_MODIFIER_PRICE = "pizzaModifierPrice";
    public static String PROP_PRICE = "price";
    public static String PROP_ID = "id";
    public static String PROP_MODIFIER = "modifier";
    public static String PROP_MULTIPLIER = "multiplier";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Double price;
    private Multiplier multiplier;
    private MenuModifier modifier;
    private PizzaModifierPrice pizzaModifierPrice;

    public BaseModifierMultiplierPrice() {
        this.initialize();
    }

    public BaseModifierMultiplierPrice(Integer id) {
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

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public static String getPriceDefaultValue() {
        return "null";
    }

    public Multiplier getMultiplier() {
        return this.multiplier;
    }

    public void setMultiplier(Multiplier multiplier) {
        this.multiplier = multiplier;
    }

    public MenuModifier getModifier() {
        return this.modifier;
    }

    public void setModifier(MenuModifier modifier) {
        this.modifier = modifier;
    }

    public PizzaModifierPrice getPizzaModifierPrice() {
        return this.pizzaModifierPrice;
    }

    public void setPizzaModifierPrice(PizzaModifierPrice pizzaModifierPrice) {
        this.pizzaModifierPrice = pizzaModifierPrice;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof ModifierMultiplierPrice)) {
            return false;
        }
        ModifierMultiplierPrice modifierMultiplierPrice = (ModifierMultiplierPrice)obj;
        if (null == this.getId() || null == modifierMultiplierPrice.getId()) {
            return this == obj;
        }
        return this.getId().equals(modifierMultiplierPrice.getId());
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

