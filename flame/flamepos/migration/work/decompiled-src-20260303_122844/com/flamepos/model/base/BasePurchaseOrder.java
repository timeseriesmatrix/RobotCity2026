/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.PurchaseOrder;
import java.io.Serializable;

public abstract class BasePurchaseOrder
implements Comparable,
Serializable {
    public static String REF = "PurchaseOrder";
    public static String PROP_NAME = "name";
    public static String PROP_ORDER_ID = "orderId";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String orderId;
    protected String name;

    public BasePurchaseOrder() {
        this.initialize();
    }

    public BasePurchaseOrder(Integer id) {
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

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof PurchaseOrder)) {
            return false;
        }
        PurchaseOrder purchaseOrder = (PurchaseOrder)obj;
        if (null == this.getId() || null == purchaseOrder.getId()) {
            return false;
        }
        return this.getId().equals(purchaseOrder.getId());
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

