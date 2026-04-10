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

import com.floreantpos.model.PosTransaction;
import com.floreantpos.model.dao.PosTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePosTransactionDAO
extends _RootDAO {
    public static PosTransactionDAO instance;

    public static PosTransactionDAO getInstance() {
        if (null == instance) {
            instance = new PosTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PosTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public PosTransaction cast(Object object) {
        return (PosTransaction)object;
    }

    public PosTransaction get(Integer key) throws HibernateException {
        return (PosTransaction)this.get(this.getReferenceClass(), key);
    }

    public PosTransaction get(Integer key, Session s) throws HibernateException {
        return (PosTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public PosTransaction load(Integer key) throws HibernateException {
        return (PosTransaction)this.load(this.getReferenceClass(), key);
    }

    public PosTransaction load(Integer key, Session s) throws HibernateException {
        return (PosTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public PosTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        PosTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PosTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<PosTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PosTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PosTransaction posTransaction) throws HibernateException {
        return (Integer)super.save(posTransaction);
    }

    public Integer save(PosTransaction posTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)posTransaction, s);
    }

    public void saveOrUpdate(PosTransaction posTransaction) throws HibernateException {
        this.saveOrUpdate((Object)posTransaction);
    }

    public void saveOrUpdate(PosTransaction posTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)posTransaction, s);
    }

    public void update(PosTransaction posTransaction) throws HibernateException {
        this.update((Object)posTransaction);
    }

    public void update(PosTransaction posTransaction, Session s) throws HibernateException {
        this.update((Object)posTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PosTransaction posTransaction) throws HibernateException {
        this.delete((Object)posTransaction);
    }

    public void delete(PosTransaction posTransaction, Session s) throws HibernateException {
        this.delete((Object)posTransaction, s);
    }

    public void refresh(PosTransaction posTransaction, Session s) throws HibernateException {
        this.refresh((Object)posTransaction, s);
    }
}

