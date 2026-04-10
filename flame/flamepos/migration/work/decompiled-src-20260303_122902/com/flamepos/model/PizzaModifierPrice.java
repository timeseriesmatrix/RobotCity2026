/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.ModifierMultiplierPrice;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.base.BasePizzaModifierPrice;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PizzaModifierPrice
extends BasePizzaModifierPrice {
    private static final long serialVersionUID = 1L;
    private Map<String, ModifierMultiplierPrice> priceMap = new HashMap<String, ModifierMultiplierPrice>();

    public PizzaModifierPrice() {
    }

    public PizzaModifierPrice(Integer id) {
        super(id);
    }

    public double getPrice() {
        return 0.0;
    }

    public double getExtraPrice() {
        return 0.0;
    }

    public void setPrice(double price) {
    }

    public void setExtraPrice(double price) {
    }

    public void initializeSizeAndPriceList(List<Multiplier> multipliers) {
        List<ModifierMultiplierPrice> priceList = this.getMultiplierPriceList();
        if (priceList == null) {
            priceList = new ArrayList<ModifierMultiplierPrice>();
        }
        for (ModifierMultiplierPrice price : priceList) {
            this.priceMap.put(price.getMultiplier().getName(), price);
        }
        for (Multiplier multiplier : multipliers) {
            ModifierMultiplierPrice priceItem = this.priceMap.get(multiplier.getName());
            if (priceItem != null) continue;
            priceItem = new ModifierMultiplierPrice();
            priceItem.setMultiplier(multiplier);
            priceList.add(priceItem);
            this.priceMap.put(multiplier.getName(), priceItem);
        }
        this.setMultiplierPriceList(priceList);
    }

    public ModifierMultiplierPrice getMultiplier(String columnName) {
        return this.priceMap.get(columnName);
    }

    public void populateMultiplierPriceListRowValue(MenuModifier modifier) {
        Iterator<ModifierMultiplierPrice> iterator = this.getMultiplierPriceList().iterator();
        while (iterator.hasNext()) {
            ModifierMultiplierPrice price = iterator.next();
            if (price.getPrice() == null) {
                iterator.remove();
                continue;
            }
            price.setModifier(modifier);
            price.setPizzaModifierPrice(this);
        }
    }
}

