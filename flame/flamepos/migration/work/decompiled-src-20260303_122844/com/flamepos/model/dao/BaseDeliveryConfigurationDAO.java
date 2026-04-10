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

import com.floreantpos.model.DeliveryConfiguration;
import com.floreantpos.model.dao.DeliveryConfigurationDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseDeliveryConfigurationDAO
extends _RootDAO {
    public static DeliveryConfigurationDAO instance;

    public static DeliveryConfigurationDAO getInstance() {
        if (null == instance) {
            instance = new DeliveryConfigurationDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return DeliveryConfiguration.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public DeliveryConfiguration cast(Object object) {
        return (DeliveryConfiguration)object;
    }

    public DeliveryConfiguration get(Integer key) throws HibernateException {
        return (DeliveryConfiguration)this.get(this.getReferenceClass(), key);
    }

    public DeliveryConfiguration get(Integer key, Session s) throws HibernateException {
        return (DeliveryConfiguration)this.get(this.getReferenceClass(), key, s);
    }

    public DeliveryConfiguration load(Integer key) throws HibernateException {
        return (DeliveryConfiguration)this.load(this.getReferenceClass(), key);
    }

    public DeliveryConfiguration load(Integer key, Session s) throws HibernateException {
        return (DeliveryConfiguration)this.load(this.getReferenceClass(), key, s);
    }

    public DeliveryConfiguration loadInitialize(Integer key, Session s) throws HibernateException {
        DeliveryConfiguration obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<DeliveryConfiguration> findAll() {
        return super.findAll();
    }

    @Override
    public List<DeliveryConfiguration> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<DeliveryConfiguration> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(DeliveryConfiguration deliveryConfiguration) throws HibernateException {
        return (Integer)super.save(deliveryConfiguration);
    }

    public Integer save(DeliveryConfiguration deliveryConfiguration, Session s) throws HibernateException {
        return (Integer)this.save((Object)deliveryConfiguration, s);
    }

    public void saveOrUpdate(DeliveryConfiguration deliveryConfiguration) throws HibernateException {
        this.saveOrUpdate((Object)deliveryConfiguration);
    }

    public void saveOrUpdate(DeliveryConfiguration deliveryConfiguration, Session s) throws HibernateException {
        this.saveOrUpdate((Object)deliveryConfiguration, s);
    }

    public void update(DeliveryConfiguration deliveryConfiguration) throws HibernateException {
        this.update((Object)deliveryConfiguration);
    }

    public void update(DeliveryConfiguration deliveryConfiguration, Session s) throws HibernateException {
        this.update((Object)deliveryConfiguration, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(DeliveryConfiguration deliveryConfiguration) throws HibernateException {
        this.delete((Object)deliveryConfiguration);
    }

    public void delete(DeliveryConfiguration deliveryConfiguration, Session s) throws HibernateException {
        this.delete((Object)deliveryConfiguration, s);
    }

    public void refresh(DeliveryConfiguration deliveryConfiguration, Session s) throws HibernateException {
        this.refresh((Object)deliveryConfiguration, s);
    }
}

