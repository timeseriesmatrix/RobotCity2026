/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.hibernate.Hibernate
 *  org.hibernate.Session
 *  org.hibernate.criterion.Order
 */
package com.floreantpos.model.dao;

import com.floreantpos.model.PayoutRecepient;
import com.floreantpos.model.dao.PayoutRecepientDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePayoutRecepientDAO
extends _RootDAO {
    public static PayoutRecepientDAO instance;

    public static PayoutRecepientDAO getInstance() {
        if (null == instance) {
            instance = new PayoutRecepientDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PayoutRecepient.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public PayoutRecepient cast(Object object) {
        return (PayoutRecepient)object;
    }

    public PayoutRecepient get(Integer key) {
        return (PayoutRecepient)this.get(this.getReferenceClass(), key);
    }

    public PayoutRecepient get(Integer key, Session s) {
        return (PayoutRecepient)this.get(this.getReferenceClass(), key, s);
    }

    public PayoutRecepient load(Integer key) {
        return (PayoutRecepient)this.load(this.getReferenceClass(), key);
    }

    public PayoutRecepient load(Integer key, Session s) {
        return (PayoutRecepient)this.load(this.getReferenceClass(), key, s);
    }

    public PayoutRecepient loadInitialize(Integer key, Session s) {
        PayoutRecepient obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PayoutRecepient> findAll() {
        return super.findAll();
    }

    @Override
    public List<PayoutRecepient> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PayoutRecepient> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PayoutRecepient payoutRecepient) {
        return (Integer)super.save(payoutRecepient);
    }

    public Integer save(PayoutRecepient payoutRecepient, Session s) {
        return (Integer)this.save((Object)payoutRecepient, s);
    }

    public void saveOrUpdate(PayoutRecepient payoutRecepient) {
        this.saveOrUpdate((Object)payoutRecepient);
    }

    public void saveOrUpdate(PayoutRecepient payoutRecepient, Session s) {
        this.saveOrUpdate((Object)payoutRecepient, s);
    }

    public void update(PayoutRecepient payoutRecepient) {
        this.update((Object)payoutRecepient);
    }

    public void update(PayoutRecepient payoutRecepient, Session s) {
        this.update((Object)payoutRecepient, s);
    }

    public void delete(Integer id) {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PayoutRecepient payoutRecepient) {
        this.delete((Object)payoutRecepient);
    }

    public void delete(PayoutRecepient payoutRecepient, Session s) {
        this.delete((Object)payoutRecepient, s);
    }

    public void refresh(PayoutRecepient payoutRecepient, Session s) {
        this.refresh((Object)payoutRecepient, s);
    }
}

