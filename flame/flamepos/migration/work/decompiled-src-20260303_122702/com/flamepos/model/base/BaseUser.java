/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Shift;
import com.floreantpos.model.Terminal;
import com.floreantpos.model.User;
import com.floreantpos.model.UserType;
import java.io.Serializable;
import java.util.Date;

public abstract class BaseUser
implements Comparable,
Serializable {
    public static String REF = "User";
    public static String PROP_LAST_CLOCK_IN_TIME = "lastClockInTime";
    public static String PROP_TYPE = "type";
    public static String PROP_PASSWORD = "password";
    public static String PROP_USER_ID = "userId";
    public static String PROP_LAST_NAME = "lastName";
    public static String PROP_SSN = "ssn";
    public static String PROP_PHONE_NO = "phoneNo";
    public static String PROP_DRIVER = "driver";
    public static String PROP_ACTIVE = "active";
    public static String PROP_CURRENT_TERMINAL = "currentTerminal";
    public static String PROP_AVAILABLE_FOR_DELIVERY = "availableForDelivery";
    public static String PROP_AUTO_ID = "autoId";
    public static String PROP_FIRST_NAME = "firstName";
    public static String PROP_COST_PER_HOUR = "costPerHour";
    public static String PROP_CLOCKED_IN = "clockedIn";
    public static String PROP_CURRENT_SHIFT = "currentShift";
    public static String PROP_LAST_CLOCK_OUT_TIME = "lastClockOutTime";
    private int hashCode = Integer.MIN_VALUE;
    private Integer autoId;
    protected Integer userId;
    protected String password;
    protected String firstName;
    protected String lastName;
    protected String ssn;
    protected Double costPerHour;
    protected Boolean clockedIn;
    protected Date lastClockInTime;
    protected Date lastClockOutTime;
    protected String phoneNo;
    protected Boolean driver;
    protected Boolean availableForDelivery;
    protected Boolean active;
    private Shift currentShift;
    private Terminal currentTerminal;
    private UserType type;

    public BaseUser() {
        this.initialize();
    }

    public BaseUser(Integer autoId) {
        this.setAutoId(autoId);
        this.initialize();
    }

    public BaseUser(Integer autoId, String password) {
        this.setAutoId(autoId);
        this.setPassword(password);
        this.initialize();
    }

    protected void initialize() {
    }

    public Integer getAutoId() {
        return this.autoId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
        this.hashCode = Integer.MIN_VALUE;
    }

    public Integer getUserId() {
        return this.userId == null ? Integer.valueOf(0) : this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSsn() {
        return this.ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Double getCostPerHour() {
        return this.costPerHour == null ? Double.valueOf(0.0) : this.costPerHour;
    }

    public void setCostPerHour(Double costPerHour) {
        this.costPerHour = costPerHour;
    }

    public Boolean isClockedIn() {
        return this.clockedIn == null ? Boolean.FALSE : this.clockedIn;
    }

    public void setClockedIn(Boolean clockedIn) {
        this.clockedIn = clockedIn;
    }

    public Date getLastClockInTime() {
        return this.lastClockInTime;
    }

    public void setLastClockInTime(Date lastClockInTime) {
        this.lastClockInTime = lastClockInTime;
    }

    public Date getLastClockOutTime() {
        return this.lastClockOutTime;
    }

    public void setLastClockOutTime(Date lastClockOutTime) {
        this.lastClockOutTime = lastClockOutTime;
    }

    public String getPhoneNo() {
        return this.phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Boolean isDriver() {
        return this.driver == null ? Boolean.FALSE : this.driver;
    }

    public void setDriver(Boolean driver) {
        this.driver = driver;
    }

    public Boolean isAvailableForDelivery() {
        return this.availableForDelivery == null ? Boolean.FALSE : this.availableForDelivery;
    }

    public void setAvailableForDelivery(Boolean availableForDelivery) {
        this.availableForDelivery = availableForDelivery;
    }

    public Boolean isActive() {
        return this.active == null ? Boolean.FALSE : this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Shift getCurrentShift() {
        return this.currentShift;
    }

    public void setCurrentShift(Shift currentShift) {
        this.currentShift = currentShift;
    }

    public Terminal getCurrentTerminal() {
        return this.currentTerminal;
    }

    public void setCurrentTerminal(Terminal currentTerminal) {
        this.currentTerminal = currentTerminal;
    }

    public UserType getType() {
        return this.type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User user = (User)obj;
        if (null == this.getAutoId() || null == user.getAutoId()) {
            return false;
        }
        return this.getAutoId().equals(user.getAutoId());
    }

    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getAutoId()) {
                return super.hashCode();
            }
            String hashStr = this.getClass().getName() + ":" + this.getAutoId().hashCode();
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

