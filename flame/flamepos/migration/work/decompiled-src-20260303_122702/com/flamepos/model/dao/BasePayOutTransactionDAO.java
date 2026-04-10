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

import com.floreantpos.model.PayOutTransaction;
import com.floreantpos.model.dao.PayOutTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePayOutTransactionDAO
extends _RootDAO {
    public static PayOutTransactionDAO instance;

    public static PayOutTransactionDAO getInstance() {
        if (null == instance) {
            instance = new PayOutTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PayOutTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public PayOutTransaction cast(Object object) {
        return (PayOutTransaction)object;
    }

    public PayOutTransaction get(Integer key) throws HibernateException {
        return (PayOutTransaction)this.get(this.getReferenceClass(), key);
    }

    public PayOutTransaction get(Integer key, Session s) throws HibernateException {
        return (PayOutTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public PayOutTransaction load(Integer key) throws HibernateException {
        return (PayOutTransaction)this.load(this.getReferenceClass(), key);
    }

    public PayOutTransaction load(Integer key, Session s) throws HibernateException {
        return (PayOutTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public PayOutTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        PayOutTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PayOutTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<PayOutTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PayOutTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PayOutTransaction payOutTransaction) throws HibernateException {
        return (Integer)super.save(payOutTransaction);
    }

    public Integer save(PayOutTransaction payOutTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)payOutTransaction, s);
    }

    public void saveOrUpdate(PayOutTransaction payOutTransaction) throws HibernateException {
        this.saveOrUpdate((Object)payOutTransaction);
    }

    public void saveOrUpdate(PayOutTransaction payOutTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)payOutTransaction, s);
    }

    public void update(PayOutTransaction payOutTransaction) throws HibernateException {
        this.update((Object)payOutTransaction);
    }

    public void update(PayOutTransaction payOutTransaction, Session s) throws HibernateException {
        this.update((Object)payOutTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PayOutTransaction payOutTransaction) throws HibernateException {
        this.delete((Object)payOutTransaction);
    }

    public void delete(PayOutTransaction payOutTransaction, Session s) throws HibernateException {
        this.delete((Object)payOutTransaction, s);
    }

    public void refresh(PayOutTransaction payOutTransaction, Session s) throws HibernateException {
        this.refresh((Object)payOutTransaction, s);
    }
}

