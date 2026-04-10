/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.ModifierMultiplierPrice;
import com.floreantpos.model.PizzaModifierPrice;
import com.floreantpos.model.Tax;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseMenuModifier
implements Comparable,
Serializable {
    public static String REF = "MenuModifier";
    public static String PROP_SHOULD_PRINT_TO_KITCHEN = "shouldPrintToKitchen";
    public static String PROP_EXTRA_PRICE = "extraPrice";
    public static String PROP_MODIFIER_GROUP = "modifierGroup";
    public static String PROP_SORT_ORDER = "sortOrder";
    public static String PROP_TAX = "tax";
    public static String PROP_NAME = "name";
    public static String PROP_BUTTON_COLOR = "buttonColor";
    public static String PROP_TRANSLATED_NAME = "translatedName";
    public static String PROP_PRICE = "price";
    public static String PROP_SHOULD_SECTION_WISE_PRICE = "shouldSectionWisePrice";
    public static String PROP_ENABLE = "enable";
    public static String PROP_TEXT_COLOR = "textColor";
    public static String PROP_PIZZA_MODIFIER = "pizzaModifier";
    public static String PROP_ID = "id";
    public static String PROP_FIXED_PRICE = "fixedPrice";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected String translatedName;
    protected Double price;
    protected Double extraPrice;
    protected Integer sortOrder;
    protected Integer buttonColor;
    protected Integer textColor;
    protected Boolean enable;
    protected Boolean fixedPrice;
    protected Boolean shouldPrintToKitchen;
    protected Boolean shouldSectionWisePrice;
    protected Boolean pizzaModifier;
    private MenuModifierGroup modifierGroup;
    private Tax tax;
    private List<PizzaModifierPrice> pizzaModifierPriceList;
    private List<ModifierMultiplierPrice> multiplierPriceList;
    private Map<String, String> properties;

    public BaseMenuModifier() {
        this.initialize();
    }

    public BaseMenuModifier(Integer id) {
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTranslatedName() {
        return this.translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }

    public Double getPrice() {
        return this.price == null ? Double.valueOf(0.0) : this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getExtraPrice() {
        return this.extraPrice == null ? Double.valueOf(0.0) : this.extraPrice;
    }

    public void setExtraPrice(Double extraPrice) {
        this.extraPrice = extraPrice;
    }

    public Integer getSortOrder() {
        return this.sortOrder == null ? Integer.valueOf(0) : this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    public Boolean isEnable() {
        return this.enable == null ? Boolean.FALSE : this.enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean isFixedPrice() {
        return this.fixedPrice == null ? Boolean.FALSE : this.fixedPrice;
    }

    public void setFixedPrice(Boolean fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public Boolean isShouldPrintToKitchen() {
        return this.shouldPrintToKitchen == null ? Boolean.valueOf(true) : this.shouldPrintToKitchen;
    }

    public void setShouldPrintToKitchen(Boolean shouldPrintToKitchen) {
        this.shouldPrintToKitchen = shouldPrintToKitchen;
    }

    public static String getShouldPrintToKitchenDefaultValue() {
        return "true";
    }

    public Boolean isShouldSectionWisePrice() {
        return this.shouldSectionWisePrice == null ? Boolean.FALSE : this.shouldSectionWisePrice;
    }

    public void setShouldSectionWisePrice(Boolean shouldSectionWisePrice) {
        this.shouldSectionWisePrice = shouldSectionWisePrice;
    }

    public Boolean isPizzaModifier() {
        return this.pizzaModifier == null ? Boolean.FALSE : this.pizzaModifier;
    }

    public void setPizzaModifier(Boolean pizzaModifier) {
        this.pizzaModifier = pizzaModifier;
    }

    public MenuModifierGroup getModifierGroup() {
        return this.modifierGroup;
    }

    public void setModifierGroup(MenuModifierGroup modifierGroup) {
        this.modifierGroup = modifierGroup;
    }

    public Tax getTax() {
        return this.tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public List<PizzaModifierPrice> getPizzaModifierPriceList() {
        return this.pizzaModifierPriceList;
    }

    public void setPizzaModifierPriceList(List<PizzaModifierPrice> pizzaModifierPriceList) {
        this.pizzaModifierPriceList = pizzaModifierPriceList;
    }

    public void addTopizzaModifierPriceList(PizzaModifierPrice pizzaModifierPrice) {
        if (null == this.getPizzaModifierPriceList()) {
            this.setPizzaModifierPriceList(new ArrayList<PizzaModifierPrice>());
        }
        this.getPizzaModifierPriceList().add(pizzaModifierPrice);
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

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof MenuModifier)) {
            return false;
        }
        MenuModifier menuModifier = (MenuModifier)obj;
        if (null == this.getId() || null == menuModifier.getId()) {
            return false;
        }
        return this.getId().equals(menuModifier.getId());
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

