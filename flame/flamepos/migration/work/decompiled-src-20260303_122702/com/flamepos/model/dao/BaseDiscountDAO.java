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

import com.floreantpos.model.Discount;
import com.floreantpos.model.dao.DiscountDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseDiscountDAO
extends _RootDAO {
    public static DiscountDAO instance;

    public static DiscountDAO getInstance() {
        if (null == instance) {
            instance = new DiscountDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Discount.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public Discount cast(Object object) {
        return (Discount)object;
    }

    public Discount get(Integer key) throws HibernateException {
        return (Discount)this.get(this.getReferenceClass(), key);
    }

    public Discount get(Integer key, Session s) throws HibernateException {
        return (Discount)this.get(this.getReferenceClass(), key, s);
    }

    public Discount load(Integer key) throws HibernateException {
        return (Discount)this.load(this.getReferenceClass(), key);
    }

    public Discount load(Integer key, Session s) throws HibernateException {
        return (Discount)this.load(this.getReferenceClass(), key, s);
    }

    public Discount loadInitialize(Integer key, Session s) throws HibernateException {
        Discount obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Discount> findAll() {
        return super.findAll();
    }

    @Override
    public List<Discount> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Discount> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(Discount discount) throws HibernateException {
        return (Integer)super.save(discount);
    }

    public Integer save(Discount discount, Session s) throws HibernateException {
        return (Integer)this.save((Object)discount, s);
    }

    public void saveOrUpdate(Discount discount) throws HibernateException {
        this.saveOrUpdate((Object)discount);
    }

    public void saveOrUpdate(Discount discount, Session s) throws HibernateException {
        this.saveOrUpdate((Object)discount, s);
    }

    public void update(Discount discount) throws HibernateException {
        this.update((Object)discount);
    }

    public void update(Discount discount, Session s) throws HibernateException {
        this.update((Object)discount, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Discount discount) throws HibernateException {
        this.delete((Object)discount);
    }

    public void delete(Discount discount, Session s) throws HibernateException {
        this.delete((Object)discount, s);
    }

    public void refresh(Discount discount, Session s) throws HibernateException {
        this.refresh((Object)discount, s);
    }
}

