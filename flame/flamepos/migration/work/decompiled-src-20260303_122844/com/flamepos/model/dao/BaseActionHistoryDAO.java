/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.ActionHistory;
import com.floreantpos.model.dao.ActionHistoryDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseActionHistoryDAO
extends _RootDAO {
    public static ActionHistoryDAO instance;

    public static ActionHistoryDAO getInstance() {
        if (null == instance) {
            instance = new ActionHistoryDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return ActionHistory.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public ActionHistory cast(Object object) {
        return (ActionHistory)object;
    }

    public ActionHistory get(Integer key) {
        return (ActionHistory)this.get(this.getReferenceClass(), key);
    }

    public ActionHistory get(Integer key, Session s) {
        return (ActionHistory)this.get(this.getReferenceClass(), key, s);
    }

    public ActionHistory load(Integer key) {
        return (ActionHistory)this.load(this.getReferenceClass(), key);
    }

    public ActionHistory load(Integer key, Session s) {
        return (ActionHistory)this.load(this.getReferenceClass(), key, s);
    }

    public ActionHistory loadInitialize(Integer key, Session s) {
        ActionHistory obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<ActionHistory> findAll() {
        return super.findAll();
    }

    @Override
    public List<ActionHistory> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<ActionHistory> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(ActionHistory actionHistory) {
        return (Integer)super.save(actionHistory);
    }

    public Integer save(ActionHistory actionHistory, Session s) {
        return (Integer)this.save((Object)actionHistory, s);
    }

    public void saveOrUpdate(ActionHistory actionHistory) {
        this.saveOrUpdate((Object)actionHistory);
    }

    public void saveOrUpdate(ActionHistory actionHistory, Session s) {
        this.saveOrUpdate((Object)actionHistory, s);
    }

    public void update(ActionHistory actionHistory) {
        this.update((Object)actionHistory);
    }

    public void update(ActionHistory actionHistory, Session s) {
        this.update((Object)actionHistory, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(ActionHistory actionHistory) {
        this.delete((Object)actionHistory);
    }

    public void delete(ActionHistory actionHistory, Session s) {
        this.delete((Object)actionHistory, s);
    }

    public void refresh(ActionHistory actionHistory, Session s) {
        this.refresh((Object)actionHistory, s);
    }
}

