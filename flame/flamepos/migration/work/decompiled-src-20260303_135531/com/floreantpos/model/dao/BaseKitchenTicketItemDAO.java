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

import com.floreantpos.model.KitchenTicketItem;
import com.floreantpos.model.dao.KitchenTicketItemDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseKitchenTicketItemDAO
extends _RootDAO {
    public static KitchenTicketItemDAO instance;

    public static KitchenTicketItemDAO getInstance() {
        if (null == instance) {
            instance = new KitchenTicketItemDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return KitchenTicketItem.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public KitchenTicketItem cast(Object object) {
        return (KitchenTicketItem)object;
    }

    public KitchenTicketItem get(Integer key) throws HibernateException {
        return (KitchenTicketItem)this.get(this.getReferenceClass(), key);
    }

    public KitchenTicketItem get(Integer key, Session s) throws HibernateException {
        return (KitchenTicketItem)this.get(this.getReferenceClass(), key, s);
    }

    public KitchenTicketItem load(Integer key) throws HibernateException {
        return (KitchenTicketItem)this.load(this.getReferenceClass(), key);
    }

    public KitchenTicketItem load(Integer key, Session s) throws HibernateException {
        return (KitchenTicketItem)this.load(this.getReferenceClass(), key, s);
    }

    public KitchenTicketItem loadInitialize(Integer key, Session s) throws HibernateException {
        KitchenTicketItem obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<KitchenTicketItem> findAll() {
        return super.findAll();
    }

    @Override
    public List<KitchenTicketItem> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<KitchenTicketItem> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(KitchenTicketItem kitchenTicketItem) throws HibernateException {
        return (Integer)super.save(kitchenTicketItem);
    }

    public Integer save(KitchenTicketItem kitchenTicketItem, Session s) throws HibernateException {
        return (Integer)this.save((Object)kitchenTicketItem, s);
    }

    public void saveOrUpdate(KitchenTicketItem kitchenTicketItem) throws HibernateException {
        this.saveOrUpdate((Object)kitchenTicketItem);
    }

    public void saveOrUpdate(KitchenTicketItem kitchenTicketItem, Session s) throws HibernateException {
        this.saveOrUpdate((Object)kitchenTicketItem, s);
    }

    public void update(KitchenTicketItem kitchenTicketItem) throws HibernateException {
        this.update((Object)kitchenTicketItem);
    }

    public void update(KitchenTicketItem kitchenTicketItem, Session s) throws HibernateException {
        this.update((Object)kitchenTicketItem, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(KitchenTicketItem kitchenTicketItem) throws HibernateException {
        this.delete((Object)kitchenTicketItem);
    }

    public void delete(KitchenTicketItem kitchenTicketItem, Session s) throws HibernateException {
        this.delete((Object)kitchenTicketItem, s);
    }

    public void refresh(KitchenTicketItem kitchenTicketItem, Session s) throws HibernateException {
        this.refresh((Object)kitchenTicketItem, s);
    }
}

