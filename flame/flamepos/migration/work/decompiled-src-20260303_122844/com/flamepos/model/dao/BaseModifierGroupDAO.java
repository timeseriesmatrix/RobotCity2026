/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.MenuModifierGroup;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseModifierGroupDAO
extends _RootDAO {
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

    public MenuModifierGroup get(Integer key) {
        return (MenuModifierGroup)this.get(this.getReferenceClass(), key);
    }

    public MenuModifierGroup get(Integer key, Session s) {
        return (MenuModifierGroup)this.get(this.getReferenceClass(), key, s);
    }

    public MenuModifierGroup load(Integer key) {
        return (MenuModifierGroup)this.load(this.getReferenceClass(), key);
    }

    public MenuModifierGroup load(Integer key, Session s) {
        return (MenuModifierGroup)this.load(this.getReferenceClass(), key, s);
    }

    public MenuModifierGroup loadInitialize(Integer key, Session s) {
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

    public Integer save(MenuModifierGroup modifierGroup) {
        return (Integer)super.save(modifierGroup);
    }

    public Integer save(MenuModifierGroup modifierGroup, Session s) {
        return (Integer)this.save((Object)modifierGroup, s);
    }

    public void saveOrUpdate(MenuModifierGroup modifierGroup) {
        this.saveOrUpdate((Object)modifierGroup);
    }

    public void saveOrUpdate(MenuModifierGroup modifierGroup, Session s) {
        this.saveOrUpdate((Object)modifierGroup, s);
    }

    public void update(MenuModifierGroup modifierGroup) {
        this.update((Object)modifierGroup);
    }

    public void update(MenuModifierGroup modifierGroup, Session s) {
        this.update((Object)modifierGroup, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(MenuModifierGroup modifierGroup) {
        this.delete((Object)modifierGroup);
    }

    public void delete(MenuModifierGroup modifierGroup, Session s) {
        this.delete((Object)modifierGroup, s);
    }

    public void refresh(MenuModifierGroup modifierGroup, Session s) {
        this.refresh((Object)modifierGroup, s);
    }
}

