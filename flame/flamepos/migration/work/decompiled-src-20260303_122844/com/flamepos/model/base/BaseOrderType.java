/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.OrderType;
import java.io.Serializable;

public abstract class BaseOrderType
implements Comparable,
Serializable {
    public static String REF = "OrderType";
    public static String PROP_CLOSE_ON_PAID = "closeOnPaid";
    public static String PROP_SHOW_TABLE_SELECTION = "showTableSelection";
    public static String PROP_SHOW_GUEST_SELECTION = "showGuestSelection";
    public static String PROP_SHOULD_PRINT_TO_KITCHEN = "shouldPrintToKitchen";
    public static String PROP_ALLOW_SEAT_BASED_ORDER = "allowSeatBasedOrder";
    public static String PROP_SHOW_ITEM_BARCODE = "showItemBarcode";
    public static String PROP_HAS_FOR_HERE_AND_TO_GO = "hasForHereAndToGo";
    public static String PROP_PRE_AUTH_CREDIT_CARD = "preAuthCreditCard";
    public static String PROP_ENABLED = "enabled";
    public static String PROP_BAR_TAB = "barTab";
    public static String PROP_SHOW_STOCK_COUNT_ON_BUTTON = "showStockCountOnButton";
    public static String PROP_NAME = "name";
    public static String PROP_SHOW_IN_LOGIN_SCREEN = "showInLoginScreen";
    public static String PROP_PREPAID = "prepaid";
    public static String PROP_SHOW_UNIT_PRICE_IN_TICKET_GRID = "showUnitPriceInTicketGrid";
    public static String PROP_REQUIRED_CUSTOMER_DATA = "requiredCustomerData";
    public static String PROP_DELIVERY = "delivery";
    public static String PROP_HIDE_ITEM_WITH_EMPTY_INVENTORY = "hideItemWithEmptyInventory";
    public static String PROP_ID = "id";
    public static String PROP_SHOW_PRICE_ON_BUTTON = "showPriceOnButton";
    public static String PROP_CONSOLIDATE_ITEMS_IN_RECEIPT = "consolidateItemsInReceipt";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected Boolean enabled;
    protected Boolean showTableSelection;
    protected Boolean showGuestSelection;
    protected Boolean shouldPrintToKitchen;
    protected Boolean prepaid;
    protected Boolean closeOnPaid;
    protected Boolean requiredCustomerData;
    protected Boolean delivery;
    protected Boolean showItemBarcode;
    protected Boolean showInLoginScreen;
    protected Boolean consolidateItemsInReceipt;
    protected Boolean allowSeatBasedOrder;
    protected Boolean hideItemWithEmptyInventory;
    protected Boolean hasForHereAndToGo;
    protected Boolean preAuthCreditCard;
    protected Boolean barTab;
    protected Boolean showPriceOnButton;
    protected Boolean showStockCountOnButton;
    protected Boolean showUnitPriceInTicketGrid;

    public BaseOrderType() {
        this.initialize();
    }

    public BaseOrderType(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseOrderType(Integer id, String name) {
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

    public Boolean isEnabled() {
        return this.enabled == null ? Boolean.FALSE : this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean isShowTableSelection() {
        return this.showTableSelection == null ? Boolean.FALSE : this.showTableSelection;
    }

    public void setShowTableSelection(Boolean showTableSelection) {
        this.showTableSelection = showTableSelection;
    }

    public Boolean isShowGuestSelection() {
        return this.showGuestSelection == null ? Boolean.FALSE : this.showGuestSelection;
    }

    public void setShowGuestSelection(Boolean showGuestSelection) {
        this.showGuestSelection = showGuestSelection;
    }

    public Boolean isShouldPrintToKitchen() {
        return this.shouldPrintToKitchen == null ? Boolean.FALSE : this.shouldPrintToKitchen;
    }

    public void setShouldPrintToKitchen(Boolean shouldPrintToKitchen) {
        this.shouldPrintToKitchen = shouldPrintToKitchen;
    }

    public Boolean isPrepaid() {
        return this.prepaid == null ? Boolean.FALSE : this.prepaid;
    }

    public void setPrepaid(Boolean prepaid) {
        this.prepaid = prepaid;
    }

    public Boolean isCloseOnPaid() {
        return this.closeOnPaid == null ? Boolean.FALSE : this.closeOnPaid;
    }

    public void setCloseOnPaid(Boolean closeOnPaid) {
        this.closeOnPaid = closeOnPaid;
    }

    public Boolean isRequiredCustomerData() {
        return this.requiredCustomerData == null ? Boolean.FALSE : this.requiredCustomerData;
    }

    public void setRequiredCustomerData(Boolean requiredCustomerData) {
        this.requiredCustomerData = requiredCustomerData;
    }

    public Boolean isDelivery() {
        return this.delivery == null ? Boolean.FALSE : this.delivery;
    }

    public void setDelivery(Boolean delivery) {
        this.delivery = delivery;
    }

    public Boolean isShowItemBarcode() {
        return this.showItemBarcode == null ? Boolean.FALSE : this.showItemBarcode;
    }

    public void setShowItemBarcode(Boolean showItemBarcode) {
        this.showItemBarcode = showItemBarcode;
    }

    public Boolean isShowInLoginScreen() {
        return this.showInLoginScreen == null ? Boolean.FALSE : this.showInLoginScreen;
    }

    public void setShowInLoginScreen(Boolean showInLoginScreen) {
        this.showInLoginScreen = showInLoginScreen;
    }

    public Boolean isConsolidateItemsInReceipt() {
        return this.consolidateItemsInReceipt == null ? Boolean.FALSE : this.consolidateItemsInReceipt;
    }

    public void setConsolidateItemsInReceipt(Boolean consolidateItemsInReceipt) {
        this.consolidateItemsInReceipt = consolidateItemsInReceipt;
    }

    public Boolean isAllowSeatBasedOrder() {
        return this.allowSeatBasedOrder == null ? Boolean.FALSE : this.allowSeatBasedOrder;
    }

    public void setAllowSeatBasedOrder(Boolean allowSeatBasedOrder) {
        this.allowSeatBasedOrder = allowSeatBasedOrder;
    }

    public Boolean isHideItemWithEmptyInventory() {
        return this.hideItemWithEmptyInventory == null ? Boolean.FALSE : this.hideItemWithEmptyInventory;
    }

    public void setHideItemWithEmptyInventory(Boolean hideItemWithEmptyInventory) {
        this.hideItemWithEmptyInventory = hideItemWithEmptyInventory;
    }

    public Boolean isHasForHereAndToGo() {
        return this.hasForHereAndToGo == null ? Boolean.FALSE : this.hasForHereAndToGo;
    }

    public void setHasForHereAndToGo(Boolean hasForHereAndToGo) {
        this.hasForHereAndToGo = hasForHereAndToGo;
    }

    public Boolean isPreAuthCreditCard() {
        return this.preAuthCreditCard == null ? Boolean.FALSE : this.preAuthCreditCard;
    }

    public void setPreAuthCreditCard(Boolean preAuthCreditCard) {
        this.preAuthCreditCard = preAuthCreditCard;
    }

    public Boolean isBarTab() {
        return this.barTab == null ? Boolean.FALSE : this.barTab;
    }

    public void setBarTab(Boolean barTab) {
        this.barTab = barTab;
    }

    public Boolean isShowPriceOnButton() {
        return this.showPriceOnButton == null ? Boolean.FALSE : this.showPriceOnButton;
    }

    public void setShowPriceOnButton(Boolean showPriceOnButton) {
        this.showPriceOnButton = showPriceOnButton;
    }

    public Boolean isShowStockCountOnButton() {
        return this.showStockCountOnButton == null ? Boolean.FALSE : this.showStockCountOnButton;
    }

    public void setShowStockCountOnButton(Boolean showStockCountOnButton) {
        this.showStockCountOnButton = showStockCountOnButton;
    }

    public Boolean isShowUnitPriceInTicketGrid() {
        return this.showUnitPriceInTicketGrid == null ? Boolean.FALSE : this.showUnitPriceInTicketGrid;
    }

    public void setShowUnitPriceInTicketGrid(Boolean showUnitPriceInTicketGrid) {
        this.showUnitPriceInTicketGrid = showUnitPriceInTicketGrid;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof OrderType)) {
            return false;
        }
        OrderType orderType = (OrderType)obj;
        if (null == this.getId() || null == orderType.getId()) {
            return false;
        }
        return this.getId().equals(orderType.getId());
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

