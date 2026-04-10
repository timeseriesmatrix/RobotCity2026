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

import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.dao.MenuModifierGroupDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseMenuModifierGroupDAO
extends _RootDAO {
    public static MenuModifierGroupDAO instance;

    public static MenuModifierGroupDAO getInstance() {
        if (null == instance) {
            instance = new MenuModifierGroupDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return MenuModifierGroup.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public MenuModifierGroup cast(Object object) {
        return (MenuModifierGroup)object;
    }

    public MenuModifierGroup get(Integer key) throws HibernateException {
        return (MenuModifierGroup)this.get(this.getReferenceClass(), key);
    }

    public MenuModifierGroup get(Integer key, Session s) throws HibernateException {
        return (MenuModifierGroup)this.get(this.getReferenceClass(), key, s);
    }

    public MenuModifierGroup load(Integer key) throws HibernateException {
        return (MenuModifierGroup)this.load(this.getReferenceClass(), key);
    }

    public MenuModifierGroup load(Integer key, Session s) throws HibernateException {
        return (MenuModifierGroup)this.load(this.getReferenceClass(), key, s);
    }

    public MenuModifierGroup loadInitialize(Integer key, Session s) throws HibernateException {
        MenuModifierGroup obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<MenuModifierGroup> findAll() {
        return super.findAll();
    }

    @Override
    public List<MenuModifierGroup> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<MenuModifierGroup> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(MenuModifierGroup menuModifierGroup) throws HibernateException {
        return (Integer)super.save(menuModifierGroup);
    }

    public Integer save(MenuModifierGroup menuModifierGroup, Session s) throws HibernateException {
        return (Integer)this.save((Object)menuModifierGroup, s);
    }

    public void saveOrUpdate(MenuModifierGroup menuModifierGroup) throws HibernateException {
        this.saveOrUpdate((Object)menuModifierGroup);
    }

    public void saveOrUpdate(MenuModifierGroup menuModifierGroup, Session s) throws HibernateException {
        this.saveOrUpdate((Object)menuModifierGroup, s);
    }

    public void update(MenuModifierGroup menuModifierGroup) throws HibernateException {
        this.update((Object)menuModifierGroup);
    }

    public void update(MenuModifierGroup menuModifierGroup, Session s) throws HibernateException {
        this.update((Object)menuModifierGroup, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(MenuModifierGroup menuModifierGroup) throws HibernateException {
        this.delete((Object)menuModifierGroup);
    }

    public void delete(MenuModifierGroup menuModifierGroup, Session s) throws HibernateException {
        this.delete((Object)menuModifierGroup, s);
    }

    public void refresh(MenuModifierGroup menuModifierGroup, Session s) throws HibernateException {
        this.refresh((Object)menuModifierGroup, s);
    }
}

