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

import com.floreantpos.model.CustomPayment;
import com.floreantpos.model.dao.CustomPaymentDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCustomPaymentDAO
extends _RootDAO {
    public static CustomPaymentDAO instance;

    public static CustomPaymentDAO getInstance() {
        if (null == instance) {
            instance = new CustomPaymentDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return CustomPayment.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public CustomPayment cast(Object object) {
        return (CustomPayment)object;
    }

    public CustomPayment get(Integer key) throws HibernateException {
        return (CustomPayment)this.get(this.getReferenceClass(), key);
    }

    public CustomPayment get(Integer key, Session s) throws HibernateException {
        return (CustomPayment)this.get(this.getReferenceClass(), key, s);
    }

    public CustomPayment load(Integer key) throws HibernateException {
        return (CustomPayment)this.load(this.getReferenceClass(), key);
    }

    public CustomPayment load(Integer key, Session s) throws HibernateException {
        return (CustomPayment)this.load(this.getReferenceClass(), key, s);
    }

    public CustomPayment loadInitialize(Integer key, Session s) throws HibernateException {
        CustomPayment obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<CustomPayment> findAll() {
        return super.findAll();
    }

    @Override
    public List<CustomPayment> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<CustomPayment> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(CustomPayment customPayment) throws HibernateException {
        return (Integer)super.save(customPayment);
    }

    public Integer save(CustomPayment customPayment, Session s) throws HibernateException {
        return (Integer)this.save((Object)customPayment, s);
    }

    public void saveOrUpdate(CustomPayment customPayment) throws HibernateException {
        this.saveOrUpdate((Object)customPayment);
    }

    public void saveOrUpdate(CustomPayment customPayment, Session s) throws HibernateException {
        this.saveOrUpdate((Object)customPayment, s);
    }

    public void update(CustomPayment customPayment) throws HibernateException {
        this.update((Object)customPayment);
    }

    public void update(CustomPayment customPayment, Session s) throws HibernateException {
        this.update((Object)customPayment, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(CustomPayment customPayment) throws HibernateException {
        this.delete((Object)customPayment);
    }

    public void delete(CustomPayment customPayment, Session s) throws HibernateException {
        this.delete((Object)customPayment, s);
    }

    public void refresh(CustomPayment customPayment, Session s) throws HibernateException {
        this.refresh((Object)customPayment, s);
    }
}

