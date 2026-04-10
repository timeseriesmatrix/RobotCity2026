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

import com.floreantpos.model.RefundTransaction;
import com.floreantpos.model.dao.RefundTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseRefundTransactionDAO
extends _RootDAO {
    public static RefundTransactionDAO instance;

    public static RefundTransactionDAO getInstance() {
        if (null == instance) {
            instance = new RefundTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return RefundTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public RefundTransaction cast(Object object) {
        return (RefundTransaction)object;
    }

    public RefundTransaction get(Integer key) throws HibernateException {
        return (RefundTransaction)this.get(this.getReferenceClass(), key);
    }

    public RefundTransaction get(Integer key, Session s) throws HibernateException {
        return (RefundTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public RefundTransaction load(Integer key) throws HibernateException {
        return (RefundTransaction)this.load(this.getReferenceClass(), key);
    }

    public RefundTransaction load(Integer key, Session s) throws HibernateException {
        return (RefundTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public RefundTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        RefundTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<RefundTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<RefundTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<RefundTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(RefundTransaction refundTransaction) throws HibernateException {
        return (Integer)super.save(refundTransaction);
    }

    public Integer save(RefundTransaction refundTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)refundTransaction, s);
    }

    public void saveOrUpdate(RefundTransaction refundTransaction) throws HibernateException {
        this.saveOrUpdate((Object)refundTransaction);
    }

    public void saveOrUpdate(RefundTransaction refundTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)refundTransaction, s);
    }

    public void update(RefundTransaction refundTransaction) throws HibernateException {
        this.update((Object)refundTransaction);
    }

    public void update(RefundTransaction refundTransaction, Session s) throws HibernateException {
        this.update((Object)refundTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(RefundTransaction refundTransaction) throws HibernateException {
        this.delete((Object)refundTransaction);
    }

    public void delete(RefundTransaction refundTransaction, Session s) throws HibernateException {
        this.delete((Object)refundTransaction, s);
    }

    public void refresh(RefundTransaction refundTransaction, Session s) throws HibernateException {
        this.refresh((Object)refundTransaction, s);
    }
}

