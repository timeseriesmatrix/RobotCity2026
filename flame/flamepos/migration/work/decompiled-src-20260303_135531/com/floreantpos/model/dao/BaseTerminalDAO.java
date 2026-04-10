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

import com.floreantpos.model.Terminal;
import com.floreantpos.model.dao.TerminalDAO;
import com.floreantpos.model.dao._RootDAO;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class BaseTerminalDAO
extends _RootDAO {
    public static TerminalDAO instance;

    public static TerminalDAO getInstance() {
        if (null == instance) {
            instance = new TerminalDAO();
        }
        return instance;
    }

    @Override
    public Class getReferenceClass() {
        return Terminal.class;
    }

    @Override
    public Order getDefaultOrder() {
        return Order.asc((String)"name");
    }

    public Terminal cast(Object object) {
        return (Terminal)object;
    }

    public Terminal get(Integer key) throws HibernateException {
        return (Terminal)this.get(this.getReferenceClass(), key);
    }

    public Terminal get(Integer key, Session s) throws HibernateException {
        return (Terminal)this.get(this.getReferenceClass(), key, s);
    }

    public Terminal load(Integer key) throws HibernateException {
        return (Terminal)this.load(this.getReferenceClass(), key);
    }

    public Terminal load(Integer key, Session s) throws HibernateException {
        return (Terminal)this.load(this.getReferenceClass(), key, s);
    }

    public Terminal loadInitialize(Integer key, Session s) throws HibernateException {
        Terminal obj = this.load(key, s);
        if (!Hibernate.isInitialized((Object)obj)) {
            Hibernate.initialize((Object)obj);
        }
        return obj;
    }

    @Override
    public List<Terminal> findAll() {
        return super.findAll();
    }

    @Override
    public List<Terminal> findAll(Order defaultOrder) {
        return super.findAll(defaultOrder);
    }

    @Override
    public List<Terminal> findAll(Session s, Order defaultOrder) {
        return super.findAll(s, defaultOrder);
    }

    public Integer save(Terminal terminal) throws HibernateException {
        return (Integer)super.save(terminal);
    }

    public Integer save(Terminal terminal, Session s) throws HibernateException {
        return (Integer)this.save((Object)terminal, s);
    }

    public void saveOrUpdate(Terminal terminal) throws HibernateException {
        this.saveOrUpdate((Object)terminal);
    }

    public void saveOrUpdate(Terminal terminal, Session s) throws HibernateException {
        this.saveOrUpdate((Object)terminal, s);
    }

    public void update(Terminal terminal) throws HibernateException {
        this.update((Object)terminal);
    }

    public void update(Terminal terminal, Session s) throws HibernateException {
        this.update((Object)terminal, s);
    }

    public void delete(Integer id) throws HibernateException {
        this.delete((Object)this.load(id));
    }

    public void delete(Integer id, Session s) throws HibernateException {
        this.delete((Object)this.load(id, s), s);
    }

    public void delete(Terminal terminal) throws HibernateException {
        this.delete((Object)terminal);
    }

    public void delete(Terminal terminal, Session s) throws HibernateException {
        this.delete((Object)terminal, s);
    }

    public void refresh(Terminal terminal, Session s) throws HibernateException {
        this.refresh((Object)terminal, s);
    }
}

