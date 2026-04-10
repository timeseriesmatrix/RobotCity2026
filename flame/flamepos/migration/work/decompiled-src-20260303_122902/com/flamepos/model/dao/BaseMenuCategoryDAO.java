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

import com.floreantpos.model.MenuCategory;
import com.floreantpos.model.dao.MenuCategoryDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseMenuCategoryDAO
extends _RootDAO {
    public static MenuCategoryDAO instance;

    public static MenuCategoryDAO getInstance() {
        if (null == instance) {
            instance = new MenuCategoryDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return MenuCategory.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public MenuCategory cast(Object object) {
        return (MenuCategory)object;
    }

    public MenuCategory get(Integer key) throws HibernateException {
        return (MenuCategory)this.get(this.getReferenceClass(), key);
    }

    public MenuCategory get(Integer key, Session s) throws HibernateException {
        return (MenuCategory)this.get(this.getReferenceClass(), key, s);
    }

    public MenuCategory load(Integer key) throws HibernateException {
        return (MenuCategory)this.load(this.getReferenceClass(), key);
    }

    public MenuCategory load(Integer key, Session s) throws HibernateException {
        return (MenuCategory)this.load(this.getReferenceClass(), key, s);
    }

    public MenuCategory loadInitialize(Integer key, Session s) throws HibernateException {
        MenuCategory obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<MenuCategory> findAll() {
        return super.findAll();
    }

    @Override
    public List<MenuCategory> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<MenuCategory> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(MenuCategory menuCategory) throws HibernateException {
        return (Integer)super.save(menuCategory);
    }

    public Integer save(MenuCategory menuCategory, Session s) throws HibernateException {
        return (Integer)this.save((Object)menuCategory, s);
    }

    public void saveOrUpdate(MenuCategory menuCategory) throws HibernateException {
        this.saveOrUpdate((Object)menuCategory);
    }

    public void saveOrUpdate(MenuCategory menuCategory, Session s) throws HibernateException {
        this.saveOrUpdate((Object)menuCategory, s);
    }

    public void update(MenuCategory menuCategory) throws HibernateException {
        this.update((Object)menuCategory);
    }

    public void update(MenuCategory menuCategory, Session s) throws HibernateException {
        this.update((Object)menuCategory, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(MenuCategory menuCategory) throws HibernateException {
        this.delete((Object)menuCategory);
    }

    public void delete(MenuCategory menuCategory, Session s) throws HibernateException {
        this.delete((Object)menuCategory, s);
    }

    public void refresh(MenuCategory menuCategory, Session s) throws HibernateException {
        this.refresh((Object)menuCategory, s);
    }
}

