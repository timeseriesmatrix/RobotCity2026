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

import com.floreantpos.model.CurrencyBalance;
import com.floreantpos.model.dao.CurrencyBalanceDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCurrencyBalanceDAO
extends _RootDAO {
    public static CurrencyBalanceDAO instance;

    public static CurrencyBalanceDAO getInstance() {
        if (null == instance) {
            instance = new CurrencyBalanceDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return CurrencyBalance.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public CurrencyBalance cast(Object object) {
        return (CurrencyBalance)object;
    }

    public CurrencyBalance get(Integer key) throws HibernateException {
        return (CurrencyBalance)this.get(this.getReferenceClass(), key);
    }

    public CurrencyBalance get(Integer key, Session s) throws HibernateException {
        return (CurrencyBalance)this.get(this.getReferenceClass(), key, s);
    }

    public CurrencyBalance load(Integer key) throws HibernateException {
        return (CurrencyBalance)this.load(this.getReferenceClass(), key);
    }

    public CurrencyBalance load(Integer key, Session s) throws HibernateException {
        return (CurrencyBalance)this.load(this.getReferenceClass(), key, s);
    }

    public CurrencyBalance loadInitialize(Integer key, Session s) throws HibernateException {
        CurrencyBalance obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<CurrencyBalance> findAll() {
        return super.findAll();
    }

    @Override
    public List<CurrencyBalance> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<CurrencyBalance> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(CurrencyBalance currencyBalance) throws HibernateException {
        return (Integer)super.save(currencyBalance);
    }

    public Integer save(CurrencyBalance currencyBalance, Session s) throws HibernateException {
        return (Integer)this.save((Object)currencyBalance, s);
    }

    public void saveOrUpdate(CurrencyBalance currencyBalance) throws HibernateException {
        this.saveOrUpdate((Object)currencyBalance);
    }

    public void saveOrUpdate(CurrencyBalance currencyBalance, Session s) throws HibernateException {
        this.saveOrUpdate((Object)currencyBalance, s);
    }

    public void update(CurrencyBalance currencyBalance) throws HibernateException {
        this.update((Object)currencyBalance);
    }

    public void update(CurrencyBalance currencyBalance, Session s) throws HibernateException {
        this.update((Object)currencyBalance, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(CurrencyBalance currencyBalance) throws HibernateException {
        this.delete((Object)currencyBalance);
    }

    public void delete(CurrencyBalance currencyBalance, Session s) throws HibernateException {
        this.delete((Object)currencyBalance, s);
    }

    public void refresh(CurrencyBalance currencyBalance, Session s) throws HibernateException {
        this.refresh((Object)currencyBalance, s);
    }
}

