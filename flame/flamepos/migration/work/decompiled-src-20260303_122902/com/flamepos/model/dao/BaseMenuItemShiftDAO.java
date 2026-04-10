/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.MenuItemShift;
import com.floreantpos.model.dao.MenuItemShiftDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseMenuItemShiftDAO
extends _RootDAO {
    public static MenuItemShiftDAO instance;

    public static MenuItemShiftDAO getInstance() {
        if (null == instance) {
            instance = new MenuItemShiftDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return MenuItemShift.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public MenuItemShift cast(Object object) {
        return (MenuItemShift)object;
    }

    public MenuItemShift get(Integer key) {
        return (MenuItemShift)this.get(this.getReferenceClass(), key);
    }

    public MenuItemShift get(Integer key, Session s) {
        return (MenuItemShift)this.get(this.getReferenceClass(), key, s);
    }

    public MenuItemShift load(Integer key) {
        return (MenuItemShift)this.load(this.getReferenceClass(), key);
    }

    public MenuItemShift load(Integer key, Session s) {
        return (MenuItemShift)this.load(this.getReferenceClass(), key, s);
    }

    public MenuItemShift loadInitialize(Integer key, Session s) {
        MenuItemShift obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<MenuItemShift> findAll() {
        return super.findAll();
    }

    @Override
    public List<MenuItemShift> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<MenuItemShift> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(MenuItemShift menuItemShift) {
        return (Integer)super.save(menuItemShift);
    }

    public Integer save(MenuItemShift menuItemShift, Session s) {
        return (Integer)this.save((Object)menuItemShift, s);
    }

    public void saveOrUpdate(MenuItemShift menuItemShift) {
        this.saveOrUpdate((Object)menuItemShift);
    }

    public void saveOrUpdate(MenuItemShift menuItemShift, Session s) {
        this.saveOrUpdate((Object)menuItemShift, s);
    }

    public void update(MenuItemShift menuItemShift) {
        this.update((Object)menuItemShift);
    }

    public void update(MenuItemShift menuItemShift, Session s) {
        this.update((Object)menuItemShift, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(MenuItemShift menuItemShift) {
        this.delete((Object)menuItemShift);
    }

    public void delete(MenuItemShift menuItemShift, Session s) {
        this.delete((Object)menuItemShift, s);
    }

    public void refresh(MenuItemShift menuItemShift, Session s) {
        this.refresh((Object)menuItemShift, s);
    }
}

