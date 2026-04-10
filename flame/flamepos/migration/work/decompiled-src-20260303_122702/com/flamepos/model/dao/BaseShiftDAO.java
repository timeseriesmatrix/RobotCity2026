/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.Shift;
import com.floreantpos.model.dao.ShiftDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseShiftDAO
extends _RootDAO {
    public static ShiftDAO instance;

    public static ShiftDAO getInstance() {
        if (null == instance) {
            instance = new ShiftDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Shift.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public Shift cast(Object object) {
        return (Shift)object;
    }

    public Shift get(Integer key) {
        return (Shift)this.get(this.getReferenceClass(), key);
    }

    public Shift get(Integer key, Session s) {
        return (Shift)this.get(this.getReferenceClass(), key, s);
    }

    public Shift load(Integer key) {
        return (Shift)this.load(this.getReferenceClass(), key);
    }

    public Shift load(Integer key, Session s) {
        return (Shift)this.load(this.getReferenceClass(), key, s);
    }

    public Shift loadInitialize(Integer key, Session s) {
        Shift obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Shift> findAll() {
        return super.findAll();
    }

    @Override
    public List<Shift> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Shift> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(Shift shift) {
        return (Integer)super.save(shift);
    }

    public Integer save(Shift shift, Session s) {
        return (Integer)this.save((Object)shift, s);
    }

    public void saveOrUpdate(Shift shift) {
        this.saveOrUpdate((Object)shift);
    }

    public void saveOrUpdate(Shift shift, Session s) {
        this.saveOrUpdate((Object)shift, s);
    }

    public void update(Shift shift) {
        this.update((Object)shift);
    }

    public void update(Shift shift, Session s) {
        this.update((Object)shift, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Shift shift) {
        this.delete((Object)shift);
    }

    public void delete(Shift shift, Session s) {
        this.delete((Object)shift, s);
    }

    public void refresh(Shift shift, Session s) {
        this.refresh((Object)shift, s);
    }
}

