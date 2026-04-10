/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Customer;
import com.floreantpos.model.ShopTable;
import com.floreantpos.model.TableBookingInfo;
import com.floreantpos.model.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class BaseTableBookingInfo
implements Comparable,
Serializable {
    public static String REF = "TableBookingInfo";
    public static String PROP_CUSTOMER = "customer";
    public static String PROP_USER = "user";
    public static String PROP_BOOKING_ID = "bookingId";
    public static String PROP_BOOKING_CHARGE = "bookingCharge";
    public static String PROP_FROM_DATE = "fromDate";
    public static String PROP_PAYMENT_STATUS = "paymentStatus";
    public static String PROP_REMAINING_BALANCE = "remainingBalance";
    public static String PROP_BOOKING_TYPE = "bookingType";
    public static String PROP_STATUS = "status";
    public static String PROP_TO_DATE = "toDate";
    public static String PROP_GUEST_COUNT = "guestCount";
    public static String PROP_ID = "id";
    public static String PROP_BOOKING_CONFIRM = "bookingConfirm";
    public static String PROP_PAID_AMOUNT = "paidAmount";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected Date fromDate;
    protected Date toDate;
    protected Integer guestCount;
    protected String status;
    protected String paymentStatus;
    protected String bookingConfirm;
    protected Double bookingCharge;
    protected Double remainingBalance;
    protected Double paidAmount;
    protected String bookingId;
    protected String bookingType;
    private User user;
    private Customer customer;
    private List<ShopTable> tables;

    public BaseTableBookingInfo() {
        this.initialize();
    }

    public BaseTableBookingInfo(Integer id) {
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

    public Date getFromDate() {
        return this.fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return this.toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Integer getGuestCount() {
        return this.guestCount == null ? Integer.valueOf(0) : this.guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getBookingConfirm() {
        return this.bookingConfirm;
    }

    public void setBookingConfirm(String bookingConfirm) {
        this.bookingConfirm = bookingConfirm;
    }

    public Double getBookingCharge() {
        return this.bookingCharge == null ? Double.valueOf(0.0) : this.bookingCharge;
    }

    public void setBookingCharge(Double bookingCharge) {
        this.bookingCharge = bookingCharge;
    }

    public Double getRemainingBalance() {
        return this.remainingBalance == null ? Double.valueOf(0.0) : this.remainingBalance;
    }

    public void setRemainingBalance(Double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public Double getPaidAmount() {
        return this.paidAmount == null ? Double.valueOf(0.0) : this.paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingType() {
        return this.bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<ShopTable> getTables() {
        return this.tables;
    }

    public void setTables(List<ShopTable> tables) {
        this.tables = tables;
    }

    public void addTotables(ShopTable shopTable) {
        if (null == this.getTables()) {
            this.setTables(new ArrayList<ShopTable>());
        }
        this.getTables().add(shopTable);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof TableBookingInfo)) {
            return false;
        }
        TableBookingInfo tableBookingInfo = (TableBookingInfo)obj;
        if (null == this.getId() || null == tableBookingInfo.getId()) {
            return this == obj;
        }
        return this.getId().equals(tableBookingInfo.getId());
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

