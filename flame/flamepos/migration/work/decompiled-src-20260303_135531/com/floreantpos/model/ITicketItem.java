/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

public interface ITicketItem {
    public String getItemCode();

    public boolean canAddCookingInstruction();

    public boolean canAddDiscount();

    public boolean canVoid();

    public boolean canAddAdOn();

    public Boolean isPrintedToKitchen();

    public String getNameDisplay();

    public Double getUnitPriceDisplay();

    public String getItemQuantityDisplay();

    public Double getTaxAmountWithoutModifiersDisplay();

    public Double getTotalAmountWithoutModifiersDisplay();

    public Double getSubTotalAmountDisplay();

    public Double getSubTotalAmountWithoutModifiersDisplay();

    public void setDiscountAmount(Double var1);

    public Double getDiscountAmount();

    public String getKitchenStatus();
}

