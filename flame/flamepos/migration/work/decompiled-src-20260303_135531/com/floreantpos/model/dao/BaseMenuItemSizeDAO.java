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

import com.floreantpos.model.MenuItemSize;
import com.floreantpos.model.dao.MenuItemSizeDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseMenuItemSizeDAO
extends _RootDAO {
    public static MenuItemSizeDAO instance;

    public static MenuItemSizeDAO getInstance() {
        if (null == instance) {
            instance = new MenuItemSizeDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return MenuItemSize.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public MenuItemSize cast(Object object) {
        return (MenuItemSize)object;
    }

    public MenuItemSize get(Integer key) throws HibernateException {
        return (MenuItemSize)this.get(this.getReferenceClass(), key);
    }

    public MenuItemSize get(Integer key, Session s) throws HibernateException {
        return (MenuItemSize)this.get(this.getReferenceClass(), key, s);
    }

    public MenuItemSize load(Integer key) throws HibernateException {
        return (MenuItemSize)this.load(this.getReferenceClass(), key);
    }

    public MenuItemSize load(Integer key, Session s) throws HibernateException {
        return (MenuItemSize)this.load(this.getReferenceClass(), key, s);
    }

    public MenuItemSize loadInitialize(Integer key, Session s) throws HibernateException {
        MenuItemSize obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<MenuItemSize> findAll() {
        return super.findAll();
    }

    @Override
    public List<MenuItemSize> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<MenuItemSize> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(MenuItemSize menuItemSize) throws HibernateException {
        return (Integer)super.save(menuItemSize);
    }

    public Integer save(MenuItemSize menuItemSize, Session s) throws HibernateException {
        return (Integer)this.save((Object)menuItemSize, s);
    }

    public void saveOrUpdate(MenuItemSize menuItemSize) throws HibernateException {
        this.saveOrUpdate((Object)menuItemSize);
    }

    public void saveOrUpdate(MenuItemSize menuItemSize, Session s) throws HibernateException {
        this.saveOrUpdate((Object)menuItemSize, s);
    }

    public void update(MenuItemSize menuItemSize) throws HibernateException {
        this.update((Object)menuItemSize);
    }

    public void update(MenuItemSize menuItemSize, Session s) throws HibernateException {
        this.update((Object)menuItemSize, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(MenuItemSize menuItemSize) throws HibernateException {
        this.delete((Object)menuItemSize);
    }

    public void delete(MenuItemSize menuItemSize, Session s) throws HibernateException {
        this.delete((Object)menuItemSize, s);
    }

    public void refresh(MenuItemSize menuItemSize, Session s) throws HibernateException {
        this.refresh((Object)menuItemSize, s);
    }
}

