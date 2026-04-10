/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.TicketItem;
import com.floreantpos.model.TicketItemDiscount;
import java.io.Serializable;

public abstract class BaseTicketItemDiscount
implements Comparable,
Serializable {
    public static String REF = "TicketItemDiscount";
    public static String PROP_NAME = "name";
    public static String PROP_AMOUNT = "amount";
    public static String PROP_VALUE = "value";
    public static String PROP_DISCOUNT_ID = "discountId";
    public static String PROP_TYPE = "type";
    public static String PROP_ID = "id";
    public static String PROP_TICKET_ITEM = "ticketItem";
    public static String PROP_MINIMUM_QUANTITY = "minimumQuantity";
    public static String PROP_AUTO_APPLY = "autoApply";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Integer discountId;
    protected String name;
    protected Integer type;
    protected Boolean autoApply;
    protected Integer minimumQuantity;
    protected Double value;
    protected Double amount;
    private TicketItem ticketItem;

    public BaseTicketItemDiscount() {
        this.initialize();
    }

    public BaseTicketItemDiscount(Integer id) {
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

    public Integer getDiscountId() {
        return this.discountId == null ? Integer.valueOf(0) : this.discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return this.type == null ? Integer.valueOf(0) : this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean isAutoApply() {
        return this.autoApply == null ? Boolean.FALSE : this.autoApply;
    }

    public void setAutoApply(Boolean autoApply) {
        this.autoApply = autoApply;
    }

    public Integer getMinimumQuantity() {
        return this.minimumQuantity == null ? Integer.valueOf(0) : this.minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Double getValue() {
        return this.value == null ? Double.valueOf(0.0) : this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getAmount() {
        return this.amount == null ? Double.valueOf(0.0) : this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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
        if (!(obj instanceof TicketItemDiscount)) {
            return false;
        }
        TicketItemDiscount ticketItemDiscount = (TicketItemDiscount)obj;
        if (null == this.getId() || null == ticketItemDiscount.getId()) {
            return false;
        }
        return this.getId().equals(ticketItemDiscount.getId());
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

