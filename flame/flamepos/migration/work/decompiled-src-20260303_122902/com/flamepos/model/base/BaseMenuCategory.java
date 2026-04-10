/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Discount;
import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.MenuGroup;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseMenuCategory
implements Comparable,
Serializable {
    public static String REF = "MenuCategory";
    public static String PROP_NAME = "name";
    public static String PROP_TEXT_COLOR_CODE = "textColorCode";
    public static String PROP_BEVERAGE = "beverage";
    public static String PROP_VISIBLE = "visible";
    public static String PROP_SORT_ORDER = "sortOrder";
    public static String PROP_BUTTON_COLOR_CODE = "buttonColorCode";
    public static String PROP_ID = "id";
    public static String PROP_TRANSLATED_NAME = "translatedName";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected String translatedName;
    protected Boolean visible;
    protected Boolean beverage;
    protected Integer sortOrder;
    protected Integer buttonColorCode;
    protected Integer textColorCode;
    private List<Discount> discounts;
    private List<MenuGroup> menuGroups;

    public BaseMenuCategory() {
        this.initialize();
    }

    public BaseMenuCategory(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseMenuCategory(Integer id, String name) {
        this.setId(id);
        this.setName(name);
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

    public Boolean isVisible() {
        return this.visible == null ? Boolean.FALSE : this.visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean isBeverage() {
        return this.beverage == null ? Boolean.FALSE : this.beverage;
    }

    public void setBeverage(Boolean beverage) {
        this.beverage = beverage;
    }

    public Integer getSortOrder() {
        return this.sortOrder == null ? Integer.valueOf(0) : this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getButtonColorCode() {
        return this.buttonColorCode == null ? Integer.valueOf(0) : this.buttonColorCode;
    }

    public void setButtonColorCode(Integer buttonColorCode) {
        this.buttonColorCode = buttonColorCode;
    }

    public Integer getTextColorCode() {
        return this.textColorCode == null ? Integer.valueOf(0) : this.textColorCode;
    }

    public void setTextColorCode(Integer textColorCode) {
        this.textColorCode = textColorCode;
    }

    public List<Discount> getDiscounts() {
        return this.discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }

    public void addTodiscounts(Discount discount) {
        if (null == this.getDiscounts()) {
            this.setDiscounts(new ArrayList<Discount>());
        }
        this.getDiscounts().add(discount);
    }

    public List<MenuGroup> getMenuGroups() {
        return this.menuGroups;
    }

    public void setMenuGroups(List<MenuGroup> menuGroups) {
        this.menuGroups = menuGroups;
    }

    public void addTomenuGroups(MenuGroup menuGroup) {
        if (null == this.getMenuGroups()) {
            this.setMenuGroups(new ArrayList<MenuGroup>());
        }
        this.getMenuGroups().add(menuGroup);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof MenuCategory)) {
            return false;
        }
        MenuCategory menuCategory = (MenuCategory)obj;
        if (null == this.getId() || null == menuCategory.getId()) {
            return false;
        }
        return this.getId().equals(menuCategory.getId());
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

