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

import com.floreantpos.model.InventoryItem;
import com.floreantpos.model.dao.InventoryItemDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseInventoryItemDAO
extends _RootDAO {
    public static InventoryItemDAO instance;

    public static InventoryItemDAO getInstance() {
        if (null == instance) {
            instance = new InventoryItemDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return InventoryItem.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public InventoryItem cast(Object object) {
        return (InventoryItem)object;
    }

    public InventoryItem get(Integer key) throws HibernateException {
        return (InventoryItem)this.get(this.getReferenceClass(), key);
    }

    public InventoryItem get(Integer key, Session s) throws HibernateException {
        return (InventoryItem)this.get(this.getReferenceClass(), key, s);
    }

    public InventoryItem load(Integer key) throws HibernateException {
        return (InventoryItem)this.load(this.getReferenceClass(), key);
    }

    public InventoryItem load(Integer key, Session s) throws HibernateException {
        return (InventoryItem)this.load(this.getReferenceClass(), key, s);
    }

    public InventoryItem loadInitialize(Integer key, Session s) throws HibernateException {
        InventoryItem obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<InventoryItem> findAll() {
        return super.findAll();
    }

    @Override
    public List<InventoryItem> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<InventoryItem> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(InventoryItem inventoryItem) throws HibernateException {
        return (Integer)super.save(inventoryItem);
    }

    public Integer save(InventoryItem inventoryItem, Session s) throws HibernateException {
        return (Integer)this.save((Object)inventoryItem, s);
    }

    public void saveOrUpdate(InventoryItem inventoryItem) throws HibernateException {
        this.saveOrUpdate((Object)inventoryItem);
    }

    public void saveOrUpdate(InventoryItem inventoryItem, Session s) throws HibernateException {
        this.saveOrUpdate((Object)inventoryItem, s);
    }

    public void update(InventoryItem inventoryItem) throws HibernateException {
        this.update((Object)inventoryItem);
    }

    public void update(InventoryItem inventoryItem, Session s) throws HibernateException {
        this.update((Object)inventoryItem, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(InventoryItem inventoryItem) throws HibernateException {
        this.delete((Object)inventoryItem);
    }

    public void delete(InventoryItem inventoryItem, Session s) throws HibernateException {
        this.delete((Object)inventoryItem, s);
    }

    public void refresh(InventoryItem inventoryItem, Session s) throws HibernateException {
        this.refresh((Object)inventoryItem, s);
    }
}

