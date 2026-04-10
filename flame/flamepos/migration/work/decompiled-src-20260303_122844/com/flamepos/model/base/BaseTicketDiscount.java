/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Ticket;
import com.floreantpos.model.TicketDiscount;
import java.io.Serializable;

public abstract class BaseTicketDiscount
implements Comparable,
Serializable {
    public static String REF = "TicketDiscount";
    public static String PROP_MINIMUM_AMOUNT = "minimumAmount";
    public static String PROP_NAME = "name";
    public static String PROP_TICKET = "ticket";
    public static String PROP_VALUE = "value";
    public static String PROP_DISCOUNT_ID = "discountId";
    public static String PROP_TYPE = "type";
    public static String PROP_ID = "id";
    public static String PROP_AUTO_APPLY = "autoApply";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Integer discountId;
    protected String name;
    protected Integer type;
    protected Boolean autoApply;
    protected Integer minimumAmount;
    protected Double value;
    private Ticket ticket;

    public BaseTicketDiscount() {
        this.initialize();
    }

    public BaseTicketDiscount(Integer id) {
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

    public Integer getMinimumAmount() {
        return this.minimumAmount == null ? Integer.valueOf(0) : this.minimumAmount;
    }

    public void setMinimumAmount(Integer minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public Double getValue() {
        return this.value == null ? Double.valueOf(0.0) : this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof TicketDiscount)) {
            return false;
        }
        TicketDiscount ticketDiscount = (TicketDiscount)obj;
        if (null == this.getId() || null == ticketDiscount.getId()) {
            return false;
        }
        return this.getId().equals(ticketDiscount.getId());
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

