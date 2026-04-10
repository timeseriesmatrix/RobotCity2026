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

import com.floreantpos.model.PurchaseOrder;
import com.floreantpos.model.dao.PurchaseOrderDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePurchaseOrderDAO
extends _RootDAO {
    public static PurchaseOrderDAO instance;

    public static PurchaseOrderDAO getInstance() {
        if (null == instance) {
            instance = new PurchaseOrderDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PurchaseOrder.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public PurchaseOrder cast(Object object) {
        return (PurchaseOrder)object;
    }

    public PurchaseOrder get(Integer key) throws HibernateException {
        return (PurchaseOrder)this.get(this.getReferenceClass(), key);
    }

    public PurchaseOrder get(Integer key, Session s) throws HibernateException {
        return (PurchaseOrder)this.get(this.getReferenceClass(), key, s);
    }

    public PurchaseOrder load(Integer key) throws HibernateException {
        return (PurchaseOrder)this.load(this.getReferenceClass(), key);
    }

    public PurchaseOrder load(Integer key, Session s) throws HibernateException {
        return (PurchaseOrder)this.load(this.getReferenceClass(), key, s);
    }

    public PurchaseOrder loadInitialize(Integer key, Session s) throws HibernateException {
        PurchaseOrder obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PurchaseOrder> findAll() {
        return super.findAll();
    }

    @Override
    public List<PurchaseOrder> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PurchaseOrder> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PurchaseOrder purchaseOrder) throws HibernateException {
        return (Integer)super.save(purchaseOrder);
    }

    public Integer save(PurchaseOrder purchaseOrder, Session s) throws HibernateException {
        return (Integer)this.save((Object)purchaseOrder, s);
    }

    public void saveOrUpdate(PurchaseOrder purchaseOrder) throws HibernateException {
        this.saveOrUpdate((Object)purchaseOrder);
    }

    public void saveOrUpdate(PurchaseOrder purchaseOrder, Session s) throws HibernateException {
        this.saveOrUpdate((Object)purchaseOrder, s);
    }

    public void update(PurchaseOrder purchaseOrder) throws HibernateException {
        this.update((Object)purchaseOrder);
    }

    public void update(PurchaseOrder purchaseOrder, Session s) throws HibernateException {
        this.update((Object)purchaseOrder, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PurchaseOrder purchaseOrder) throws HibernateException {
        this.delete((Object)purchaseOrder);
    }

    public void delete(PurchaseOrder purchaseOrder, Session s) throws HibernateException {
        this.delete((Object)purchaseOrder, s);
    }

    public void refresh(PurchaseOrder purchaseOrder, Session s) throws HibernateException {
        this.refresh((Object)purchaseOrder, s);
    }
}

