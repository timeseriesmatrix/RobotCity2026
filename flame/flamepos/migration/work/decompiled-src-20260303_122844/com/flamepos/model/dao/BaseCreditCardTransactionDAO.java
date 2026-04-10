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

import com.floreantpos.model.CreditCardTransaction;
import com.floreantpos.model.dao.CreditCardTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCreditCardTransactionDAO
extends _RootDAO {
    public static CreditCardTransactionDAO instance;

    public static CreditCardTransactionDAO getInstance() {
        if (null == instance) {
            instance = new CreditCardTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return CreditCardTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public CreditCardTransaction cast(Object object) {
        return (CreditCardTransaction)object;
    }

    public CreditCardTransaction get(Integer key) throws HibernateException {
        return (CreditCardTransaction)this.get(this.getReferenceClass(), key);
    }

    public CreditCardTransaction get(Integer key, Session s) throws HibernateException {
        return (CreditCardTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public CreditCardTransaction load(Integer key) throws HibernateException {
        return (CreditCardTransaction)this.load(this.getReferenceClass(), key);
    }

    public CreditCardTransaction load(Integer key, Session s) throws HibernateException {
        return (CreditCardTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public CreditCardTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        CreditCardTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<CreditCardTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<CreditCardTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<CreditCardTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(CreditCardTransaction creditCardTransaction) throws HibernateException {
        return (Integer)super.save(creditCardTransaction);
    }

    public Integer save(CreditCardTransaction creditCardTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)creditCardTransaction, s);
    }

    public void saveOrUpdate(CreditCardTransaction creditCardTransaction) throws HibernateException {
        this.saveOrUpdate((Object)creditCardTransaction);
    }

    public void saveOrUpdate(CreditCardTransaction creditCardTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)creditCardTransaction, s);
    }

    public void update(CreditCardTransaction creditCardTransaction) throws HibernateException {
        this.update((Object)creditCardTransaction);
    }

    public void update(CreditCardTransaction creditCardTransaction, Session s) throws HibernateException {
        this.update((Object)creditCardTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(CreditCardTransaction creditCardTransaction) throws HibernateException {
        this.delete((Object)creditCardTransaction);
    }

    public void delete(CreditCardTransaction creditCardTransaction, Session s) throws HibernateException {
        this.delete((Object)creditCardTransaction, s);
    }

    public void refresh(CreditCardTransaction creditCardTransaction, Session s) throws HibernateException {
        this.refresh((Object)creditCardTransaction, s);
    }
}

