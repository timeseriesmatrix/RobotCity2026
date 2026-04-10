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

import com.floreantpos.model.MenuItemModifierGroup;
import com.floreantpos.model.dao.MenuItemModifierGroupDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseMenuItemModifierGroupDAO
extends _RootDAO {
    public static MenuItemModifierGroupDAO instance;

    public static MenuItemModifierGroupDAO getInstance() {
        if (null == instance) {
            instance = new MenuItemModifierGroupDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return MenuItemModifierGroup.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public MenuItemModifierGroup cast(Object object) {
        return (MenuItemModifierGroup)object;
    }

    public MenuItemModifierGroup get(Integer key) throws HibernateException {
        return (MenuItemModifierGroup)this.get(this.getReferenceClass(), key);
    }

    public MenuItemModifierGroup get(Integer key, Session s) throws HibernateException {
        return (MenuItemModifierGroup)this.get(this.getReferenceClass(), key, s);
    }

    public MenuItemModifierGroup load(Integer key) throws HibernateException {
        return (MenuItemModifierGroup)this.load(this.getReferenceClass(), key);
    }

    public MenuItemModifierGroup load(Integer key, Session s) throws HibernateException {
        return (MenuItemModifierGroup)this.load(this.getReferenceClass(), key, s);
    }

    public MenuItemModifierGroup loadInitialize(Integer key, Session s) throws HibernateException {
        MenuItemModifierGroup obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<MenuItemModifierGroup> findAll() {
        return super.findAll();
    }

    @Override
    public List<MenuItemModifierGroup> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<MenuItemModifierGroup> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(MenuItemModifierGroup menuItemModifierGroup) throws HibernateException {
        return (Integer)super.save(menuItemModifierGroup);
    }

    public Integer save(MenuItemModifierGroup menuItemModifierGroup, Session s) throws HibernateException {
        return (Integer)this.save((Object)menuItemModifierGroup, s);
    }

    public void saveOrUpdate(MenuItemModifierGroup menuItemModifierGroup) throws HibernateException {
        this.saveOrUpdate((Object)menuItemModifierGroup);
    }

    public void saveOrUpdate(MenuItemModifierGroup menuItemModifierGroup, Session s) throws HibernateException {
        this.saveOrUpdate((Object)menuItemModifierGroup, s);
    }

    public void update(MenuItemModifierGroup menuItemModifierGroup) throws HibernateException {
        this.update((Object)menuItemModifierGroup);
    }

    public void update(MenuItemModifierGroup menuItemModifierGroup, Session s) throws HibernateException {
        this.update((Object)menuItemModifierGroup, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(MenuItemModifierGroup menuItemModifierGroup) throws HibernateException {
        this.delete((Object)menuItemModifierGroup);
    }

    public void delete(MenuItemModifierGroup menuItemModifierGroup, Session s) throws HibernateException {
        this.delete((Object)menuItemModifierGroup, s);
    }

    public void refresh(MenuItemModifierGroup menuItemModifierGroup, Session s) throws HibernateException {
        this.refresh((Object)menuItemModifierGroup, s);
    }
}

