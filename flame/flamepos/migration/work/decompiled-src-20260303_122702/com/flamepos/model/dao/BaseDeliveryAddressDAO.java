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

import com.floreantpos.model.DeliveryAddress;
import com.floreantpos.model.dao.DeliveryAddressDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseDeliveryAddressDAO
extends _RootDAO {
    public static DeliveryAddressDAO instance;

    public static DeliveryAddressDAO getInstance() {
        if (null == instance) {
            instance = new DeliveryAddressDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return DeliveryAddress.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public DeliveryAddress cast(Object object) {
        return (DeliveryAddress)object;
    }

    public DeliveryAddress get(Integer key) throws HibernateException {
        return (DeliveryAddress)this.get(this.getReferenceClass(), key);
    }

    public DeliveryAddress get(Integer key, Session s) throws HibernateException {
        return (DeliveryAddress)this.get(this.getReferenceClass(), key, s);
    }

    public DeliveryAddress load(Integer key) throws HibernateException {
        return (DeliveryAddress)this.load(this.getReferenceClass(), key);
    }

    public DeliveryAddress load(Integer key, Session s) throws HibernateException {
        return (DeliveryAddress)this.load(this.getReferenceClass(), key, s);
    }

    public DeliveryAddress loadInitialize(Integer key, Session s) throws HibernateException {
        DeliveryAddress obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<DeliveryAddress> findAll() {
        return super.findAll();
    }

    @Override
    public List<DeliveryAddress> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<DeliveryAddress> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(DeliveryAddress deliveryAddress) throws HibernateException {
        return (Integer)super.save(deliveryAddress);
    }

    public Integer save(DeliveryAddress deliveryAddress, Session s) throws HibernateException {
        return (Integer)this.save((Object)deliveryAddress, s);
    }

    public void saveOrUpdate(DeliveryAddress deliveryAddress) throws HibernateException {
        this.saveOrUpdate((Object)deliveryAddress);
    }

    public void saveOrUpdate(DeliveryAddress deliveryAddress, Session s) throws HibernateException {
        this.saveOrUpdate((Object)deliveryAddress, s);
    }

    public void update(DeliveryAddress deliveryAddress) throws HibernateException {
        this.update((Object)deliveryAddress);
    }

    public void update(DeliveryAddress deliveryAddress, Session s) throws HibernateException {
        this.update((Object)deliveryAddress, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(DeliveryAddress deliveryAddress) throws HibernateException {
        this.delete((Object)deliveryAddress);
    }

    public void delete(DeliveryAddress deliveryAddress, Session s) throws HibernateException {
        this.delete((Object)deliveryAddress, s);
    }

    public void refresh(DeliveryAddress deliveryAddress, Session s) throws HibernateException {
        this.refresh((Object)deliveryAddress, s);
    }
}

