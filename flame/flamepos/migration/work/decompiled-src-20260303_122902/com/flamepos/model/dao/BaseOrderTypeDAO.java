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

import com.floreantpos.model.OrderType;
import com.floreantpos.model.dao.OrderTypeDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseOrderTypeDAO
extends _RootDAO {
    public static OrderTypeDAO instance;

    public static OrderTypeDAO getInstance() {
        if (null == instance) {
            instance = new OrderTypeDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return OrderType.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public OrderType cast(Object object) {
        return (OrderType)object;
    }

    public OrderType get(Integer key) throws HibernateException {
        return (OrderType)this.get(this.getReferenceClass(), key);
    }

    public OrderType get(Integer key, Session s) throws HibernateException {
        return (OrderType)this.get(this.getReferenceClass(), key, s);
    }

    public OrderType load(Integer key) throws HibernateException {
        return (OrderType)this.load(this.getReferenceClass(), key);
    }

    public OrderType load(Integer key, Session s) throws HibernateException {
        return (OrderType)this.load(this.getReferenceClass(), key, s);
    }

    public OrderType loadInitialize(Integer key, Session s) throws HibernateException {
        OrderType obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<OrderType> findAll() {
        return super.findAll();
    }

    @Override
    public List<OrderType> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<OrderType> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(OrderType orderType) throws HibernateException {
        return (Integer)super.save(orderType);
    }

    public Integer save(OrderType orderType, Session s) throws HibernateException {
        return (Integer)this.save((Object)orderType, s);
    }

    public void saveOrUpdate(OrderType orderType) throws HibernateException {
        this.saveOrUpdate((Object)orderType);
    }

    public void saveOrUpdate(OrderType orderType, Session s) throws HibernateException {
        this.saveOrUpdate((Object)orderType, s);
    }

    public void update(OrderType orderType) throws HibernateException {
        this.update((Object)orderType);
    }

    public void update(OrderType orderType, Session s) throws HibernateException {
        this.update((Object)orderType, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(OrderType orderType) throws HibernateException {
        this.delete((Object)orderType);
    }

    public void delete(OrderType orderType, Session s) throws HibernateException {
        this.delete((Object)orderType, s);
    }

    public void refresh(OrderType orderType, Session s) throws HibernateException {
        this.refresh((Object)orderType, s);
    }
}

