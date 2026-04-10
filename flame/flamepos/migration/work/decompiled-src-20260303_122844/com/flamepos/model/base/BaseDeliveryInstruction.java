/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Customer;
import com.floreantpos.model.DeliveryInstruction;
import java.io.Serializable;

public abstract class BaseDeliveryInstruction
implements Comparable,
Serializable {
    public static String REF = "DeliveryInstruction";
    public static String PROP_CUSTOMER = "customer";
    public static String PROP_NOTES = "notes";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String notes;
    private Customer customer;

    public BaseDeliveryInstruction() {
        this.initialize();
    }

    public BaseDeliveryInstruction(Integer id) {
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

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof DeliveryInstruction)) {
            return false;
        }
        DeliveryInstruction deliveryInstruction = (DeliveryInstruction)obj;
        if (null == this.getId() || null == deliveryInstruction.getId()) {
            return false;
        }
        return this.getId().equals(deliveryInstruction.getId());
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

