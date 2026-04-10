/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.main.Application;
import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.base.BaseTicketItemModifier;
import com.floreantpos.util.NumberUtil;

public class TicketItemModifier
extends BaseTicketItemModifier
implements ITicketItem {
    private static final long serialVersionUID = 1L;
    public static final int NORMAL_MODIFIER = 1;
    public static final int EXTRA_MODIFIER = 3;
    public static final int CRUST = 5;
    public static final int SEPERATOR = 6;
    private boolean selected;
    boolean priceIncludesTax;
    private int tableRowNum;

    public TicketItemModifier() {
    }

    public TicketItemModifier(Integer id) {
        super(id);
    }

    public int getTableRowNum() {
        return this.tableRowNum;
    }

    public void setTableRowNum(int tableRowNum) {
        this.tableRowNum = tableRowNum;
    }

    @Override
    public String toString() {
        return this.getNameDisplay();
    }

    @Override
    public boolean canAddCookingInstruction() {
        return false;
    }

    public void calculatePrice() {
        if (this.isInfoOnly().booleanValue()) {
            return;
        }
        this.priceIncludesTax = Application.getInstance().isPriceIncludesTax();
        this.calculateSubTotal();
        this.calculateTax();
        this.setTotalAmount(NumberUtil.roundToTwoDigit(this.calculateTotal()));
    }

    public void merge(TicketItemModifier otherItem) {
        this.setItemCount(this.getItemCount() + otherItem.getItemCount());
    }

    private void calculateTax() {
        double tax = this.getSubTotalAmount() * (this.getTaxRate() / 100.0);
        double subtotal = this.getSubTotalAmount();
        double taxRate = this.getTaxRate();
        tax = this.priceIncludesTax ? this.getSubTotalAmount() * (this.getTaxRate() / 100.0) : subtotal * (taxRate / 100.0);
        this.setTaxAmount(NumberUtil.roundToTwoDigit(tax));
    }

    private double calculateTotal() {
        if (this.priceIncludesTax) {
            return this.getSubTotalAmount();
        }
        return this.getSubTotalAmount() + this.getTaxAmount();
    }

    private double calculateSubTotal() {
        double total = 0.0;
        total = NumberUtil.roundToTwoDigit((double)this.getItemCount().intValue() * this.getUnitPrice());
        this.setSubTotalAmount(total);
        return total;
    }

    @Override
    public String getMultiplierName() {
        return this.multiplierName == null ? "" : this.multiplierName;
    }

    @Override
    public String getNameDisplay() {
        if (this.isInfoOnly().booleanValue()) {
            return this.getName().trim();
        }
        int itemCount = this.getItemCount();
        if (this.getTicketItem().isPizzaType().booleanValue()) {
            itemCount /= this.getTicketItem().getItemCount().intValue();
        }
        String display = itemCount > 1 ? itemCount + "x " + this.getName() : this.getName().trim();
        if (this.getModifierType() == 1) {
            display = display + "*";
        }
        return display;
    }

    @Override
    public Double getUnitPriceDisplay() {
        if (this.isInfoOnly().booleanValue()) {
            return null;
        }
        return this.getUnitPrice();
    }

    @Override
    public String getItemQuantityDisplay() {
        if (this.isInfoOnly().booleanValue()) {
            return null;
        }
        return "";
    }

    @Override
    public Double getTaxAmountWithoutModifiersDisplay() {
        if (this.isInfoOnly().booleanValue()) {
            return null;
        }
        return this.getTaxAmount();
    }

    @Override
    public Double getTotalAmountWithoutModifiersDisplay() {
        if (this.isInfoOnly().booleanValue()) {
            return null;
        }
        return this.getTotalAmount();
    }

    @Override
    public Double getSubTotalAmountDisplay() {
        return null;
    }

    @Override
    public Double getSubTotalAmountWithoutModifiersDisplay() {
        if (this.isInfoOnly().booleanValue()) {
            return null;
        }
        return this.getSubTotalAmount();
    }

    public boolean isPriceIncludesTax() {
        return this.priceIncludesTax;
    }

    public void setPriceIncludesTax(boolean priceIncludesTax) {
        this.priceIncludesTax = priceIncludesTax;
    }

    @Override
    public String getItemCode() {
        return "";
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean canAddDiscount() {
        return false;
    }

    @Override
    public boolean canVoid() {
        return false;
    }

    @Override
    public boolean canAddAdOn() {
        return false;
    }

    @Override
    public void setDiscountAmount(Double amount) {
    }

    @Override
    public Double getDiscountAmount() {
        return null;
    }

    @Override
    public String getKitchenStatus() {
        if (super.getStatus() == null) {
            return "";
        }
        return super.getStatus();
    }
}

