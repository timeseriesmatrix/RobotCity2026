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

import com.floreantpos.model.Restaurant;
import com.floreantpos.model.dao.RestaurantDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseRestaurantDAO
extends _RootDAO {
    public static RestaurantDAO instance;

    public static RestaurantDAO getInstance() {
        if (null == instance) {
            instance = new RestaurantDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Restaurant.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public Restaurant cast(Object object) {
        return (Restaurant)object;
    }

    public Restaurant get(Integer key) throws HibernateException {
        return (Restaurant)this.get(this.getReferenceClass(), key);
    }

    public Restaurant get(Integer key, Session s) throws HibernateException {
        return (Restaurant)this.get(this.getReferenceClass(), key, s);
    }

    public Restaurant load(Integer key) throws HibernateException {
        return (Restaurant)this.load(this.getReferenceClass(), key);
    }

    public Restaurant load(Integer key, Session s) throws HibernateException {
        return (Restaurant)this.load(this.getReferenceClass(), key, s);
    }

    public Restaurant loadInitialize(Integer key, Session s) throws HibernateException {
        Restaurant obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Restaurant> findAll() {
        return super.findAll();
    }

    @Override
    public List<Restaurant> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Restaurant> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(Restaurant restaurant) throws HibernateException {
        return (Integer)super.save(restaurant);
    }

    public Integer save(Restaurant restaurant, Session s) throws HibernateException {
        return (Integer)this.save((Object)restaurant, s);
    }

    public void saveOrUpdate(Restaurant restaurant) throws HibernateException {
        this.saveOrUpdate((Object)restaurant);
    }

    public void saveOrUpdate(Restaurant restaurant, Session s) throws HibernateException {
        this.saveOrUpdate((Object)restaurant, s);
    }

    public void update(Restaurant restaurant) throws HibernateException {
        this.update((Object)restaurant);
    }

    public void update(Restaurant restaurant, Session s) throws HibernateException {
        this.update((Object)restaurant, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Restaurant restaurant) throws HibernateException {
        this.delete((Object)restaurant);
    }

    public void delete(Restaurant restaurant, Session s) throws HibernateException {
        this.delete((Object)restaurant, s);
    }

    public void refresh(Restaurant restaurant, Session s) throws HibernateException {
        this.refresh((Object)restaurant, s);
    }
}

