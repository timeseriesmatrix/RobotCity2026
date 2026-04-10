/*
 * Decompiled with CFR 0.151.
 */
package com.floreantpos.model;

import com.floreantpos.model.UserPermission;
import com.floreantpos.model.base.BaseUserType;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="user-type")
public class UserType
extends BaseUserType {
    private static final long serialVersionUID = 1L;

    public UserType() {
    }

    public UserType(Integer id) {
        super(id);
    }

    public void clearPermissions() {
        Set<UserPermission> permissions = this.getPermissions();
        if (permissions != null) {
            permissions.clear();
        }
    }

    public boolean hasPermission(UserPermission permission) {
        Set<UserPermission> permissions = this.getPermissions();
        if (permissions == null) {
            return false;
        }
        return permissions.contains(permission);
    }

    @Override
    public String toString() {
        String s = this.getName();
        return s;
    }
}

