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

import com.floreantpos.model.PizzaPrice;
import com.floreantpos.model.dao.PizzaPriceDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePizzaPriceDAO
extends _RootDAO {
    public static PizzaPriceDAO instance;

    public static PizzaPriceDAO getInstance() {
        if (null == instance) {
            instance = new PizzaPriceDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PizzaPrice.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public PizzaPrice cast(Object object) {
        return (PizzaPrice)object;
    }

    public PizzaPrice get(Integer key) throws HibernateException {
        return (PizzaPrice)this.get(this.getReferenceClass(), key);
    }

    public PizzaPrice get(Integer key, Session s) throws HibernateException {
        return (PizzaPrice)this.get(this.getReferenceClass(), key, s);
    }

    public PizzaPrice load(Integer key) throws HibernateException {
        return (PizzaPrice)this.load(this.getReferenceClass(), key);
    }

    public PizzaPrice load(Integer key, Session s) throws HibernateException {
        return (PizzaPrice)this.load(this.getReferenceClass(), key, s);
    }

    public PizzaPrice loadInitialize(Integer key, Session s) throws HibernateException {
        PizzaPrice obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PizzaPrice> findAll() {
        return super.findAll();
    }

    @Override
    public List<PizzaPrice> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PizzaPrice> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PizzaPrice pizzaPrice) throws HibernateException {
        return (Integer)super.save(pizzaPrice);
    }

    public Integer save(PizzaPrice pizzaPrice, Session s) throws HibernateException {
        return (Integer)this.save((Object)pizzaPrice, s);
    }

    public void saveOrUpdate(PizzaPrice pizzaPrice) throws HibernateException {
        this.saveOrUpdate((Object)pizzaPrice);
    }

    public void saveOrUpdate(PizzaPrice pizzaPrice, Session s) throws HibernateException {
        this.saveOrUpdate((Object)pizzaPrice, s);
    }

    public void update(PizzaPrice pizzaPrice) throws HibernateException {
        this.update((Object)pizzaPrice);
    }

    public void update(PizzaPrice pizzaPrice, Session s) throws HibernateException {
        this.update((Object)pizzaPrice, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PizzaPrice pizzaPrice) throws HibernateException {
        this.delete((Object)pizzaPrice);
    }

    public void delete(PizzaPrice pizzaPrice, Session s) throws HibernateException {
        this.delete((Object)pizzaPrice, s);
    }

    public void refresh(PizzaPrice pizzaPrice, Session s) throws HibernateException {
        this.refresh((Object)pizzaPrice, s);
    }
}

