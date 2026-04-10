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

import com.floreantpos.model.DebitCardTransaction;
import com.floreantpos.model.dao.DebitCardTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseDebitCardTransactionDAO
extends _RootDAO {
    public static DebitCardTransactionDAO instance;

    public static DebitCardTransactionDAO getInstance() {
        if (null == instance) {
            instance = new DebitCardTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return DebitCardTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public DebitCardTransaction cast(Object object) {
        return (DebitCardTransaction)object;
    }

    public DebitCardTransaction get(Integer key) throws HibernateException {
        return (DebitCardTransaction)this.get(this.getReferenceClass(), key);
    }

    public DebitCardTransaction get(Integer key, Session s) throws HibernateException {
        return (DebitCardTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public DebitCardTransaction load(Integer key) throws HibernateException {
        return (DebitCardTransaction)this.load(this.getReferenceClass(), key);
    }

    public DebitCardTransaction load(Integer key, Session s) throws HibernateException {
        return (DebitCardTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public DebitCardTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        DebitCardTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<DebitCardTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<DebitCardTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<DebitCardTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(DebitCardTransaction debitCardTransaction) throws HibernateException {
        return (Integer)super.save(debitCardTransaction);
    }

    public Integer save(DebitCardTransaction debitCardTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)debitCardTransaction, s);
    }

    public void saveOrUpdate(DebitCardTransaction debitCardTransaction) throws HibernateException {
        this.saveOrUpdate((Object)debitCardTransaction);
    }

    public void saveOrUpdate(DebitCardTransaction debitCardTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)debitCardTransaction, s);
    }

    public void update(DebitCardTransaction debitCardTransaction) throws HibernateException {
        this.update((Object)debitCardTransaction);
    }

    public void update(DebitCardTransaction debitCardTransaction, Session s) throws HibernateException {
        this.update((Object)debitCardTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(DebitCardTransaction debitCardTransaction) throws HibernateException {
        this.delete((Object)debitCardTransaction);
    }

    public void delete(DebitCardTransaction debitCardTransaction, Session s) throws HibernateException {
        this.delete((Object)debitCardTransaction, s);
    }

    public void refresh(DebitCardTransaction debitCardTransaction, Session s) throws HibernateException {
        this.refresh((Object)debitCardTransaction, s);
    }
}

