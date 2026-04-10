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

import com.floreantpos.model.CashTransaction;
import com.floreantpos.model.dao.CashTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCashTransactionDAO
extends _RootDAO {
    public static CashTransactionDAO instance;

    public static CashTransactionDAO getInstance() {
        if (null == instance) {
            instance = new CashTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return CashTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public CashTransaction cast(Object object) {
        return (CashTransaction)object;
    }

    public CashTransaction get(Integer key) throws HibernateException {
        return (CashTransaction)this.get(this.getReferenceClass(), key);
    }

    public CashTransaction get(Integer key, Session s) throws HibernateException {
        return (CashTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public CashTransaction load(Integer key) throws HibernateException {
        return (CashTransaction)this.load(this.getReferenceClass(), key);
    }

    public CashTransaction load(Integer key, Session s) throws HibernateException {
        return (CashTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public CashTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        CashTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<CashTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<CashTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<CashTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(CashTransaction cashTransaction) throws HibernateException {
        return (Integer)super.save(cashTransaction);
    }

    public Integer save(CashTransaction cashTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)cashTransaction, s);
    }

    public void saveOrUpdate(CashTransaction cashTransaction) throws HibernateException {
        this.saveOrUpdate((Object)cashTransaction);
    }

    public void saveOrUpdate(CashTransaction cashTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)cashTransaction, s);
    }

    public void update(CashTransaction cashTransaction) throws HibernateException {
        this.update((Object)cashTransaction);
    }

    public void update(CashTransaction cashTransaction, Session s) throws HibernateException {
        this.update((Object)cashTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(CashTransaction cashTransaction) throws HibernateException {
        this.delete((Object)cashTransaction);
    }

    public void delete(CashTransaction cashTransaction, Session s) throws HibernateException {
        this.delete((Object)cashTransaction, s);
    }

    public void refresh(CashTransaction cashTransaction, Session s) throws HibernateException {
        this.refresh((Object)cashTransaction, s);
    }
}

