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

import com.floreantpos.model.Currency;
import com.floreantpos.model.dao.CurrencyDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCurrencyDAO
extends _RootDAO {
    public static CurrencyDAO instance;

    public static CurrencyDAO getInstance() {
        if (null == instance) {
            instance = new CurrencyDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Currency.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public Currency cast(Object object) {
        return (Currency)object;
    }

    public Currency get(Integer key) throws HibernateException {
        return (Currency)this.get(this.getReferenceClass(), key);
    }

    public Currency get(Integer key, Session s) throws HibernateException {
        return (Currency)this.get(this.getReferenceClass(), key, s);
    }

    public Currency load(Integer key) throws HibernateException {
        return (Currency)this.load(this.getReferenceClass(), key);
    }

    public Currency load(Integer key, Session s) throws HibernateException {
        return (Currency)this.load(this.getReferenceClass(), key, s);
    }

    public Currency loadInitialize(Integer key, Session s) throws HibernateException {
        Currency obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Currency> findAll() {
        return super.findAll();
    }

    @Override
    public List<Currency> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Currency> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(Currency currency) throws HibernateException {
        return (Integer)super.save(currency);
    }

    public Integer save(Currency currency, Session s) throws HibernateException {
        return (Integer)this.save((Object)currency, s);
    }

    public void saveOrUpdate(Currency currency) throws HibernateException {
        this.saveOrUpdate((Object)currency);
    }

    public void saveOrUpdate(Currency currency, Session s) throws HibernateException {
        this.saveOrUpdate((Object)currency, s);
    }

    public void update(Currency currency) throws HibernateException {
        this.update((Object)currency);
    }

    public void update(Currency currency, Session s) throws HibernateException {
        this.update((Object)currency, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Currency currency) throws HibernateException {
        this.delete((Object)currency);
    }

    public void delete(Currency currency, Session s) throws HibernateException {
        this.delete((Object)currency, s);
    }

    public void refresh(Currency currency, Session s) throws HibernateException {
        this.refresh((Object)currency, s);
    }
}

