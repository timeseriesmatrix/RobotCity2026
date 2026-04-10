/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.MenuModifier;
import com.floreantpos.model.dao.ModifierDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseModifierDAO
extends _RootDAO {
    public static ModifierDAO instance;

    public static ModifierDAO getInstance() {
        if (null == instance) {
            instance = new ModifierDAO();
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

    public MenuModifier get(Integer key) {
        return (MenuModifier)this.get(this.getReferenceClass(), key);
    }

    public MenuModifier get(Integer key, Session s) {
        return (MenuModifier)this.get(this.getReferenceClass(), key, s);
    }

    public MenuModifier load(Integer key) {
        return (MenuModifier)this.load(this.getReferenceClass(), key);
    }

    public MenuModifier load(Integer key, Session s) {
        return (MenuModifier)this.load(this.getReferenceClass(), key, s);
    }

    public MenuModifier loadInitialize(Integer key, Session s) {
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

    public Integer save(MenuModifier modifier) {
        return (Integer)super.save(modifier);
    }

    public Integer save(MenuModifier modifier, Session s) {
        return (Integer)this.save((Object)modifier, s);
    }

    public void saveOrUpdate(MenuModifier modifier) {
        this.saveOrUpdate((Object)modifier);
    }

    public void saveOrUpdate(MenuModifier modifier, Session s) {
        this.saveOrUpdate((Object)modifier, s);
    }

    public void update(MenuModifier modifier) {
        this.update((Object)modifier);
    }

    public void update(MenuModifier modifier, Session s) {
        this.update((Object)modifier, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(MenuModifier modifier) {
        this.delete((Object)modifier);
    }

    public void delete(MenuModifier modifier, Session s) {
        this.delete((Object)modifier, s);
    }

    public void refresh(MenuModifier modifier, Session s) {
        this.refresh((Object)modifier, s);
    }
}

