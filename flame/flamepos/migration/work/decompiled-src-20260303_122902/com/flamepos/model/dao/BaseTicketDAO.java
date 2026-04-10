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

import com.floreantpos.model.Ticket;
import com.floreantpos.model.dao.TicketDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseTicketDAO
extends _RootDAO {
    public static TicketDAO instance;

    public static TicketDAO getInstance() {
        if (null == instance) {
            instance = new TicketDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Ticket.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public Ticket cast(Object object) {
        return (Ticket)object;
    }

    public Ticket get(Integer key) throws HibernateException {
        return (Ticket)this.get(this.getReferenceClass(), key);
    }

    public Ticket get(Integer key, Session s) throws HibernateException {
        return (Ticket)this.get(this.getReferenceClass(), key, s);
    }

    public Ticket load(Integer key) throws HibernateException {
        return (Ticket)this.load(this.getReferenceClass(), key);
    }

    public Ticket load(Integer key, Session s) throws HibernateException {
        return (Ticket)this.load(this.getReferenceClass(), key, s);
    }

    public Ticket loadInitialize(Integer key, Session s) throws HibernateException {
        Ticket obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Ticket> findAll() {
        return super.findAll();
    }

    @Override
    public List<Ticket> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Ticket> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(Ticket ticket) throws HibernateException {
        return (Integer)super.save(ticket);
    }

    public Integer save(Ticket ticket, Session s) throws HibernateException {
        return (Integer)this.save((Object)ticket, s);
    }

    public void saveOrUpdate(Ticket ticket) throws HibernateException {
        this.saveOrUpdate((Object)ticket);
    }

    public void saveOrUpdate(Ticket ticket, Session s) throws HibernateException {
        this.saveOrUpdate((Object)ticket, s);
    }

    public void update(Ticket ticket) throws HibernateException {
        this.update((Object)ticket);
    }

    public void update(Ticket ticket, Session s) throws HibernateException {
        this.update((Object)ticket, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Ticket ticket) throws HibernateException {
        this.delete((Object)ticket);
    }

    public void delete(Ticket ticket, Session s) throws HibernateException {
        this.delete((Object)ticket, s);
    }

    public void refresh(Ticket ticket, Session s) throws HibernateException {
        this.refresh((Object)ticket, s);
    }
}

