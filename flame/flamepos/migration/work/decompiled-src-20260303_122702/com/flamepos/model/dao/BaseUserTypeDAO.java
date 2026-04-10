/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.UserType;
import com.floreantpos.model.dao.UserTypeDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseUserTypeDAO
extends _RootDAO {
    public static UserTypeDAO instance;

    public static UserTypeDAO getInstance() {
        if (null == instance) {
            instance = new UserTypeDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return UserType.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public UserType cast(Object object) {
        return (UserType)object;
    }

    public UserType get(Integer key) throws HibernateException {
        return (UserType)this.get(this.getReferenceClass(), key);
    }

    public UserType get(Integer key, Session s) throws HibernateException {
        return (UserType)this.get(this.getReferenceClass(), key, s);
    }

    public UserType load(Integer key) throws HibernateException {
        return (UserType)this.load(this.getReferenceClass(), key);
    }

    public UserType load(Integer key, Session s) throws HibernateException {
        return (UserType)this.load(this.getReferenceClass(), key, s);
    }

    public UserType loadInitialize(Integer key, Session s) throws HibernateException {
        UserType obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<UserType> findAll() {
        return super.findAll();
    }

    @Override
    public List<UserType> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<UserType> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(UserType userType) throws HibernateException {
        return (Integer)super.save(userType);
    }

    public Integer save(UserType userType, Session s) throws HibernateException {
        return (Integer)this.save((Object)userType, s);
    }

    public void saveOrUpdate(UserType userType) throws HibernateException {
        this.saveOrUpdate((Object)userType);
    }

    public void saveOrUpdate(UserType userType, Session s) throws HibernateException {
        this.saveOrUpdate((Object)userType, s);
    }

    public void update(UserType userType) throws HibernateException {
        this.update((Object)userType);
    }

    public void update(UserType userType, Session s) throws HibernateException {
        this.update((Object)userType, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(UserType userType) throws HibernateException {
        this.delete((Object)userType);
    }

    public void delete(UserType userType, Session s) throws HibernateException {
        this.delete((Object)userType, s);
    }

    public void refresh(UserType userType, Session s) throws HibernateException {
        this.refresh((Object)userType, s);
    }
}

