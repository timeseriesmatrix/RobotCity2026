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

import com.floreantpos.model.VoidTransaction;
import com.floreantpos.model.dao.VoidTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseVoidTransactionDAO
extends _RootDAO {
    public static VoidTransactionDAO instance;

    public static VoidTransactionDAO getInstance() {
        if (null == instance) {
            instance = new VoidTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return VoidTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public VoidTransaction cast(Object object) {
        return (VoidTransaction)object;
    }

    public VoidTransaction get(Integer key) throws HibernateException {
        return (VoidTransaction)this.get(this.getReferenceClass(), key);
    }

    public VoidTransaction get(Integer key, Session s) throws HibernateException {
        return (VoidTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public VoidTransaction load(Integer key) throws HibernateException {
        return (VoidTransaction)this.load(this.getReferenceClass(), key);
    }

    public VoidTransaction load(Integer key, Session s) throws HibernateException {
        return (VoidTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public VoidTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        VoidTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<VoidTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<VoidTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<VoidTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(VoidTransaction voidTransaction) throws HibernateException {
        return (Integer)super.save(voidTransaction);
    }

    public Integer save(VoidTransaction voidTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)voidTransaction, s);
    }

    public void saveOrUpdate(VoidTransaction voidTransaction) throws HibernateException {
        this.saveOrUpdate((Object)voidTransaction);
    }

    public void saveOrUpdate(VoidTransaction voidTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)voidTransaction, s);
    }

    public void update(VoidTransaction voidTransaction) throws HibernateException {
        this.update((Object)voidTransaction);
    }

    public void update(VoidTransaction voidTransaction, Session s) throws HibernateException {
        this.update((Object)voidTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(VoidTransaction voidTransaction) throws HibernateException {
        this.delete((Object)voidTransaction);
    }

    public void delete(VoidTransaction voidTransaction, Session s) throws HibernateException {
        this.delete((Object)voidTransaction, s);
    }

    public void refresh(VoidTransaction voidTransaction, Session s) throws HibernateException {
        this.refresh((Object)voidTransaction, s);
    }
}

