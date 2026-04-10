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

import com.floreantpos.model.DrawerAssignedHistory;
import com.floreantpos.model.dao.DrawerAssignedHistoryDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseDrawerAssignedHistoryDAO
extends _RootDAO {
    public static DrawerAssignedHistoryDAO instance;

    public static DrawerAssignedHistoryDAO getInstance() {
        if (null == instance) {
            instance = new DrawerAssignedHistoryDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return DrawerAssignedHistory.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public DrawerAssignedHistory cast(Object object) {
        return (DrawerAssignedHistory)object;
    }

    public DrawerAssignedHistory get(Integer key) throws HibernateException {
        return (DrawerAssignedHistory)this.get(this.getReferenceClass(), key);
    }

    public DrawerAssignedHistory get(Integer key, Session s) throws HibernateException {
        return (DrawerAssignedHistory)this.get(this.getReferenceClass(), key, s);
    }

    public DrawerAssignedHistory load(Integer key) throws HibernateException {
        return (DrawerAssignedHistory)this.load(this.getReferenceClass(), key);
    }

    public DrawerAssignedHistory load(Integer key, Session s) throws HibernateException {
        return (DrawerAssignedHistory)this.load(this.getReferenceClass(), key, s);
    }

    public DrawerAssignedHistory loadInitialize(Integer key, Session s) throws HibernateException {
        DrawerAssignedHistory obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<DrawerAssignedHistory> findAll() {
        return super.findAll();
    }

    @Override
    public List<DrawerAssignedHistory> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<DrawerAssignedHistory> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(DrawerAssignedHistory drawerAssignedHistory) throws HibernateException {
        return (Integer)super.save(drawerAssignedHistory);
    }

    public Integer save(DrawerAssignedHistory drawerAssignedHistory, Session s) throws HibernateException {
        return (Integer)this.save((Object)drawerAssignedHistory, s);
    }

    public void saveOrUpdate(DrawerAssignedHistory drawerAssignedHistory) throws HibernateException {
        this.saveOrUpdate((Object)drawerAssignedHistory);
    }

    public void saveOrUpdate(DrawerAssignedHistory drawerAssignedHistory, Session s) throws HibernateException {
        this.saveOrUpdate((Object)drawerAssignedHistory, s);
    }

    public void update(DrawerAssignedHistory drawerAssignedHistory) throws HibernateException {
        this.update((Object)drawerAssignedHistory);
    }

    public void update(DrawerAssignedHistory drawerAssignedHistory, Session s) throws HibernateException {
        this.update((Object)drawerAssignedHistory, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(DrawerAssignedHistory drawerAssignedHistory) throws HibernateException {
        this.delete((Object)drawerAssignedHistory);
    }

    public void delete(DrawerAssignedHistory drawerAssignedHistory, Session s) throws HibernateException {
        this.delete((Object)drawerAssignedHistory, s);
    }

    public void refresh(DrawerAssignedHistory drawerAssignedHistory, Session s) throws HibernateException {
        this.refresh((Object)drawerAssignedHistory, s);
    }
}

