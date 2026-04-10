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

import com.floreantpos.model.TerminalPrinters;
import com.floreantpos.model.dao.TerminalPrintersDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseTerminalPrintersDAO
extends _RootDAO {
    public static TerminalPrintersDAO instance;

    public static TerminalPrintersDAO getInstance() {
        if (null == instance) {
            instance = new TerminalPrintersDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return TerminalPrinters.class;
    }

    @Override
    public Order getDefaultOrder() {
        return null;
    }

    public TerminalPrinters cast(Object object) {
        return (TerminalPrinters)object;
    }

    public TerminalPrinters get(Integer key) throws HibernateException {
        return (TerminalPrinters)this.get(this.getReferenceClass(), key);
    }

    public TerminalPrinters get(Integer key, Session s) throws HibernateException {
        return (TerminalPrinters)this.get(this.getReferenceClass(), key, s);
    }

    public TerminalPrinters load(Integer key) throws HibernateException {
        return (TerminalPrinters)this.load(this.getReferenceClass(), key);
    }

    public TerminalPrinters load(Integer key, Session s) throws HibernateException {
        return (TerminalPrinters)this.load(this.getReferenceClass(), key, s);
    }

    public TerminalPrinters loadInitialize(Integer key, Session s) throws HibernateException {
        TerminalPrinters obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<TerminalPrinters> findAll() {
        return super.findAll();
    }

    @Override
    public List<TerminalPrinters> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<TerminalPrinters> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(TerminalPrinters terminalPrinters) throws HibernateException {
        return (Integer)super.save(terminalPrinters);
    }

    public Integer save(TerminalPrinters terminalPrinters, Session s) throws HibernateException {
        return (Integer)this.save((Object)terminalPrinters, s);
    }

    public void saveOrUpdate(TerminalPrinters terminalPrinters) throws HibernateException {
        this.saveOrUpdate((Object)terminalPrinters);
    }

    public void saveOrUpdate(TerminalPrinters terminalPrinters, Session s) throws HibernateException {
        this.saveOrUpdate((Object)terminalPrinters, s);
    }

    public void update(TerminalPrinters terminalPrinters) throws HibernateException {
        this.update((Object)terminalPrinters);
    }

    public void update(TerminalPrinters terminalPrinters, Session s) throws HibernateException {
        this.update((Object)terminalPrinters, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(TerminalPrinters terminalPrinters) throws HibernateException {
        this.delete((Object)terminalPrinters);
    }

    public void delete(TerminalPrinters terminalPrinters, Session s) throws HibernateException {
        this.delete((Object)terminalPrinters, s);
    }

    public void refresh(TerminalPrinters terminalPrinters, Session s) throws HibernateException {
        this.refresh((Object)terminalPrinters, s);
    }
}

