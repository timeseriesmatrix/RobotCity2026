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

import com.floreantpos.model.InventoryTransaction;
import com.floreantpos.model.dao.InventoryTransactionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseInventoryTransactionDAO
extends _RootDAO {
    public static InventoryTransactionDAO instance;

    public static InventoryTransactionDAO getInstance() {
        if (null == instance) {
            instance = new InventoryTransactionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return InventoryTransaction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public InventoryTransaction cast(Object object) {
        return (InventoryTransaction)object;
    }

    public InventoryTransaction get(Integer key) throws HibernateException {
        return (InventoryTransaction)this.get(this.getReferenceClass(), key);
    }

    public InventoryTransaction get(Integer key, Session s) throws HibernateException {
        return (InventoryTransaction)this.get(this.getReferenceClass(), key, s);
    }

    public InventoryTransaction load(Integer key) throws HibernateException {
        return (InventoryTransaction)this.load(this.getReferenceClass(), key);
    }

    public InventoryTransaction load(Integer key, Session s) throws HibernateException {
        return (InventoryTransaction)this.load(this.getReferenceClass(), key, s);
    }

    public InventoryTransaction loadInitialize(Integer key, Session s) throws HibernateException {
        InventoryTransaction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<InventoryTransaction> findAll() {
        return super.findAll();
    }

    @Override
    public List<InventoryTransaction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<InventoryTransaction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(InventoryTransaction inventoryTransaction) throws HibernateException {
        return (Integer)super.save(inventoryTransaction);
    }

    public Integer save(InventoryTransaction inventoryTransaction, Session s) throws HibernateException {
        return (Integer)this.save((Object)inventoryTransaction, s);
    }

    public void saveOrUpdate(InventoryTransaction inventoryTransaction) throws HibernateException {
        this.saveOrUpdate((Object)inventoryTransaction);
    }

    public void saveOrUpdate(InventoryTransaction inventoryTransaction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)inventoryTransaction, s);
    }

    public void update(InventoryTransaction inventoryTransaction) throws HibernateException {
        this.update((Object)inventoryTransaction);
    }

    public void update(InventoryTransaction inventoryTransaction, Session s) throws HibernateException {
        this.update((Object)inventoryTransaction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(InventoryTransaction inventoryTransaction) throws HibernateException {
        this.delete((Object)inventoryTransaction);
    }

    public void delete(InventoryTransaction inventoryTransaction, Session s) throws HibernateException {
        this.delete((Object)inventoryTransaction, s);
    }

    public void refresh(InventoryTransaction inventoryTransaction, Session s) throws HibernateException {
        this.refresh((Object)inventoryTransaction, s);
    }
}

