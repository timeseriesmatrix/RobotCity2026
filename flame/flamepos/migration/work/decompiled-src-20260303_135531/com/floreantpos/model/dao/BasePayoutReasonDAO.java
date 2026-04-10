/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.PayoutReason;
import com.floreantpos.model.dao.PayoutReasonDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePayoutReasonDAO
extends _RootDAO {
    public static PayoutReasonDAO instance;

    public static PayoutReasonDAO getInstance() {
        if (null == instance) {
            instance = new PayoutReasonDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PayoutReason.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public PayoutReason cast(Object object) {
        return (PayoutReason)object;
    }

    public PayoutReason get(Integer key) {
        return (PayoutReason)this.get(this.getReferenceClass(), key);
    }

    public PayoutReason get(Integer key, Session s) {
        return (PayoutReason)this.get(this.getReferenceClass(), key, s);
    }

    public PayoutReason load(Integer key) {
        return (PayoutReason)this.load(this.getReferenceClass(), key);
    }

    public PayoutReason load(Integer key, Session s) {
        return (PayoutReason)this.load(this.getReferenceClass(), key, s);
    }

    public PayoutReason loadInitialize(Integer key, Session s) {
        PayoutReason obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PayoutReason> findAll() {
        return super.findAll();
    }

    @Override
    public List<PayoutReason> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PayoutReason> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PayoutReason payoutReason) {
        return (Integer)super.save(payoutReason);
    }

    public Integer save(PayoutReason payoutReason, Session s) {
        return (Integer)this.save((Object)payoutReason, s);
    }

    public void saveOrUpdate(PayoutReason payoutReason) {
        this.saveOrUpdate((Object)payoutReason);
    }

    public void saveOrUpdate(PayoutReason payoutReason, Session s) {
        this.saveOrUpdate((Object)payoutReason, s);
    }

    public void update(PayoutReason payoutReason) {
        this.update((Object)payoutReason);
    }

    public void update(PayoutReason payoutReason, Session s) {
        this.update((Object)payoutReason, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PayoutReason payoutReason) {
        this.delete((Object)payoutReason);
    }

    public void delete(PayoutReason payoutReason, Session s) {
        this.delete((Object)payoutReason, s);
    }

    public void refresh(PayoutReason payoutReason, Session s) {
        this.refresh((Object)payoutReason, s);
    }
}

