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

import com.floreantpos.model.PizzaModifierPrice;
import com.floreantpos.model.dao.PizzaModifierPriceDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePizzaModifierPriceDAO
extends _RootDAO {
    public static PizzaModifierPriceDAO instance;

    public static PizzaModifierPriceDAO getInstance() {
        if (null == instance) {
            instance = new PizzaModifierPriceDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PizzaModifierPrice.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public PizzaModifierPrice cast(Object object) {
        return (PizzaModifierPrice)object;
    }

    public PizzaModifierPrice get(Integer key) throws HibernateException {
        return (PizzaModifierPrice)this.get(this.getReferenceClass(), key);
    }

    public PizzaModifierPrice get(Integer key, Session s) throws HibernateException {
        return (PizzaModifierPrice)this.get(this.getReferenceClass(), key, s);
    }

    public PizzaModifierPrice load(Integer key) throws HibernateException {
        return (PizzaModifierPrice)this.load(this.getReferenceClass(), key);
    }

    public PizzaModifierPrice load(Integer key, Session s) throws HibernateException {
        return (PizzaModifierPrice)this.load(this.getReferenceClass(), key, s);
    }

    public PizzaModifierPrice loadInitialize(Integer key, Session s) throws HibernateException {
        PizzaModifierPrice obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PizzaModifierPrice> findAll() {
        return super.findAll();
    }

    @Override
    public List<PizzaModifierPrice> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PizzaModifierPrice> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PizzaModifierPrice pizzaModifierPrice) throws HibernateException {
        return (Integer)super.save(pizzaModifierPrice);
    }

    public Integer save(PizzaModifierPrice pizzaModifierPrice, Session s) throws HibernateException {
        return (Integer)this.save((Object)pizzaModifierPrice, s);
    }

    public void saveOrUpdate(PizzaModifierPrice pizzaModifierPrice) throws HibernateException {
        this.saveOrUpdate((Object)pizzaModifierPrice);
    }

    public void saveOrUpdate(PizzaModifierPrice pizzaModifierPrice, Session s) throws HibernateException {
        this.saveOrUpdate((Object)pizzaModifierPrice, s);
    }

    public void update(PizzaModifierPrice pizzaModifierPrice) throws HibernateException {
        this.update((Object)pizzaModifierPrice);
    }

    public void update(PizzaModifierPrice pizzaModifierPrice, Session s) throws HibernateException {
        this.update((Object)pizzaModifierPrice, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PizzaModifierPrice pizzaModifierPrice) throws HibernateException {
        this.delete((Object)pizzaModifierPrice);
    }

    public void delete(PizzaModifierPrice pizzaModifierPrice, Session s) throws HibernateException {
        this.delete((Object)pizzaModifierPrice, s);
    }

    public void refresh(PizzaModifierPrice pizzaModifierPrice, Session s) throws HibernateException {
        this.refresh((Object)pizzaModifierPrice, s);
    }
}

