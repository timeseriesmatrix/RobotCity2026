/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.AttendenceHistory;
import com.floreantpos.model.dao.AttendenceHistoryDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseAttendenceHistoryDAO
extends _RootDAO {
    public static AttendenceHistoryDAO instance;

    public static AttendenceHistoryDAO getInstance() {
        if (null == instance) {
            instance = new AttendenceHistoryDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return AttendenceHistory.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public AttendenceHistory cast(Object object) {
        return (AttendenceHistory)object;
    }

    public AttendenceHistory get(Integer key) {
        return (AttendenceHistory)this.get(this.getReferenceClass(), key);
    }

    public AttendenceHistory get(Integer key, Session s) {
        return (AttendenceHistory)this.get(this.getReferenceClass(), key, s);
    }

    public AttendenceHistory load(Integer key) {
        return (AttendenceHistory)this.load(this.getReferenceClass(), key);
    }

    public AttendenceHistory load(Integer key, Session s) {
        return (AttendenceHistory)this.load(this.getReferenceClass(), key, s);
    }

    public AttendenceHistory loadInitialize(Integer key, Session s) {
        AttendenceHistory obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<AttendenceHistory> findAll() {
        return super.findAll();
    }

    @Override
    public List<AttendenceHistory> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<AttendenceHistory> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(AttendenceHistory attendenceHistory) {
        return (Integer)super.save(attendenceHistory);
    }

    public Integer save(AttendenceHistory attendenceHistory, Session s) {
        return (Integer)this.save((Object)attendenceHistory, s);
    }

    public void saveOrUpdate(AttendenceHistory attendenceHistory) {
        this.saveOrUpdate((Object)attendenceHistory);
    }

    public void saveOrUpdate(AttendenceHistory attendenceHistory, Session s) {
        this.saveOrUpdate((Object)attendenceHistory, s);
    }

    public void update(AttendenceHistory attendenceHistory) {
        this.update((Object)attendenceHistory);
    }

    public void update(AttendenceHistory attendenceHistory, Session s) {
        this.update((Object)attendenceHistory, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(AttendenceHistory attendenceHistory) {
        this.delete((Object)attendenceHistory);
    }

    public void delete(AttendenceHistory attendenceHistory, Session s) {
        this.delete((Object)attendenceHistory, s);
    }

    public void refresh(AttendenceHistory attendenceHistory, Session s) {
        this.refresh((Object)attendenceHistory, s);
    }
}

