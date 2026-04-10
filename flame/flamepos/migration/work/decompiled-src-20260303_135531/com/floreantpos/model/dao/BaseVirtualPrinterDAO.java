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

import com.floreantpos.model.VirtualPrinter;
import com.floreantpos.model.dao.VirtualPrinterDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseVirtualPrinterDAO
extends _RootDAO {
    public static VirtualPrinterDAO instance;

    public static VirtualPrinterDAO getInstance() {
        if (null == instance) {
            instance = new VirtualPrinterDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return VirtualPrinter.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public VirtualPrinter cast(Object object) {
        return (VirtualPrinter)object;
    }

    public VirtualPrinter get(Integer key) throws HibernateException {
        return (VirtualPrinter)this.get(this.getReferenceClass(), key);
    }

    public VirtualPrinter get(Integer key, Session s) throws HibernateException {
        return (VirtualPrinter)this.get(this.getReferenceClass(), key, s);
    }

    public VirtualPrinter load(Integer key) throws HibernateException {
        return (VirtualPrinter)this.load(this.getReferenceClass(), key);
    }

    public VirtualPrinter load(Integer key, Session s) throws HibernateException {
        return (VirtualPrinter)this.load(this.getReferenceClass(), key, s);
    }

    public VirtualPrinter loadInitialize(Integer key, Session s) throws HibernateException {
        VirtualPrinter obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<VirtualPrinter> findAll() {
        return super.findAll();
    }

    @Override
    public List<VirtualPrinter> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<VirtualPrinter> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(VirtualPrinter virtualPrinter) throws HibernateException {
        return (Integer)super.save(virtualPrinter);
    }

    public Integer save(VirtualPrinter virtualPrinter, Session s) throws HibernateException {
        return (Integer)this.save((Object)virtualPrinter, s);
    }

    public void saveOrUpdate(VirtualPrinter virtualPrinter) throws HibernateException {
        this.saveOrUpdate((Object)virtualPrinter);
    }

    public void saveOrUpdate(VirtualPrinter virtualPrinter, Session s) throws HibernateException {
        this.saveOrUpdate((Object)virtualPrinter, s);
    }

    public void update(VirtualPrinter virtualPrinter) throws HibernateException {
        this.update((Object)virtualPrinter);
    }

    public void update(VirtualPrinter virtualPrinter, Session s) throws HibernateException {
        this.update((Object)virtualPrinter, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(VirtualPrinter virtualPrinter) throws HibernateException {
        this.delete((Object)virtualPrinter);
    }

    public void delete(VirtualPrinter virtualPrinter, Session s) throws HibernateException {
        this.delete((Object)virtualPrinter, s);
    }

    public void refresh(VirtualPrinter virtualPrinter, Session s) throws HibernateException {
        this.refresh((Object)virtualPrinter, s);
    }
}

