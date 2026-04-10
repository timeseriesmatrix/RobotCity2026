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

import com.floreantpos.model.TicketItemDiscount;
import com.floreantpos.model.dao.TicketItemDiscountDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseTicketItemDiscountDAO
extends _RootDAO {
    public static TicketItemDiscountDAO instance;

    public static TicketItemDiscountDAO getInstance() {
        if (null == instance) {
            instance = new TicketItemDiscountDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return TicketItemDiscount.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public TicketItemDiscount cast(Object object) {
        return (TicketItemDiscount)object;
    }

    public TicketItemDiscount get(Integer key) throws HibernateException {
        return (TicketItemDiscount)this.get(this.getReferenceClass(), key);
    }

    public TicketItemDiscount get(Integer key, Session s) throws HibernateException {
        return (TicketItemDiscount)this.get(this.getReferenceClass(), key, s);
    }

    public TicketItemDiscount load(Integer key) throws HibernateException {
        return (TicketItemDiscount)this.load(this.getReferenceClass(), key);
    }

    public TicketItemDiscount load(Integer key, Session s) throws HibernateException {
        return (TicketItemDiscount)this.load(this.getReferenceClass(), key, s);
    }

    public TicketItemDiscount loadInitialize(Integer key, Session s) throws HibernateException {
        TicketItemDiscount obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<TicketItemDiscount> findAll() {
        return super.findAll();
    }

    @Override
    public List<TicketItemDiscount> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<TicketItemDiscount> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(TicketItemDiscount ticketItemDiscount) throws HibernateException {
        return (Integer)super.save(ticketItemDiscount);
    }

    public Integer save(TicketItemDiscount ticketItemDiscount, Session s) throws HibernateException {
        return (Integer)this.save((Object)ticketItemDiscount, s);
    }

    public void saveOrUpdate(TicketItemDiscount ticketItemDiscount) throws HibernateException {
        this.saveOrUpdate((Object)ticketItemDiscount);
    }

    public void saveOrUpdate(TicketItemDiscount ticketItemDiscount, Session s) throws HibernateException {
        this.saveOrUpdate((Object)ticketItemDiscount, s);
    }

    public void update(TicketItemDiscount ticketItemDiscount) throws HibernateException {
        this.update((Object)ticketItemDiscount);
    }

    public void update(TicketItemDiscount ticketItemDiscount, Session s) throws HibernateException {
        this.update((Object)ticketItemDiscount, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(TicketItemDiscount ticketItemDiscount) throws HibernateException {
        this.delete((Object)ticketItemDiscount);
    }

    public void delete(TicketItemDiscount ticketItemDiscount, Session s) throws HibernateException {
        this.delete((Object)ticketItemDiscount, s);
    }

    public void refresh(TicketItemDiscount ticketItemDiscount, Session s) throws HibernateException {
        this.refresh((Object)ticketItemDiscount, s);
    }
}

