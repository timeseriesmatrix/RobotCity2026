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

import com.floreantpos.model.PizzaCrust;
import com.floreantpos.model.dao.PizzaCrustDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePizzaCrustDAO
extends _RootDAO {
    public static PizzaCrustDAO instance;

    public static PizzaCrustDAO getInstance() {
        if (null == instance) {
            instance = new PizzaCrustDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PizzaCrust.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public PizzaCrust cast(Object object) {
        return (PizzaCrust)object;
    }

    public PizzaCrust get(Integer key) throws HibernateException {
        return (PizzaCrust)this.get(this.getReferenceClass(), key);
    }

    public PizzaCrust get(Integer key, Session s) throws HibernateException {
        return (PizzaCrust)this.get(this.getReferenceClass(), key, s);
    }

    public PizzaCrust load(Integer key) throws HibernateException {
        return (PizzaCrust)this.load(this.getReferenceClass(), key);
    }

    public PizzaCrust load(Integer key, Session s) throws HibernateException {
        return (PizzaCrust)this.load(this.getReferenceClass(), key, s);
    }

    public PizzaCrust loadInitialize(Integer key, Session s) throws HibernateException {
        PizzaCrust obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PizzaCrust> findAll() {
        return super.findAll();
    }

    @Override
    public List<PizzaCrust> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PizzaCrust> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PizzaCrust pizzaCrust) throws HibernateException {
        return (Integer)super.save(pizzaCrust);
    }

    public Integer save(PizzaCrust pizzaCrust, Session s) throws HibernateException {
        return (Integer)this.save((Object)pizzaCrust, s);
    }

    public void saveOrUpdate(PizzaCrust pizzaCrust) throws HibernateException {
        this.saveOrUpdate((Object)pizzaCrust);
    }

    public void saveOrUpdate(PizzaCrust pizzaCrust, Session s) throws HibernateException {
        this.saveOrUpdate((Object)pizzaCrust, s);
    }

    public void update(PizzaCrust pizzaCrust) throws HibernateException {
        this.update((Object)pizzaCrust);
    }

    public void update(PizzaCrust pizzaCrust, Session s) throws HibernateException {
        this.update((Object)pizzaCrust, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PizzaCrust pizzaCrust) throws HibernateException {
        this.delete((Object)pizzaCrust);
    }

    public void delete(PizzaCrust pizzaCrust, Session s) throws HibernateException {
        this.delete((Object)pizzaCrust, s);
    }

    public void refresh(PizzaCrust pizzaCrust, Session s) throws HibernateException {
        this.refresh((Object)pizzaCrust, s);
    }
}

