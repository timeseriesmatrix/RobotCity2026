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

import com.floreantpos.model.TicketDiscount;
import com.floreantpos.model.dao.TicketDiscountDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseTicketDiscountDAO
extends _RootDAO {
    public static TicketDiscountDAO instance;

    public static TicketDiscountDAO getInstance() {
        if (null == instance) {
            instance = new TicketDiscountDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return TicketDiscount.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public TicketDiscount cast(Object object) {
        return (TicketDiscount)object;
    }

    public TicketDiscount get(Integer key) throws HibernateException {
        return (TicketDiscount)this.get(this.getReferenceClass(), key);
    }

    public TicketDiscount get(Integer key, Session s) throws HibernateException {
        return (TicketDiscount)this.get(this.getReferenceClass(), key, s);
    }

    public TicketDiscount load(Integer key) throws HibernateException {
        return (TicketDiscount)this.load(this.getReferenceClass(), key);
    }

    public TicketDiscount load(Integer key, Session s) throws HibernateException {
        return (TicketDiscount)this.load(this.getReferenceClass(), key, s);
    }

    public TicketDiscount loadInitialize(Integer key, Session s) throws HibernateException {
        TicketDiscount obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<TicketDiscount> findAll() {
        return super.findAll();
    }

    @Override
    public List<TicketDiscount> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<TicketDiscount> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(TicketDiscount ticketDiscount) throws HibernateException {
        return (Integer)super.save(ticketDiscount);
    }

    public Integer save(TicketDiscount ticketDiscount, Session s) throws HibernateException {
        return (Integer)this.save((Object)ticketDiscount, s);
    }

    public void saveOrUpdate(TicketDiscount ticketDiscount) throws HibernateException {
        this.saveOrUpdate((Object)ticketDiscount);
    }

    public void saveOrUpdate(TicketDiscount ticketDiscount, Session s) throws HibernateException {
        this.saveOrUpdate((Object)ticketDiscount, s);
    }

    public void update(TicketDiscount ticketDiscount) throws HibernateException {
        this.update((Object)ticketDiscount);
    }

    public void update(TicketDiscount ticketDiscount, Session s) throws HibernateException {
        this.update((Object)ticketDiscount, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(TicketDiscount ticketDiscount) throws HibernateException {
        this.delete((Object)ticketDiscount);
    }

    public void delete(TicketDiscount ticketDiscount, Session s) throws HibernateException {
        this.delete((Object)ticketDiscount, s);
    }

    public void refresh(TicketDiscount ticketDiscount, Session s) throws HibernateException {
        this.refresh((Object)ticketDiscount, s);
    }
}

