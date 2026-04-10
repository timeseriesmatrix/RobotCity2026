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

import com.floreantpos.model.DeliveryInstruction;
import com.floreantpos.model.dao.DeliveryInstructionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseDeliveryInstructionDAO
extends _RootDAO {
    public static DeliveryInstructionDAO instance;

    public static DeliveryInstructionDAO getInstance() {
        if (null == instance) {
            instance = new DeliveryInstructionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return DeliveryInstruction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public DeliveryInstruction cast(Object object) {
        return (DeliveryInstruction)object;
    }

    public DeliveryInstruction get(Integer key) throws HibernateException {
        return (DeliveryInstruction)this.get(this.getReferenceClass(), key);
    }

    public DeliveryInstruction get(Integer key, Session s) throws HibernateException {
        return (DeliveryInstruction)this.get(this.getReferenceClass(), key, s);
    }

    public DeliveryInstruction load(Integer key) throws HibernateException {
        return (DeliveryInstruction)this.load(this.getReferenceClass(), key);
    }

    public DeliveryInstruction load(Integer key, Session s) throws HibernateException {
        return (DeliveryInstruction)this.load(this.getReferenceClass(), key, s);
    }

    public DeliveryInstruction loadInitialize(Integer key, Session s) throws HibernateException {
        DeliveryInstruction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<DeliveryInstruction> findAll() {
        return super.findAll();
    }

    @Override
    public List<DeliveryInstruction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<DeliveryInstruction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(DeliveryInstruction deliveryInstruction) throws HibernateException {
        return (Integer)super.save(deliveryInstruction);
    }

    public Integer save(DeliveryInstruction deliveryInstruction, Session s) throws HibernateException {
        return (Integer)this.save((Object)deliveryInstruction, s);
    }

    public void saveOrUpdate(DeliveryInstruction deliveryInstruction) throws HibernateException {
        this.saveOrUpdate((Object)deliveryInstruction);
    }

    public void saveOrUpdate(DeliveryInstruction deliveryInstruction, Session s) throws HibernateException {
        this.saveOrUpdate((Object)deliveryInstruction, s);
    }

    public void update(DeliveryInstruction deliveryInstruction) throws HibernateException {
        this.update((Object)deliveryInstruction);
    }

    public void update(DeliveryInstruction deliveryInstruction, Session s) throws HibernateException {
        this.update((Object)deliveryInstruction, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(DeliveryInstruction deliveryInstruction) throws HibernateException {
        this.delete((Object)deliveryInstruction);
    }

    public void delete(DeliveryInstruction deliveryInstruction, Session s) throws HibernateException {
        this.delete((Object)deliveryInstruction, s);
    }

    public void refresh(DeliveryInstruction deliveryInstruction, Session s) throws HibernateException {
        this.refresh((Object)deliveryInstruction, s);
    }
}

