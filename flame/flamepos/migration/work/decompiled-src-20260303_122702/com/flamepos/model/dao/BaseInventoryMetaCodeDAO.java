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

import com.floreantpos.model.InventoryMetaCode;
import com.floreantpos.model.dao.InventoryMetaCodeDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseInventoryMetaCodeDAO
extends _RootDAO {
    public static InventoryMetaCodeDAO instance;

    public static InventoryMetaCodeDAO getInstance() {
        if (null == instance) {
            instance = new InventoryMetaCodeDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return InventoryMetaCode.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public InventoryMetaCode cast(Object object) {
        return (InventoryMetaCode)object;
    }

    public InventoryMetaCode get(Integer key) throws HibernateException {
        return (InventoryMetaCode)this.get(this.getReferenceClass(), key);
    }

    public InventoryMetaCode get(Integer key, Session s) throws HibernateException {
        return (InventoryMetaCode)this.get(this.getReferenceClass(), key, s);
    }

    public InventoryMetaCode load(Integer key) throws HibernateException {
        return (InventoryMetaCode)this.load(this.getReferenceClass(), key);
    }

    public InventoryMetaCode load(Integer key, Session s) throws HibernateException {
        return (InventoryMetaCode)this.load(this.getReferenceClass(), key, s);
    }

    public InventoryMetaCode loadInitialize(Integer key, Session s) throws HibernateException {
        InventoryMetaCode obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<InventoryMetaCode> findAll() {
        return super.findAll();
    }

    @Override
    public List<InventoryMetaCode> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<InventoryMetaCode> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(InventoryMetaCode inventoryMetaCode) throws HibernateException {
        return (Integer)super.save(inventoryMetaCode);
    }

    public Integer save(InventoryMetaCode inventoryMetaCode, Session s) throws HibernateException {
        return (Integer)this.save((Object)inventoryMetaCode, s);
    }

    public void saveOrUpdate(InventoryMetaCode inventoryMetaCode) throws HibernateException {
        this.saveOrUpdate((Object)inventoryMetaCode);
    }

    public void saveOrUpdate(InventoryMetaCode inventoryMetaCode, Session s) throws HibernateException {
        this.saveOrUpdate((Object)inventoryMetaCode, s);
    }

    public void update(InventoryMetaCode inventoryMetaCode) throws HibernateException {
        this.update((Object)inventoryMetaCode);
    }

    public void update(InventoryMetaCode inventoryMetaCode, Session s) throws HibernateException {
        this.update((Object)inventoryMetaCode, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(InventoryMetaCode inventoryMetaCode) throws HibernateException {
        this.delete((Object)inventoryMetaCode);
    }

    public void delete(InventoryMetaCode inventoryMetaCode, Session s) throws HibernateException {
        this.delete((Object)inventoryMetaCode, s);
    }

    public void refresh(InventoryMetaCode inventoryMetaCode, Session s) throws HibernateException {
        this.refresh((Object)inventoryMetaCode, s);
    }
}

