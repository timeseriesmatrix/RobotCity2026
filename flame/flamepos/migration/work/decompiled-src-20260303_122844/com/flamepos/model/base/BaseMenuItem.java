/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Discount;
import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.MenuItem;
import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.MenuItemShift;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PizzaPrice;
import com.floreantpos.model.PrinterGroup;
import com.floreantpos.model.Recepie;
import com.floreantpos.model.Tax;
import com.floreantpos.model.Terminal;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseMenuItem
implements Comparable,
Serializable {
    public static String REF = "MenuItem";
    public static String PROP_UNIT_NAME = "unitName";
    public static String PROP_BUY_PRICE = "buyPrice";
    public static String PROP_STOCK_AMOUNT = "stockAmount";
    public static String PROP_PARENT = "parent";
    public static String PROP_BARCODE = "barcode";
    public static String PROP_DESCRIPTION = "description";
    public static String PROP_SHOW_IMAGE_ONLY = "showImageOnly";
    public static String PROP_VISIBLE = "visible";
    public static String PROP_DISCOUNT_RATE = "discountRate";
    public static String PROP_SORT_ORDER = "sortOrder";
    public static String PROP_TAX = "tax";
    public static String PROP_IMAGE_DATA = "imageData";
    public static String PROP_FRACTIONAL_UNIT = "fractionalUnit";
    public static String PROP_PIZZA_TYPE = "pizzaType";
    public static String PROP_NAME = "name";
    public static String PROP_PRINTER_GROUP = "printerGroup";
    public static String PROP_TEXT_COLOR_CODE = "textColorCode";
    public static String PROP_DISABLE_WHEN_STOCK_AMOUNT_IS_ZERO = "disableWhenStockAmountIsZero";
    public static String PROP_RECEPIE = "recepie";
    public static String PROP_DEFAULT_SELL_PORTION = "defaultSellPortion";
    public static String PROP_PRICE = "price";
    public static String PROP_BUTTON_COLOR_CODE = "buttonColorCode";
    public static String PROP_ID = "id";
    public static String PROP_TRANSLATED_NAME = "translatedName";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected String description;
    protected String unitName;
    protected String translatedName;
    protected String barcode;
    protected Double buyPrice;
    protected Double stockAmount;
    protected Double price;
    protected Double discountRate;
    protected Boolean visible;
    protected Boolean disableWhenStockAmountIsZero;
    protected Integer sortOrder;
    protected Integer buttonColorCode;
    protected Integer textColorCode;
    protected byte[] imageData;
    protected Boolean showImageOnly;
    protected Boolean fractionalUnit;
    protected Boolean pizzaType;
    protected Integer defaultSellPortion;
    private MenuGroup parent;
    private Tax tax;
    private Recepie recepie;
    private PrinterGroup printerGroup;
    private List<PizzaPrice> pizzaPriceList;
    private List<MenuItemShift> shifts;
    private List<Discount> discounts;
    private List<MenuItemModifierGroup> menuItemModiferGroups;
    private List<Terminal> terminals;
    private Map<String, String> properties;
    private List<OrderType> orderTypeList;

    public BaseMenuItem() {
        this.initialize();
    }

    public BaseMenuItem(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseMenuItem(Integer id, String name, Double buyPrice, Double price) {
        this.setId(id);
        this.setName(name);
        this.setBuyPrice(buyPrice);
        this.setPrice(price);
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnitName() {
        return this.unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getTranslatedName() {
        return this.translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }

    public String getBarcode() {
        return this.barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Double getBuyPrice() {
        return this.buyPrice == null ? Double.valueOf(0.0) : this.buyPrice;
    }

    public void setBuyPrice(Double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Double getStockAmount() {
        return this.stockAmount == null ? Double.valueOf(0.0) : this.stockAmount;
    }

    public void setStockAmount(Double stockAmount) {
        this.stockAmount = stockAmount;
    }

    public Double getPrice() {
        return this.price == null ? Double.valueOf(0.0) : this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscountRate() {
        return this.discountRate == null ? Double.valueOf(0.0) : this.discountRate;
    }

    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }

    public Boolean isVisible() {
        return this.visible == null ? Boolean.valueOf(true) : this.visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public static String getVisibleDefaultValue() {
        return "true";
    }

    public Boolean isDisableWhenStockAmountIsZero() {
        return this.disableWhenStockAmountIsZero == null ? Boolean.valueOf(false) : this.disableWhenStockAmountIsZero;
    }

    public void setDisableWhenStockAmountIsZero(Boolean disableWhenStockAmountIsZero) {
        this.disableWhenStockAmountIsZero = disableWhenStockAmountIsZero;
    }

    public static String getDisableWhenStockAmountIsZeroDefaultValue() {
        return "false";
    }

    public Integer getSortOrder() {
        return this.sortOrder == null ? Integer.valueOf(0) : this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getButtonColorCode() {
        return this.buttonColorCode == null ? null : this.buttonColorCode;
    }

    public void setButtonColorCode(Integer buttonColorCode) {
        this.buttonColorCode = buttonColorCode;
    }

    public static String getButtonColorCodeDefaultValue() {
        return "null";
    }

    public Integer getTextColorCode() {
        return this.textColorCode == null ? null : this.textColorCode;
    }

    public void setTextColorCode(Integer textColorCode) {
        this.textColorCode = textColorCode;
    }

    public static String getTextColorCodeDefaultValue() {
        return "null";
    }

    public byte[] getImageData() {
        return this.imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public Boolean isShowImageOnly() {
        return this.showImageOnly == null ? Boolean.FALSE : this.showImageOnly;
    }

    public void setShowImageOnly(Boolean showImageOnly) {
        this.showImageOnly = showImageOnly;
    }

    public Boolean isFractionalUnit() {
        return this.fractionalUnit == null ? Boolean.FALSE : this.fractionalUnit;
    }

    public void setFractionalUnit(Boolean fractionalUnit) {
        this.fractionalUnit = fractionalUnit;
    }

    public Boolean isPizzaType() {
        return this.pizzaType == null ? Boolean.FALSE : this.pizzaType;
    }

    public void setPizzaType(Boolean pizzaType) {
        this.pizzaType = pizzaType;
    }

    public Integer getDefaultSellPortion() {
        return this.defaultSellPortion == null ? Integer.valueOf(0) : this.defaultSellPortion;
    }

    public void setDefaultSellPortion(Integer defaultSellPortion) {
        this.defaultSellPortion = defaultSellPortion;
    }

    public MenuGroup getParent() {
        return this.parent;
    }

    public void setParent(MenuGroup parent) {
        this.parent = parent;
    }

    public Tax getTax() {
        return this.tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Recepie getRecepie() {
        return this.recepie;
    }

    public void setRecepie(Recepie recepie) {
        this.recepie = recepie;
    }

    public PrinterGroup getPrinterGroup() {
        return this.printerGroup;
    }

    public void setPrinterGroup(PrinterGroup printerGroup) {
        this.printerGroup = printerGroup;
    }

    public List<PizzaPrice> getPizzaPriceList() {
        return this.pizzaPriceList;
    }

    public void setPizzaPriceList(List<PizzaPrice> pizzaPriceList) {
        this.pizzaPriceList = pizzaPriceList;
    }

    public void addTopizzaPriceList(PizzaPrice pizzaPrice) {
        if (null == this.getPizzaPriceList()) {
            this.setPizzaPriceList(new ArrayList<PizzaPrice>());
        }
        this.getPizzaPriceList().add(pizzaPrice);
    }

    public List<MenuItemShift> getShifts() {
        return this.shifts;
    }

    public void setShifts(List<MenuItemShift> shifts) {
        this.shifts = shifts;
    }

    public void addToshifts(MenuItemShift menuItemShift) {
        if (null == this.getShifts()) {
            this.setShifts(new ArrayList<MenuItemShift>());
        }
        this.getShifts().add(menuItemShift);
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

    public List<MenuItemModifierGroup> getMenuItemModiferGroups() {
        return this.menuItemModiferGroups;
    }

    public void setMenuItemModiferGroups(List<MenuItemModifierGroup> menuItemModiferGroups) {
        this.menuItemModiferGroups = menuItemModiferGroups;
    }

    public void addTomenuItemModiferGroups(MenuItemModifierGroup menuItemModifierGroup) {
        if (null == this.getMenuItemModiferGroups()) {
            this.setMenuItemModiferGroups(new ArrayList<MenuItemModifierGroup>());
        }
        this.getMenuItemModiferGroups().add(menuItemModifierGroup);
    }

    public List<Terminal> getTerminals() {
        return this.terminals;
    }

    public void setTerminals(List<Terminal> terminals) {
        this.terminals = terminals;
    }

    public void addToterminals(Terminal terminal) {
        if (null == this.getTerminals()) {
            this.setTerminals(new ArrayList<Terminal>());
        }
        this.getTerminals().add(terminal);
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<OrderType> getOrderTypeList() {
        return this.orderTypeList;
    }

    public void setOrderTypeList(List<OrderType> orderTypeList) {
        this.orderTypeList = orderTypeList;
    }

    public void addToorderTypeList(OrderType orderType) {
        if (null == this.getOrderTypeList()) {
            this.setOrderTypeList(new ArrayList<OrderType>());
        }
        this.getOrderTypeList().add(orderType);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof MenuItem)) {
            return false;
        }
        MenuItem menuItem = (MenuItem)obj;
        if (null == this.getId() || null == menuItem.getId()) {
            return this == obj;
        }
        return this.getId().equals(menuItem.getId());
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

