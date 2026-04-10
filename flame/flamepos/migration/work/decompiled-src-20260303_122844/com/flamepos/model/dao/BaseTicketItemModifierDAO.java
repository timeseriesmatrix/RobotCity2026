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

import com.floreantpos.model.TicketItemModifier;
import com.floreantpos.model.dao.TicketItemModifierDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseTicketItemModifierDAO
extends _RootDAO {
    public static TicketItemModifierDAO instance;

    public static TicketItemModifierDAO getInstance() {
        if (null == instance) {
            instance = new TicketItemModifierDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return TicketItemModifier.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public TicketItemModifier cast(Object object) {
        return (TicketItemModifier)object;
    }

    public TicketItemModifier get(Integer key) throws HibernateException {
        return (TicketItemModifier)this.get(this.getReferenceClass(), key);
    }

    public TicketItemModifier get(Integer key, Session s) throws HibernateException {
        return (TicketItemModifier)this.get(this.getReferenceClass(), key, s);
    }

    public TicketItemModifier load(Integer key) throws HibernateException {
        return (TicketItemModifier)this.load(this.getReferenceClass(), key);
    }

    public TicketItemModifier load(Integer key, Session s) throws HibernateException {
        return (TicketItemModifier)this.load(this.getReferenceClass(), key, s);
    }

    public TicketItemModifier loadInitialize(Integer key, Session s) throws HibernateException {
        TicketItemModifier obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<TicketItemModifier> findAll() {
        return super.findAll();
    }

    @Override
    public List<TicketItemModifier> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<TicketItemModifier> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(TicketItemModifier ticketItemModifier) throws HibernateException {
        return (Integer)super.save(ticketItemModifier);
    }

    public Integer save(TicketItemModifier ticketItemModifier, Session s) throws HibernateException {
        return (Integer)this.save((Object)ticketItemModifier, s);
    }

    public void saveOrUpdate(TicketItemModifier ticketItemModifier) throws HibernateException {
        this.saveOrUpdate((Object)ticketItemModifier);
    }

    public void saveOrUpdate(TicketItemModifier ticketItemModifier, Session s) throws HibernateException {
        this.saveOrUpdate((Object)ticketItemModifier, s);
    }

    public void update(TicketItemModifier ticketItemModifier) throws HibernateException {
        this.update((Object)ticketItemModifier);
    }

    public void update(TicketItemModifier ticketItemModifier, Session s) throws HibernateException {
        this.update((Object)ticketItemModifier, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(TicketItemModifier ticketItemModifier) throws HibernateException {
        this.delete((Object)ticketItemModifier);
    }

    public void delete(TicketItemModifier ticketItemModifier, Session s) throws HibernateException {
        this.delete((Object)ticketItemModifier, s);
    }

    public void refresh(TicketItemModifier ticketItemModifier, Session s) throws HibernateException {
        this.refresh((Object)ticketItemModifier, s);
    }
}

