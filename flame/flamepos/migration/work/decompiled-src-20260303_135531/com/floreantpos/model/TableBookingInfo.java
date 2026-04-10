/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.Customer;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.base.BaseTableBookingInfo;
import java.util.Iterator;
import java.util.List;

public class TableBookingInfo
extends BaseTableBookingInfo {
    private static final long serialVersionUID = 1L;
    public static final String STATUS_CANCEL = "cancel";
    public static final String STATUS_CLOSE = "close";
    public static final String STATUS_NO_APR = "no appear";
    public static final String STATUS_SEAT = "seat";
    public static final String STATUS_DELAY = "delay";
    public static final String STATUS_OPEN = "open";
    private String customerInfo;
    private String bookedTableNumbers;

    public TableBookingInfo() {
    }

    public TableBookingInfo(Integer id) {
        super(id);
    }

    @Override
    public String toString() {
        return this.getId().toString();
    }

    public String getCustomerInfo() {
        Customer customer = this.getCustomer();
        if (customer == null) {
            return this.customerInfo;
        }
        if (!customer.getFirstName().equals("")) {
            this.customerInfo = customer.getFirstName();
            return this.customerInfo;
        }
        if (!customer.getMobileNo().equals("")) {
            this.customerInfo = customer.getMobileNo();
            return this.customerInfo;
        }
        if (!customer.getLoyaltyNo().equals("")) {
            this.customerInfo = customer.getLoyaltyNo();
            return this.customerInfo;
        }
        return this.customerInfo;
    }

    public void setCustomerInfo(String customerInfo) {
        this.customerInfo = customerInfo;
    }

    public String getBookedTableNumbers() {
        if (this.bookedTableNumbers != null) {
            return this.bookedTableNumbers;
        }
        List<ShopTable> shopTables = this.getTables();
        if (shopTables == null || shopTables.isEmpty()) {
            return null;
        }
        String tableNumbers = "";
        Iterator<ShopTable> iterator = shopTables.iterator();
        while (iterator.hasNext()) {
            ShopTable shopTable = iterator.next();
            tableNumbers = tableNumbers + shopTable.getTableNumber();
            if (!iterator.hasNext()) continue;
            tableNumbers = tableNumbers + ", ";
        }
        return tableNumbers;
    }

    public void setBookedTableNumbers(String bookTableNumbers) {
        this.bookedTableNumbers = bookTableNumbers;
    }
}

