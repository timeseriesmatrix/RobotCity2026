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

import com.floreantpos.model.RecepieItem;
import com.floreantpos.model.dao.RecepieItemDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseRecepieItemDAO
extends _RootDAO {
    public static RecepieItemDAO instance;

    public static RecepieItemDAO getInstance() {
        if (null == instance) {
            instance = new RecepieItemDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return RecepieItem.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public RecepieItem cast(Object object) {
        return (RecepieItem)object;
    }

    public RecepieItem get(Integer key) throws HibernateException {
        return (RecepieItem)this.get(this.getReferenceClass(), key);
    }

    public RecepieItem get(Integer key, Session s) throws HibernateException {
        return (RecepieItem)this.get(this.getReferenceClass(), key, s);
    }

    public RecepieItem load(Integer key) throws HibernateException {
        return (RecepieItem)this.load(this.getReferenceClass(), key);
    }

    public RecepieItem load(Integer key, Session s) throws HibernateException {
        return (RecepieItem)this.load(this.getReferenceClass(), key, s);
    }

    public RecepieItem loadInitialize(Integer key, Session s) throws HibernateException {
        RecepieItem obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<RecepieItem> findAll() {
        return super.findAll();
    }

    @Override
    public List<RecepieItem> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<RecepieItem> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(RecepieItem recepieItem) throws HibernateException {
        return (Integer)super.save(recepieItem);
    }

    public Integer save(RecepieItem recepieItem, Session s) throws HibernateException {
        return (Integer)this.save((Object)recepieItem, s);
    }

    public void saveOrUpdate(RecepieItem recepieItem) throws HibernateException {
        this.saveOrUpdate((Object)recepieItem);
    }

    public void saveOrUpdate(RecepieItem recepieItem, Session s) throws HibernateException {
        this.saveOrUpdate((Object)recepieItem, s);
    }

    public void update(RecepieItem recepieItem) throws HibernateException {
        this.update((Object)recepieItem);
    }

    public void update(RecepieItem recepieItem, Session s) throws HibernateException {
        this.update((Object)recepieItem, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(RecepieItem recepieItem) throws HibernateException {
        this.delete((Object)recepieItem);
    }

    public void delete(RecepieItem recepieItem, Session s) throws HibernateException {
        this.delete((Object)recepieItem, s);
    }

    public void refresh(RecepieItem recepieItem, Session s) throws HibernateException {
        this.refresh((Object)recepieItem, s);
    }
}

