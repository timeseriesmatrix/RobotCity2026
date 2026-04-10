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

import com.floreantpos.model.Multiplier;
import com.floreantpos.model.dao.MultiplierDAO;
import com.floreantpos.model.dao._RootDAO;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseMultiplierDAO
extends _RootDAO {
    public static MultiplierDAO instance;

    public static MultiplierDAO getInstance() {
        if (null == instance) {
            instance = new MultiplierDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Multiplier.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public Multiplier cast(Object object) {
        return (Multiplier)object;
    }

    public Multiplier get(String key) throws HibernateException {
        return (Multiplier)this.get(this.getReferenceClass(), (Serializable)((Object)key));
    }

    public Multiplier get(String key, Session s) throws HibernateException {
        return (Multiplier)this.get(this.getReferenceClass(), (Serializable)((Object)key), s);
    }

    public Multiplier load(String key) throws HibernateException {
        return (Multiplier)this.load(this.getReferenceClass(), (Serializable)((Object)key));
    }

    public Multiplier load(String key, Session s) throws HibernateException {
        return (Multiplier)this.load(this.getReferenceClass(), (Serializable)((Object)key), s);
    }

    public Multiplier loadInitialize(String key, Session s) throws HibernateException {
        Multiplier obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Multiplier> findAll() {
        return super.findAll();
    }

    @Override
    public List<Multiplier> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Multiplier> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public String save(Multiplier multiplier) throws HibernateException {
        return (String)((Object)super.save(multiplier));
    }

    public String save(Multiplier multiplier, Session s) throws HibernateException {
        return (String)((Object)this.save((Object)multiplier, s));
    }

    public void saveOrUpdate(Multiplier multiplier) throws HibernateException {
        this.saveOrUpdate((Object)multiplier);
    }

    public void saveOrUpdate(Multiplier multiplier, Session s) throws HibernateException {
        this.saveOrUpdate((Object)multiplier, s);
    }

    public void update(Multiplier multiplier) throws HibernateException {
        this.update((Object)multiplier);
    }

    public void update(Multiplier multiplier, Session s) throws HibernateException {
        this.update((Object)multiplier, s);
    }

    public void delete(String id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(String id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Multiplier multiplier) throws HibernateException {
        this.delete((Object)multiplier);
    }

    public void delete(Multiplier multiplier, Session s) throws HibernateException {
        this.delete((Object)multiplier, s);
    }

    public void refresh(Multiplier multiplier, Session s) throws HibernateException {
        this.refresh((Object)multiplier, s);
    }
}

