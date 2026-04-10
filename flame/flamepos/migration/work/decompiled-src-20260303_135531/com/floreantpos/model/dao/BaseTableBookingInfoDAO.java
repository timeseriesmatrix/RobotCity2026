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

import com.floreantpos.model.TableBookingInfo;
import com.floreantpos.model.dao.TableBookingInfoDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseTableBookingInfoDAO
extends _RootDAO {
    public static TableBookingInfoDAO instance;

    public static TableBookingInfoDAO getInstance() {
        if (null == instance) {
            instance = new TableBookingInfoDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return TableBookingInfo.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public TableBookingInfo cast(Object object) {
        return (TableBookingInfo)object;
    }

    public TableBookingInfo get(Integer key) throws HibernateException {
        return (TableBookingInfo)this.get(this.getReferenceClass(), key);
    }

    public TableBookingInfo get(Integer key, Session s) throws HibernateException {
        return (TableBookingInfo)this.get(this.getReferenceClass(), key, s);
    }

    public TableBookingInfo load(Integer key) throws HibernateException {
        return (TableBookingInfo)this.load(this.getReferenceClass(), key);
    }

    public TableBookingInfo load(Integer key, Session s) throws HibernateException {
        return (TableBookingInfo)this.load(this.getReferenceClass(), key, s);
    }

    public TableBookingInfo loadInitialize(Integer key, Session s) throws HibernateException {
        TableBookingInfo obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<TableBookingInfo> findAll() {
        return super.findAll();
    }

    @Override
    public List<TableBookingInfo> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<TableBookingInfo> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(TableBookingInfo tableBookingInfo) throws HibernateException {
        return (Integer)super.save(tableBookingInfo);
    }

    public Integer save(TableBookingInfo tableBookingInfo, Session s) throws HibernateException {
        return (Integer)this.save((Object)tableBookingInfo, s);
    }

    public void saveOrUpdate(TableBookingInfo tableBookingInfo) throws HibernateException {
        this.saveOrUpdate((Object)tableBookingInfo);
    }

    public void saveOrUpdate(TableBookingInfo tableBookingInfo, Session s) throws HibernateException {
        this.saveOrUpdate((Object)tableBookingInfo, s);
    }

    public void update(TableBookingInfo tableBookingInfo) throws HibernateException {
        this.update((Object)tableBookingInfo);
    }

    public void update(TableBookingInfo tableBookingInfo, Session s) throws HibernateException {
        this.update((Object)tableBookingInfo, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(TableBookingInfo tableBookingInfo) throws HibernateException {
        this.delete((Object)tableBookingInfo);
    }

    public void delete(TableBookingInfo tableBookingInfo, Session s) throws HibernateException {
        this.delete((Object)tableBookingInfo, s);
    }

    public void refresh(TableBookingInfo tableBookingInfo, Session s) throws HibernateException {
        this.refresh((Object)tableBookingInfo, s);
    }
}

