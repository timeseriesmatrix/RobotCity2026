/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model.base;

import com.floreantpos.model.UserPermission;
import com.floreantpos.model.UserType;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public abstract class BaseUserType
implements Comparable,
Serializable {
    public static String REF = "UserType";
    public static String PROP_NAME = "name";
    public static String PROP_ID = "id";
    private int hashCode = Integer.MIN_VALUE;
    private Integer id;
    private String name;
    private Set<UserPermission> permissions;

    public BaseUserType() {
        this.initialize();
    }

    public BaseUserType(Integer id) {
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserPermission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public void addTopermissions(UserPermission userPermission) {
        if (null == this.getPermissions()) {
            this.setPermissions(new TreeSet<UserPermission>());
        }
        this.getPermissions().add(userPermission);
    }

    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof UserType)) {
            return false;
        }
        UserType userType = (UserType)obj;
        if (null == this.getId() || null == userType.getId()) {
            return false;
        }
        return this.getId().equals(userType.getId());
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

