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

import com.floreantpos.model.TicketItem;
import com.floreantpos.model.dao.TicketItemDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseTicketItemDAO
extends _RootDAO {
    public static TicketItemDAO instance;

    public static TicketItemDAO getInstance() {
        if (null == instance) {
            instance = new TicketItemDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return TicketItem.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public TicketItem cast(Object object) {
        return (TicketItem)object;
    }

    public TicketItem get(Integer key) throws HibernateException {
        return (TicketItem)this.get(this.getReferenceClass(), key);
    }

    public TicketItem get(Integer key, Session s) throws HibernateException {
        return (TicketItem)this.get(this.getReferenceClass(), key, s);
    }

    public TicketItem load(Integer key) throws HibernateException {
        return (TicketItem)this.load(this.getReferenceClass(), key);
    }

    public TicketItem load(Integer key, Session s) throws HibernateException {
        return (TicketItem)this.load(this.getReferenceClass(), key, s);
    }

    public TicketItem loadInitialize(Integer key, Session s) throws HibernateException {
        TicketItem obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<TicketItem> findAll() {
        return super.findAll();
    }

    @Override
    public List<TicketItem> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<TicketItem> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(TicketItem ticketItem) throws HibernateException {
        return (Integer)super.save(ticketItem);
    }

    public Integer save(TicketItem ticketItem, Session s) throws HibernateException {
        return (Integer)this.save((Object)ticketItem, s);
    }

    public void saveOrUpdate(TicketItem ticketItem) throws HibernateException {
        this.saveOrUpdate((Object)ticketItem);
    }

    public void saveOrUpdate(TicketItem ticketItem, Session s) throws HibernateException {
        this.saveOrUpdate((Object)ticketItem, s);
    }

    public void update(TicketItem ticketItem) throws HibernateException {
        this.update((Object)ticketItem);
    }

    public void update(TicketItem ticketItem, Session s) throws HibernateException {
        this.update((Object)ticketItem, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(TicketItem ticketItem) throws HibernateException {
        this.delete((Object)ticketItem);
    }

    public void delete(TicketItem ticketItem, Session s) throws HibernateException {
        this.delete((Object)ticketItem, s);
    }

    public void refresh(TicketItem ticketItem, Session s) throws HibernateException {
        this.refresh((Object)ticketItem, s);
    }
}

