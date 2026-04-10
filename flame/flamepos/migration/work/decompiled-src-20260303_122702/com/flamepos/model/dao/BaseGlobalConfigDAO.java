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

import com.floreantpos.model.GlobalConfig;
import com.floreantpos.model.dao.GlobalConfigDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseGlobalConfigDAO
extends _RootDAO {
    public static GlobalConfigDAO instance;

    public static GlobalConfigDAO getInstance() {
        if (null == instance) {
            instance = new GlobalConfigDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return GlobalConfig.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public GlobalConfig cast(Object object) {
        return (GlobalConfig)object;
    }

    public GlobalConfig get(Integer key) throws HibernateException {
        return (GlobalConfig)this.get(this.getReferenceClass(), key);
    }

    public GlobalConfig get(Integer key, Session s) throws HibernateException {
        return (GlobalConfig)this.get(this.getReferenceClass(), key, s);
    }

    public GlobalConfig load(Integer key) throws HibernateException {
        return (GlobalConfig)this.load(this.getReferenceClass(), key);
    }

    public GlobalConfig load(Integer key, Session s) throws HibernateException {
        return (GlobalConfig)this.load(this.getReferenceClass(), key, s);
    }

    public GlobalConfig loadInitialize(Integer key, Session s) throws HibernateException {
        GlobalConfig obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<GlobalConfig> findAll() {
        return super.findAll();
    }

    @Override
    public List<GlobalConfig> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<GlobalConfig> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(GlobalConfig globalConfig) throws HibernateException {
        return (Integer)super.save(globalConfig);
    }

    public Integer save(GlobalConfig globalConfig, Session s) throws HibernateException {
        return (Integer)this.save((Object)globalConfig, s);
    }

    public void saveOrUpdate(GlobalConfig globalConfig) throws HibernateException {
        this.saveOrUpdate((Object)globalConfig);
    }

    public void saveOrUpdate(GlobalConfig globalConfig, Session s) throws HibernateException {
        this.saveOrUpdate((Object)globalConfig, s);
    }

    public void update(GlobalConfig globalConfig) throws HibernateException {
        this.update((Object)globalConfig);
    }

    public void update(GlobalConfig globalConfig, Session s) throws HibernateException {
        this.update((Object)globalConfig, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(GlobalConfig globalConfig) throws HibernateException {
        this.delete((Object)globalConfig);
    }

    public void delete(GlobalConfig globalConfig, Session s) throws HibernateException {
        this.delete((Object)globalConfig, s);
    }

    public void refresh(GlobalConfig globalConfig, Session s) throws HibernateException {
        this.refresh((Object)globalConfig, s);
    }
}

