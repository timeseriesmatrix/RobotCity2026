/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemModifier;
import java.io.Serializable;

public abstract class BaseTicketItemModifier
implements Comparable,
Serializable {
    public static String REF = "TicketItemModifier";
    public static String PROP_MULTIPLIER_NAME = "multiplierName";
    public static String PROP_STATUS = "status";
    public static String PROP_SHOULD_PRINT_TO_KITCHEN = "shouldPrintToKitchen";
    public static String PROP_TICKET_ITEM = "ticketItem";
    public static String PROP_INFO_ONLY = "infoOnly";
    public static String PROP_TOTAL_AMOUNT = "totalAmount";
    public static String PROP_MENU_ITEM_MODIFIER_GROUP_ID = "menuItemModifierGroupId";
    public static String PROP_NAME = "name";
    public static String PROP_UNIT_PRICE = "unitPrice";
    public static String PROP_TAX_AMOUNT = "taxAmount";
    public static String PROP_SHOULD_SECTION_WISE_PRICE = "shouldSectionWisePrice";
    public static String PROP_TAX_RATE = "taxRate";
    public static String PROP_ITEM_COUNT = "itemCount";
    public static String PROP_MODIFIER_TYPE = "modifierType";
    public static String PROP_SECTION_NAME = "sectionName";
    public static String PROP_ID = "id";
    public static String PROP_PRINTED_TO_KITCHEN = "printedToKitchen";
    public static String PROP_MODIFIER_ID = "modifierId";
    public static String PROP_SUB_TOTAL_AMOUNT = "subTotalAmount";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Integer modifierId;
    protected Integer menuItemModifierGroupId;
    protected Integer itemCount;
    protected String name;
    protected Double unitPrice;
    protected Double taxRate;
    protected Integer modifierType;
    protected Double subTotalAmount;
    protected Double totalAmount;
    protected Double taxAmount;
    protected Boolean infoOnly;
    protected String sectionName;
    protected String multiplierName;
    protected Boolean shouldPrintToKitchen;
    protected Boolean shouldSectionWisePrice;
    protected String status;
    protected Boolean printedToKitchen;
    private TicketItem ticketItem;

    public BaseTicketItemModifier() {
        this.initialize();
    }

    public BaseTicketItemModifier(Integer id) {
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

    public Integer getModifierId() {
        return this.modifierId == null ? Integer.valueOf(0) : this.modifierId;
    }

    public void setModifierId(Integer modifierId) {
        this.modifierId = modifierId;
    }

    public Integer getMenuItemModifierGroupId() {
        return this.menuItemModifierGroupId == null ? Integer.valueOf(0) : this.menuItemModifierGroupId;
    }

    public void setMenuItemModifierGroupId(Integer menuItemModifierGroupId) {
        this.menuItemModifierGroupId = menuItemModifierGroupId;
    }

    public Integer getItemCount() {
        return this.itemCount == null ? Integer.valueOf(0) : this.itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getUnitPrice() {
        return this.unitPrice == null ? Double.valueOf(0.0) : this.unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getTaxRate() {
        return this.taxRate == null ? Double.valueOf(0.0) : this.taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public Integer getModifierType() {
        return this.modifierType == null ? Integer.valueOf(0) : this.modifierType;
    }

    public void setModifierType(Integer modifierType) {
        this.modifierType = modifierType;
    }

    public Double getSubTotalAmount() {
        return this.subTotalAmount == null ? Double.valueOf(0.0) : this.subTotalAmount;
    }

    public void setSubTotalAmount(Double subTotalAmount) {
        this.subTotalAmount = subTotalAmount;
    }

    public Double getTotalAmount() {
        return this.totalAmount == null ? Double.valueOf(0.0) : this.totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getTaxAmount() {
        return this.taxAmount == null ? Double.valueOf(0.0) : this.taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Boolean isInfoOnly() {
        return this.infoOnly == null ? Boolean.FALSE : this.infoOnly;
    }

    public void setInfoOnly(Boolean infoOnly) {
        this.infoOnly = infoOnly;
    }

    public String getSectionName() {
        return this.sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getMultiplierName() {
        return this.multiplierName;
    }

    public void setMultiplierName(String multiplierName) {
        this.multiplierName = multiplierName;
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean isPrintedToKitchen() {
        return this.printedToKitchen == null ? Boolean.FALSE : this.printedToKitchen;
    }

    public void setPrintedToKitchen(Boolean printedToKitchen) {
        this.printedToKitchen = printedToKitchen;
    }

    public TicketItem getTicketItem() {
        return this.ticketItem;
    }

    public void setTicketItem(TicketItem ticketItem) {
        this.ticketItem = ticketItem;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof TicketItemModifier)) {
            return false;
        }
        TicketItemModifier ticketItemModifier = (TicketItemModifier)obj;
        if (null == this.getId() || null == ticketItemModifier.getId()) {
            return false;
        }
        return this.getId().equals(ticketItemModifier.getId());
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

