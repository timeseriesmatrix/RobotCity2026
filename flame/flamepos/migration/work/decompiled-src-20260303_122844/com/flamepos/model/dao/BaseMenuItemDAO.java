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

import com.floreantpos.model.MenuItem;
import com.floreantpos.model.dao.MenuItemDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseMenuItemDAO
extends _RootDAO {
    public static MenuItemDAO instance;

    public static MenuItemDAO getInstance() {
        if (null == instance) {
            instance = new MenuItemDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return MenuItem.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public MenuItem cast(Object object) {
        return (MenuItem)object;
    }

    public MenuItem get(Integer key) throws HibernateException {
        return (MenuItem)this.get(this.getReferenceClass(), key);
    }

    public MenuItem get(Integer key, Session s) throws HibernateException {
        return (MenuItem)this.get(this.getReferenceClass(), key, s);
    }

    public MenuItem load(Integer key) throws HibernateException {
        return (MenuItem)this.load(this.getReferenceClass(), key);
    }

    public MenuItem load(Integer key, Session s) throws HibernateException {
        return (MenuItem)this.load(this.getReferenceClass(), key, s);
    }

    public MenuItem loadInitialize(Integer key, Session s) throws HibernateException {
        MenuItem obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<MenuItem> findAll() {
        return super.findAll();
    }

    @Override
    public List<MenuItem> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<MenuItem> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(MenuItem menuItem) throws HibernateException {
        return (Integer)super.save(menuItem);
    }

    public Integer save(MenuItem menuItem, Session s) throws HibernateException {
        return (Integer)this.save((Object)menuItem, s);
    }

    public void saveOrUpdate(MenuItem menuItem) throws HibernateException {
        this.saveOrUpdate((Object)menuItem);
    }

    public void saveOrUpdate(MenuItem menuItem, Session s) throws HibernateException {
        this.saveOrUpdate((Object)menuItem, s);
    }

    public void update(MenuItem menuItem) throws HibernateException {
        this.update((Object)menuItem);
    }

    public void update(MenuItem menuItem, Session s) throws HibernateException {
        this.update((Object)menuItem, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(MenuItem menuItem) throws HibernateException {
        this.delete((Object)menuItem);
    }

    public void delete(MenuItem menuItem, Session s) throws HibernateException {
        this.delete((Object)menuItem, s);
    }

    public void refresh(MenuItem menuItem, Session s) throws HibernateException {
        this.refresh((Object)menuItem, s);
    }
}

