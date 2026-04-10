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

import com.floreantpos.model.InventoryWarehouse;
import com.floreantpos.model.dao.InventoryWarehouseDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseInventoryWarehouseDAO
extends _RootDAO {
    public static InventoryWarehouseDAO instance;

    public static InventoryWarehouseDAO getInstance() {
        if (null == instance) {
            instance = new InventoryWarehouseDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return InventoryWarehouse.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public InventoryWarehouse cast(Object object) {
        return (InventoryWarehouse)object;
    }

    public InventoryWarehouse get(Integer key) throws HibernateException {
        return (InventoryWarehouse)this.get(this.getReferenceClass(), key);
    }

    public InventoryWarehouse get(Integer key, Session s) throws HibernateException {
        return (InventoryWarehouse)this.get(this.getReferenceClass(), key, s);
    }

    public InventoryWarehouse load(Integer key) throws HibernateException {
        return (InventoryWarehouse)this.load(this.getReferenceClass(), key);
    }

    public InventoryWarehouse load(Integer key, Session s) throws HibernateException {
        return (InventoryWarehouse)this.load(this.getReferenceClass(), key, s);
    }

    public InventoryWarehouse loadInitialize(Integer key, Session s) throws HibernateException {
        InventoryWarehouse obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<InventoryWarehouse> findAll() {
        return super.findAll();
    }

    @Override
    public List<InventoryWarehouse> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<InventoryWarehouse> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(InventoryWarehouse inventoryWarehouse) throws HibernateException {
        return (Integer)super.save(inventoryWarehouse);
    }

    public Integer save(InventoryWarehouse inventoryWarehouse, Session s) throws HibernateException {
        return (Integer)this.save((Object)inventoryWarehouse, s);
    }

    public void saveOrUpdate(InventoryWarehouse inventoryWarehouse) throws HibernateException {
        this.saveOrUpdate((Object)inventoryWarehouse);
    }

    public void saveOrUpdate(InventoryWarehouse inventoryWarehouse, Session s) throws HibernateException {
        this.saveOrUpdate((Object)inventoryWarehouse, s);
    }

    public void update(InventoryWarehouse inventoryWarehouse) throws HibernateException {
        this.update((Object)inventoryWarehouse);
    }

    public void update(InventoryWarehouse inventoryWarehouse, Session s) throws HibernateException {
        this.update((Object)inventoryWarehouse, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(InventoryWarehouse inventoryWarehouse) throws HibernateException {
        this.delete((Object)inventoryWarehouse);
    }

    public void delete(InventoryWarehouse inventoryWarehouse, Session s) throws HibernateException {
        this.delete((Object)inventoryWarehouse, s);
    }

    public void refresh(InventoryWarehouse inventoryWarehouse, Session s) throws HibernateException {
        this.refresh((Object)inventoryWarehouse, s);
    }
}

