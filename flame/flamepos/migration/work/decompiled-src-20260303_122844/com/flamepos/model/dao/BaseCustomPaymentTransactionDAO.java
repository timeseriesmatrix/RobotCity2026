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

import com.floreantpos.model.CustomPaymentTransaction;
import com.floreantpos.model.dao.CustomPaymentTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCustomPaymentTransactionDAO
extends _RootDAO {
    public static CustomPaymentTransactionDAO instance;

    public static CustomPaymentTransactionDAO getInstance() {
        if (null == instance) {
            instance = new CustomPaymentTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return CustomPaymentTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public CustomPaymentTransaction cast(Object object) {
        return (CustomPaymentTransaction)object;
    }

    public CustomPaymentTransaction get(Integer key) throws HibernateException {
        return (CustomPaymentTransaction)this.get(this.getReferenceClass(), key);
    }

    public CustomPaymentTransaction get(Integer key, Session s) throws HibernateException {
        return (CustomPaymentTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public CustomPaymentTransaction load(Integer key) throws HibernateException {
        return (CustomPaymentTransaction)this.load(this.getReferenceClass(), key);
    }

    public CustomPaymentTransaction load(Integer key, Session s) throws HibernateException {
        return (CustomPaymentTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public CustomPaymentTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        CustomPaymentTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<CustomPaymentTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<CustomPaymentTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<CustomPaymentTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(CustomPaymentTransaction customPaymentTransaction) throws HibernateException {
        return (Integer)super.save(customPaymentTransaction);
    }

    public Integer save(CustomPaymentTransaction customPaymentTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)customPaymentTransaction, s);
    }

    public void saveOrUpdate(CustomPaymentTransaction customPaymentTransaction) throws HibernateException {
        this.saveOrUpdate((Object)customPaymentTransaction);
    }

    public void saveOrUpdate(CustomPaymentTransaction customPaymentTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)customPaymentTransaction, s);
    }

    public void update(CustomPaymentTransaction customPaymentTransaction) throws HibernateException {
        this.update((Object)customPaymentTransaction);
    }

    public void update(CustomPaymentTransaction customPaymentTransaction, Session s) throws HibernateException {
        this.update((Object)customPaymentTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(CustomPaymentTransaction customPaymentTransaction) throws HibernateException {
        this.delete((Object)customPaymentTransaction);
    }

    public void delete(CustomPaymentTransaction customPaymentTransaction, Session s) throws HibernateException {
        this.delete((Object)customPaymentTransaction, s);
    }

    public void refresh(CustomPaymentTransaction customPaymentTransaction, Session s) throws HibernateException {
        this.refresh((Object)customPaymentTransaction, s);
    }
}

