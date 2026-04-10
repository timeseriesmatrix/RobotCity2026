/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.PrinterGroup;
import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemCookingInstruction;
import com.floreantpos.model.TicketItemDiscount;
import com.floreantpos.model.TicketItemModifier;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTicketItem
implements Comparable,
Serializable {
    public static String REF = "TicketItem";
    public static String PROP_BEVERAGE = "beverage";
    public static String PROP_SIZE_MODIFIER = "sizeModifier";
    public static String PROP_TAX_RATE = "taxRate";
    public static String PROP_ITEM_UNIT_NAME = "itemUnitName";
    public static String PROP_DISCOUNT_AMOUNT = "discountAmount";
    public static String PROP_PIZZA_TYPE = "pizzaType";
    public static String PROP_PIZZA_SECTION_MODE_TYPE = "pizzaSectionModeType";
    public static String PROP_SHOULD_PRINT_TO_KITCHEN = "shouldPrintToKitchen";
    public static String PROP_TICKET = "ticket";
    public static String PROP_INVENTORY_HANDLED = "inventoryHandled";
    public static String PROP_STOCK_AMOUNT_ADJUSTED = "stockAmountAdjusted";
    public static String PROP_HAS_MODIFIERS = "hasModifiers";
    public static String PROP_TOTAL_AMOUNT_WITHOUT_MODIFIERS = "totalAmountWithoutModifiers";
    public static String PROP_TREAT_AS_SEAT = "treatAsSeat";
    public static String PROP_ITEM_QUANTITY = "itemQuantity";
    public static String PROP_ITEM_ID = "itemId";
    public static String PROP_CATEGORY_NAME = "categoryName";
    public static String PROP_GROUP_NAME = "groupName";
    public static String PROP_SEAT_NUMBER = "seatNumber";
    public static String PROP_ITEM_COUNT = "itemCount";
    public static String PROP_UNIT_PRICE = "unitPrice";
    public static String PROP_TAX_AMOUNT = "taxAmount";
    public static String PROP_FRACTIONAL_UNIT = "fractionalUnit";
    public static String PROP_NAME = "name";
    public static String PROP_PRINTER_GROUP = "printerGroup";
    public static String PROP_STATUS = "status";
    public static String PROP_PRINTED_TO_KITCHEN = "printedToKitchen";
    public static String PROP_SUBTOTAL_AMOUNT = "subtotalAmount";
    public static String PROP_TAX_AMOUNT_WITHOUT_MODIFIERS = "taxAmountWithoutModifiers";
    public static String PROP_ID = "id";
    public static String PROP_SUBTOTAL_AMOUNT_WITHOUT_MODIFIERS = "subtotalAmountWithoutModifiers";
    public static String PROP_TOTAL_AMOUNT = "totalAmount";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Integer itemId;
    protected Integer itemCount;
    protected Double itemQuantity;
    protected String name;
    protected String itemUnitName;
    protected String groupName;
    protected String categoryName;
    protected Double unitPrice;
    protected Double taxRate;
    protected Double subtotalAmount;
    protected Double subtotalAmountWithoutModifiers;
    protected Double discountAmount;
    protected Double taxAmount;
    protected Double taxAmountWithoutModifiers;
    protected Double totalAmount;
    protected Double totalAmountWithoutModifiers;
    protected Boolean beverage;
    protected Boolean inventoryHandled;
    protected Boolean shouldPrintToKitchen;
    protected Boolean treatAsSeat;
    protected Integer seatNumber;
    protected Boolean fractionalUnit;
    protected Boolean hasModifiers;
    protected Boolean printedToKitchen;
    protected String status;
    protected Boolean stockAmountAdjusted;
    protected Boolean pizzaType;
    protected Integer pizzaSectionModeType;
    private TicketItemModifier sizeModifier;
    private Ticket ticket;
    private PrinterGroup printerGroup;
    private List<TicketItemModifier> ticketItemModifiers;
    private List<TicketItemModifier> addOns;
    private List<TicketItemDiscount> discounts;
    private List<TicketItemCookingInstruction> cookingInstructions;

    public BaseTicketItem() {
        this.initialize();
    }

    public BaseTicketItem(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseTicketItem(Integer id, Ticket ticket) {
        this.setId(id);
        this.setTicket(ticket);
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

    public Integer getItemId() {
        return this.itemId == null ? Integer.valueOf(0) : this.itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getItemCount() {
        return this.itemCount == null ? Integer.valueOf(0) : this.itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Double getItemQuantity() {
        return this.itemQuantity == null ? Double.valueOf(0.0) : this.itemQuantity;
    }

    public void setItemQuantity(Double itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemUnitName() {
        return this.itemUnitName;
    }

    public void setItemUnitName(String itemUnitName) {
        this.itemUnitName = itemUnitName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public Double getSubtotalAmount() {
        return this.subtotalAmount == null ? Double.valueOf(0.0) : this.subtotalAmount;
    }

    public void setSubtotalAmount(Double subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public Double getSubtotalAmountWithoutModifiers() {
        return this.subtotalAmountWithoutModifiers == null ? Double.valueOf(0.0) : this.subtotalAmountWithoutModifiers;
    }

    public void setSubtotalAmountWithoutModifiers(Double subtotalAmountWithoutModifiers) {
        this.subtotalAmountWithoutModifiers = subtotalAmountWithoutModifiers;
    }

    public Double getDiscountAmount() {
        return this.discountAmount == null ? Double.valueOf(0.0) : this.discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getTaxAmount() {
        return this.taxAmount == null ? Double.valueOf(0.0) : this.taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getTaxAmountWithoutModifiers() {
        return this.taxAmountWithoutModifiers == null ? Double.valueOf(0.0) : this.taxAmountWithoutModifiers;
    }

    public void setTaxAmountWithoutModifiers(Double taxAmountWithoutModifiers) {
        this.taxAmountWithoutModifiers = taxAmountWithoutModifiers;
    }

    public Double getTotalAmount() {
        return this.totalAmount == null ? Double.valueOf(0.0) : this.totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getTotalAmountWithoutModifiers() {
        return this.totalAmountWithoutModifiers == null ? Double.valueOf(0.0) : this.totalAmountWithoutModifiers;
    }

    public void setTotalAmountWithoutModifiers(Double totalAmountWithoutModifiers) {
        this.totalAmountWithoutModifiers = totalAmountWithoutModifiers;
    }

    public Boolean isBeverage() {
        return this.beverage == null ? Boolean.FALSE : this.beverage;
    }

    public void setBeverage(Boolean beverage) {
        this.beverage = beverage;
    }

    public Boolean isInventoryHandled() {
        return this.inventoryHandled == null ? Boolean.FALSE : this.inventoryHandled;
    }

    public void setInventoryHandled(Boolean inventoryHandled) {
        this.inventoryHandled = inventoryHandled;
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

    public Boolean isTreatAsSeat() {
        return this.treatAsSeat == null ? Boolean.FALSE : this.treatAsSeat;
    }

    public void setTreatAsSeat(Boolean treatAsSeat) {
        this.treatAsSeat = treatAsSeat;
    }

    public Integer getSeatNumber() {
        return this.seatNumber == null ? Integer.valueOf(0) : this.seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Boolean isFractionalUnit() {
        return this.fractionalUnit == null ? Boolean.FALSE : this.fractionalUnit;
    }

    public void setFractionalUnit(Boolean fractionalUnit) {
        this.fractionalUnit = fractionalUnit;
    }

    public Boolean isHasModifiers() {
        return this.hasModifiers == null ? Boolean.FALSE : this.hasModifiers;
    }

    public void setHasModifiers(Boolean hasModifiers) {
        this.hasModifiers = hasModifiers;
    }

    public Boolean isPrintedToKitchen() {
        return this.printedToKitchen == null ? Boolean.FALSE : this.printedToKitchen;
    }

    public void setPrintedToKitchen(Boolean printedToKitchen) {
        this.printedToKitchen = printedToKitchen;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean isStockAmountAdjusted() {
        return this.stockAmountAdjusted == null ? Boolean.FALSE : this.stockAmountAdjusted;
    }

    public void setStockAmountAdjusted(Boolean stockAmountAdjusted) {
        this.stockAmountAdjusted = stockAmountAdjusted;
    }

    public Boolean isPizzaType() {
        return this.pizzaType == null ? Boolean.FALSE : this.pizzaType;
    }

    public void setPizzaType(Boolean pizzaType) {
        this.pizzaType = pizzaType;
    }

    public Integer getPizzaSectionModeType() {
        return this.pizzaSectionModeType == null ? Integer.valueOf(0) : this.pizzaSectionModeType;
    }

    public void setPizzaSectionModeType(Integer pizzaSectionModeType) {
        this.pizzaSectionModeType = pizzaSectionModeType;
    }

    public TicketItemModifier getSizeModifier() {
        return this.sizeModifier;
    }

    public void setSizeModifier(TicketItemModifier sizeModifier) {
        this.sizeModifier = sizeModifier;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public PrinterGroup getPrinterGroup() {
        return this.printerGroup;
    }

    public void setPrinterGroup(PrinterGroup printerGroup) {
        this.printerGroup = printerGroup;
    }

    public List<TicketItemModifier> getTicketItemModifiers() {
        return this.ticketItemModifiers;
    }

    public void setTicketItemModifiers(List<TicketItemModifier> ticketItemModifiers) {
        this.ticketItemModifiers = ticketItemModifiers;
    }

    public void addToticketItemModifiers(TicketItemModifier ticketItemModifier) {
        if (null == this.getTicketItemModifiers()) {
            this.setTicketItemModifiers(new ArrayList<TicketItemModifier>());
        }
        this.getTicketItemModifiers().add(ticketItemModifier);
    }

    public List<TicketItemModifier> getAddOns() {
        return this.addOns;
    }

    public void setAddOns(List<TicketItemModifier> addOns) {
        this.addOns = addOns;
    }

    public void addToaddOns(TicketItemModifier ticketItemModifier) {
        if (null == this.getAddOns()) {
            this.setAddOns(new ArrayList<TicketItemModifier>());
        }
        this.getAddOns().add(ticketItemModifier);
    }

    public List<TicketItemDiscount> getDiscounts() {
        return this.discounts;
    }

    public void setDiscounts(List<TicketItemDiscount> discounts) {
        this.discounts = discounts;
    }

    public void addTodiscounts(TicketItemDiscount ticketItemDiscount) {
        if (null == this.getDiscounts()) {
            this.setDiscounts(new ArrayList<TicketItemDiscount>());
        }
        this.getDiscounts().add(ticketItemDiscount);
    }

    public List<TicketItemCookingInstruction> getCookingInstructions() {
        return this.cookingInstructions;
    }

    public void setCookingInstructions(List<TicketItemCookingInstruction> cookingInstructions) {
        this.cookingInstructions = cookingInstructions;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof TicketItem)) {
            return false;
        }
        TicketItem ticketItem = (TicketItem)obj;
        if (null == this.getId() || null == ticketItem.getId()) {
            return false;
        }
        return this.getId().equals(ticketItem.getId());
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

