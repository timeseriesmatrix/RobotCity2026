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

import com.floreantpos.model.DrawerPullReport;
import com.floreantpos.model.dao.DrawerPullReportDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseDrawerPullReportDAO
extends _RootDAO {
    public static DrawerPullReportDAO instance;

    public static DrawerPullReportDAO getInstance() {
        if (null == instance) {
            instance = new DrawerPullReportDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return DrawerPullReport.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public DrawerPullReport cast(Object object) {
        return (DrawerPullReport)object;
    }

    public DrawerPullReport get(Integer key) throws HibernateException {
        return (DrawerPullReport)this.get(this.getReferenceClass(), key);
    }

    public DrawerPullReport get(Integer key, Session s) throws HibernateException {
        return (DrawerPullReport)this.get(this.getReferenceClass(), key, s);
    }

    public DrawerPullReport load(Integer key) throws HibernateException {
        return (DrawerPullReport)this.load(this.getReferenceClass(), key);
    }

    public DrawerPullReport load(Integer key, Session s) throws HibernateException {
        return (DrawerPullReport)this.load(this.getReferenceClass(), key, s);
    }

    public DrawerPullReport loadInitialize(Integer key, Session s) throws HibernateException {
        DrawerPullReport obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<DrawerPullReport> findAll() {
        return super.findAll();
    }

    @Override
    public List<DrawerPullReport> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<DrawerPullReport> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(DrawerPullReport drawerPullReport) throws HibernateException {
        return (Integer)super.save(drawerPullReport);
    }

    public Integer save(DrawerPullReport drawerPullReport, Session s) throws HibernateException {
        return (Integer)this.save((Object)drawerPullReport, s);
    }

    public void saveOrUpdate(DrawerPullReport drawerPullReport) throws HibernateException {
        this.saveOrUpdate((Object)drawerPullReport);
    }

    public void saveOrUpdate(DrawerPullReport drawerPullReport, Session s) throws HibernateException {
        this.saveOrUpdate((Object)drawerPullReport, s);
    }

    public void update(DrawerPullReport drawerPullReport) throws HibernateException {
        this.update((Object)drawerPullReport);
    }

    public void update(DrawerPullReport drawerPullReport, Session s) throws HibernateException {
        this.update((Object)drawerPullReport, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(DrawerPullReport drawerPullReport) throws HibernateException {
        this.delete((Object)drawerPullReport);
    }

    public void delete(DrawerPullReport drawerPullReport, Session s) throws HibernateException {
        this.delete((Object)drawerPullReport, s);
    }

    public void refresh(DrawerPullReport drawerPullReport, Session s) throws HibernateException {
        this.refresh((Object)drawerPullReport, s);
    }
}

