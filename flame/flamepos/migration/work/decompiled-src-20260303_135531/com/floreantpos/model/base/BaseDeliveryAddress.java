/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Customer;
import com.floreantpos.model.DeliveryAddress;
import java.io.Serializable;

public abstract class BaseDeliveryAddress
implements Comparable,
Serializable {
    public static String REF = "DeliveryAddress";
    public static String PROP_CUSTOMER = "customer";
    public static String PROP_DISTANCE = "distance";
    public static String PROP_ROOM_NO = "roomNo";
    public static String PROP_PHONE_EXTENSION = "phoneExtension";
    public static String PROP_ADDRESS = "address";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String address;
    protected String phoneExtension;
    protected String roomNo;
    protected Double distance;
    private Customer customer;

    public BaseDeliveryAddress() {
        this.initialize();
    }

    public BaseDeliveryAddress(Integer id) {
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

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneExtension() {
        return this.phoneExtension;
    }

    public void setPhoneExtension(String phoneExtension) {
        this.phoneExtension = phoneExtension;
    }

    public String getRoomNo() {
        return this.roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Double getDistance() {
        return this.distance == null ? Double.valueOf(0.0) : this.distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
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
        if (!(obj instanceof DeliveryAddress)) {
            return false;
        }
        DeliveryAddress deliveryAddress = (DeliveryAddress)obj;
        if (null == this.getId() || null == deliveryAddress.getId()) {
            return false;
        }
        return this.getId().equals(deliveryAddress.getId());
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

