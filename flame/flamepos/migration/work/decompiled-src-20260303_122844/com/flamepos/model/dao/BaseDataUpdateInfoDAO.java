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

import com.floreantpos.model.DataUpdateInfo;
import com.floreantpos.model.dao.DataUpdateInfoDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseDataUpdateInfoDAO
extends _RootDAO {
    public static DataUpdateInfoDAO instance;

    public static DataUpdateInfoDAO getInstance() {
        if (null == instance) {
            instance = new DataUpdateInfoDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return DataUpdateInfo.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public DataUpdateInfo cast(Object object) {
        return (DataUpdateInfo)object;
    }

    public DataUpdateInfo get(Integer key) throws HibernateException {
        return (DataUpdateInfo)this.get(this.getReferenceClass(), key);
    }

    public DataUpdateInfo get(Integer key, Session s) throws HibernateException {
        return (DataUpdateInfo)this.get(this.getReferenceClass(), key, s);
    }

    public DataUpdateInfo load(Integer key) throws HibernateException {
        return (DataUpdateInfo)this.load(this.getReferenceClass(), key);
    }

    public DataUpdateInfo load(Integer key, Session s) throws HibernateException {
        return (DataUpdateInfo)this.load(this.getReferenceClass(), key, s);
    }

    public DataUpdateInfo loadInitialize(Integer key, Session s) throws HibernateException {
        DataUpdateInfo obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<DataUpdateInfo> findAll() {
        return super.findAll();
    }

    @Override
    public List<DataUpdateInfo> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<DataUpdateInfo> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(DataUpdateInfo dataUpdateInfo) throws HibernateException {
        return (Integer)super.save(dataUpdateInfo);
    }

    public Integer save(DataUpdateInfo dataUpdateInfo, Session s) throws HibernateException {
        return (Integer)this.save((Object)dataUpdateInfo, s);
    }

    public void saveOrUpdate(DataUpdateInfo dataUpdateInfo) throws HibernateException {
        this.saveOrUpdate((Object)dataUpdateInfo);
    }

    public void saveOrUpdate(DataUpdateInfo dataUpdateInfo, Session s) throws HibernateException {
        this.saveOrUpdate((Object)dataUpdateInfo, s);
    }

    public void update(DataUpdateInfo dataUpdateInfo) throws HibernateException {
        this.update((Object)dataUpdateInfo);
    }

    public void update(DataUpdateInfo dataUpdateInfo, Session s) throws HibernateException {
        this.update((Object)dataUpdateInfo, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(DataUpdateInfo dataUpdateInfo) throws HibernateException {
        this.delete((Object)dataUpdateInfo);
    }

    public void delete(DataUpdateInfo dataUpdateInfo, Session s) throws HibernateException {
        this.delete((Object)dataUpdateInfo, s);
    }

    public void refresh(DataUpdateInfo dataUpdateInfo, Session s) throws HibernateException {
        this.refresh((Object)dataUpdateInfo, s);
    }
}

