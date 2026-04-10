/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.model;

import com.floreantpos.config.TerminalConfig;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.ModifierMultiplierPrice;
import com.floreantpos.model.Multiplier;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PizzaModifierPrice;
import com.floreantpos.model.base.BaseMenuModifier;
import com.floreantpos.util.POSUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class MenuModifier
extends BaseMenuModifier {
    private static final long serialVersionUID = 1L;
    private transient MenuItemModifierGroup menuItemModifierGroup;

    public MenuModifier() {
    }

    public MenuModifier(Integer id) {
        super(id);
    }

    public MenuItemModifierGroup getMenuItemModifierGroup() {
        return this.menuItemModifierGroup;
    }

    public void setMenuItemModifierGroup(MenuItemModifierGroup menuItemModifierGroup) {
        this.menuItemModifierGroup = menuItemModifierGroup;
    }

    @Override
    public Integer getSortOrder() {
        return this.sortOrder == null ? 9999 : this.sortOrder;
    }

    @Override
    public Integer getButtonColor() {
        return this.buttonColor;
    }

    @Override
    public Integer getTextColor() {
        return this.textColor;
    }

    public String getDisplayName() {
        if (TerminalConfig.isUseTranslatedName() && StringUtils.isNotEmpty((String)this.getTranslatedName())) {
            return this.getTranslatedName();
        }
        return super.getName();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getUniqueId() {
        return ("menu_modifier_" + this.getName() + "_" + this.getId()).replaceAll("\\s+", "_");
    }

    public void addProperty(String name, String value) {
        if (this.getProperties() == null) {
            this.setProperties(new HashMap<String, String>());
        }
        this.getProperties().put(name, value);
    }

    public boolean hasProperty(String key) {
        return this.getProperty(key) != null;
    }

    public String getProperty(String key) {
        if (this.getProperties() == null) {
            return null;
        }
        return this.getProperties().get(key);
    }

    public String getProperty(String key, String defaultValue) {
        if (this.getProperties() == null) {
            return null;
        }
        String string = this.getProperties().get(key);
        if (StringUtils.isEmpty((String)string)) {
            return defaultValue;
        }
        return string;
    }

    public void removeProperty(String typeProperty, String taxProperty) {
        Map<String, String> properties = this.getProperties();
        if (properties == null) {
            return;
        }
        properties.remove(typeProperty);
        properties.remove(taxProperty);
    }

    public double getPriceForMultiplier(Multiplier multiplier) {
        double defaultPrice = this.getPrice();
        if (multiplier == null || multiplier.isMain().booleanValue()) {
            return defaultPrice;
        }
        List<ModifierMultiplierPrice> priceList = this.getMultiplierPriceList();
        if (priceList == null || priceList.isEmpty()) {
            return defaultPrice * multiplier.getRate() / 100.0;
        }
        for (ModifierMultiplierPrice multiplierPrice : priceList) {
            if (!multiplier.getName().equals(multiplierPrice.getMultiplier().getName())) continue;
            return multiplierPrice.getPrice();
        }
        return defaultPrice * multiplier.getRate() / 100.0;
    }

    public double getPriceForSize(MenuItemSize size, boolean extra) {
        return this.getPriceForSizeAndMultiplier(size, extra, null);
    }

    public double getPriceForSizeAndMultiplier(MenuItemSize size, boolean extra, Multiplier multiplier) {
        List<PizzaModifierPrice> priceList = this.getPizzaModifierPriceList();
        double regularPrice = 0.0;
        if (this.isPizzaModifier().booleanValue() && priceList != null) {
            for (PizzaModifierPrice pizzaModifierPrice : priceList) {
                List<ModifierMultiplierPrice> multiplierPriceList;
                if (size.getId().intValue() != pizzaModifierPrice.getSize().getId().intValue() || (multiplierPriceList = pizzaModifierPrice.getMultiplierPriceList()) == null) continue;
                Double multiplierPrice = null;
                for (ModifierMultiplierPrice price : multiplierPriceList) {
                    String priceTableMultiplierName = price.getMultiplier().getName();
                    if (priceTableMultiplierName.equals("Regular")) {
                        regularPrice = price.getPrice();
                        if (!multiplier.getName().equals("Regular")) continue;
                        return regularPrice;
                    }
                    if (!priceTableMultiplierName.equals(multiplier.getName())) continue;
                    multiplierPrice = price.getPrice();
                }
                if (multiplierPrice == null) continue;
                return multiplierPrice;
            }
        }
        return regularPrice * multiplier.getRate() / 100.0;
    }

    public double getPriceByOrderType(OrderType type) {
        double defaultPrice = this.getPrice();
        if (type == null) {
            return defaultPrice;
        }
        String priceProp = this.getProperty(type.name() + "_PRICE");
        if (priceProp == null) {
            return defaultPrice;
        }
        try {
            return Double.parseDouble(priceProp);
        }
        catch (Exception e) {
            return defaultPrice;
        }
    }

    public double getTaxByOrderType(OrderType type) {
        if (this.getTax() == null) {
            return 0.0;
        }
        double defaultTax = this.getTax().getRate();
        if (type == null) {
            return defaultTax;
        }
        String taxProp = this.getProperty(type.name() + "_TAX");
        if (taxProp == null) {
            return defaultTax;
        }
        try {
            return Double.parseDouble(taxProp);
        }
        catch (Exception e) {
            return defaultTax;
        }
    }

    public double getExtraPriceByOrderType(OrderType type) {
        double defaultPrice = this.getExtraPrice();
        if (type == null) {
            return defaultPrice;
        }
        String extraPriceProp = this.getProperty(type.name() + "_EXTRA_PRICE");
        if (extraPriceProp == null) {
            return defaultPrice;
        }
        try {
            return Double.parseDouble(extraPriceProp);
        }
        catch (Exception e) {
            return defaultPrice;
        }
    }

    public double getExtraTaxByOrderType(OrderType type) {
        if (this.getTax() == null) {
            return 0.0;
        }
        double defaultTax = this.getTax().getRate();
        if (type == null) {
            return defaultTax;
        }
        String extraTaxProp = this.getProperty(type.name() + "_EXTRA_TAX");
        if (extraTaxProp == null) {
            return defaultTax;
        }
        try {
            return Double.parseDouble(extraTaxProp);
        }
        catch (Exception e) {
            return defaultTax;
        }
    }

    public boolean isPropertyValueTrue(String propertyName) {
        String property = this.getProperty(propertyName);
        return POSUtil.getBoolean(property);
    }

    public void setPriceByOrderType(String type, double price) {
        type = type.replaceAll(" ", "_");
        this.addProperty(type + "_PRICE", String.valueOf(price));
    }

    public void setTaxByOrderType(String type, double taxRate) {
        type = type.replaceAll(" ", "_");
        this.addProperty(type + "_TAX", String.valueOf(taxRate));
    }

    public void setExtraPriceByOrderType(String type, double extraPrice) {
        type = type.replaceAll(" ", "_");
        this.addProperty(type + "_EXTRA_PRICE", String.valueOf(extraPrice));
    }

    public void setExtraTaxByOrderType(String type, double extraTaxRate) {
        type = type.replaceAll(" ", "_");
        this.addProperty(type + "_EXTRA_TAX", String.valueOf(extraTaxRate));
    }
}

