/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.OrderType;
import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.PizzaPrice;
import java.io.Serializable;

public abstract class BasePizzaPrice
implements Comparable,
Serializable {
    public static String REF = "PizzaPrice";
    public static String PROP_CRUST = "crust";
    public static String PROP_ORDER_TYPE = "orderType";
    public static String PROP_PRICE = "price";
    public static String PROP_SIZE = "size";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Double price;
    private MenuItemSize size;
    private PizzaCrust crust;
    private OrderType orderType;

    public BasePizzaPrice() {
        this.initialize();
    }

    public BasePizzaPrice(Integer id) {
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

    public Double getPrice() {
        return this.price == null ? Double.valueOf(0.0) : this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public MenuItemSize getSize() {
        return this.size;
    }

    public void setSize(MenuItemSize size) {
        this.size = size;
    }

    public PizzaCrust getCrust() {
        return this.crust;
    }

    public void setCrust(PizzaCrust crust) {
        this.crust = crust;
    }

    public OrderType getOrderType() {
        return this.orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof PizzaPrice)) {
            return false;
        }
        PizzaPrice pizzaPrice = (PizzaPrice)obj;
        if (null == this.getId() || null == pizzaPrice.getId()) {
            return false;
        }
        return this.getId().equals(pizzaPrice.getId());
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

