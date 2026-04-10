/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.ITicketItem;
import com.floreantpos.model.base.BaseTicketItemDiscount;
import com.floreantpos.util.DiscountUtil;

public class TicketItemDiscount
extends BaseTicketItemDiscount
implements ITicketItem {
    private static final long serialVersionUID = 1L;
    private int tableRowNum;

    public TicketItemDiscount() {
    }

    public TicketItemDiscount(Integer id) {
        super(id);
    }

    public TicketItemDiscount(TicketItemDiscount fromDiscount) {
        this.setDiscountId(fromDiscount.getDiscountId());
        this.setName(fromDiscount.getName());
        this.setType(fromDiscount.getType());
        this.setAutoApply(fromDiscount.isAutoApply());
        this.setMinimumQuantity(fromDiscount.getMinimumQuantity());
        this.setValue(fromDiscount.getValue());
        this.setAmount(fromDiscount.getAmount());
    }

    public double calculateDiscount() {
        Double discountAmount = DiscountUtil.calculateDiscountAmount(this);
        this.setDiscountAmount(discountAmount);
        return discountAmount;
    }

    public void setTableRowNum(int tableRowNum) {
        this.tableRowNum = tableRowNum;
    }

    public int getTableRowNum() {
        return this.tableRowNum;
    }

    @Override
    public String getItemCode() {
        return "";
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public String getNameDisplay() {
        return "   * " + this.getName();
    }

    @Override
    public boolean canAddCookingInstruction() {
        return false;
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
        return false;
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
        return DiscountUtil.calculateDiscountAmount(this);
    }

    @Override
    public void setDiscountAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public Double getDiscountAmount() {
        return this.amount;
    }

    @Override
    public String getKitchenStatus() {
        return "";
    }
}

