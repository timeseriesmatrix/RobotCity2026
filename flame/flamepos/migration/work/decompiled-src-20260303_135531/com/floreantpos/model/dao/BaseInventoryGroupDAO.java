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

import com.floreantpos.model.InventoryGroup;
import com.floreantpos.model.dao.InventoryGroupDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseInventoryGroupDAO
extends _RootDAO {
    public static InventoryGroupDAO instance;

    public static InventoryGroupDAO getInstance() {
        if (null == instance) {
            instance = new InventoryGroupDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return InventoryGroup.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public InventoryGroup cast(Object object) {
        return (InventoryGroup)object;
    }

    public InventoryGroup get(Integer key) throws HibernateException {
        return (InventoryGroup)this.get(this.getReferenceClass(), key);
    }

    public InventoryGroup get(Integer key, Session s) throws HibernateException {
        return (InventoryGroup)this.get(this.getReferenceClass(), key, s);
    }

    public InventoryGroup load(Integer key) throws HibernateException {
        return (InventoryGroup)this.load(this.getReferenceClass(), key);
    }

    public InventoryGroup load(Integer key, Session s) throws HibernateException {
        return (InventoryGroup)this.load(this.getReferenceClass(), key, s);
    }

    public InventoryGroup loadInitialize(Integer key, Session s) throws HibernateException {
        InventoryGroup obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<InventoryGroup> findAll() {
        return super.findAll();
    }

    @Override
    public List<InventoryGroup> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<InventoryGroup> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(InventoryGroup inventoryGroup) throws HibernateException {
        return (Integer)super.save(inventoryGroup);
    }

    public Integer save(InventoryGroup inventoryGroup, Session s) throws HibernateException {
        return (Integer)this.save((Object)inventoryGroup, s);
    }

    public void saveOrUpdate(InventoryGroup inventoryGroup) throws HibernateException {
        this.saveOrUpdate((Object)inventoryGroup);
    }

    public void saveOrUpdate(InventoryGroup inventoryGroup, Session s) throws HibernateException {
        this.saveOrUpdate((Object)inventoryGroup, s);
    }

    public void update(InventoryGroup inventoryGroup) throws HibernateException {
        this.update((Object)inventoryGroup);
    }

    public void update(InventoryGroup inventoryGroup, Session s) throws HibernateException {
        this.update((Object)inventoryGroup, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(InventoryGroup inventoryGroup) throws HibernateException {
        this.delete((Object)inventoryGroup);
    }

    public void delete(InventoryGroup inventoryGroup, Session s) throws HibernateException {
        this.delete((Object)inventoryGroup, s);
    }

    public void refresh(InventoryGroup inventoryGroup, Session s) throws HibernateException {
        this.refresh((Object)inventoryGroup, s);
    }
}

