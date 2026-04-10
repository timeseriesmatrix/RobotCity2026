/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.base.BaseTicketItemCookingInstruction;

public class TicketItemCookingInstruction
extends BaseTicketItemCookingInstruction
implements ITicketItem {
    private static final long serialVersionUID = 1L;
    private int tableRowNum;

    public int getTableRowNum() {
        return this.tableRowNum;
    }

    public void setTableRowNum(int tableRowNum) {
        this.tableRowNum = tableRowNum;
    }

    @Override
    public boolean canAddCookingInstruction() {
        return false;
    }

    @Override
    public String toString() {
        return this.getDescription();
    }

    @Override
    public String getNameDisplay() {
        return "   * " + this.getDescription();
    }

    @Override
    public Double getUnitPriceDisplay() {
        return null;
    }

    @Override
    public String getItemQuantityDisplay() {
        return null;
    }

    @Override
    public Double getSubTotalAmountDisplay() {
        return null;
    }

    @Override
    public Double getTaxAmountWithoutModifiersDisplay() {
        return null;
    }

    @Override
    public Double getTotalAmountWithoutModifiersDisplay() {
        return null;
    }

    @Override
    public Double getSubTotalAmountWithoutModifiersDisplay() {
        return null;
    }

    @Override
    public String getItemCode() {
        return "";
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
    public Boolean isPrintedToKitchen() {
        return super.isPrintedToKitchen();
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
        return "";
    }
}

