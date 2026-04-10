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

import com.floreantpos.model.DeliveryCharge;
import com.floreantpos.model.dao.DeliveryChargeDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseDeliveryChargeDAO
extends _RootDAO {
    public static DeliveryChargeDAO instance;

    public static DeliveryChargeDAO getInstance() {
        if (null == instance) {
            instance = new DeliveryChargeDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return DeliveryCharge.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public DeliveryCharge cast(Object object) {
        return (DeliveryCharge)object;
    }

    public DeliveryCharge get(Integer key) throws HibernateException {
        return (DeliveryCharge)this.get(this.getReferenceClass(), key);
    }

    public DeliveryCharge get(Integer key, Session s) throws HibernateException {
        return (DeliveryCharge)this.get(this.getReferenceClass(), key, s);
    }

    public DeliveryCharge load(Integer key) throws HibernateException {
        return (DeliveryCharge)this.load(this.getReferenceClass(), key);
    }

    public DeliveryCharge load(Integer key, Session s) throws HibernateException {
        return (DeliveryCharge)this.load(this.getReferenceClass(), key, s);
    }

    public DeliveryCharge loadInitialize(Integer key, Session s) throws HibernateException {
        DeliveryCharge obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<DeliveryCharge> findAll() {
        return super.findAll();
    }

    @Override
    public List<DeliveryCharge> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<DeliveryCharge> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(DeliveryCharge deliveryCharge) throws HibernateException {
        return (Integer)super.save(deliveryCharge);
    }

    public Integer save(DeliveryCharge deliveryCharge, Session s) throws HibernateException {
        return (Integer)this.save((Object)deliveryCharge, s);
    }

    public void saveOrUpdate(DeliveryCharge deliveryCharge) throws HibernateException {
        this.saveOrUpdate((Object)deliveryCharge);
    }

    public void saveOrUpdate(DeliveryCharge deliveryCharge, Session s) throws HibernateException {
        this.saveOrUpdate((Object)deliveryCharge, s);
    }

    public void update(DeliveryCharge deliveryCharge) throws HibernateException {
        this.update((Object)deliveryCharge);
    }

    public void update(DeliveryCharge deliveryCharge, Session s) throws HibernateException {
        this.update((Object)deliveryCharge, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(DeliveryCharge deliveryCharge) throws HibernateException {
        this.delete((Object)deliveryCharge);
    }

    public void delete(DeliveryCharge deliveryCharge, Session s) throws HibernateException {
        this.delete((Object)deliveryCharge, s);
    }

    public void refresh(DeliveryCharge deliveryCharge, Session s) throws HibernateException {
        this.refresh((Object)deliveryCharge, s);
    }
}

