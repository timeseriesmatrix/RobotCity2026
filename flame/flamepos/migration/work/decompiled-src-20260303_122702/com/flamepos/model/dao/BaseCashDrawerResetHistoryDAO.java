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

import com.floreantpos.model.CashDrawerResetHistory;
import com.floreantpos.model.dao.CashDrawerResetHistoryDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCashDrawerResetHistoryDAO
extends _RootDAO {
    public static CashDrawerResetHistoryDAO instance;

    public static CashDrawerResetHistoryDAO getInstance() {
        if (null == instance) {
            instance = new CashDrawerResetHistoryDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return CashDrawerResetHistory.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public CashDrawerResetHistory cast(Object object) {
        return (CashDrawerResetHistory)object;
    }

    public CashDrawerResetHistory get(Integer key) throws HibernateException {
        return (CashDrawerResetHistory)this.get(this.getReferenceClass(), key);
    }

    public CashDrawerResetHistory get(Integer key, Session s) throws HibernateException {
        return (CashDrawerResetHistory)this.get(this.getReferenceClass(), key, s);
    }

    public CashDrawerResetHistory load(Integer key) throws HibernateException {
        return (CashDrawerResetHistory)this.load(this.getReferenceClass(), key);
    }

    public CashDrawerResetHistory load(Integer key, Session s) throws HibernateException {
        return (CashDrawerResetHistory)this.load(this.getReferenceClass(), key, s);
    }

    public CashDrawerResetHistory loadInitialize(Integer key, Session s) throws HibernateException {
        CashDrawerResetHistory obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<CashDrawerResetHistory> findAll() {
        return super.findAll();
    }

    @Override
    public List<CashDrawerResetHistory> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<CashDrawerResetHistory> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(CashDrawerResetHistory cashDrawerResetHistory) throws HibernateException {
        return (Integer)super.save(cashDrawerResetHistory);
    }

    public Integer save(CashDrawerResetHistory cashDrawerResetHistory, Session s) throws HibernateException {
        return (Integer)this.save((Object)cashDrawerResetHistory, s);
    }

    public void saveOrUpdate(CashDrawerResetHistory cashDrawerResetHistory) throws HibernateException {
        this.saveOrUpdate((Object)cashDrawerResetHistory);
    }

    public void saveOrUpdate(CashDrawerResetHistory cashDrawerResetHistory, Session s) throws HibernateException {
        this.saveOrUpdate((Object)cashDrawerResetHistory, s);
    }

    public void update(CashDrawerResetHistory cashDrawerResetHistory) throws HibernateException {
        this.update((Object)cashDrawerResetHistory);
    }

    public void update(CashDrawerResetHistory cashDrawerResetHistory, Session s) throws HibernateException {
        this.update((Object)cashDrawerResetHistory, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(CashDrawerResetHistory cashDrawerResetHistory) throws HibernateException {
        this.delete((Object)cashDrawerResetHistory);
    }

    public void delete(CashDrawerResetHistory cashDrawerResetHistory, Session s) throws HibernateException {
        this.delete((Object)cashDrawerResetHistory, s);
    }

    public void refresh(CashDrawerResetHistory cashDrawerResetHistory, Session s) throws HibernateException {
        this.refresh((Object)cashDrawerResetHistory, s);
    }
}

