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

import com.floreantpos.model.ShopTableType;
import com.floreantpos.model.dao.ShopTableTypeDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseShopTableTypeDAO
extends _RootDAO {
    public static ShopTableTypeDAO instance;

    public static ShopTableTypeDAO getInstance() {
        if (null == instance) {
            instance = new ShopTableTypeDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return ShopTableType.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public ShopTableType cast(Object object) {
        return (ShopTableType)object;
    }

    public ShopTableType get(Integer key) throws HibernateException {
        return (ShopTableType)this.get(this.getReferenceClass(), key);
    }

    public ShopTableType get(Integer key, Session s) throws HibernateException {
        return (ShopTableType)this.get(this.getReferenceClass(), key, s);
    }

    public ShopTableType load(Integer key) throws HibernateException {
        return (ShopTableType)this.load(this.getReferenceClass(), key);
    }

    public ShopTableType load(Integer key, Session s) throws HibernateException {
        return (ShopTableType)this.load(this.getReferenceClass(), key, s);
    }

    public ShopTableType loadInitialize(Integer key, Session s) throws HibernateException {
        ShopTableType obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<ShopTableType> findAll() {
        return super.findAll();
    }

    @Override
    public List<ShopTableType> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<ShopTableType> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(ShopTableType shopTableType) throws HibernateException {
        return (Integer)super.save(shopTableType);
    }

    public Integer save(ShopTableType shopTableType, Session s) throws HibernateException {
        return (Integer)this.save((Object)shopTableType, s);
    }

    public void saveOrUpdate(ShopTableType shopTableType) throws HibernateException {
        this.saveOrUpdate((Object)shopTableType);
    }

    public void saveOrUpdate(ShopTableType shopTableType, Session s) throws HibernateException {
        this.saveOrUpdate((Object)shopTableType, s);
    }

    public void update(ShopTableType shopTableType) throws HibernateException {
        this.update((Object)shopTableType);
    }

    public void update(ShopTableType shopTableType, Session s) throws HibernateException {
        this.update((Object)shopTableType, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(ShopTableType shopTableType) throws HibernateException {
        this.delete((Object)shopTableType);
    }

    public void delete(ShopTableType shopTableType, Session s) throws HibernateException {
        this.delete((Object)shopTableType, s);
    }

    public void refresh(ShopTableType shopTableType, Session s) throws HibernateException {
        this.refresh((Object)shopTableType, s);
    }
}

