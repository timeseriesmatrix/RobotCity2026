/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Restaurant;
import java.io.Serializable;

public abstract class BaseRestaurant
implements Comparable,
Serializable {
    public static String REF = "Restaurant";
    public static String PROP_ITEM_PRICE_INCLUDES_TAX = "itemPriceIncludesTax";
    public static String PROP_TELEPHONE = "telephone";
    public static String PROP_TICKET_FOOTER_MESSAGE = "ticketFooterMessage";
    public static String PROP_SERVICE_CHARGE_PERCENTAGE = "serviceChargePercentage";
    public static String PROP_UNIQUE_ID = "uniqueId";
    public static String PROP_ZIP_CODE = "zipCode";
    public static String PROP_NAME = "name";
    public static String PROP_DEFAULT_GRATUITY_PERCENTAGE = "defaultGratuityPercentage";
    public static String PROP_CURRENCY_NAME = "currencyName";
    public static String PROP_TABLES = "tables";
    public static String PROP_ID = "id";
    public static String PROP_CAPACITY = "capacity";
    public static String PROP_ALLOW_MODIFIER_MAX_EXCEED = "allowModifierMaxExceed";
    public static String PROP_ADDRESS_LINE1 = "addressLine1";
    public static String PROP_ADDRESS_LINE2 = "addressLine2";
    public static String PROP_ADDRESS_LINE3 = "addressLine3";
    public static String PROP_CURRENCY_SYMBOL = "currencySymbol";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Integer uniqueId;
    protected String name;
    protected String addressLine1;
    protected String addressLine2;
    protected String addressLine3;
    protected String zipCode;
    protected String telephone;
    protected Integer capacity;
    protected Integer tables;
    protected String currencyName;
    protected String currencySymbol;
    protected Double serviceChargePercentage;
    protected Double defaultGratuityPercentage;
    protected String ticketFooterMessage;
    protected Boolean itemPriceIncludesTax;
    protected Boolean allowModifierMaxExceed;

    public BaseRestaurant() {
        this.initialize();
    }

    public BaseRestaurant(Integer id) {
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

    public Integer getUniqueId() {
        return this.uniqueId == null ? Integer.valueOf(0) : this.uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return this.addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getCapacity() {
        return this.capacity == null ? Integer.valueOf(0) : this.capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getTables() {
        return this.tables == null ? Integer.valueOf(0) : this.tables;
    }

    public void setTables(Integer tables) {
        this.tables = tables;
    }

    public String getCurrencyName() {
        return this.currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return this.currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public Double getServiceChargePercentage() {
        return this.serviceChargePercentage == null ? Double.valueOf(0.0) : this.serviceChargePercentage;
    }

    public void setServiceChargePercentage(Double serviceChargePercentage) {
        this.serviceChargePercentage = serviceChargePercentage;
    }

    public Double getDefaultGratuityPercentage() {
        return this.defaultGratuityPercentage == null ? Double.valueOf(0.0) : this.defaultGratuityPercentage;
    }

    public void setDefaultGratuityPercentage(Double defaultGratuityPercentage) {
        this.defaultGratuityPercentage = defaultGratuityPercentage;
    }

    public String getTicketFooterMessage() {
        return this.ticketFooterMessage;
    }

    public void setTicketFooterMessage(String ticketFooterMessage) {
        this.ticketFooterMessage = ticketFooterMessage;
    }

    public Boolean isItemPriceIncludesTax() {
        return this.itemPriceIncludesTax == null ? Boolean.FALSE : this.itemPriceIncludesTax;
    }

    public void setItemPriceIncludesTax(Boolean itemPriceIncludesTax) {
        this.itemPriceIncludesTax = itemPriceIncludesTax;
    }

    public Boolean isAllowModifierMaxExceed() {
        return this.allowModifierMaxExceed == null ? Boolean.FALSE : this.allowModifierMaxExceed;
    }

    public void setAllowModifierMaxExceed(Boolean allowModifierMaxExceed) {
        this.allowModifierMaxExceed = allowModifierMaxExceed;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Restaurant)) {
            return false;
        }
        Restaurant restaurant = (Restaurant)obj;
        if (null == this.getId() || null == restaurant.getId()) {
            return this == obj;
        }
        return this.getId().equals(restaurant.getId());
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

