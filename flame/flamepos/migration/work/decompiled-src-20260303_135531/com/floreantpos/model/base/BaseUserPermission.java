/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.UserPermission;
import java.io.Serializable;

public abstract class BaseUserPermission
implements Comparable,
Serializable {
    public static String REF = "UserPermission";
    public static String PROP_NAME = "name";
    private int hashCode = Integer.MIN_VALUE;
    private String name;

    public BaseUserPermission() {
        this.initialize();
    }

    public BaseUserPermission(String name) {
        this.setName(name);
        this.initialize();
    }

    protected void initialize() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.hashCode = Integer.MIN_VALUE;
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof UserPermission)) {
            return false;
        }
        UserPermission userPermission = (UserPermission)obj;
        if (null == this.getName() || null == userPermission.getName()) {
            return false;
        }
        return this.getName().equals(userPermission.getName());
    }

    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getName()) {
                return super.hashCode();
            }
            String hashStr = this.getClass().getName() + ":" + this.getName().hashCode();
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

