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

import com.floreantpos.model.ShopTable;
import com.floreantpos.model.dao.ShopTableDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseShopTableDAO
extends _RootDAO {
    public static ShopTableDAO instance;

    public static ShopTableDAO getInstance() {
        if (null == instance) {
            instance = new ShopTableDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return ShopTable.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public ShopTable cast(Object object) {
        return (ShopTable)object;
    }

    public ShopTable get(Integer key) throws HibernateException {
        return (ShopTable)this.get(this.getReferenceClass(), key);
    }

    public ShopTable get(Integer key, Session s) throws HibernateException {
        return (ShopTable)this.get(this.getReferenceClass(), key, s);
    }

    public ShopTable load(Integer key) throws HibernateException {
        return (ShopTable)this.load(this.getReferenceClass(), key);
    }

    public ShopTable load(Integer key, Session s) throws HibernateException {
        return (ShopTable)this.load(this.getReferenceClass(), key, s);
    }

    public ShopTable loadInitialize(Integer key, Session s) throws HibernateException {
        ShopTable obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<ShopTable> findAll() {
        return super.findAll();
    }

    @Override
    public List<ShopTable> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<ShopTable> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(ShopTable shopTable) throws HibernateException {
        return (Integer)super.save(shopTable);
    }

    public Integer save(ShopTable shopTable, Session s) throws HibernateException {
        return (Integer)this.save((Object)shopTable, s);
    }

    public void saveOrUpdate(ShopTable shopTable) throws HibernateException {
        this.saveOrUpdate((Object)shopTable);
    }

    public void saveOrUpdate(ShopTable shopTable, Session s) throws HibernateException {
        this.saveOrUpdate((Object)shopTable, s);
    }

    public void update(ShopTable shopTable) throws HibernateException {
        this.update((Object)shopTable);
    }

    public void update(ShopTable shopTable, Session s) throws HibernateException {
        this.update((Object)shopTable, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(ShopTable shopTable) throws HibernateException {
        this.delete((Object)shopTable);
    }

    public void delete(ShopTable shopTable, Session s) throws HibernateException {
        this.delete((Object)shopTable, s);
    }

    public void refresh(ShopTable shopTable, Session s) throws HibernateException {
        this.refresh((Object)shopTable, s);
    }
}

