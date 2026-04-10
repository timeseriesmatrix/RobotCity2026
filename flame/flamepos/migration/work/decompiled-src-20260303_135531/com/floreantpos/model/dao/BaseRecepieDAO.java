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

import com.floreantpos.model.Recepie;
import com.floreantpos.model.dao.RecepieDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseRecepieDAO
extends _RootDAO {
    public static RecepieDAO instance;

    public static RecepieDAO getInstance() {
        if (null == instance) {
            instance = new RecepieDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Recepie.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public Recepie cast(Object object) {
        return (Recepie)object;
    }

    public Recepie get(Integer key) throws HibernateException {
        return (Recepie)this.get(this.getReferenceClass(), key);
    }

    public Recepie get(Integer key, Session s) throws HibernateException {
        return (Recepie)this.get(this.getReferenceClass(), key, s);
    }

    public Recepie load(Integer key) throws HibernateException {
        return (Recepie)this.load(this.getReferenceClass(), key);
    }

    public Recepie load(Integer key, Session s) throws HibernateException {
        return (Recepie)this.load(this.getReferenceClass(), key, s);
    }

    public Recepie loadInitialize(Integer key, Session s) throws HibernateException {
        Recepie obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Recepie> findAll() {
        return super.findAll();
    }

    @Override
    public List<Recepie> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Recepie> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(Recepie recepie) throws HibernateException {
        return (Integer)super.save(recepie);
    }

    public Integer save(Recepie recepie, Session s) throws HibernateException {
        return (Integer)this.save((Object)recepie, s);
    }

    public void saveOrUpdate(Recepie recepie) throws HibernateException {
        this.saveOrUpdate((Object)recepie);
    }

    public void saveOrUpdate(Recepie recepie, Session s) throws HibernateException {
        this.saveOrUpdate((Object)recepie, s);
    }

    public void update(Recepie recepie) throws HibernateException {
        this.update((Object)recepie);
    }

    public void update(Recepie recepie, Session s) throws HibernateException {
        this.update((Object)recepie, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Recepie recepie) throws HibernateException {
        this.delete((Object)recepie);
    }

    public void delete(Recepie recepie, Session s) throws HibernateException {
        this.delete((Object)recepie, s);
    }

    public void refresh(Recepie recepie, Session s) throws HibernateException {
        this.refresh((Object)recepie, s);
    }
}

