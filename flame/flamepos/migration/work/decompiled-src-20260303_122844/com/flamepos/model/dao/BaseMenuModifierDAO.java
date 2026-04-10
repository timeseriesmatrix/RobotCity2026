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

import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.dao.MenuModifierDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseMenuModifierDAO
extends _RootDAO {
    public static MenuModifierDAO instance;

    public static MenuModifierDAO getInstance() {
        if (null == instance) {
            instance = new MenuModifierDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return MenuModifier.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public MenuModifier cast(Object object) {
        return (MenuModifier)object;
    }

    public MenuModifier get(Integer key) throws HibernateException {
        return (MenuModifier)this.get(this.getReferenceClass(), key);
    }

    public MenuModifier get(Integer key, Session s) throws HibernateException {
        return (MenuModifier)this.get(this.getReferenceClass(), key, s);
    }

    public MenuModifier load(Integer key) throws HibernateException {
        return (MenuModifier)this.load(this.getReferenceClass(), key);
    }

    public MenuModifier load(Integer key, Session s) throws HibernateException {
        return (MenuModifier)this.load(this.getReferenceClass(), key, s);
    }

    public MenuModifier loadInitialize(Integer key, Session s) throws HibernateException {
        MenuModifier obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<MenuModifier> findAll() {
        return super.findAll();
    }

    @Override
    public List<MenuModifier> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<MenuModifier> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(MenuModifier menuModifier) throws HibernateException {
        return (Integer)super.save(menuModifier);
    }

    public Integer save(MenuModifier menuModifier, Session s) throws HibernateException {
        return (Integer)this.save((Object)menuModifier, s);
    }

    public void saveOrUpdate(MenuModifier menuModifier) throws HibernateException {
        this.saveOrUpdate((Object)menuModifier);
    }

    public void saveOrUpdate(MenuModifier menuModifier, Session s) throws HibernateException {
        this.saveOrUpdate((Object)menuModifier, s);
    }

    public void update(MenuModifier menuModifier) throws HibernateException {
        this.update((Object)menuModifier);
    }

    public void update(MenuModifier menuModifier, Session s) throws HibernateException {
        this.update((Object)menuModifier, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(MenuModifier menuModifier) throws HibernateException {
        this.delete((Object)menuModifier);
    }

    public void delete(MenuModifier menuModifier, Session s) throws HibernateException {
        this.delete((Object)menuModifier, s);
    }

    public void refresh(MenuModifier menuModifier, Session s) throws HibernateException {
        this.refresh((Object)menuModifier, s);
    }
}

