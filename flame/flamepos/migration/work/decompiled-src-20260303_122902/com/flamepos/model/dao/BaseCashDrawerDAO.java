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

import com.floreantpos.model.CashDrawer;
import com.floreantpos.model.dao.CashDrawerDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCashDrawerDAO
extends _RootDAO {
    public static CashDrawerDAO instance;

    public static CashDrawerDAO getInstance() {
        if (null == instance) {
            instance = new CashDrawerDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return CashDrawer.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public CashDrawer cast(Object object) {
        return (CashDrawer)object;
    }

    public CashDrawer get(Integer key) throws HibernateException {
        return (CashDrawer)this.get(this.getReferenceClass(), key);
    }

    public CashDrawer get(Integer key, Session s) throws HibernateException {
        return (CashDrawer)this.get(this.getReferenceClass(), key, s);
    }

    public CashDrawer load(Integer key) throws HibernateException {
        return (CashDrawer)this.load(this.getReferenceClass(), key);
    }

    public CashDrawer load(Integer key, Session s) throws HibernateException {
        return (CashDrawer)this.load(this.getReferenceClass(), key, s);
    }

    public CashDrawer loadInitialize(Integer key, Session s) throws HibernateException {
        CashDrawer obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<CashDrawer> findAll() {
        return super.findAll();
    }

    @Override
    public List<CashDrawer> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<CashDrawer> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(CashDrawer cashDrawer) throws HibernateException {
        return (Integer)super.save(cashDrawer);
    }

    public Integer save(CashDrawer cashDrawer, Session s) throws HibernateException {
        return (Integer)this.save((Object)cashDrawer, s);
    }

    public void saveOrUpdate(CashDrawer cashDrawer) throws HibernateException {
        this.saveOrUpdate((Object)cashDrawer);
    }

    public void saveOrUpdate(CashDrawer cashDrawer, Session s) throws HibernateException {
        this.saveOrUpdate((Object)cashDrawer, s);
    }

    public void update(CashDrawer cashDrawer) throws HibernateException {
        this.update((Object)cashDrawer);
    }

    public void update(CashDrawer cashDrawer, Session s) throws HibernateException {
        this.update((Object)cashDrawer, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(CashDrawer cashDrawer) throws HibernateException {
        this.delete((Object)cashDrawer);
    }

    public void delete(CashDrawer cashDrawer, Session s) throws HibernateException {
        this.delete((Object)cashDrawer, s);
    }

    public void refresh(CashDrawer cashDrawer, Session s) throws HibernateException {
        this.refresh((Object)cashDrawer, s);
    }
}

