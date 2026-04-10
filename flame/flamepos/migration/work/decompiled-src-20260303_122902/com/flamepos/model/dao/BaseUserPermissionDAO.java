/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.UserPermission;
import com.floreantpos.model.dao.UserPermissionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseUserPermissionDAO
extends _RootDAO {
    public static UserPermissionDAO instance;

    public static UserPermissionDAO getInstance() {
        if (null == instance) {
            instance = new UserPermissionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return UserPermission.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public UserPermission cast(Object object) {
        return (UserPermission)object;
    }

    public UserPermission get(String key) {
        return (UserPermission)this.get(this.getReferenceClass(), (Serializable)((Object)key));
    }

    public UserPermission get(String key, Session s) {
        return (UserPermission)this.get(this.getReferenceClass(), (Serializable)((Object)key), s);
    }

    public UserPermission load(String key) {
        return (UserPermission)this.load(this.getReferenceClass(), (Serializable)((Object)key));
    }

    public UserPermission load(String key, Session s) {
        return (UserPermission)this.load(this.getReferenceClass(), (Serializable)((Object)key), s);
    }

    public UserPermission loadInitialize(String key, Session s) {
        UserPermission obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<UserPermission> findAll() {
        return super.findAll();
    }

    @Override
    public List<UserPermission> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<UserPermission> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public String save(UserPermission userPermission) {
        return (String)((Object)super.save(userPermission));
    }

    public String save(UserPermission userPermission, Session s) {
        return (String)((Object)this.save((Object)userPermission, s));
    }

    public void saveOrUpdate(UserPermission userPermission) {
        this.saveOrUpdate((Object)userPermission);
    }

    public void saveOrUpdate(UserPermission userPermission, Session s) {
        this.saveOrUpdate((Object)userPermission, s);
    }

    public void update(UserPermission userPermission) {
        this.update((Object)userPermission);
    }

    public void update(UserPermission userPermission, Session s) {
        this.update((Object)userPermission, s);
    }

    public void delete(String id) {
        this.delete((Object)this.load(id));
    }

    public void delete(String id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(UserPermission userPermission) {
        this.delete((Object)userPermission);
    }

    public void delete(UserPermission userPermission, Session s) {
        this.delete((Object)userPermission, s);
    }

    public void refresh(UserPermission userPermission, Session s) {
        this.refresh((Object)userPermission, s);
    }
}

