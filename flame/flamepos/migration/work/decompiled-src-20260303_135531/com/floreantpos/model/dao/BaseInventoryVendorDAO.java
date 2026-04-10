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

import com.floreantpos.model.InventoryVendor;
import com.floreantpos.model.dao.InventoryVendorDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseInventoryVendorDAO
extends _RootDAO {
    public static InventoryVendorDAO instance;

    public static InventoryVendorDAO getInstance() {
        if (null == instance) {
            instance = new InventoryVendorDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return InventoryVendor.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public InventoryVendor cast(Object object) {
        return (InventoryVendor)object;
    }

    public InventoryVendor get(Integer key) throws HibernateException {
        return (InventoryVendor)this.get(this.getReferenceClass(), key);
    }

    public InventoryVendor get(Integer key, Session s) throws HibernateException {
        return (InventoryVendor)this.get(this.getReferenceClass(), key, s);
    }

    public InventoryVendor load(Integer key) throws HibernateException {
        return (InventoryVendor)this.load(this.getReferenceClass(), key);
    }

    public InventoryVendor load(Integer key, Session s) throws HibernateException {
        return (InventoryVendor)this.load(this.getReferenceClass(), key, s);
    }

    public InventoryVendor loadInitialize(Integer key, Session s) throws HibernateException {
        InventoryVendor obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<InventoryVendor> findAll() {
        return super.findAll();
    }

    @Override
    public List<InventoryVendor> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<InventoryVendor> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(InventoryVendor inventoryVendor) throws HibernateException {
        return (Integer)super.save(inventoryVendor);
    }

    public Integer save(InventoryVendor inventoryVendor, Session s) throws HibernateException {
        return (Integer)this.save((Object)inventoryVendor, s);
    }

    public void saveOrUpdate(InventoryVendor inventoryVendor) throws HibernateException {
        this.saveOrUpdate((Object)inventoryVendor);
    }

    public void saveOrUpdate(InventoryVendor inventoryVendor, Session s) throws HibernateException {
        this.saveOrUpdate((Object)inventoryVendor, s);
    }

    public void update(InventoryVendor inventoryVendor) throws HibernateException {
        this.update((Object)inventoryVendor);
    }

    public void update(InventoryVendor inventoryVendor, Session s) throws HibernateException {
        this.update((Object)inventoryVendor, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(InventoryVendor inventoryVendor) throws HibernateException {
        this.delete((Object)inventoryVendor);
    }

    public void delete(InventoryVendor inventoryVendor, Session s) throws HibernateException {
        this.delete((Object)inventoryVendor, s);
    }

    public void refresh(InventoryVendor inventoryVendor, Session s) throws HibernateException {
        this.refresh((Object)inventoryVendor, s);
    }
}

