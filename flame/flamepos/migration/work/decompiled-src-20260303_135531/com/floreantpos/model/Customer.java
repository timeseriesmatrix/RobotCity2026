/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.base.BaseCustomer;
import java.util.HashMap;

public class Customer
extends BaseCustomer {
    private static final long serialVersionUID = 1L;

    public Customer() {
    }

    public Customer(Integer autoId) {
        super(autoId);
    }

    public void addProperty(String name, String value) {
        if (this.getProperties() == null) {
            this.setProperties(new HashMap<String, String>());
        }
        this.getProperties().put(name, value);
    }

    public boolean hasProperty(String key) {
        return this.getProperty(key) != null;
    }

    public String getProperty(String key) {
        if (this.getProperties() == null) {
            return null;
        }
        return this.getProperties().get(key);
    }

    @Override
    public String toString() {
        String fName = this.getFirstName();
        return fName;
    }

    @Override
    public String getName() {
        String name = super.getFirstName() + " " + super.getLastName();
        return name;
    }
}

