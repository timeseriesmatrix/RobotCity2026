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

import com.floreantpos.model.Gratuity;
import com.floreantpos.model.dao.GratuityDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseGratuityDAO
extends _RootDAO {
    public static GratuityDAO instance;

    public static GratuityDAO getInstance() {
        if (null == instance) {
            instance = new GratuityDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Gratuity.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public Gratuity cast(Object object) {
        return (Gratuity)object;
    }

    public Gratuity get(Integer key) throws HibernateException {
        return (Gratuity)this.get(this.getReferenceClass(), key);
    }

    public Gratuity get(Integer key, Session s) throws HibernateException {
        return (Gratuity)this.get(this.getReferenceClass(), key, s);
    }

    public Gratuity load(Integer key) throws HibernateException {
        return (Gratuity)this.load(this.getReferenceClass(), key);
    }

    public Gratuity load(Integer key, Session s) throws HibernateException {
        return (Gratuity)this.load(this.getReferenceClass(), key, s);
    }

    public Gratuity loadInitialize(Integer key, Session s) throws HibernateException {
        Gratuity obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Gratuity> findAll() {
        return super.findAll();
    }

    @Override
    public List<Gratuity> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Gratuity> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(Gratuity gratuity) throws HibernateException {
        return (Integer)super.save(gratuity);
    }

    public Integer save(Gratuity gratuity, Session s) throws HibernateException {
        return (Integer)this.save((Object)gratuity, s);
    }

    public void saveOrUpdate(Gratuity gratuity) throws HibernateException {
        this.saveOrUpdate((Object)gratuity);
    }

    public void saveOrUpdate(Gratuity gratuity, Session s) throws HibernateException {
        this.saveOrUpdate((Object)gratuity, s);
    }

    public void update(Gratuity gratuity) throws HibernateException {
        this.update((Object)gratuity);
    }

    public void update(Gratuity gratuity, Session s) throws HibernateException {
        this.update((Object)gratuity, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Gratuity gratuity) throws HibernateException {
        this.delete((Object)gratuity);
    }

    public void delete(Gratuity gratuity, Session s) throws HibernateException {
        this.delete((Object)gratuity, s);
    }

    public void refresh(Gratuity gratuity, Session s) throws HibernateException {
        this.refresh((Object)gratuity, s);
    }
}

