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

import com.floreantpos.model.InventoryUnit;
import com.floreantpos.model.dao.InventoryUnitDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseInventoryUnitDAO
extends _RootDAO {
    public static InventoryUnitDAO instance;

    public static InventoryUnitDAO getInstance() {
        if (null == instance) {
            instance = new InventoryUnitDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return InventoryUnit.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public InventoryUnit cast(Object object) {
        return (InventoryUnit)object;
    }

    public InventoryUnit get(Integer key) throws HibernateException {
        return (InventoryUnit)this.get(this.getReferenceClass(), key);
    }

    public InventoryUnit get(Integer key, Session s) throws HibernateException {
        return (InventoryUnit)this.get(this.getReferenceClass(), key, s);
    }

    public InventoryUnit load(Integer key) throws HibernateException {
        return (InventoryUnit)this.load(this.getReferenceClass(), key);
    }

    public InventoryUnit load(Integer key, Session s) throws HibernateException {
        return (InventoryUnit)this.load(this.getReferenceClass(), key, s);
    }

    public InventoryUnit loadInitialize(Integer key, Session s) throws HibernateException {
        InventoryUnit obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<InventoryUnit> findAll() {
        return super.findAll();
    }

    @Override
    public List<InventoryUnit> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<InventoryUnit> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(InventoryUnit inventoryUnit) throws HibernateException {
        return (Integer)super.save(inventoryUnit);
    }

    public Integer save(InventoryUnit inventoryUnit, Session s) throws HibernateException {
        return (Integer)this.save((Object)inventoryUnit, s);
    }

    public void saveOrUpdate(InventoryUnit inventoryUnit) throws HibernateException {
        this.saveOrUpdate((Object)inventoryUnit);
    }

    public void saveOrUpdate(InventoryUnit inventoryUnit, Session s) throws HibernateException {
        this.saveOrUpdate((Object)inventoryUnit, s);
    }

    public void update(InventoryUnit inventoryUnit) throws HibernateException {
        this.update((Object)inventoryUnit);
    }

    public void update(InventoryUnit inventoryUnit, Session s) throws HibernateException {
        this.update((Object)inventoryUnit, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(InventoryUnit inventoryUnit) throws HibernateException {
        this.delete((Object)inventoryUnit);
    }

    public void delete(InventoryUnit inventoryUnit, Session s) throws HibernateException {
        this.delete((Object)inventoryUnit, s);
    }

    public void refresh(InventoryUnit inventoryUnit, Session s) throws HibernateException {
        this.refresh((Object)inventoryUnit, s);
    }
}

