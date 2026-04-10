/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.CookingInstruction;
import com.floreantpos.model.dao.CookingInstructionDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseCookingInstructionDAO
extends _RootDAO {
    public static CookingInstructionDAO instance;

    public static CookingInstructionDAO getInstance() {
        if (null == instance) {
            instance = new CookingInstructionDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return CookingInstruction.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public CookingInstruction cast(Object object) {
        return (CookingInstruction)object;
    }

    public CookingInstruction get(Integer key) {
        return (CookingInstruction)this.get(this.getReferenceClass(), key);
    }

    public CookingInstruction get(Integer key, Session s) {
        return (CookingInstruction)this.get(this.getReferenceClass(), key, s);
    }

    public CookingInstruction load(Integer key) {
        return (CookingInstruction)this.load(this.getReferenceClass(), key);
    }

    public CookingInstruction load(Integer key, Session s) {
        return (CookingInstruction)this.load(this.getReferenceClass(), key, s);
    }

    public CookingInstruction loadInitialize(Integer key, Session s) {
        CookingInstruction obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<CookingInstruction> findAll() {
        return super.findAll();
    }

    @Override
    public List<CookingInstruction> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<CookingInstruction> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(CookingInstruction cookingInstruction) {
        return (Integer)super.save(cookingInstruction);
    }

    public Integer save(CookingInstruction cookingInstruction, Session s) {
        return (Integer)this.save((Object)cookingInstruction, s);
    }

    public void saveOrUpdate(CookingInstruction cookingInstruction) {
        this.saveOrUpdate((Object)cookingInstruction);
    }

    public void saveOrUpdate(CookingInstruction cookingInstruction, Session s) {
        this.saveOrUpdate((Object)cookingInstruction, s);
    }

    public void update(CookingInstruction cookingInstruction) {
        this.update((Object)cookingInstruction);
    }

    public void update(CookingInstruction cookingInstruction, Session s) {
        this.update((Object)cookingInstruction, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(CookingInstruction cookingInstruction) {
        this.delete((Object)cookingInstruction);
    }

    public void delete(CookingInstruction cookingInstruction, Session s) {
        this.delete((Object)cookingInstruction, s);
    }

    public void refresh(CookingInstruction cookingInstruction, Session s) {
        this.refresh((Object)cookingInstruction, s);
    }
}

