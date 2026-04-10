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

import com.floreantpos.model.KitchenTicket;
import com.floreantpos.model.dao.KitchenTicketDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseKitchenTicketDAO
extends _RootDAO {
    public static KitchenTicketDAO instance;

    public static KitchenTicketDAO getInstance() {
        if (null == instance) {
            instance = new KitchenTicketDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return KitchenTicket.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public KitchenTicket cast(Object object) {
        return (KitchenTicket)object;
    }

    public KitchenTicket get(Integer key) throws HibernateException {
        return (KitchenTicket)this.get(this.getReferenceClass(), key);
    }

    public KitchenTicket get(Integer key, Session s) throws HibernateException {
        return (KitchenTicket)this.get(this.getReferenceClass(), key, s);
    }

    public KitchenTicket load(Integer key) throws HibernateException {
        return (KitchenTicket)this.load(this.getReferenceClass(), key);
    }

    public KitchenTicket load(Integer key, Session s) throws HibernateException {
        return (KitchenTicket)this.load(this.getReferenceClass(), key, s);
    }

    public KitchenTicket loadInitialize(Integer key, Session s) throws HibernateException {
        KitchenTicket obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<KitchenTicket> findAll() {
        return super.findAll();
    }

    @Override
    public List<KitchenTicket> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<KitchenTicket> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(KitchenTicket kitchenTicket) throws HibernateException {
        return (Integer)super.save(kitchenTicket);
    }

    public Integer save(KitchenTicket kitchenTicket, Session s) throws HibernateException {
        return (Integer)this.save((Object)kitchenTicket, s);
    }

    public void saveOrUpdate(KitchenTicket kitchenTicket) throws HibernateException {
        this.saveOrUpdate((Object)kitchenTicket);
    }

    public void saveOrUpdate(KitchenTicket kitchenTicket, Session s) throws HibernateException {
        this.saveOrUpdate((Object)kitchenTicket, s);
    }

    public void update(KitchenTicket kitchenTicket) throws HibernateException {
        this.update((Object)kitchenTicket);
    }

    public void update(KitchenTicket kitchenTicket, Session s) throws HibernateException {
        this.update((Object)kitchenTicket, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(KitchenTicket kitchenTicket) throws HibernateException {
        this.delete((Object)kitchenTicket);
    }

    public void delete(KitchenTicket kitchenTicket, Session s) throws HibernateException {
        this.delete((Object)kitchenTicket, s);
    }

    public void refresh(KitchenTicket kitchenTicket, Session s) throws HibernateException {
        this.refresh((Object)kitchenTicket, s);
    }
}

