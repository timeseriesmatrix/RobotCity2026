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

import com.floreantpos.model.MenuGroup;
import com.floreantpos.model.dao.MenuGroupDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseMenuGroupDAO
extends _RootDAO {
    public static MenuGroupDAO instance;

    public static MenuGroupDAO getInstance() {
        if (null == instance) {
            instance = new MenuGroupDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return MenuGroup.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public MenuGroup cast(Object object) {
        return (MenuGroup)object;
    }

    public MenuGroup get(Integer key) throws HibernateException {
        return (MenuGroup)this.get(this.getReferenceClass(), key);
    }

    public MenuGroup get(Integer key, Session s) throws HibernateException {
        return (MenuGroup)this.get(this.getReferenceClass(), key, s);
    }

    public MenuGroup load(Integer key) throws HibernateException {
        return (MenuGroup)this.load(this.getReferenceClass(), key);
    }

    public MenuGroup load(Integer key, Session s) throws HibernateException {
        return (MenuGroup)this.load(this.getReferenceClass(), key, s);
    }

    public MenuGroup loadInitialize(Integer key, Session s) throws HibernateException {
        MenuGroup obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<MenuGroup> findAll() {
        return super.findAll();
    }

    @Override
    public List<MenuGroup> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<MenuGroup> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(MenuGroup menuGroup) throws HibernateException {
        return (Integer)super.save(menuGroup);
    }

    public Integer save(MenuGroup menuGroup, Session s) throws HibernateException {
        return (Integer)this.save((Object)menuGroup, s);
    }

    public void saveOrUpdate(MenuGroup menuGroup) throws HibernateException {
        this.saveOrUpdate((Object)menuGroup);
    }

    public void saveOrUpdate(MenuGroup menuGroup, Session s) throws HibernateException {
        this.saveOrUpdate((Object)menuGroup, s);
    }

    public void update(MenuGroup menuGroup) throws HibernateException {
        this.update((Object)menuGroup);
    }

    public void update(MenuGroup menuGroup, Session s) throws HibernateException {
        this.update((Object)menuGroup, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(MenuGroup menuGroup) throws HibernateException {
        this.delete((Object)menuGroup);
    }

    public void delete(MenuGroup menuGroup, Session s) throws HibernateException {
        this.delete((Object)menuGroup, s);
    }

    public void refresh(MenuGroup menuGroup, Session s) throws HibernateException {
        this.refresh((Object)menuGroup, s);
    }
}

