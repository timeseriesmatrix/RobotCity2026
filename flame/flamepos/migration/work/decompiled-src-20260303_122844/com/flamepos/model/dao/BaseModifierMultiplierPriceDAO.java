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

import com.floreantpos.model.ModifierMultiplierPrice;
import com.floreantpos.model.dao.ModifierMultiplierPriceDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseModifierMultiplierPriceDAO
extends _RootDAO {
    public static ModifierMultiplierPriceDAO instance;

    public static ModifierMultiplierPriceDAO getInstance() {
        if (null == instance) {
            instance = new ModifierMultiplierPriceDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return ModifierMultiplierPrice.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public ModifierMultiplierPrice cast(Object object) {
        return (ModifierMultiplierPrice)object;
    }

    public ModifierMultiplierPrice get(Integer key) throws HibernateException {
        return (ModifierMultiplierPrice)this.get(this.getReferenceClass(), key);
    }

    public ModifierMultiplierPrice get(Integer key, Session s) throws HibernateException {
        return (ModifierMultiplierPrice)this.get(this.getReferenceClass(), key, s);
    }

    public ModifierMultiplierPrice load(Integer key) throws HibernateException {
        return (ModifierMultiplierPrice)this.load(this.getReferenceClass(), key);
    }

    public ModifierMultiplierPrice load(Integer key, Session s) throws HibernateException {
        return (ModifierMultiplierPrice)this.load(this.getReferenceClass(), key, s);
    }

    public ModifierMultiplierPrice loadInitialize(Integer key, Session s) throws HibernateException {
        ModifierMultiplierPrice obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<ModifierMultiplierPrice> findAll() {
        return super.findAll();
    }

    @Override
    public List<ModifierMultiplierPrice> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<ModifierMultiplierPrice> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(ModifierMultiplierPrice modifierMultiplierPrice) throws HibernateException {
        return (Integer)super.save(modifierMultiplierPrice);
    }

    public Integer save(ModifierMultiplierPrice modifierMultiplierPrice, Session s) throws HibernateException {
        return (Integer)this.save((Object)modifierMultiplierPrice, s);
    }

    public void saveOrUpdate(ModifierMultiplierPrice modifierMultiplierPrice) throws HibernateException {
        this.saveOrUpdate((Object)modifierMultiplierPrice);
    }

    public void saveOrUpdate(ModifierMultiplierPrice modifierMultiplierPrice, Session s) throws HibernateException {
        this.saveOrUpdate((Object)modifierMultiplierPrice, s);
    }

    public void update(ModifierMultiplierPrice modifierMultiplierPrice) throws HibernateException {
        this.update((Object)modifierMultiplierPrice);
    }

    public void update(ModifierMultiplierPrice modifierMultiplierPrice, Session s) throws HibernateException {
        this.update((Object)modifierMultiplierPrice, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(ModifierMultiplierPrice modifierMultiplierPrice) throws HibernateException {
        this.delete((Object)modifierMultiplierPrice);
    }

    public void delete(ModifierMultiplierPrice modifierMultiplierPrice, Session s) throws HibernateException {
        this.delete((Object)modifierMultiplierPrice, s);
    }

    public void refresh(ModifierMultiplierPrice modifierMultiplierPrice, Session s) throws HibernateException {
        this.refresh((Object)modifierMultiplierPrice, s);
    }
}

