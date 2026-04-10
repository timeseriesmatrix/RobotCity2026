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

import com.floreantpos.model.PrinterGroup;
import com.floreantpos.model.dao.PrinterGroupDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BasePrinterGroupDAO
extends _RootDAO {
    public static PrinterGroupDAO instance;

    public static PrinterGroupDAO getInstance() {
        if (null == instance) {
            instance = new PrinterGroupDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return PrinterGroup.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public PrinterGroup cast(Object object) {
        return (PrinterGroup)object;
    }

    public PrinterGroup get(Integer key) throws HibernateException {
        return (PrinterGroup)this.get(this.getReferenceClass(), key);
    }

    public PrinterGroup get(Integer key, Session s) throws HibernateException {
        return (PrinterGroup)this.get(this.getReferenceClass(), key, s);
    }

    public PrinterGroup load(Integer key) throws HibernateException {
        return (PrinterGroup)this.load(this.getReferenceClass(), key);
    }

    public PrinterGroup load(Integer key, Session s) throws HibernateException {
        return (PrinterGroup)this.load(this.getReferenceClass(), key, s);
    }

    public PrinterGroup loadInitialize(Integer key, Session s) throws HibernateException {
        PrinterGroup obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<PrinterGroup> findAll() {
        return super.findAll();
    }

    @Override
    public List<PrinterGroup> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<PrinterGroup> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(PrinterGroup printerGroup) throws HibernateException {
        return (Integer)super.save(printerGroup);
    }

    public Integer save(PrinterGroup printerGroup, Session s) throws HibernateException {
        return (Integer)this.save((Object)printerGroup, s);
    }

    public void saveOrUpdate(PrinterGroup printerGroup) throws HibernateException {
        this.saveOrUpdate((Object)printerGroup);
    }

    public void saveOrUpdate(PrinterGroup printerGroup, Session s) throws HibernateException {
        this.saveOrUpdate((Object)printerGroup, s);
    }

    public void update(PrinterGroup printerGroup) throws HibernateException {
        this.update((Object)printerGroup);
    }

    public void update(PrinterGroup printerGroup, Session s) throws HibernateException {
        this.update((Object)printerGroup, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(PrinterGroup printerGroup) throws HibernateException {
        this.delete((Object)printerGroup);
    }

    public void delete(PrinterGroup printerGroup, Session s) throws HibernateException {
        this.delete((Object)printerGroup, s);
    }

    public void refresh(PrinterGroup printerGroup, Session s) throws HibernateException {
        this.refresh((Object)printerGroup, s);
    }
}

