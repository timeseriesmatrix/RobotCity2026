/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.VoidReason;
import com.floreantpos.model.dao.VoidReasonDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseVoidReasonDAO
extends _RootDAO {
    public static VoidReasonDAO instance;

    public static VoidReasonDAO getInstance() {
        if (null == instance) {
            instance = new VoidReasonDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return VoidReason.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public VoidReason cast(Object object) {
        return (VoidReason)object;
    }

    public VoidReason get(Integer key) {
        return (VoidReason)this.get(this.getReferenceClass(), key);
    }

    public VoidReason get(Integer key, Session s) {
        return (VoidReason)this.get(this.getReferenceClass(), key, s);
    }

    public VoidReason load(Integer key) {
        return (VoidReason)this.load(this.getReferenceClass(), key);
    }

    public VoidReason load(Integer key, Session s) {
        return (VoidReason)this.load(this.getReferenceClass(), key, s);
    }

    public VoidReason loadInitialize(Integer key, Session s) {
        VoidReason obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<VoidReason> findAll() {
        return super.findAll();
    }

    @Override
    public List<VoidReason> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<VoidReason> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(VoidReason voidReason) {
        return (Integer)super.save(voidReason);
    }

    public Integer save(VoidReason voidReason, Session s) {
        return (Integer)this.save((Object)voidReason, s);
    }

    public void saveOrUpdate(VoidReason voidReason) {
        this.saveOrUpdate((Object)voidReason);
    }

    public void saveOrUpdate(VoidReason voidReason, Session s) {
        this.saveOrUpdate((Object)voidReason, s);
    }

    public void update(VoidReason voidReason) {
        this.update((Object)voidReason);
    }

    public void update(VoidReason voidReason, Session s) {
        this.update((Object)voidReason, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(VoidReason voidReason) {
        this.delete((Object)voidReason);
    }

    public void delete(VoidReason voidReason, Session s) {
        this.delete((Object)voidReason, s);
    }

    public void refresh(VoidReason voidReason, Session s) {
        this.refresh((Object)voidReason, s);
    }
}

