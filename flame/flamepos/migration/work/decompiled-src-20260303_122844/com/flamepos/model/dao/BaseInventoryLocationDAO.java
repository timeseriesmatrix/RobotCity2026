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

import com.floreantpos.model.InventoryLocation;
import com.floreantpos.model.dao.InventoryLocationDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseInventoryLocationDAO
extends _RootDAO {
    public static InventoryLocationDAO instance;

    public static InventoryLocationDAO getInstance() {
        if (null == instance) {
            instance = new InventoryLocationDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return InventoryLocation.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public InventoryLocation cast(Object object) {
        return (InventoryLocation)object;
    }

    public InventoryLocation get(Integer key) throws HibernateException {
        return (InventoryLocation)this.get(this.getReferenceClass(), key);
    }

    public InventoryLocation get(Integer key, Session s) throws HibernateException {
        return (InventoryLocation)this.get(this.getReferenceClass(), key, s);
    }

    public InventoryLocation load(Integer key) throws HibernateException {
        return (InventoryLocation)this.load(this.getReferenceClass(), key);
    }

    public InventoryLocation load(Integer key, Session s) throws HibernateException {
        return (InventoryLocation)this.load(this.getReferenceClass(), key, s);
    }

    public InventoryLocation loadInitialize(Integer key, Session s) throws HibernateException {
        InventoryLocation obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<InventoryLocation> findAll() {
        return super.findAll();
    }

    @Override
    public List<InventoryLocation> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<InventoryLocation> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(InventoryLocation inventoryLocation) throws HibernateException {
        return (Integer)super.save(inventoryLocation);
    }

    public Integer save(InventoryLocation inventoryLocation, Session s) throws HibernateException {
        return (Integer)this.save((Object)inventoryLocation, s);
    }

    public void saveOrUpdate(InventoryLocation inventoryLocation) throws HibernateException {
        this.saveOrUpdate((Object)inventoryLocation);
    }

    public void saveOrUpdate(InventoryLocation inventoryLocation, Session s) throws HibernateException {
        this.saveOrUpdate((Object)inventoryLocation, s);
    }

    public void update(InventoryLocation inventoryLocation) throws HibernateException {
        this.update((Object)inventoryLocation);
    }

    public void update(InventoryLocation inventoryLocation, Session s) throws HibernateException {
        this.update((Object)inventoryLocation, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(InventoryLocation inventoryLocation) throws HibernateException {
        this.delete((Object)inventoryLocation);
    }

    public void delete(InventoryLocation inventoryLocation, Session s) throws HibernateException {
        this.delete((Object)inventoryLocation, s);
    }

    public void refresh(InventoryLocation inventoryLocation, Session s) throws HibernateException {
        this.refresh((Object)inventoryLocation, s);
    }
}

