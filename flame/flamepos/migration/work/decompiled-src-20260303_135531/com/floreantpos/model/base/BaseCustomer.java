/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.Customer;
import com.floreantpos.model.DeliveryAddress;
import com.floreantpos.model.DeliveryInstruction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseCustomer
implements Comparable,
Serializable {
    public static String REF = "Customer";
    public static String PROP_PICTURE = "picture";
    public static String PROP_SOCIAL_SECURITY_NUMBER = "socialSecurityNumber";
    public static String PROP_WORK_PHONE_NO = "workPhoneNo";
    public static String PROP_VIP = "vip";
    public static String PROP_LOYALTY_POINT = "loyaltyPoint";
    public static String PROP_SALUTATION = "salutation";
    public static String PROP_NOTE = "note";
    public static String PROP_HOME_PHONE_NO = "homePhoneNo";
    public static String PROP_COUNTRY = "country";
    public static String PROP_LAST_NAME = "lastName";
    public static String PROP_ZIP_CODE = "zipCode";
    public static String PROP_DOB = "dob";
    public static String PROP_CITY = "city";
    public static String PROP_SSN = "ssn";
    public static String PROP_MOBILE_NO = "mobileNo";
    public static String PROP_NAME = "name";
    public static String PROP_STATE = "state";
    public static String PROP_EMAIL = "email";
    public static String PROP_CREDIT_SPENT = "creditSpent";
    public static String PROP_ADDRESS = "address";
    public static String PROP_AUTO_ID = "autoId";
    public static String PROP_FIRST_NAME = "firstName";
    public static String PROP_CREDIT_CARD_NO = "creditCardNo";
    public static String PROP_CREDIT_LIMIT = "creditLimit";
    public static String PROP_LOYALTY_NO = "loyaltyNo";
    private int hashCode = Integer.MIN_VALUE;
    private Integer autoId;
    protected String loyaltyNo;
    protected Integer loyaltyPoint;
    protected String socialSecurityNumber;
    protected byte[] picture;
    protected String homePhoneNo;
    protected String mobileNo;
    protected String workPhoneNo;
    protected String email;
    protected String salutation;
    protected String firstName;
    protected String lastName;
    protected String name;
    protected String dob;
    protected String ssn;
    protected String address;
    protected String city;
    protected String state;
    protected String zipCode;
    protected String country;
    protected Boolean vip;
    protected Double creditLimit;
    protected Double creditSpent;
    protected String creditCardNo;
    protected String note;
    private List<DeliveryAddress> deliveryAddresses;
    private List<DeliveryInstruction> deliveryInstructions;
    private Map<String, String> properties;

    public BaseCustomer() {
        this.initialize();
    }

    public BaseCustomer(Integer autoId) {
        this.setAutoId(autoId);
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

    public String getLoyaltyNo() {
        return this.loyaltyNo;
    }

    public void setLoyaltyNo(String loyaltyNo) {
        this.loyaltyNo = loyaltyNo;
    }

    public Integer getLoyaltyPoint() {
        return this.loyaltyPoint == null ? Integer.valueOf(0) : this.loyaltyPoint;
    }

    public void setLoyaltyPoint(Integer loyaltyPoint) {
        this.loyaltyPoint = loyaltyPoint;
    }

    public String getSocialSecurityNumber() {
        return this.socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public byte[] getPicture() {
        return this.picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getHomePhoneNo() {
        return this.homePhoneNo;
    }

    public void setHomePhoneNo(String homePhoneNo) {
        this.homePhoneNo = homePhoneNo;
    }

    public String getMobileNo() {
        return this.mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getWorkPhoneNo() {
        return this.workPhoneNo;
    }

    public void setWorkPhoneNo(String workPhoneNo) {
        this.workPhoneNo = workPhoneNo;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSalutation() {
        return this.salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return this.dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSsn() {
        return this.ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean isVip() {
        return this.vip == null ? Boolean.FALSE : this.vip;
    }

    public void setVip(Boolean vip) {
        this.vip = vip;
    }

    public Double getCreditLimit() {
        return this.creditLimit == null ? Double.valueOf(0.0) : this.creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Double getCreditSpent() {
        return this.creditSpent == null ? Double.valueOf(0.0) : this.creditSpent;
    }

    public void setCreditSpent(Double creditSpent) {
        this.creditSpent = creditSpent;
    }

    public String getCreditCardNo() {
        return this.creditCardNo;
    }

    public void setCreditCardNo(String creditCardNo) {
        this.creditCardNo = creditCardNo;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<DeliveryAddress> getDeliveryAddresses() {
        return this.deliveryAddresses;
    }

    public void setDeliveryAddresses(List<DeliveryAddress> deliveryAddresses) {
        this.deliveryAddresses = deliveryAddresses;
    }

    public void addTodeliveryAddresses(DeliveryAddress deliveryAddress) {
        if (null == this.getDeliveryAddresses()) {
            this.setDeliveryAddresses(new ArrayList<DeliveryAddress>());
        }
        this.getDeliveryAddresses().add(deliveryAddress);
    }

    public List<DeliveryInstruction> getDeliveryInstructions() {
        return this.deliveryInstructions;
    }

    public void setDeliveryInstructions(List<DeliveryInstruction> deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    public void addTodeliveryInstructions(DeliveryInstruction deliveryInstruction) {
        if (null == this.getDeliveryInstructions()) {
            this.setDeliveryInstructions(new ArrayList<DeliveryInstruction>());
        }
        this.getDeliveryInstructions().add(deliveryInstruction);
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof Customer)) {
            return false;
        }
        Customer customer = (Customer)obj;
        if (null == this.getAutoId() || null == customer.getAutoId()) {
            return false;
        }
        return this.getAutoId().equals(customer.getAutoId());
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

