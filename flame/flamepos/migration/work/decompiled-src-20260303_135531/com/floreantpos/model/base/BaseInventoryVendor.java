/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.InventoryVendor;
import java.io.Serializable;

public abstract class BaseInventoryVendor
implements Comparable,
Serializable {
    public static String REF = "InventoryVendor";
    public static String PROP_ZIP = "zip";
    public static String PROP_EMAIL = "email";
    public static String PROP_ADDRESS = "address";
    public static String PROP_STATE = "state";
    public static String PROP_PHONE = "phone";
    public static String PROP_VISIBLE = "visible";
    public static String PROP_COUNTRY = "country";
    public static String PROP_CITY = "city";
    public static String PROP_ID = "id";
    public static String PROP_FAX = "fax";
    public static String PROP_NAME = "name";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    protected String name;
    protected Boolean visible;
    protected String address;
    protected String city;
    protected String state;
    protected String zip;
    protected String country;
    protected String email;
    protected String phone;
    protected String fax;

    public BaseInventoryVendor() {
        this.initialize();
    }

    public BaseInventoryVendor(Integer id) {
        this.setId(id);
        this.initialize();
    }

    public BaseInventoryVendor(Integer id, String name, String address, String city, String state, String zip, String country, String email, String phone) {
        this.setId(id);
        this.setName(name);
        this.setAddress(address);
        this.setCity(city);
        this.setState(state);
        this.setZip(zip);
        this.setCountry(country);
        this.setEmail(email);
        this.setPhone(phone);
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isVisible() {
        return this.visible == null ? Boolean.FALSE : this.visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
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

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof InventoryVendor)) {
            return false;
        }
        InventoryVendor inventoryVendor = (InventoryVendor)obj;
        if (null == this.getId() || null == inventoryVendor.getId()) {
            return false;
        }
        return this.getId().equals(inventoryVendor.getId());
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

