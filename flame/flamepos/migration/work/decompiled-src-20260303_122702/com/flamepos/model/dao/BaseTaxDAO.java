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

import com.floreantpos.model.Tax;
import com.floreantpos.model.dao.TaxDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseTaxDAO
extends _RootDAO {
    public static TaxDAO instance;

    public static TaxDAO getInstance() {
        if (null == instance) {
            instance = new TaxDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Tax.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public Tax cast(Object object) {
        return (Tax)object;
    }

    public Tax get(Integer key) throws HibernateException {
        return (Tax)this.get(this.getReferenceClass(), key);
    }

    public Tax get(Integer key, Session s) throws HibernateException {
        return (Tax)this.get(this.getReferenceClass(), key, s);
    }

    public Tax load(Integer key) throws HibernateException {
        return (Tax)this.load(this.getReferenceClass(), key);
    }

    public Tax load(Integer key, Session s) throws HibernateException {
        return (Tax)this.load(this.getReferenceClass(), key, s);
    }

    public Tax loadInitialize(Integer key, Session s) throws HibernateException {
        Tax obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Tax> findAll() {
        return super.findAll();
    }

    @Override
    public List<Tax> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Tax> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(Tax tax) throws HibernateException {
        return (Integer)super.save(tax);
    }

    public Integer save(Tax tax, Session s) throws HibernateException {
        return (Integer)this.save((Object)tax, s);
    }

    public void saveOrUpdate(Tax tax) throws HibernateException {
        this.saveOrUpdate((Object)tax);
    }

    public void saveOrUpdate(Tax tax, Session s) throws HibernateException {
        this.saveOrUpdate((Object)tax, s);
    }

    public void update(Tax tax) throws HibernateException {
        this.update((Object)tax);
    }

    public void update(Tax tax, Session s) throws HibernateException {
        this.update((Object)tax, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Tax tax) throws HibernateException {
        this.delete((Object)tax);
    }

    public void delete(Tax tax, Session s) throws HibernateException {
        this.delete((Object)tax, s);
    }

    public void refresh(Tax tax, Session s) throws HibernateException {
        this.refresh((Object)tax, s);
    }
}

